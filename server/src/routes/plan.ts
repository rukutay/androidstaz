import type { FastifyPluginAsync } from "fastify";
import type { Prisma } from "../generated/prisma/client.js";
import { prisma } from "../db.js";
import { allStepsPresent, ownedJourney } from "../journeys.js";
import { bestSchema, excitedSchema, interestsSchema, valuesSchema } from "../schemas/steps.js";
import { generatePlan, type PlanData } from "../services/planGenerator.js";
import { renderPlanHtml } from "../services/printTemplate.js";

const serializePlan = (p: { journeyId: string; version: number; generatedAt: Date; data: unknown }) => ({
  journeyId: p.journeyId,
  version: p.version,
  generatedAt: p.generatedAt.toISOString(),
  data: p.data,
});

export const planRoutes: FastifyPluginAsync = async (app) => {
  app.post<{ Params: { id: string } }>("/journey/:id/plan/generate", async (req, reply) => {
    const journey = await ownedJourney(req, reply, req.params.id);
    if (!journey) return;
    if (!allStepsPresent(journey)) {
      return reply.code(409).send({
        error: "steps_incomplete",
        message: "Your plan is waiting. Complete all four steps to generate it.",
      });
    }
    const data = generatePlan({
      userName: req.userName,
      interests: interestsSchema.parse(journey.interests),
      best: bestSchema.parse(journey.best),
      values: valuesSchema.parse(journey.values),
      excited: excitedSchema.parse(journey.excited),
    });
    const last = await prisma.plan.findFirst({ where: { journeyId: journey.id }, orderBy: { version: "desc" } });
    const plan = await prisma.plan.create({
      data: { journeyId: journey.id, version: (last?.version ?? 0) + 1, data: data as Prisma.InputJsonValue },
    });
    await prisma.journey.update({ where: { id: journey.id }, data: { status: "plan_generated" } });
    return reply.code(201).send(serializePlan(plan));
  });

  // Plan history (S-12 versions view)
  app.get<{ Params: { id: string } }>("/journey/:id/plans", async (req, reply) => {
    const journey = await ownedJourney(req, reply, req.params.id);
    if (!journey) return;
    const plans = await prisma.plan.findMany({ where: { journeyId: journey.id }, orderBy: { version: "desc" } });
    return plans.map((p) => ({ version: p.version, generatedAt: p.generatedAt.toISOString() }));
  });

  app.get<{ Params: { id: string }; Querystring: { version?: string } }>("/journey/:id/plan", async (req, reply) => {
    const journey = await ownedJourney(req, reply, req.params.id);
    if (!journey) return;
    const version = req.query.version ? Number(req.query.version) : undefined;
    const plan = await prisma.plan.findFirst({
      where: { journeyId: journey.id, ...(version ? { version } : {}) },
      orderBy: { version: "desc" },
    });
    if (!plan) return reply.code(404).send({ error: "not_found", message: "No plan generated yet." });
    return serializePlan(plan);
  });

  // PDF export (POC): branded HTML with print styles; use the browser's "Save as PDF".
  app.post<{ Params: { id: string }; Querystring: { version?: string } }>("/journey/:id/plan/export", async (req, reply) => {
    const journey = await ownedJourney(req, reply, req.params.id);
    if (!journey) return;
    const version = req.query.version ? Number(req.query.version) : undefined;
    const plan = await prisma.plan.findFirst({
      where: { journeyId: journey.id, ...(version ? { version } : {}) },
      orderBy: { version: "desc" },
    });
    if (!plan) return reply.code(404).send({ error: "not_found", message: "No plan generated yet." });
    return reply.type("text/html; charset=utf-8").send(renderPlanHtml(plan.data as unknown as PlanData, plan.version, plan.generatedAt));
  });
};
