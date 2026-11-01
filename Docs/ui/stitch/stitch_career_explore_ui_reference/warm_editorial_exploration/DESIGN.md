---
name: Warm Editorial Exploration
colors:
  surface: '#fcf9f8'
  surface-dim: '#dcd9d9'
  surface-bright: '#fcf9f8'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f2'
  surface-container: '#f0eded'
  surface-container-high: '#eae7e7'
  surface-container-highest: '#e5e2e1'
  on-surface: '#1c1b1b'
  on-surface-variant: '#51443c'
  inverse-surface: '#313030'
  inverse-on-surface: '#f3f0ef'
  outline: '#83746b'
  outline-variant: '#d5c3b8'
  surface-tint: '#805533'
  primary: '#6f4627'
  on-primary: '#ffffff'
  primary-container: '#8b5e3c'
  on-primary-container: '#ffe3d1'
  inverse-primary: '#f4bb92'
  secondary: '#7d5630'
  on-secondary: '#ffffff'
  secondary-container: '#ffca9b'
  on-secondary-container: '#7a532e'
  tertiary: '#544f46'
  on-tertiary: '#ffffff'
  tertiary-container: '#6c675d'
  on-tertiary-container: '#efe7da'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#ffdcc5'
  primary-fixed-dim: '#f4bb92'
  on-primary-fixed: '#301400'
  on-primary-fixed-variant: '#653d1e'
  secondary-fixed: '#ffdcbf'
  secondary-fixed-dim: '#f0bd8f'
  on-secondary-fixed: '#2d1600'
  on-secondary-fixed-variant: '#623f1b'
  tertiary-fixed: '#e9e2d5'
  tertiary-fixed-dim: '#cdc6b9'
  on-tertiary-fixed: '#1e1b13'
  on-tertiary-fixed-variant: '#4b463d'
  background: '#fcf9f8'
  on-background: '#1c1b1b'
  surface-variant: '#e5e2e1'
typography:
  display-lg:
    fontFamily: Playfair Display
    fontSize: 36px
    fontWeight: '400'
    lineHeight: 44px
  display-lg-mobile:
    fontFamily: Playfair Display
    fontSize: 28px
    fontWeight: '400'
    lineHeight: 36px
  headline-lg:
    fontFamily: Playfair Display
    fontSize: 24px
    fontWeight: '400'
    lineHeight: 32px
  headline-md:
    fontFamily: Playfair Display
    fontSize: 20px
    fontWeight: '400'
    lineHeight: 28px
  title-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 26px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 22px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system is grounded in a calm, warm, editorial sensibility built specifically for users navigating quarterlife identity shifts and career crossroads. It balances the structure of a mindful journal with the clarity of a modern guidance tool, rejecting hyper-gamification and sterile productivity software tropes.

### Emotional Target
The interface evokes steady reassurance, clarity, and deliberate pacing. Visual tension is minimized to counter career anxiety. It feels tactile, thoughtful, and human—like a high-end physical notebook paired with accessible digital utility.

### Visual Architecture
- **Editorial Typography Pairing**: Literary serif headlines convey depth and intention, while utilitarian sans-serif UI elements maintain rapid legibility on mobile viewports.
- **Natural Tactility**: Rich sand foundations, gentle stone-tinted card fills, and delicate hairline separators replace synthetic neon states and dense black shadows.
- **Purposeful Breathing Space**: Uncrowded layouts that allow open contemplation across assessments, reflective prompts, and career path reviews.

## Colors

The palette is rooted in earthy tones and natural finishes, calibrated to lower cognitive strain during introspection.

### Core Roles
- **Primary (`#8B5E3C`)**: Career Brown. Used for primary call-to-action fills, dominant active states, focused indicators, and key structural accents.
- **Secondary (`#C4956A`)**: Career Tan. Used for secondary highlights, subtle icon fills, and quiet active tints.
- **Tertiary (`#EDE5D8`)**: Sand Mid. Applied to secondary card surfaces, input hover states, and layered container backgrounds.
- **Neutral Surface Canvas (`#F5F0E8`)**: Warm Sand base canvas background across the entire mobile experience.

### Typography & Content
- **Text Primary (`#1A1A1A`)**: Near black for optimal contrast across titles, body narrative, and high-priority metadata.
- **Text Secondary (`#555555`)**: Mid-tone charcoal for secondary descriptions, hints, and timestamp markers.
- **Text Muted / Placeholder (`#999999`)**: Subtle stone for inactive placeholders and passive metadata.

### Borders & Structural Lines
- **Border Subtle (`#E0D8CE`)**: Default hairline border for elevated cards and section dividers.
- **Border Input (`#D0C8BE`)**: Form element and secondary container outlines.

### Feedback & Status
- **Error (`#C0392B`)**: Terracotta crimson for inline validation, critical banners, and error cues.
- **Success (`#27AE60`)**: Forest green for completed milestone indicators, achievements, and confirmed inputs.

## Typography

