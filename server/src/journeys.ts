import type { FastifyReply, FastifyRequest } from "fastify";
import { prisma } from "./db.js";
import { stepKeys } from "./schemas/steps.js";
import type { Journey } from "./generated/prisma/client.js";

export function serializeJourney(j: Journey) {
  const steps = {
    interests: j.interests ?? null,
    best: j.best ?? null,
    values: j.values ?? null,
    excited: j.excited ?? null,
  };
  return {
    id: j.id,
    version: j.version,
    status: j.status,
    updatedAt: j.updatedAt.toISOString(),
    steps,
  };
}

export const allStepsPresent = (j: Journey) => stepKeys.every((k) => j[k] != null);

// Loads a journey owned by the current user, or replies 404.
export async function ownedJourney(req: FastifyRequest, reply: FastifyReply, id: string) {
  const journey = await prisma.journey.findFirst({ where: { id, userId: req.userId } });
  if (!journey) {
    reply.code(404).send({ error: "not_found", message: "Journey not found." });
    return null;
  }
  return journey;
}
