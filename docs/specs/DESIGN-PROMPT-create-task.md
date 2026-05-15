# 🎨 Design Prompt — GoodLife "Create Task" Screen

**Para usar en:** Figma AI, Galileo AI, Uizard, v0.dev, o cualquier herramienta de diseño IA.

---

## PROMPT COMPLETO (copiar y pegar)

---

Design a mobile screen (Android, 390×844pt) for a health and wellness app called **GoodLife**.

---

### Brand Identity & Colors

The app uses a **health/wellness** aesthetic: clean, modern, energizing. The color palette is:

- **Primary Dark:** `#003C3C` (dark teal-green — used for headers, active states, button gradient start)
- **Primary Accent:** `#1EC691` (vibrant mint-green — used for highlights, selected states, button gradient end)
- **Task Accent:** `#1976D2` (blue — used for Task type badge text and icon)
- **Task Background:** `#E3F2FD` (very light blue — Task type badge background)
- **Surface White:** `#FFFFFF` (screen backgrounds)
- **Card Surface:** `#F8F8F8` (subtle card backgrounds)
- **Text Primary:** `#1A1A1A`
- **Text Secondary:** `#666666`
- **Text Placeholder:** `#999999`
- **Border Default:** `#E0E0E0`
- **Error:** `#E53935` (red)
- **Green Selected:** `#00D26B` (selected day chips)
- **Gradient button:** horizontal gradient from `#003C3C` → `#1EC691` → `#6EDBFF`

**Typography:**
- Headlines / Titles: **Ubuntu** font (Bold / Medium / SemiBold)
- Body / Labels / Inputs: **Inter** font (Regular / Medium / SemiBold)

---

### What to Design

Design **4 screens** as a Figma-style frame mockup on a white artboard, phone frames side by side:

---

#### Screen 1 — Main Daily Screen with FAB

The main Daily tab with a floating action button (+) in the bottom-right corner.
The FAB uses the primary gradient (`#003C3C` → `#1EC691`).
Show a small daily progress card at top (green progress bar, "72% completado").
Below it: horizontal scrollable filter chips (Todos, Tareas, Hábitos, Entrenos, Comidas).
Below chips: a list of 3-4 daily task cards.
The FAB is prominent, 56dp circle, white + icon, gradient background.

---

#### Screen 2 — Bottom Sheet: "Create Task" Form (Collapsed/Default state)

A **Modal Bottom Sheet** slides up over the daily screen (sheet takes ~85% of screen height).
The sheet has:

**Drag handle** (short pill, `#E0E0E0`) centered at the top.

**Header row:**
- Left side: badge `[TAREA]` — rounded rectangle, background `#E3F2FD`, text `#1976D2`, Inter SemiBold 12sp, with a small checklist icon in blue
- Center: title "Nueva tarea" — Ubuntu Medium 20sp, `#1A1A1A`
- Right side: X close icon button, `#666666`

**Form body** (scrollable, padding 20dp horizontal):

---

**"Título" field (required):**
```
Label: "Título *"  Inter SemiBold 13sp  #1A1A1A
Input field: rounded rectangle border (2dp gradient: #003C3C → #1EC691), height 52dp
Placeholder: "Ej: Comprar verduras"  Inter Regular 16sp  #999999
```

---

**"Descripción" field (optional):**
```
Label: "Descripción (opcional)"  Inter SemiBold 13sp  #666666
Input field: same rounded rectangle border, height 80dp (multiline)
Placeholder: "Añade más detalles..."  #999999
```

---

**Section divider** (thin line `#E0E0E0`, margin 8dp top/bottom)

---

**"¿Cuándo?" section:**

Label: "¿Cuándo?" — Ubuntu Medium 18sp `#1A1A1A`

**Scheduling Mode Toggle (Segmented Button):**
Two equal-width segments side by side, full width, rounded corners 12dp, height 44dp:
- Left segment: "Una vez" 
- Right segment: "Se repite"
Active segment: white background, shadow, text `#003C3C` Inter SemiBold 14sp
Inactive segment: transparent, text `#999999` Inter Medium 14sp
Container background: `#F8F8F8`, border `#E0E0E0` 1dp

**[WHEN "Una vez" is selected — shown below toggle]:**

Date field button:
```
Rounded rectangle, background #F8F8F8, border #E0E0E0 1.5dp, height 52dp, padding 16dp
Left: calendar icon #1EC691, 20dp
Center: "Mié, 04 de marzo de 2026"  Inter Medium 15sp  #1A1A1A
Right: chevron-right icon  #999999
```

---

**"Hora (opcional)" section:**

Label: "Hora" — Ubuntu Medium 16sp `#1A1A1A`

