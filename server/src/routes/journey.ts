import type { FastifyPluginAsync } from "fastify";
import type { Prisma } from "../generated/prisma/client.js";
import { prisma } from "../db.js";
import { allStepsPresent, ownedJourney, serializeJourney } from "../journeys.js";
import { stepKeys, stepSchemas, type StepKey } from "../schemas/steps.js";

const REFLECT_QUESTIONS = [
  "What exactly were you doing in that moment?",
  "Who or what else made it feel that way?",
  "What would it take to have more moments like this?",
];

export const journeyRoutes: FastifyPluginAsync = async (app) => {
  // Current = latest version; created on first access so the client needs no empty state.
  app.get("/journey/current", async (req) => {
    const latest = await prisma.journey.findFirst({ where: { userId: req.userId }, orderBy: { version: "desc" } });
    const journey = latest ?? (await prisma.journey.create({ data: { userId: req.userId, version: 1 } }));
    return serializeJourney(journey);
  });

  app.post("/journey", async (req, reply) => {
    const latest = await prisma.journey.findFirst({ where: { userId: req.userId }, orderBy: { version: "desc" } });
    const journey = await prisma.journey.create({ data: { userId: req.userId, version: (latest?.version ?? 0) + 1 } });
    return reply.code(201).send(serializeJourney(journey));
  });

  app.put<{ Params: { id: string; step: string } }>("/journey/:id/:step", async (req, reply) => {
    if (!stepKeys.includes(req.params.step as StepKey)) {
      return reply.code(404).send({ error: "not_found", message: "Unknown step." });
    }
    const journey = await ownedJourney(req, reply, req.params.id);
    if (!journey) return;
    const step = req.params.step as StepKey;
    const data = stepSchemas[step].parse(req.body);
    const updated = await prisma.journey.update({
      where: { id: journey.id },
      data: { [step]: data as Prisma.InputJsonValue },
    });
    if (journey.status !== "plan_generated" && allStepsPresent(updated)) {
      return serializeJourney(await prisma.journey.update({ where: { id: journey.id }, data: { status: "steps_complete" } }));
    }
    return serializeJourney(updated);
  });

  // AI-assist mock (S-07): canned follow-up questions, no LLM.
  app.post<{ Params: { id: string } }>("/journey/:id/best/reflect", async (req, reply) => {
    const journey = await ownedJourney(req, reply, req.params.id);
    if (!journey) return;
    return { questions: REFLECT_QUESTIONS };
  });
};
