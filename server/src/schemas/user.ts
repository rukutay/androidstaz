import { z } from "zod";

export const registerSchema = z.object({
  name: z.string().trim().min(1).max(80),
  email: z.email().max(200),
  country: z.string().trim().max(80).optional(),
});
