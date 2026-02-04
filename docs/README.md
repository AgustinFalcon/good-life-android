# 📚 Documentación GoodLife Android

Índice de especificaciones y guías del proyecto.

---

## 🎯 Especificaciones (SPECs)

Documentación oficial de features core implementadas.

| SPEC | Feature | Estado | Descripción |
|------|---------|--------|-------------|
| [SPEC-001](specs/SPEC-001-register-screen.md) | Register Screen | ✅ Completado | Pantalla de registro |
| [SPEC-002](specs/SPEC-002-biometric-login.md) | Biometric Login | ✅ Completado | Autenticación biométrica |
| [SPEC-003](specs/SPEC-003-main-scaffold.md) | Main Scaffold | 🚧 En Progreso | Bottom Navigation + Tabs |
| [SPEC-004](specs/SPEC-004-date-provider.md) | DateProvider | ✅ Completado | Core de fechas (KMP-ready) |

---

## 📝 Documentación Archivada

Documentación de proceso preservada como referencia histórica.

- [archive/README.md](archive/README.md) - Índice de documentos archivados
- [archive/ANALISIS-CLOCK-Y-MEJORAS.md](archive/ANALISIS-CLOCK-Y-MEJORAS.md) - Análisis del problema de Clock
- [archive/DATEPROVIDER-IMPLEMENTACION-COMPLETA.md](archive/DATEPROVIDER-IMPLEMENTACION-COMPLETA.md) - Tracking de implementación
- [archive/INSTRUCCIONES-VALIDACION.md](archive/INSTRUCCIONES-VALIDACION.md) - Checklist de validación

---

## 🚀 Guías Rápidas

### Uso de DateProvider

```kotlin
class MyViewModel(
    private val dateProvider: DateProvider
) : ViewModel() {
    
    // Obtener fecha actual (timezone local)
    val today = dateProvider.today()
    
    // Obtener ayer/mañana
    val yesterday = dateProvider.yesterday()
    val tomorrow = dateProvider.tomorrow()
    
    // Formatear para UI
    val headerText = when (someDate) {
        today -> "Hoy"
        yesterday -> "Ayer"
        tomorrow -> "Mañana"
        else -> someDate.format(...)
    }
}
```

### Testing con FakeDateProvider

```kotlin
@Test
fun `muestra "Hoy" cuando la fecha es hoy`() {
    // Given
    val fixedDate = LocalDate(2025, 12, 25)
    val fakeProvider = FakeDateProvider(fixedDate)
    
    // When
    val viewModel = MyViewModel(fakeProvider)
    
    // Then
    assertEquals("Hoy", viewModel.headerText)
}
```

---

## 📊 Arquitectura del Proyecto

```
app/
├── domain/          ← Lógica de negocio pura
│   ├── model/      
│   ├── repository/ 
│   └── usecase/    
│
├── data/            ← Acceso a datos
│   ├── remote/     ← Retrofit
│   ├── local/      ← Room
│   └── repository/ 
│
├── presentation/    ← UI Layer
│   ├── screen/     
│   ├── components/ 
│   └── navigation/ 
│
├── core/            ← Utilidades
│   ├── datetime/   ← DateProvider, AppLanguage
│   ├── network/    
│   └── result/     
│
└── di/              ← Koin
```

---

## 🎯 Patrones del Proyecto

### 1. Owner Pattern

```
FeatureScreenOwner.kt  → ViewModel injection
FeatureScreen.kt       → UI pura
FeatureViewModel.kt    → State management
```

### 2. DateProvider Pattern

```kotlin
// ✅ Usar DateProvider (no Clock directamente)
class ViewModel(private val dateProvider: DateProvider)

// ✅ Formatear en ViewModel (no en UI)
data class UiState(
    val headerText: String,  // "Hoy" (ya formateado)
    val dayNumber: Int
)
```

### 3. Sealed Classes

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(...) : UiState()
    data class Error(val message: String) : UiState()
}
```

---

## 📞 Referencias

- **Changelog:** [CHANGELOG.md](../CHANGELOG.md)
- **Specs:** [specs/](specs/)
- **Archive:** [archive/](archive/)

---

**Última actualización:** 2026-02-04
