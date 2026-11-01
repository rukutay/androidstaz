# Google Stitch prompts — Career Explore (Android POC)

Goal: generate a base UI in Google Stitch that we translate into Jetpack Compose (Compose Multiplatform) in `client/composeApp`. Stitch output is a **visual reference**, not code we ship: layouts, spacing, hierarchy and components are reused; colours, fonts and copy below are fixed by the Career Explore Development Guidelines.

## How to use

1. In Stitch choose **Mobile** (Android phone, ~412 x 915).
2. Paste the **Master prompt** first. Then generate screens **one at a time** with the screen prompts (Stitch keeps the style across screens in one project).
3. For each screen, export:
   - a **PNG screenshot** → `Docs/ui/stitch/<screen-id>.png` (e.g. `S-06-interests.png`)
   - the **HTML/CSS code** (Stitch "Code" export) → `Docs/ui/stitch/<screen-id>.html`
   - optional: Figma copy, if you prefer to share a Figma link instead.
4. Tell me when the files are in place; I map them to Compose components.

---

## Master prompt (paste first)

```
Design a calm, warm, editorial-style Android mobile app called "Career Explore" - a guided self-discovery and career-planning app for people aged 18-35 (the "quarterlife" moment). It is NOT a job board or CV tool: it is a reflective journey of four steps (Interests, My Best, Values, Get Excited & Objectives) that ends in a personalised 3-month career plan. Tone: warm, direct, encouraging, grounded - like a wise friend. No hype, no gamification badges, no stock clipart.

Visual style:
- Background "Sand" #F5F0E8; cards white #FFFFFF or "Sand Mid" #EDE5D8; card radius 12px, hairline border #E0D8CE, no heavy shadows.
- Primary "Career Brown" #8B5E3C for primary buttons, headings, selected states; "Career Tan" #C4956A for secondary accents and dividers.
- Text: near black #1A1A1A, secondary #555555, muted/placeholder #999999. Error #C0392B, success #27AE60.
- Typography: display and step titles in an elegant serif (Playfair Display, regular, 40-56px on hero only, ~32px elsewhere on mobile); everything else Inter: H1 28-32 semibold, H2 22-24 semibold, H3 18-20 medium, body 16 regular (line height 1.6), caption 13-14, buttons 15-16 medium. Uppercase labels with letter spacing 0.12em. Brand quotes in Playfair Display italic 18-20px.
- 8px spacing grid; 16px horizontal page padding; inputs and buttons 48px tall, input radius 8px with 1px border #D0C8BE (focus: Career Brown border with soft brown glow).
- Buttons: Primary = brown fill, white text. Secondary = white fill, brown text, 1px brown border. Ghost = transparent, dark gray text, 1px mid-gray border. Disabled = #E0E0E0 with mid-gray text.
- Chips/tags: pill shape (radius 20px), Sand background, brown text, small "x" to remove.
- Thin 4px step progress bar (brown fill on sand track). Outline icons (Phosphor or Tabler style), filled only for the active bottom-nav item.
- Bottom navigation with 5 items: Home, My Journey, My Plan, Insights, Profile. Locked items (My Plan before Step 1, Insights before Step 2) shown muted with a small lock.
- Accessible: 44px minimum touch targets, labels above inputs (never placeholder-only), errors shown with text not colour alone, contrast WCAG AA.
```

---

## Screen prompts

Paste one at a time after the master prompt. Text in quotes is the exact copy to use.

### S-01..03 Onboarding (one screen, swipeable 3-card pager)
```
Onboarding screen with a horizontal pager of 3 full-screen cards. Each card: large warm editorial illustration area in the top 55% (diverse young adults, candid, soft sand and brown palette), then a serif headline and a short paragraph. Card 1: "What's Next?" / "Quarterlife is a moment of questions. This app helps you find your direction, one step at a time." Card 2: "Know Yourself Better" / "Four short steps: your interests, your best moments, your must-have values, and what excites you." Card 3: "Turn Insight Into Action" / "Your answers become a personal 3-month plan you can revise, export, and take to a coach." Dot page indicator, "Skip" text button top-right, "Next" primary button at the bottom; on card 3 the button reads "Get Started".
```