Typography establishes an editorial hierarchy with a deliberate dual-font architecture:
- **Playfair Display**: Used exclusively for large section breaks, step headers, and editorial reflection questions. Its unhurried serif styling invites contemplation.
- **Inter**: Applied across all transactional layers, lists, body text, form elements, and control labels to maximize readability on small Android displays.

### Hierarchy Guidelines
- Always keep `display-lg-mobile` as the primary header level on standard mobile screens to avoid premature wrapping.
- Reserve `Playfair Display` for read-only narrative text, milestones, and questions; never use it inside functional interactive components such as buttons, form fields, or navigation tabs.
- Ensure body copy utilizes `lineHeight: 26px` for long-form reflections to support high legibility against the sand background.

## Elevation & Depth

Visual depth is achieved through structural layering and soft material borders rather than heavy ambient drop shadows, sustaining the calm, printed-paper aesthetic.

### Surface Tiers
- **Tier 0 (Canvas Base)**: `#F5F0E8` (Sand). The universal base layer.
- **Tier 1 (Surface Containers & Cards)**: `#FFFFFF` (White) or `#EDE5D8` (Sand Mid). Both sit on the canvas with a `1px` solid border in `#E0D8CE`. No drop shadows are permitted on standard cards.
- **Tier 2 (Modals & Bottom Sheets)**: `#FFFFFF` overlaid with a diffuse tint (`rgba(26, 26, 26, 0.08)` blur 12px, offset Y 4px).
- **Overlays**: Translucent backdrop scrim using `rgba(26, 26, 26, 0.35)` to focus attention without abrupt blackouts.

## Shapes

The design system uses a deliberate hierarchy of corner radii to separate interactive utilities from content containers:
- **Interactive Controls (Buttons, Inputs)**: `8px` (`0.5rem`). Delivers clean, structured boundaries for actionable elements.
- **Cards & Content Blocks**: `12px` (`0.75rem`). Softens larger visual groupings without feeling bubbly.
- **Chips & Tags**: Full pill geometry (`20px` / `1.25rem`). Visually signals tap-to-select or categorized status badges.

## Components

### Buttons
All buttons share a strict `48px` height, `8px` corner radius, and `16px` horizontal padding with `label-lg` (Inter Semibold). Minimum touch footprint is 48x48px.
- **Primary Button**: Background `#8B5E3C`, text `#FFFFFF`. On pressed: background `#704A2F`.
- **Secondary Button**: Background `#FFFFFF`, border `1px` solid `#8B5E3C`, text `#8B5E3C`. On pressed: background `#EDE5D8`.
- **Ghost Button**: Background transparent, border none, text `#8B5E3C`. On pressed: background `rgba(139, 94, 60, 0.08)`.

### Input Fields
- **Height**: Fixed `48px` single-line, `8px` corner radius.
- **Styling**: Background `#FFFFFF`, border `1px` solid `#D0C8BE`, text `#1A1A1A`, placeholder `#999999`.
- **States**: Focused border switches to `1.5px` solid `#8B5E3C` without outer glow. Error border switches to `1px` solid `#C0392B` with supporting error copy in `body-sm`.

### Cards
- **Geometry**: `12px` corner radius, `1px` solid border `#E0D8CE`. Zero drop shadow.
- **Fills**: Primary information cards use `#FFFFFF`. Contextual, secondary, or quote cards use `#EDE5D8`.
- **Padding**: Standard content inset of `16px` (`space-md`).

### Chips
- **Geometry**: Pill radius (`20px`), height `32px`, horizontal padding `12px`.
- **Default / Unselected**: Background `#EDE5D8`, text `#8B5E3C`, border `1px` solid transparent.
- **Selected**: Background `#8B5E3C`, text `#FFFFFF`, border `1px` solid `#8B5E3C`.

### Progress Indicator
- **Geometry**: Height `4px`, fully rounded ends (`2px` radius).
- **Track**: Background `#E0D8CE`.
- **Active Fill**: `#8B5E3C` transitioning smoothly across milestones.

### Lists & Navigation Rows
- **Structure**: Grouped on white or transparent rows, divided by `1px` hairline divider `#E0D8CE`.
- **Touch Height**: Minimum `56px` row height with centralized titles in `title-md` and trailing metadata or chevron in `#999999`.

### Selection Controls (Checkboxes & Radios)
- **Checkboxes**: `20px` square, `4px` radius, `1.5px` border `#8B5E3C`. Checked state fills `#8B5E3C` with white checkmark.
- **Radio Buttons**: `20px` circular ring `#8B5E3C`. Checked state displays a solid inner dot (`10px` diameter) in `#8B5E3C`.
- **Touch Target**: Padded out to a full `44x44px` invisible target box.

### Reflection Prompt Block (Custom Domain Component)
- Distinct editorial card with `#EDE5D8` background, `12px` radius, hairline border `#E0D8CE`, and a left vertical accent marker (`3px` solid `#C4956A`). Used for open-ended journaling questions using `headline-md` (Playfair Display).