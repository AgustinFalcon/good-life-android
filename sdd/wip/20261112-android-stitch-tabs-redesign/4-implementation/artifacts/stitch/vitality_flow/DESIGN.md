---
name: Vitality Flow
colors:
  surface: '#f9f9ff'
  surface-dim: '#d3daef'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#e9edff'
  surface-container-high: '#e1e8fd'
  surface-container-highest: '#dce2f7'
  on-surface: '#141b2b'
  on-surface-variant: '#3c4a42'
  inverse-surface: '#293040'
  inverse-on-surface: '#edf0ff'
  outline: '#6c7a72'
  outline-variant: '#bbcac0'
  surface-tint: '#006c4d'
  primary: '#006c4d'
  on-primary: '#ffffff'
  primary-container: '#1ec691'
  on-primary-container: '#004c35'
  inverse-primary: '#46dfa8'
  secondary: '#0d6c4b'
  on-secondary: '#ffffff'
  secondary-container: '#a0f4ca'
  on-secondary-container: '#187250'
  tertiary: '#006e1c'
  on-tertiary: '#ffffff'
  tertiary-container: '#61c462'
  on-tertiary-container: '#004e11'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#69fcc2'
  primary-fixed-dim: '#46dfa8'
  on-primary-fixed: '#002115'
  on-primary-fixed-variant: '#005139'
  secondary-fixed: '#a0f4ca'
  secondary-fixed-dim: '#85d7ae'
  on-secondary-fixed: '#002114'
  on-secondary-fixed-variant: '#005237'
  tertiary-fixed: '#94f990'
  tertiary-fixed-dim: '#78dc77'
  on-tertiary-fixed: '#002204'
  on-tertiary-fixed-variant: '#005313'
  background: '#f9f9ff'
  on-background: '#141b2b'
  surface-variant: '#dce2f7'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  headline-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  title-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 11px
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
  margin: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system establishes a welcoming, active, and restorative environment tailored for health, fitness, and nutrition tracking on modern Android devices. Combining Material 3 foundations with biometric freshness, the visual identity centers on organic vitality, clear metrics, and friction-free habit tracking.

The target audience consists of mobile-first users pursuing sustainable daily well-being, seeking effortless logging of workouts, dietary habits, and mental balance. The UI elicits optimism, clean structure, and gentle encouragement without clinical coldness.

The aesthetic blends **Modern Clean Material** with **Soft Tactility**:
- Crisp white elevated cards grounded against airy mint backgrounds.
- High-radii corners (pill actions, organic 24dp card surfaces) providing ergonomic touch targets.
- Argentine/Rioplatense Spanish tone (voseo direct, energetic: *"Creá tu rutina"*, *"Empezá hoy"*, *"Reintentá"*), deliberately personal and motivating.
- Clean header architecture omitting notification bells to focus entirely on user progression and reduce cognitive fatigue.

## Colors

The palette balances vivid energetic greens with deep foliage tones and ultra-soft mint canvas surfaces. 

- **Primary (`#1EC691`)**: GoodLife vibrant mint-emerald. Used for primary CTA buttons, active selection indicators, prominent metrics, and progress rings.
- **Secondary (`#0B6B4A`)**: Deep forest green. Provides deep contrast for headers, highlighted badges, and focused active icon states against light surfaces.
- **Tertiary / Success (`#4CAF50`)**: True nature green. Handles completed milestones, streak confirmations, and positive dietary thresholds.
- **Error (`#E53935`)**: Controlled crimson. Flags incomplete logs, missed targets, or network errors with immediate clarity.
- **Backgrounds**: Base canvas defaults to `#F4FBF8` (light airy tint), transitioning to `#E8F8F3` for secondary modules, hero card containers, and segment backgrounds.
- **Surface & Cards (`#FFFFFF`)**: Pure white cards float above the soft mint canvas, framed by subtle borders (`#E5E7EB`) to define structural boundaries on screens running at variable brightness.

## Typography

The type system relies on **Plus Jakarta Sans** for headlines, labels, and running body text, bringing a geometric, contemporary, and warmly rounded rhythm that seamlessly integrates with Jetpack Compose Material 3 scales.

- **Title & Header Roles**: `headline-sm` (`headlineSmall`, 24sp Bold) delivers high-impact screen titles and daily summary heads; `title-md` (`titleMedium`, 16sp SemiBold) organizes module sections and card category headers.
- **Body Text**: `body-md` (`bodyMedium`, 14sp Regular) powers daily logging details, nutritional breakdowns, and user activity descriptions.
- **Interactive & Form Roles**: `label-lg` (`labelLarge`, 14sp SemiBold) serves pill buttons, chips, and segment actions to preserve clarity at compact sizes.
- **Copy Voice**: Applied across all strings using Argentine voseo imperative forms (*"Empezá a registrar"*, *"Sumá tus calorías"*, *"Cargá tu comida"*).

## Layout & Spacing

The layout is built upon an **8dp baseline grid** optimized specifically for standard Android viewports (390dp to 412dp screen widths, matching Pixel and modern Galaxy form factors).

