package com.agusstkd.goodlife.core.datetime.language

/**
 * Textos de validación para formularios.
 *
 * KMP-ready: pure Kotlin, sin Android Context.
 * Placeholders usan `{0}`, `{1}` para reemplazo dinámico.
 *
 * @see ValidateEmailUseCase
 * @see ValidatePasswordUseCase
 * @see ValidateUserNameUseCase
 * @see ValidateFullNameUseCase
 * @see ValidatePasswordMatchUseCase
 */
data class ValidationTexts(
    val fieldRequired: String,
    val minLengthFormat: String,
    val invalidEmailFormat: String,
    val passwordRequired: String,
    val passwordMinLengthFormat: String,
    val passwordMaxLengthFormat: String,
    val passwordNeedsUppercase: String,
    val passwordNeedsNumber: String,
    val passwordNeedsSpecialChar: String,
    val passwordsDontMatch: String,
    val usernameRequired: String,
    val usernameMinLengthFormat: String,
    val usernameInvalidChars: String,
    val fullNameRequired: String,
    val fullNameTooShort: String,
    val fullNameInvalidChars: String,
    val invalidFullName: String,
    val invalidUsername: String,
    val invalidEmail: String,
    val invalidPassword: String,
)

/**
 * Textos de error genéricos usados en UseCases, ViewModels y capa de datos.
 *
 * Placeholders: `{0}` para valores dinámicos (ej: código HTTP).
 */
data class ErrorTexts(
    val loginError: String,
    val registerError: String,
    val unexpectedState: String,
    val operationInProgress: String,
    val userLoadError: String,
    val logoutError: String,
    val noAuthenticatedUser: String,
    val dataLoadError: String,
    val connectionError: String,
    val serverErrorFormat: String,
    val unknownError: String,
    val mustAcceptTerms: String,
)

/**
 * Textos del MainScaffold (acciones rápidas y opciones de comida).
 */
data class MainScaffoldTexts(
    val task: String,
    val habit: String,
    val workout: String,
    val meal: String,
    val others: String,
    val dailySummary: String,
    val breakfast: String,
    val lunch: String,
    val dinner: String,
    val snack: String,
    val preWorkout: String,
    val postWorkout: String,
    val tabDaily: String,
    val tabWorkouts: String,
    val tabFood: String,
    val tabMore: String,
    val tabExercises: String,
    val tabMyExercises: String,
    val tabStatistics: String,
)

/**
 * Textos para la pantalla Home.
 */
data class HomeTexts(
    val welcome: String,
    val sessionActive: String,
    val logout: String,
    val error: String,
    val retry: String,
)

/**
 * Textos para pantallas de autenticación (Login + Register).
 */
data class AuthTexts(
    val loginSubtitle: String,
    val emailOrUsername: String,
    val password: String,
    val login: String,
    val createAccount: String,
    val or: String,
    val loginWithGoogle: String,
    val loginWithApple: String,
    val enableBiometricLogin: String,
    val termsPrefix: String,
    val terms: String,
    val and: String,
    val privacy: String,
    val register: String,
    val registerSubtitle: String,
    val fullName: String,
    val username: String,
    val email: String,
    val confirmPassword: String,
    val termsAndConditions: String,
    val alreadyHaveAccount: String,
    val ok: String,
    val biometricPromptTitle: String,
    val biometricPromptSubtitle: String,
    val biometricPromptCancel: String,
    val loginSuccess: String,
)

/**
 * Content descriptions para accesibilidad.
 */
data class AccessibilityTexts(
    val hide: String,
    val show: String,
    val openCalendar: String,
    val previousDay: String,
    val nextDay: String,
    val notifications: String,
    val filter: String,
    val add: String,
    val goToDaily: String,
    val goToWorkouts: String,
    val goToMeals: String,
    val goToMore: String,
    val close: String,
    val appLogo: String,
)

/**
 * Textos para la pantalla Splash.
 */