### S-04 Profile / Create account (POC: no password)
```
Create-your-profile screen. Serif title "Let's get to know you", short subtitle "We only ask for what we need." Fields with labels above: "First name" (required), "Email address" (required), "Country/region" (dropdown, optional). Primary full-width button "Create my account". Small muted note below: "Demo version - no password needed." Links "Privacy policy" and "Terms" in caption size at the bottom.
```

### S-05 Home Dashboard
```
Home dashboard. Greeting "Hi, Anna" (serif). Centre: a circular progress ring 0-100% made of 4 arc segments, one per step, each segment a different shade from the brown/tan palette, percentage in the middle; tapping a segment opens that step. Below: a prominent "Resume" card: "Continue where you left off" with step name "Step 2: My Best", one-line description and primary button "Continue". Row of 4 step status chips (Interests, My Best, Values, Get Excited) each with an icon for Not started / In progress / Complete. A "Daily prompt" card with a quote in serif italic, e.g. "This takes effort, and it's worth it.", and a heart icon to favourite. A quick stats bar: "23 interests identified - 3 values selected - 3 objectives set". Bottom navigation (Home active). Include an empty-state variant where the resume card says "Start your journey".
```

### S-06 Step 1 - Interests
```
Step screen "Step 1 of 4 - Interests", estimated time "30 min", thin progress bar at 25%. Serif title "Interests". Instruction (max 3 sentences): "List every job or task that interests you - big or small, conventional or not. There's no wrong answer here." Brand quote in serif italic: "No one can discover you until you do". Text input with label "Add an interest" and an "Add" button (max 60 characters). Below, a wrapping tag cloud of added interests as removable sand chips (show ~12, e.g. Photography, Teaching or coaching others, Writing stories, Working outdoors...). Live counter "12 added - aim for 20+" (when 20+: "Great work. Now look for patterns."). A collapsible "Need ideas?" suggestion drawer with category tabs or headers (Creative & Expressive, People & Relationships, Analytical & Problem-Solving, Leadership & Influence, Physical & Practical, Business & Enterprise) and tappable suggestion chips with a "+". Show the "My top 2 interests" picker section (select 2 chips from your list, selected = brown filled). Bottom bar: secondary "Back", primary "Save & Continue". Small "Changes saved" status text near the top.
```

### S-07 Step 2 - My Best
```
Step screen "Step 2 of 4 - My Best", "40 min", progress bar 50%. Serif title "My Best". Instruction: "Think of a moment you were genuinely at your best. What was happening? What made it that way?" A collapsible example card in sand showing a sample answer. Then 3 expandable "moment" cards (can add up to 6 with "+ Add another moment"). Each card: multiline field "I was at my best when..." (300 char counter), then three single-line fields "I was at my best because... (1)", "(2)", "(3)", optional context tag toggles (Solo / Team, Indoor / Outdoor, Structured / Creative), collapse chevron. A secondary button with a sparkle icon "Help me reflect" that opens a bottom sheet with 3 follow-up questions and "Use" / "Dismiss" actions. Gentle inline nudge "Three moments give a clearer picture - add more if you can." Bottom bar: "Back" and "Save & Continue".
```

