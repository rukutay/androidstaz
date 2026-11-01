import { z } from "zod";

// Server validates structure and hard limits only; UX messages live in the client (Guidelines 8.3).

const shortText = z.string().trim().min(1).max(60);
const note = z.string().trim().min(1).max(200);

export const interestsSchema = z.object({
  items: z.array(shortText).max(100),
  topTwo: z.array(shortText).max(2).default([]),
});

export const bestSchema = z.object({
  examples: z
    .array(
      z.object({
        // may be empty while the user is still drafting (auto-save); the plan skips empty moments
        text: z.string().trim().max(300),
        reasons: z.array(note).max(3),
        tags: z.array(z.string().trim().min(1).max(40)).max(10).default([]),
      }),
    )
    .max(6),
});

export const valuesSchema = z.object({
  selected: z.array(shortText).max(3),
  reflection: z.string().trim().max(1000).default(""),
});

export const importanceSchema = z.enum(["important", "somewhat", "not"]);

const wantElement = z.object({
  text: z.string().trim().max(200),
  importance: importanceSchema,
});

export const jobWantSchema = z.object({
  role: wantElement,
  company: wantElement,
  location: wantElement,
  culture: wantElement,
  benefit: wantElement,
});

export const objectiveSchema = z.object({
  timeframe: z.enum(["2 weeks", "1 month", "3 months"]),
  // may be empty while drafting (auto-save); the plan skips objectives without an action
  action: z.string().trim().max(300),
  outcome: z.string().trim().max(300),
  challenges: z.array(note).max(5).default([]),
  resources: z.array(note).max(5).default([]),
});

export const excitedSchema = z.object({
  excitementItems: z.array(note).max(10),
  jobWants: z.array(jobWantSchema).max(5).default([]),
  objectives: z.array(objectiveSchema).max(3).default([]),
});

export const stepSchemas = {
  interests: interestsSchema,
  best: bestSchema,
  values: valuesSchema,
  excited: excitedSchema,
} as const;

export type StepKey = keyof typeof stepSchemas;
export const stepKeys = Object.keys(stepSchemas) as StepKey[];

export type Interests = z.infer<typeof interestsSchema>;
export type Best = z.infer<typeof bestSchema>;
export type Values = z.infer<typeof valuesSchema>;
export type Excited = z.infer<typeof excitedSchema>;
