// Insights (S-13), POC version: plain word frequency, no AI.
const STOP_WORDS = new Set([
  "the", "and", "for", "with", "that", "this", "from", "have", "has", "into", "about", "your", "you",
  "are", "was", "were", "but", "not", "all", "any", "can", "out", "get", "got", "when", "who", "what",
  "than", "then", "them", "they", "their", "work", "working", "other", "others", "like", "more", "most",
]);

export function keywordClustering(texts: string[], limit = 10) {
  const counts = new Map<string, number>();
  for (const text of texts) {
    for (const word of text.toLowerCase().match(/\p{L}+/gu) ?? []) {
      if (word.length < 3 || STOP_WORDS.has(word)) continue;
      counts.set(word, (counts.get(word) ?? 0) + 1);
    }
  }
  return [...counts.entries()]
    .map(([word, count]) => ({ word, count }))
    .sort((a, b) => b.count - a.count || a.word.localeCompare(b.word))
    .slice(0, limit);
}

export const INSIGHTS_DISCLAIMER =
  "These are reflections for self-exploration, not a professional assessment.";
