import type { Best, Excited, Interests, Values } from "../schemas/steps.js";

export interface PlanInput {
  userName: string;
  interests: Interests;
  best: Best;
  values: Values;
  excited: Excited;
}

// Plan screen content (S-12), assembled from the four steps.
export function generatePlan({ userName, interests, best, values, excited }: PlanInput) {
  return {
    userName,
    interests: { items: interests.items, topTwo: interests.topTwo },
    bestMoments: best.examples
      .filter((e) => e.text.length > 0)
      .map((e) => ({ headline: e.text, topReason: e.reasons[0] ?? null })),
    values: { selected: values.selected, reflection: values.reflection },
    excitement: excited.excitementItems.slice(0, 5),
    jobWants: excited.jobWants,
    objectives: excited.objectives.filter((o) => o.action.length > 0),
  };
}

export type PlanData = ReturnType<typeof generatePlan>;