### S-08 Step 3 - Happy Standards (Values)
```
Step screen "Step 3 of 4 - Happy Standards", "20 min", progress 75%. Serif title "Your must-haves". Instruction: "Your happiness should never be a trade-off. Choose the three values you need in place to feel truly fulfilled." Quote in serif italic: "Your happiness should never be a trade-off". Live counter "2 / 3 selected". A grid of 20 tappable value cards (on a phone use 2 columns, compact cards with an outline icon and label): Being busy, Getting recognition, Feeling challenged, Position & status, Caring for others, Solving problems, Work-life balance, Having influence, Developing others, Using my creativity, Taking risks, Financial security, Working independently, Good relations with colleagues, Variety, Being an expert, Making a difference, Developing myself, Being given responsibility, Individuality. States: unselected (white), selected (brown fill, white text, check icon), disabled once 3 are chosen (greyed). When 3 are selected show a reflection card: "If a role can't offer Variety, Making a difference, and Work-life balance - how will that affect your happiness at work?" with a multiline answer field. Bottom bar "Back" / "Save & Continue".
```

### S-09 Step 4a - Get Excited
```
Step screen "Step 4 of 4 - Get Excited", "30 min", progress 85%. Serif title "What excites you?". Instruction: "What gets you excited to start the day? Be as specific as you can." 10 numbered input rows, placeholder "When I get to..."; filled rows highlighted (white with brown number badge), empty rows dimmed. An "I'm stuck" text button that opens a bottom sheet of example ideas grouped by work type. After 5 entries show a sand nudge card "Do you notice what these have in common?". Bottom bar "Back" / "Continue".
```

### S-10 Step 4b - Job Wants & Objectives
```
Step screen "Step 4 of 4 - Wants & Objectives". Part 1 "My job wants": 5 collapsible cards "Want 1"..."Want 5". Each card builds the sentence "I WANT to be [role] in a [company type] within [location/commute] that has [mission/culture] and offers [benefit]" as five short labelled inputs; next to each input a compact 3-option segmented control "Important / Somewhat / Not important" (pill, brown selected). Part 2 "My 3 objectives": pick up to 3 wants to turn into objectives; each objective card has a timeframe segmented control "2 weeks / 1 month / 3 months" ("In the next ..."), field "I will..." and field "so that..." (measurable outcome). Bottom bar "Back" / "Continue".
```

### S-11 Step 4c - Challenges & Resources
```
Screen "Challenges & Resources". For each of 3 objectives (pager or stacked sections): the objective as a read-only sand card at the top ("In the next 1 month I will talk to two coaches so that I understand the path"). Below, two lists with add inputs: "Challenges" (helper: "Outside elements, personal tendencies, circumstances") and "Resources" (helper: "Skills, contacts, training, self-belief, money, support"), each up to 5 items shown as removable rows. Large primary brown button at the bottom: "Generate My Plan".
```

### S-12 My Plan Summary
```
"My Plan" screen, a beautiful one-page personal career plan. Header: serif "My Plan", subtitle "Anna - 1 Nov 2026 - v2" with a "History" chip showing previous versions. Sections as cards: "My top interests" (tag cloud with 2 highlighted brown chips), "When I am at my best" (cards with headline + top reason), "My non-negotiable values" (3 value cards + reflection quote in serif italic), "What excites me" (top 5 as a clean list), "My 3-month objectives" (numbered cards; each lists challenges and resources underneath). Top message "Your personal plan is ready. Ground your next steps in what you know about yourself." Actions: "Export as PDF" (secondary), "Share" (ghost), "Revisit a step" (ghost), and a warm coach banner "Ready to go deeper? Partner with a Career Explore coach to move confidently toward your goals." with button "Talk to a coach". Empty state variant: "Your plan is waiting. Complete all four steps to generate it."
```

### Optional (demo elements)

**S-13 Insights**
```
"Insights" screen: a disclaimer note at the top "These are reflections for self-exploration, not a professional assessment." A keyword cloud card "Themes in your answers" with keywords sized by frequency (teaching x3, people x2...), a "Work environment" card with two simple sliders/bars (Solo - Team, Structured - Creative), and a coach CTA at the bottom.
```

**S-14 Coach Connect**
```
"Ready to go deeper?" screen: warm serif headline, 3 bullet benefits of the Career Explore Quarterlife coaching program, a coach profile card (photo placeholder, name "Anita", short bio), primary button "Email us to learn more", secondary "Visit our website".
```
