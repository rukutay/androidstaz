import type { FastifyPluginAsync } from "fastify";
import { prisma } from "../db.js";

const dayOfYear = (d: Date) => Math.floor((Date.UTC(d.getUTCFullYear(), d.getUTCMonth(), d.getUTCDate()) - Date.UTC(d.getUTCFullYear(), 0, 0)) / 86_400_000);

export const promptRoutes: FastifyPluginAsync = async (app) => {
  // Deterministic "prompt of the day": rotates through active prompts by day of year.
  app.get("/prompts/daily", async (_req, reply) => {
    const prompts = await prisma.dailyPrompt.findMany({ where: { isActive: true }, orderBy: { text: "asc" } });
    if (prompts.length === 0) return reply.code(404).send({ error: "not_found", message: "No prompts available." });
    const p = prompts[dayOfYear(new Date()) % prompts.length]!;
    return { id: p.id, text: p.text, category: p.category };
  });
};
