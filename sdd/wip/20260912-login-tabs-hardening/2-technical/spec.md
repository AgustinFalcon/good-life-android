# Especificación técnica

- Mutex sólo serializa `updateItemStatus`; las lecturas usan job cancelable y request id.
- Los ViewModels Daily y Meals no propagan `result.message` a `UiState`; conservan detalle sólo para diagnóstico seguro.
- `isLoading` bloquea acciones incompatibles en ViewModel y deshabilita controles visibles.
- Cada contrato agrega prueba de regresión con fuente suspendida/mensaje distintivo.