import type { FastifyPluginAsync } from "fastify";
import { prisma } from "../db.js";

export const userRoutes: FastifyPluginAsync = async (app) => {
  app.get("/user/me", async (req) => prisma.user.findUniqueOrThrow({ where: { id: req.userId } }));
};
