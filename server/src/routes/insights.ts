import type { FastifyPluginAsync } from "fastify";
import { ownedJourney } from "../journeys.js";
import { excitedSchema, interestsSchema } from "../schemas/steps.js";
import { INSIGHTS_DISCLAIMER, keywordClustering } from "../services/keywordClustering.js";

export const insightsRoutes: FastifyPluginAsync = async (app) => {
  app.get<{ Params: { journeyId: string } }>("/insights/:journeyId", async (req, reply) => {
    const journey = await ownedJourney(req, reply, req.params.journeyId);
    if (!journey) return;
    const interests = journey.interests ? interestsSchema.parse(journey.interests) : null;
    const excited = journey.excited ? excitedSchema.parse(journey.excited) : null;
    const texts = [...(interests?.items ?? []), ...(excited?.excitementItems ?? [])];
    return { keywords: keywordClustering(texts), disclaimer: INSIGHTS_DISCLAIMER };
  });
};
