import type { FastifyPluginAsync } from "fastify";
import { prisma } from "../db.js";
import { registerSchema } from "../schemas/user.js";

export const authRoutes: FastifyPluginAsync = async (app) => {
  // DEMO: no password. Registering an existing email returns that user (200) instead of 409,
  // so the demo can be restarted on the device without a login flow.
  app.post("/auth/register", async (req, reply) => {
    const body = registerSchema.parse(req.body);
    const existing = await prisma.user.findUnique({ where: { email: body.email.toLowerCase() } });
    if (existing) return reply.code(200).send(existing);
    const user = await prisma.user.create({
      data: { name: body.name, email: body.email.toLowerCase(), country: body.country },
    });
    return reply.code(201).send(user);
  });
};
