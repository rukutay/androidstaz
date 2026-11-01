# UI notes — Stitch designs vs. the Compose app

Stitch exports: `Docs/ui/stitch/stitch_career_explore_ui_reference/` (13 screens + `warm_editorial_exploration/DESIGN.md`).
Compose code: `client/composeApp/src/commonMain/kotlin/dk/careerexplore/poc/` (`theme/`, `ui/components/`, `ui/screens/`).

## Taken from Stitch
Design tokens from `DESIGN.md` (Sand canvas, Career Brown CTAs, 12dp cards with hairline border, 8dp controls, pill chips, 4dp progress bar, reflection block with tan accent), Playfair Display for narrative headings and quotes, Inter for everything interactive, layouts of all 13 screens, top bar with avatar, 5-item bottom navigation.

## Deliberately different (and why)
| Stitch | App | Why |
|---|---|---|
| Stock photos (onboarding, quote cards, coach) | Warm gradient + icon placeholder (`IllustrationBox`) | No licensed imagery; real brand assets come from Career Explore (Guidelines 13.2) |
| "Anita Chen, ACC", rating 4.9, "120+ sessions", testimonial by "Maya" | "Anita, Career Explore coach" + "photo and bio will be provided" | Invented credentials/testimonials must not ship; testimonials are "Won't (v1)" in the Guidelines |
| Energy drivers & drainers, % spectrums from nowhere | Keyword themes from the server + work-environment bars computed from My Best context tags | Only show insights backed by the user's own data |
| "Pattern takeaway" AI sentence | Template sentence from the top 3 keywords | No AI in the POC |
| "View Fresh Start", "Mindful pacing" card, "Foundational Identity Phase" | Omitted | Not in the Guidelines, no data behind them |
| Step 4b "Formula" + 5 wants + 3 objectives on one screen | Same, wants collapsible; objectives add/remove (max 3) | — |
| Step screens with bottom nav and Back/Continue at the end of content | Sticky Back/Continue bar above the bottom nav | Long pages (20+ chips) otherwise hide the main action |

## Content marked DRAFT (for Career Explore to review)
`domain/Content.kt`: onboarding card texts, excitement examples, the My Best example, coach benefits, onboarding overlines; server `prisma/seed.ts`: 12 daily prompts (Guidelines ask for 60+).
