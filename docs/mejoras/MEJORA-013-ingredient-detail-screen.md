# MEJORA-013 — Pantalla de Detalle de Ingrediente

## Idea

Cuando el usuario toca un ingrediente en el catálogo (paso 2 del wizard o en cualquier lista), navegar a una pantalla de detalle del ingrediente que muestre toda su información nutricional.

## Por qué tiene sentido

- El `IngredientItem` en el catálogo solo muestra nombre + serving + macro dots. No hay espacio para mostrar los 4 macros completos con sus valores exactos.
- Un ingrediente "combinado" (ej: masa de empanada = harina + manteca + sal) tiene sub-ingredientes propios que merecen su propia pantalla.
- Permite al usuario verificar la calidad del dato antes de agregarlo a su meal.

## Diseño propuesto

Reutiliza componentes ya existentes:

```
┌──────────────────────────────────────────┐
│  [←]   Pechuga de Pollo    [custom 👤]   │
├──────────────────────────────────────────┤
│                                          │
│  ┌────────── Imagen ──────────────────┐  │
│  │       (AsyncImage / placeholder)   │  │
│  └────────────────────────────────────┘  │
│                                          │
│  ┌──── MacrosSummaryCard ─────────────┐  │
│  │  🔥 165 kcal                       │  │
│  │  💪 31g P  🍞 0g C  🧈 3.6g G     │  │
│  └────────────────────────────────────┘  │
│                                          │
│  Porción de referencia: 100g             │
│  Marca: —                                │
│                                          │
│  ── Sub-ingredientes (si es combinado) ──│
│  (solo para ingredientes custom del      │
│   usuario con sub-items)                 │
│                                          │
└──────────────────────────────────────────┘
```

## Componentes reutilizables

- `MacrosSummaryCard` — ya existe, acepta `calories`, `protein`, `carbs`, `fat`, `title`
- `AsyncImage` (Coil) — igual que `MealCatalogItem`
- `IngredientItem` — el propio item del catálogo puede ser reutilizado para sub-ingredientes

## Impacto backend

Requiere `GET /api/v1/ingredients/{id}` para obtener el detalle completo. Verificar si el endpoint ya existe.

## Ingredientes combinados

Si un ingrediente custom tiene sub-ingredientes, el backend necesitaría un campo `subIngredients: List<IngredientSummary>?`. Esto es una extensión futura — por ahora los ingredientes son atómicos.

## Prioridad

**Baja** — el flujo de creación funciona sin esta pantalla. Implementar una vez que el wizard esté completo y funcional en producción.
