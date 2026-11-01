import { randomUUID } from "node:crypto";
import { afterAll, beforeAll, describe, expect, it } from "vitest";
import { buildApp } from "../src/app.js";
import { prisma } from "../src/db.js";
import { keywordClustering } from "../src/services/keywordClustering.js";

// Level: API tests against the local Docker PostgreSQL (see README). Test users are deleted afterwards.
const emailPrefix = `test-${randomUUID()}`;
let app: Awaited<ReturnType<typeof buildApp>>;

beforeAll(async () => {
  app = await buildApp({ logger: false });
  await app.ready();
});

afterAll(async () => {
  await prisma.user.deleteMany({ where: { email: { startsWith: emailPrefix } } });
  await app.close();
  await prisma.$disconnect();
});

async function registerUser(label: string) {
  const res = await app.inject({
    method: "POST",
    url: "/api/v1/auth/register",
    payload: { name: "Test User", email: `${emailPrefix}-${label}@example.test`, country: "DK" },
  });
  return { res, user: res.json() as { id: string } };
}

describe("health and auth stub", () => {
  it("answers health without auth", async () => {
    const res = await app.inject({ method: "GET", url: "/api/v1/health" });
    expect(res.statusCode).toBe(200);
    expect(res.json()).toEqual({ status: "ok" });
  });

  it("rejects protected routes without a known X-User-Id", async () => {
    const res = await app.inject({ method: "GET", url: "/api/v1/user/me" });
    expect(res.statusCode).toBe(401);
    const unknown = await app.inject({ method: "GET", url: "/api/v1/user/me", headers: { "x-user-id": randomUUID() } });
    expect(unknown.statusCode).toBe(401);
  });

  it("registers a user and returns the same user for the same email", async () => {
    const first = await registerUser("reg");
    expect(first.res.statusCode).toBe(201);
    const second = await registerUser("reg");
    expect(second.res.statusCode).toBe(200);
    expect(second.user.id).toBe(first.user.id);
    const me = await app.inject({ method: "GET", url: "/api/v1/user/me", headers: { "x-user-id": first.user.id } });
    expect(me.json().name).toBe("Test User");
  });
});

describe("journey to plan flow", () => {
  it("runs all four steps, generates plan versions, insights and export", async () => {
    const { user } = await registerUser("flow");
    const headers = { "x-user-id": user.id };

    const journey = (await app.inject({ method: "GET", url: "/api/v1/journey/current", headers })).json();
    expect(journey.version).toBe(1);
    expect(journey.steps.interests).toBeNull();

    // plan cannot be generated before all steps are saved
    const early = await app.inject({ method: "POST", url: `/api/v1/journey/${journey.id}/plan/generate`, headers });
    expect(early.statusCode).toBe(409);

    const put = (step: string, payload: unknown) =>
      app.inject({ method: "PUT", url: `/api/v1/journey/${journey.id}/${step}`, headers, payload: payload as object });

    expect((await put("interests", { items: ["Teaching people", "Writing stories", "Teaching kids"], topTwo: ["Teaching people"] })).statusCode).toBe(200);
    // a half-written draft moment is accepted (auto-save) but left out of the plan
    expect((await put("best", { examples: [{ text: "Led a workshop", reasons: ["I helped others"] }, { text: "", reasons: ["draft"] }] })).statusCode).toBe(200);
    expect((await put("values", { selected: ["Variety", "Making a difference", "Work-life balance"], reflection: "It would drain me." })).statusCode).toBe(200);
    const excited = await put("excited", {
      excitementItems: ["Teaching a new skill"],
      objectives: [{ timeframe: "1 month", action: "talk to two coaches", outcome: "I understand the path", challenges: ["time"], resources: ["a mentor"] }],
    });
    expect(excited.statusCode).toBe(200);
    expect(excited.json().status).toBe("steps_complete");

    const v1 = await app.inject({ method: "POST", url: `/api/v1/journey/${journey.id}/plan/generate`, headers });
    expect(v1.statusCode).toBe(201);
    expect(v1.json().version).toBe(1);
    const v2 = await app.inject({ method: "POST", url: `/api/v1/journey/${journey.id}/plan/generate`, headers });
    expect(v2.json().version).toBe(2);

    const plan = (await app.inject({ method: "GET", url: `/api/v1/journey/${journey.id}/plan`, headers })).json();
    expect(plan.version).toBe(2);
    expect(plan.data.userName).toBe("Test User");
    expect(plan.data.objectives).toHaveLength(1);
    expect(plan.data.bestMoments).toHaveLength(1);

    const insights = (await app.inject({ method: "GET", url: `/api/v1/insights/${journey.id}`, headers })).json();
    expect(insights.keywords[0]).toEqual({ word: "teaching", count: 3 });

    const exported = await app.inject({ method: "POST", url: `/api/v1/journey/${journey.id}/plan/export`, headers });
    expect(exported.headers["content-type"]).toContain("text/html");
    expect(exported.body).toContain("Test User");
  });

  it("rejects invalid step payloads and other users' journeys", async () => {
    const owner = (await registerUser("owner")).user;
    const other = (await registerUser("other")).user;
    const journey = (await app.inject({ method: "GET", url: "/api/v1/journey/current", headers: { "x-user-id": owner.id } })).json();

    const tooMany = await app.inject({
      method: "PUT",
      url: `/api/v1/journey/${journey.id}/values`,
      headers: { "x-user-id": owner.id },
      payload: { selected: ["a", "b", "c", "d"] },
    });
    expect(tooMany.statusCode).toBe(400);
    expect(tooMany.json().error).toBe("validation_error");

    const foreign = await app.inject({ method: "GET", url: `/api/v1/journey/${journey.id}/plan`, headers: { "x-user-id": other.id } });
    expect(foreign.statusCode).toBe(404);
  });
});

describe("daily prompt and AI-assist mock", () => {
  it("returns a prompt of the day and canned reflection questions", async () => {
    const { user } = await registerUser("prompt");
    const headers = { "x-user-id": user.id };
    const prompt = await app.inject({ method: "GET", url: "/api/v1/prompts/daily", headers });
    expect(prompt.statusCode).toBe(200); // requires `npm run db:seed`
    expect(prompt.json().text).toBeTruthy();

    const journey = (await app.inject({ method: "GET", url: "/api/v1/journey/current", headers })).json();
    const reflect = await app.inject({ method: "POST", url: `/api/v1/journey/${journey.id}/best/reflect`, headers });
    expect(reflect.json().questions).toHaveLength(3);
  });
});

describe("keywordClustering", () => {
  it("counts words, ignores stop words and short words", () => {
    const result = keywordClustering(["Teaching and coaching", "teaching people", "The art"]);
    expect(result[0]).toEqual({ word: "teaching", count: 2 });
    expect(result.map((r) => r.word)).not.toContain("and");
    expect(result.map((r) => r.word)).not.toContain("the");
  });
});