- **Screen Margins**: Uniform 16dp (`1rem`) outer screen horizontal margins maximize usable space while preventing thumb clip on curved glass edges.
- **Gutters & Spacing**: 16dp (`gutter`) separates multi-column dashboard cards (e.g., Water & Step split-cards), while 8dp (`space-sm`) governs internal chip rows and tag groups.
- **Vertical Flow**: 24dp (`space-lg`) separates distinct semantic blocks (e.g., between Hero Daily Ring and Section Carousels); 16dp (`space-md`) spaces adjacent interactive cards.
- **Edge-to-Edge Navigation**: Content scrolls beneath a translucent or tinted system bar, ending with a 16dp buffer above the bottom navigation bar.

## Elevation & Depth

Visual hierarchy abandons heavy, muddy drop shadows in favor of a crisp **Tonal Layering + Low-Contrast Outline** model calibrated for high-density OLED and AMOLED displays:

- **Surface Tier 0 (Canvas)**: Background tint at `#F4FBF8` / `#E8F8F3`.
- **Surface Tier 1 (Cards & Sheets)**: Crisp `#FFFFFF` surfaces layered with a precise `1dp` border stroke of `#E5E7EB`.
- **Card Elevation**: Default resting cards employ `elevation = 0.dp` with the structural `#E5E7EB` outline to maintain visual lightness.
- **Hover & Dragged States**: Interactive cards in motion utilize an ambient, ultra-diffused shadow: `offset = (0.dp, 4.dp)`, `blur = 12.dp`, tint `#0B6B4A` at 6% opacity.
- **Floating Modals / Snackbars**: Elevated to `6.dp` with zero hard contours, using soft mint-ambient drop shadows.

## Shapes

The geometric signature emphasizes friendly, human-centric curves with distinct tokenized corner shapes in Jetpack Compose:

- **Cards & Surfaces**: `RoundedCornerShape(24.dp)` — used for all content cards, bottom sheet containers, and metric summary modules.
- **Primary Buttons & Action Elements**: `RoundedCornerShape(28.dp)` or full pill `RoundedCornerShape(9999.dp)` (Compose `CircleShape` / `ShapeDefaults.ExtraLarge`) for comfortable, accessible thumb taps.
- **Text Inputs & Form Fields**: `RoundedCornerShape(12.dp)` (`ShapeDefaults.Medium`) — ensuring structural stability and contrast against the higher-radius cards.
- **Chips & Tags**: `RoundedCornerShape(8.dp)` to `RoundedCornerShape(12.dp)` for dense nutrition and tag classification.

## Components

### Buttons
- **Primary Button**: Pill-shaped `RoundedCornerShape(28.dp)`, min height `52dp`. Container color `#1EC691`, label text in `#FFFFFF` with `label-lg` styling. Disabled state drops opacity to 40% with `#E5E7EB` background.
- **Secondary / Outlined Button**: Pill shape with `1.5dp` outline in `#0B6B4A`, transparent or `#E8F8F3` container, text color `#0B6B4A`.
- **Micro-Actions / Icon Buttons**: 44dp x 44dp circular touch target with `#F4FBF8` background and `#0B6B4A` icon tint.

### Cards
- **Structure**: Surface `#FFFFFF`, border `1dp` solid `#E5E7EB`, corner radius `24dp`.
- **Internal Padding**: 20dp horizontal, 18dp vertical.
- **Hierarchy**: Section titles inside cards use `title-md` (`#0B6B4A`), supporting metric captions use `body-md` (`#111827`).

### Input Fields
- **Container**: `OutlinedTextField` with `RoundedCornerShape(12.dp)`.
- **Unfocused State**: Outline `#E5E7EB`, container background `#FFFFFF`.
- **Focused State**: Outline `2dp` `#1EC691`, container `#F4FBF8`.
- **Placeholder & Helper**: Rioplatense hints (*"Escribí acá...", "Ej. 2500 kcal"*). Error messages appear in `#E53935` using `body-sm`.

### Chips & Badges
- **Filter Chips**: Height `32dp`, radius `12dp`. Inactive: `#FFFFFF` fill, `#E5E7EB` outline, `#111827` text. Active: `#E8F8F3` fill, `#1EC691` border, `#0B6B4A` text with leading checkmark.
- **Metric Pill**: Radius `9999.dp`, background `#E8F8F3`, text `#0B6B4A` (`label-sm`).

### Checkboxes & Radio Buttons
- **Checkbox**: `RoundedCornerShape(6.dp)`. Checked color `#1EC691` with white vector check; unchecked border `#E5E7EB`.
- **Radio**: Outer ring `#1EC691` when active, inner filled dot `#0B6B4A`.

### NavigationBar (Jetpack Compose)
- **Container**: Elevated or surface-anchored bar at `#FFFFFF` with a subtle top border `1dp` `#E5E7EB`.
- **Items (4 Tabs)**:
  1. **Diario**: Icon `Icons.Rounded.CalendarToday` / `EventNote`.
  2. **Entrenar**: Icon `Icons.Rounded.FitnessCenter`.
  3. **Comida**: Icon `Icons.Rounded.Restaurant` / `Nutrition`.
  4. **Más**: Icon `Icons.Rounded.GridTrack` / `MoreHoriz`.
- **Indicator**: Active pill pill-indicator tinted in `#E8F8F3` with `#0B6B4A` icon tint. Inactive icons tinted `#9CA3AF`. Labels use `label-md` SemiBold.

### Progress & Biometric Trackers
- **Ring Indicators**: 8dp stroke width, base track `#E8F8F3`, indicator fill `#1EC691` transitioning to `#4CAF50` when completed.