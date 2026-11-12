# Design System Specification: Nutrition & Vitality

## 1. Overview & Creative North Star: "The Living Editorial"
This design system rejects the "utility-only" look of standard fitness trackers. Our Creative North Star is **The Living Editorial**. We treat nutrition data not as a spreadsheet, but as a premium lifestyle magazine. 

By leveraging **Material 3** logic through a high-end lens, we move away from rigid grids and 1px borders. Instead, we use **intentional asymmetry**, **overlapping data visualizations**, and **tonal depth**. The interface should feel like a curated wellness journal: breathable, sophisticated, and deeply intentional. We use white space not as "empty room," but as a structural element that guides the eye toward refined food photography and vibrant macro-nutrient data.

---

## 2. Color Theory & Surface Logic
Our palette is rooted in a warm, organic base (`surface: #fff8f6`) to avoid the clinical "coldness" of pure white.

### The "No-Line" Rule
**Strict Mandate:** Designers are prohibited from using 1px solid borders to section content. Boundaries must be defined through:
- **Background Shifts:** Placing a `surface-container-low` card on a `surface` background.
- **Tonal Transitions:** Using the `outline-variant` (#dec1af) at **10% opacity** only when a structural guide is absolutely required for accessibility.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers—like stacked sheets of fine vellum.
1.  **Base Layer:** `surface` (#fff8f6) – The canvas.
2.  **Sectioning Layer:** `surface-container-low` (#fef1ec) – To group related modules (e.g., the daily meal log).
3.  **Content Layer:** `surface-container-lowest` (#ffffff) – Used for primary cards to create a "lifted" feel against the warmer background.

### The Glass & Gradient Rule
To achieve a premium feel, floating elements (like a "Quick Add" FAB or a persistent macro summary) should utilize **Glassmorphism**:
- **Fill:** `surface_variant` (#ede0db) at 70% opacity.
- **Effect:** `backdrop-filter: blur(12px)`.
- **Signature Accents:** Use a subtle linear gradient for primary CTAs: `primary` (#964900) to `primary_container` (#f57c00). This adds "soul" and prevents the orange from feeling flat or "cheap."

---

## 3. Typography: Editorial Authority
We utilize a pairing of **Plus Jakarta Sans** for character and **Inter** for clinical precision. (Note: While Roboto was requested, the editorial direction upgrades the experience to these more sophisticated weights for a high-end feel).

*   **Display (Plus Jakarta Sans):** Used for big "hero" moments—total calories or daily streaks. High-contrast sizing creates an authoritative hierarchy.
*   **Headline & Title (Plus Jakarta Sans):** Bold and expressive. Used to name meals (e.g., "Post-Workout Fuel").
*   **Body & Labels (Inter):** Highly legible, used for nutritional facts and input descriptions.

---

## 4. Elevation & Depth: Tonal Layering
We move away from the "drop shadow" era. Depth is now a property of light and material density.

*   **The Layering Principle:** Place a `surface-container-lowest` card on a `surface-container-low` background. This creates a soft, natural "lift" without a single drop shadow.
*   **Ambient Shadows:** If a shadow is required (elevation 2dp), use:
    *   **Blur:** 16px - 24px.
    *   **Opacity:** 4% - 6%.
    *   **Color:** Use a tinted shadow (`on-surface` #201a17) rather than pure black to mimic natural light hitting a warm surface.
*   **Ghost Borders:** If accessibility requires a stroke, use `outline-variant` (#dec1af) at 20% opacity. Never use 100% opaque lines.

---

## 5. Components

### Buttons & Interaction
*   **Primary:** Pill-shaped (`rounded-full`). Gradient fill (`primary` to `primary-container`). Use `on-primary` (#ffffff) for text.
*   **Secondary:** `surface-container-high` background with `primary` text. No border.
*   **Interaction State:** On hover/press, increase the `surface-tint` overlay by 8%.

### Chips (Macros & Tags)
*   **Shape:** Pill (`rounded-full`).
*   **Logic:** Chips for Protein, Carbs, and Fats should use their specific macro colors at 12% opacity for the background, with the full-strength color for the text label.
    *   *Protein:* BG: `#42A5F5` (12%), Text: `#004a78`.

### Input Fields
*   **Corner Radius:** 12dp (`rounded-md`).
*   **Styling:** Fill with `surface-container-highest` (#ede0db). Transition to a `primary` ghost border (20% opacity) on focus.
*   **Labels:** Use `label-md` (Inter) in `on-surface-variant`.

### Nutrition Cards & Lists
*   **Rule:** Forbid divider lines.
*   **Separation:** Use `spacing-6` (1.5rem) of vertical white space or a shift from `surface-container-low` to `surface-container-lowest`.
*   **Macro Visualization:** Use asymmetric ring charts or "soft-bar" charts with `rounded-full` caps to represent Carbs/Fats/Protein.

### Suggested Premium Component: The "Glass Macro-Bar"
A persistent, bottom-docked glassmorphic bar that shows a real-time "remaining balance" of calories. It uses `backdrop-blur` to let the food photography of the meal log peek through as the user scrolls.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use "Breathing Room." If you think a card needs more margin, add 8px more.
*   **Do** use high-quality food photography with a shallow depth of field to complement the "Editorial" look.
*   **Do** use `primary_fixed_dim` (#ffb786) for subtle callouts that don't need the intensity of the main orange.

### Don't:
*   **Don't** use 1px solid dividers (ever).
*   **Don't** use pure black (#000000) for text. Use `on-surface` (#201a17).
*   **Don't** use "default" Material shadows. Always tint and diffuse them.
*   **Don't** crowd the screen. If a module has too much data, use a progressive disclosure pattern (e.g., "Tap to expand macros").