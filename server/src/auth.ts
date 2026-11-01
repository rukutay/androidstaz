import type { FastifyReply, FastifyRequest } from "fastify";
import { z } from "zod";
import { prisma } from "./db.js";

declare module "fastify" {
  interface FastifyRequest {
    userId: string;
    userName: string;
  }
}

const uuid = z.uuid();

// DEMO STUB: identifies the user by the X-User-Id header. Not a security control.
// This hook is the seam where JWT verification replaces it later (Guidelines 6.7).
export async function authenticate(req: FastifyRequest, reply: FastifyReply) {
  const parsed = uuid.safeParse(req.headers["x-user-id"]);
  const user = parsed.success ? await prisma.user.findUnique({ where: { id: parsed.data } }) : null;
  if (!user) {
    return reply.code(401).send({ error: "unauthorized", message: "Missing or unknown X-User-Id." });
  }
  req.userId = user.id;
  req.userName = user.name;
}