data class SplashTexts(
    val tapToContinue: String,
)

/**
 * Wrapper de textos para pantallas de autenticación (Login + Register).
 *
 * Agrupa AuthTexts + AccessibilityTexts en un solo objeto para
 * mantener firmas de función limpias.
 */
data class AuthScreenTexts(
    val auth: AuthTexts,
    val accessibility: AccessibilityTexts
)

/**
 * Textos para la pantalla Daily (tab de tareas diarias).
 *
 * Incluye labels de UI y títulos de error que el Owner usa
 * para decidir qué UI mostrar.
 */
data class DailyTexts(
    val dailyProgressFormat: String,
    val completedOfFormat: String,
    val totalForDayFormat: String,
    val schedulePrefix: String,
    val serverError: String,
    val sessionExpired: String,
    val noData: String,
    val noPermissions: String,
    val invalidRequest: String,
    val error: String,
    val tapToRetry: String,
    /** Badge "NEXT UP" visible en el primer item pendiente de la lista. */
    val nextUp: String,
    val inProgress: String,
    val all: String,
    val task: String,
    val habit: String,
    val workout: String,
    val meal: String,
)

/**
 * Textos compartidos del selector de fecha (calendario).
 * Usado en todos los bottom sheets de selección de fecha.
 */
data class DatePickerTexts(
    val title: String,
    val cancel: String,
    val ok: String = "OK",
    val confirm: String,
    val pastDaysHint: String,
)

/**
 * Textos compartidos para pantallas de creación de items diarios.
 * Task, Habit, Workout y Meal comparten estos labels de scheduling.
 */
data class CreateItemSharedTexts(
    val fieldTitleLabel: String,
    val fieldTitlePlaceholder: String,
    val fieldDescriptionLabel: String,
    val fieldDescriptionPlaceholder: String,
    val whenSectionTitle: String,
    val modeOnce: String,
    val modeRepeats: String,
    val daysRowTitle: String,
    val fromDateLabel: String,
    val toDateLabel: String,
    val noEndDate: String,
    val hasEndDate: String,
    val noSpecificTime: String,
    val timeLabel: String,
    val reminderLabel: String,
    val cancelButton: String,
    val confirmLabel: String,
    val cancelLabel: String,
    val retryLabel: String,
)

/**
 * Textos específicos para la pantalla de creación de Tarea.
 */
data class CreateTaskTexts(
    val screenTitle: String,
    val typeBadge: String,
    val saveButton: String,
    val successTitle: String,
    val errorTitleEmpty: String,
    val errorNoDate: String,
    val errorNoDays: String,
    val errorEndBeforeStart: String,
    val errorServer: String,
    val errorNetwork: String,
)

/**
 * Nombres localizados para las 13 categorías de hábito.
 *
 * Cada propiedad corresponde a un valor del enum [HabitCategory].
 * Se usan en el selector de categoría de la pantalla Create Habit.
 */
data class HabitCategoryTexts(
    val hydration: String,
    val meditation: String,
    val reading: String,
    val exercise: String,
    val sleep: String,
    val nutrition: String,
    val learning: String,
    val mindfulness: String,
    val social: String,
    val creativity: String,
    val productivity: String,
    val health: String,
    val custom: String,
)

/**
 * Textos específicos para la pantalla de creación de Hábito.
 */
data class CreateHabitTexts(
    val screenTitle: String,
    val typeBadge: String,
    val saveButton: String,
    val successTitle: String,
    val categorySectionTitle: String,
    val categoryPlaceholder: String,
    val goalSectionTitle: String,
    val targetValuePlaceholder: String,
    val unitPlaceholder: String,
    val errorNameEmpty: String,
    val errorNoDays: String,
    val errorNoCategory: String,
    val errorInvalidGoal: String,
    val errorUnitEmpty: String,
    val errorEndBeforeStart: String,
    val errorServer: String,
    val errorNetwork: String,
)