Toggle switch row:
```
Left: "Sin hora específica"  Inter Regular 14sp  #666666
Right: Material3 Switch — OFF state (gray track)
```

---

**Bottom CTA button:**
```
Full-width rounded button, height 56dp, corner radius 16dp
Background: horizontal gradient #003C3C → #1EC691 → #6EDBFF
Text: "GUARDAR TAREA"  Inter SemiBold 16sp  #FFFFFF  letter-spacing 0.5
Shadow: 0px 4px 16px rgba(30, 198, 145, 0.35)
```

---

#### Screen 3 — Bottom Sheet: "Se repite" mode (Recurring Task)

Same sheet as Screen 2 but with "Se repite" tab active in the toggle.

Show below the toggle:

**Day chips row:**
7 circular/pill chips: LUN | MAR | MIÉ | JUE | VIE | SÁB | DOM  
Each chip: 40×40dp, centered label Inter SemiBold 13sp  
- **Selected:** background `#1EC691`, text `#FFFFFF`, subtle shadow  
- **Unselected:** background `#F8F8F8`, text `#666666`, border `#E0E0E0`  
Show LUN, MIÉ, VIE as selected (the user chose Mon/Wed/Fri).

**Desde (start date):**
Label: "Desde"  Inter SemiBold 13sp  #666666
Date field button: same style as Screen 2 date field, shows "Lun, 02 de marzo de 2026"

**Hasta:**
Label: "Hasta"  Inter SemiBold 13sp  #666666
Toggle row: "Sin fecha de fin" / Switch — show this as OFF (meaning there IS an end date)
Date field button: shows "Mié, 31 de marzo de 2026"

Then show the Hora section as in Screen 2 (OFF state).
Then the CTA button.

---

#### Screen 4 — Calendar Date Picker Bottom Sheet

A **second Bottom Sheet** layered on top of Screen 2 (the form sheet is slightly visible behind, dimmed):

**This calendar sheet:**
- Drag handle at top
- Header: "Seleccionar fecha"  Ubuntu Medium 18sp  `#1A1A1A`  
  Right side: two text buttons "Cancelar" (`#666666`) and "OK" (`#1EC691` bold)
- Calendar grid for **Marzo 2026**:
  - Month navigation: `◀  Marzo 2026  ▶` — arrows are `#1EC691`, month/year is Ubuntu SemiBold 18sp
  - Day headers: LU MA MI JU VI SA DO — Inter Medium 12sp `#999999`
  - Day numbers: Inter Regular 14sp `#1A1A1A`
  - **Today (28 Feb):** not highlighted (we're in March view)
  - **Selected day (4 March):** filled circle background `#1EC691`, text `#FFFFFF`, subtle glow shadow `rgba(30, 198, 145, 0.3)`
  - Past days: `#CCCCCC` text, no interaction
  - Current day if shown: thin border circle `#1EC691`
  - Cells: 40×40dp each, 4dp gap
- Below calendar: subtle caption "Los días anteriores a hoy no están disponibles"  Inter Regular 12sp  `#999999`
- Bottom: full-width "Seleccionar" button — same gradient as the main CTA

The calendar sheet takes ~70% of screen height (shorter than the form sheet).
The form sheet is still visible behind, at ~30% scale with a dark overlay `rgba(0,0,0,0.5)`.

---

### Overall Design Style

- **Style:** Clean, modern, health-focused. Similar to apps like Notion, Linear, or Oura — functional but beautiful.
- **Elevation / Depth:** Use subtle shadows on cards and bottom sheets. Sheets have `8dp` corner radius on top, `0dp` bottom.
- **Spacing:** 20dp horizontal padding inside sheets. 16dp between form sections. 8dp between label and input.
- **Border Radius:** Inputs 12dp. Buttons 16dp. Chips (days) 20dp (circular). Badges 6dp.
- **Icons:** Material Symbols Rounded style. Thin-to-medium stroke weight.
- **Status bar:** Show realistic Android status bar at top.
- **Bottom navigation:** Slightly visible at the bottom of Screen 1 (Daily, Workouts, Food, More tabs). Daily tab is active, icon color `#1EC691`.
- **Sheet background:** Pure white `#FFFFFF`. Subtle drop shadow on top edge.
- **No skeuomorphism.** Flat Material Design 3 foundation with brand color overlay.

---

### Deliverable

4 mobile frames arranged horizontally on a clean artboard.
Label each frame underneath: "1. Daily Tab", "2. Crear Tarea (Una vez)", "3. Crear Tarea (Se repite)", "4. Selector de Fecha".
Use realistic content (not Lorem Ipsum for labels — use the actual Spanish text shown above).
Show the phone frames with thin bezels.
Background artboard: `#F5F5F5`.

---

*Prompt prepared for GoodLife Android app — Feb 28, 2026*
