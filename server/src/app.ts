import { existsSync } from "node:fs";
import path from "node:path";
import Fastify from "fastify";
import fastifyStatic from "@fastify/static";
import { ZodError } from "zod";
import { authenticate } from "./auth.js";
import { authRoutes } from "./routes/auth.js";
import { healthRoutes } from "./routes/health.js";
import { insightsRoutes } from "./routes/insights.js";
import { journeyRoutes } from "./routes/journey.js";
import { planRoutes } from "./routes/plan.js";
import { promptRoutes } from "./routes/prompts.js";
import { userRoutes } from "./routes/user.js";

export async function buildApp({ logger = true }: { logger?: boolean } = {}) {
  const app = Fastify({ logger });

  app.setErrorHandler((err, _req, reply) => {
    if (err instanceof ZodError) {
      return reply.code(400).send({
        error: "validation_error",
        message: "Invalid request body.",
        issues: err.issues.map((i) => ({ path: i.path.join("."), message: i.message })),
      });
    }
    return reply.send(err);
  });

  app.register(
    async (api) => {
      api.register(healthRoutes);
      api.register(authRoutes);
      // Everything below requires the (demo) X-User-Id header
      api.register(async (protectedApi) => {
        protectedApi.addHook("preHandler", authenticate);
        protectedApi.register(userRoutes);
        protectedApi.register(journeyRoutes);
        protectedApi.register(planRoutes);
        protectedApi.register(insightsRoutes);
        protectedApi.register(promptRoutes);
      });
    },
    { prefix: "/api/v1" },
  );

  // Optional: serve a built web bundle copied into server/public (web demo, no longer the main target)
  const publicDir = path.join(process.cwd(), "public");
  if (existsSync(publicDir)) {
    await app.register(fastifyStatic, { root: publicDir });
  }

  return app;
}
