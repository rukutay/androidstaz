import type { PlanData } from "./planGenerator.js";

const esc = (s: string) =>
  s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");

const list = (items: string[]) => (items.length ? `<ul>${items.map((i) => `<li>${esc(i)}</li>`).join("")}</ul>` : "<p class=\"muted\">-</p>");

// Branded HTML with print styles: the browser's "Save as PDF" produces the PDF export (POC).
export function renderPlanHtml(plan: PlanData, version: number, generatedAt: Date) {
  const date = generatedAt.toISOString().slice(0, 10);
  const objectives = plan.objectives
    .map(
      (o, i) => `<div class="card"><h3>${i + 1}. In the next ${esc(o.timeframe)}</h3>
<p><strong>I will</strong> ${esc(o.action)}</p>
<p><strong>so that</strong> ${esc(o.outcome)}</p>
<p class="label">Challenges</p>${list(o.challenges)}
<p class="label">Resources</p>${list(o.resources)}</div>`,
    )
    .join("");
  return `<!DOCTYPE html>
<html lang="en"><head><meta charset="utf-8"><title>My Plan - Career Explore</title>
<style>
body{font-family:Inter,Arial,sans-serif;background:#F5F0E8;color:#1A1A1A;line-height:1.6;margin:0;padding:24px}
main{max-width:720px;margin:0 auto}
h1,h2{font-family:"Playfair Display",Georgia,serif;font-weight:400;color:#8B5E3C;line-height:1.2}
.muted,.label{color:#555}.label{font-size:13px;letter-spacing:.12em;text-transform:uppercase;margin-bottom:0}
.card{background:#fff;border:.5px solid #E0D8CE;border-radius:12px;padding:20px;margin:16px 0}
.chip{display:inline-block;background:#EDE5D8;color:#8B5E3C;border-radius:20px;padding:6px 14px;margin:4px}
@media print{body{background:#fff}.card{break-inside:avoid}}
</style></head><body><main>
<h1>My Plan</h1>
<p class="muted">${esc(plan.userName)} &middot; ${date} &middot; v${version}</p>
<h2>My top interests</h2>
<p>${plan.interests.items.map((i) => `<span class="chip">${esc(i)}</span>`).join("")}</p>
<p>Top 2: ${esc(plan.interests.topTwo.join(", ") || "-")}</p>
<h2>When I am at my best</h2>
${plan.bestMoments.map((b) => `<div class="card"><strong>${esc(b.headline)}</strong><p class="muted">${esc(b.topReason ?? "")}</p></div>`).join("")}
<h2>My non-negotiable values</h2>
<p>${plan.values.selected.map((v) => `<span class="chip">${esc(v)}</span>`).join("")}</p>
<p><em>${esc(plan.values.reflection)}</em></p>
<h2>What excites me</h2>
${list(plan.excitement)}
<h2>My 3-month objectives</h2>
${objectives}
</main></body></html>`;
}
