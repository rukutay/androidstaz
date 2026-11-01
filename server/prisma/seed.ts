import dotenv from "dotenv";
import { PrismaPg } from "@prisma/adapter-pg";
import { PrismaClient } from "../src/generated/prisma/client.js";

dotenv.config({ path: "../.env", quiet: true });

const prisma = new PrismaClient({ adapter: new PrismaPg({ connectionString: process.env.DATABASE_URL }) });

// POC: ~12 prompts instead of the 60+ in the Guidelines (12.2).
const prompts: { text: string; category: string }[] = [
  { text: "No one can discover you until you do.", category: "quote" },
  { text: "Your happiness should never be a trade-off.", category: "quote" },
  { text: "What gave you energy this week?", category: "reflection" },
  { text: "Which task made you lose track of time recently?", category: "reflection" },
  { text: "A lot can change in a month. What would you like to change?", category: "reflection" },
  { text: "Think of one small step you can take today.", category: "action" },
  { text: "Who could you talk to about the work you are curious about?", category: "action" },
  { text: "What would you do if you knew you could not fail?", category: "reflection" },
  { text: "This takes effort, and it's worth it.", category: "quote" },
  { text: "Notice what you are drawn to without being asked.", category: "reflection" },
  { text: "Which of your values felt respected today?", category: "reflection" },
  { text: "Celebrate a small win from this week.", category: "action" },
];

async function main() {
  if ((await prisma.dailyPrompt.count()) === 0) {
    await prisma.dailyPrompt.createMany({ data: prompts });
    console.log(`Seeded ${prompts.length} daily prompts`);
  } else {
    console.log("Daily prompts already present - skipped");
  }
}

main().finally(() => prisma.$disconnect());
