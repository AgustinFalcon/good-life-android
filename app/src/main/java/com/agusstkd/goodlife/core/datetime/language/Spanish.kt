package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

data object Spanish : AppLanguage {

    override val monthNames = MonthNames(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
    )

    override val dayNamesShort = DayOfWeekNames(
        "lun", "mar", "mié", "jue", "vie", "sáb", "dom"
    )

    override val relativeTexts = RelativeDateTexts(
        today = "Hoy",
        yesterday = "Ayer",
        tomorrow = "Mañana"
    )

    override val dailyItemLabels = DailyItemLabels(
        task = "Tarea",
        habit = "Hábito",
        workout = "Entrenamiento",
        meal = "Comida"
    )

    override val validationTexts = ValidationTexts(
        fieldRequired = "El campo no puede estar vacío",
        minLengthFormat = "Debe tener al menos {0} caracteres",
        invalidEmailFormat = "El formato del email no es válido",
        passwordRequired = "La contraseña no puede estar vacía",
        passwordMinLengthFormat = "La contraseña debe tener al menos {0} caracteres",
        passwordMaxLengthFormat = "La contraseña no puede exceder {0} caracteres",
        passwordNeedsUppercase = "La contraseña debe contener al menos una mayúscula",
        passwordNeedsNumber = "La contraseña debe contener al menos un número",
        passwordNeedsSpecialChar = "La contraseña debe contener al menos un carácter especial",
        passwordsDontMatch = "Las contraseñas no coinciden",
        usernameRequired = "El nombre de usuario no puede estar vacío",
        usernameMinLengthFormat = "El nombre de usuario debe tener al menos {0} caracteres",
        usernameInvalidChars = "Solo letras, números y guiones bajos",
        fullNameRequired = "El nombre no puede estar vacío",
        fullNameTooShort = "El nombre es muy corto",
        fullNameInvalidChars = "El nombre solo puede contener letras",
        invalidFullName = "Nombre inválido",
        invalidUsername = "Nombre de usuario inválido",
        invalidEmail = "Email inválido",
        invalidPassword = "Contraseña inválida"
    )

    override val errorTexts = ErrorTexts(
        loginError = "Error al iniciar sesión",
        registerError = "Error al registrar",
        unexpectedState = "Estado inesperado",
        operationInProgress = "Operación en curso",
        userLoadError = "Error al cargar usuario",
        logoutError = "Error al cerrar sesión",
        noAuthenticatedUser = "No hay usuario autenticado",
        dataLoadError = "Error al cargar datos",
        connectionError = "Error de conexión",
        serverErrorFormat = "Error del servidor (código: {0})",
        unknownError = "Error desconocido",
        mustAcceptTerms = "Debes aceptar los términos y condiciones"
    )

    override val dailyTexts = DailyTexts(
        dailyProgressFormat = "Progreso diario: {0}%",
        completedOfFormat = "{0} de {1} completados",
        totalForDayFormat = "Total del día: {0} items",
        schedulePrefix = "Horario",
        serverError = "Error del servidor",
        sessionExpired = "Sesión expirada",
        noData = "Sin datos",
        noPermissions = "Sin permisos",
        invalidRequest = "Solicitud inválida",
        error = "Error",
        tapToRetry = "Tap para reintentar",
        nextUp = "PRÓXIMO",
        inProgress = "EN PROGRESO",
        all = "Todos",
        task = "Tareas",
        habit = "Hábitos",
        workout = "Entrenos",
        meal = "Comidas"
    )

    override val mainScaffoldTexts = MainScaffoldTexts(
        task = "Tarea",
        habit = "Hábito",
        workout = "Rutina",
        meal = "Comida",
        others = "Otros",
        dailySummary = "Resumen diario",
        breakfast = "Desayuno",
        lunch = "Almuerzo",
        dinner = "Cena",
        snack = "Snack",
        preWorkout = "Pre entreno",
        postWorkout = "Post entreno",
        tabDaily = "Diario",
        tabWorkouts = "Ejercicios",
        tabFood = "Comida",
        tabMore = "Más",
        tabExercises = "Ejercicios",
        tabMyExercises = "Mis ejercicios",
        tabStatistics = "Estadísticas"
    )

    override val homeTexts = HomeTexts(
        welcome = "¡Bienvenido!",
        sessionActive = "Has iniciado sesión correctamente.\nTu sesión está activa.",
        logout = "Cerrar sesión",
        error = "Error",
        retry = "Reintentar"
    )

    override val authTexts = AuthTexts(
        loginSubtitle = "Tu bienestar, tu ritmo",
        emailOrUsername = "Correo electrónico o usuario",
        password = "Contraseña",
        login = "Iniciar Sesión",
        createAccount = "Crear Cuenta",
        or = "o",
        loginWithGoogle = "Login con Google",
        loginWithApple = "Login con Apple",
        enableBiometricLogin = "Activar login con huella",
        termsPrefix = "Al continuar, aceptas nuestros ",
        terms = "Términos",
        and = " y ",
        privacy = "Privacidad",
        register = "Registrarme",
        registerSubtitle = "Crea tu cuenta para empezar tu vida saludable",
        fullName = "Nombre completo",
        username = "Nombre de usuario",
        email = "Correo electrónico",
        confirmPassword = "Confirmar contraseña",
        termsAndConditions = "Términos y Condiciones",
        alreadyHaveAccount = "¿Ya tienes cuenta? ",
        ok = "OK",
        biometricPromptTitle = "Iniciar sesión en GoodLife",
        biometricPromptSubtitle = "Usa tu huella, rostro, PIN o patrón",
        biometricPromptCancel = "Cancelar",
        loginSuccess = "Login Exitoso"
    )

    override val accessibilityTexts = AccessibilityTexts(
        hide = "Ocultar",
        show = "Mostrar",
        openCalendar = "Abrir calendario",
        previousDay = "Día anterior",
        nextDay = "Día siguiente",
        notifications = "Notificaciones",
        filter = "Filtrar",
        add = "Agregar",
        goToDaily = "Ir a diario de tareas",
        goToWorkouts = "Ir a ejercicios y rutinas",
        goToMeals = "Ir a registro de comidas",
        goToMore = "Más opciones y configuración",
        close = "Cerrar",
        appLogo = "Logo GoodLife",
    )

    override val formats = DateFormats(
        full = LocalDate.Format {
            dayOfMonth()
            chars(" de ")
            monthName(monthNames)
            chars(" de ")
            year()
        },
        dayMonth = LocalDate.Format {
            dayOfMonth()
            chars(" de ")
            monthName(monthNames)
        },
        dayNameAndDate = LocalDate.Format {
            dayOfWeek(dayNamesShort)
            chars(", ")
            dayOfMonth()
            chars(" de ")
            monthName(monthNames)
        }
    )

    override val datePickerTexts = DatePickerTexts(
        title = "Seleccionar fecha",
        cancel = "Cancelar",
        confirm = "Confirmar",
        pastDaysHint = "Los días anteriores no están disponibles",
    )

    override val createItemSharedTexts = CreateItemSharedTexts(
        fieldTitleLabel = "Título",
        fieldTitlePlaceholder = "Ej: Comprar verduras",
        fieldDescriptionLabel = "Descripción (opcional)",
        fieldDescriptionPlaceholder = "Añade una nota...",
        whenSectionTitle = "¿Cuándo?",
        modeOnce = "Una vez",
        modeRepeats = "Se repite",
        daysRowTitle = "Días de la semana",
        fromDateLabel = "Desde",
        toDateLabel = "Hasta",
        noEndDate = "Sin fecha de fin",
        hasEndDate = "Con fecha de fin",
        noSpecificTime = "Sin hora específica",
        timeLabel = "Hora de inicio",
        reminderLabel = "Recordatorio",
        cancelButton = "Cancelar",
        confirmLabel = "Confirmar",
        cancelLabel = "Cancelar",
        retryLabel = "Reintentar",
    )

    override val createTaskTexts = CreateTaskTexts(
        screenTitle = "Nueva tarea",
        typeBadge = "TAREA",
        saveButton = "Guardar tarea",
        successTitle = "Tarea creada con éxito",
        errorTitleEmpty = "El título no puede estar vacío",
        errorNoDate = "Seleccioná una fecha",
        errorNoDays = "Seleccioná al menos un día",
        errorEndBeforeStart = "La fecha de fin no puede ser anterior al inicio",
        errorServer = "Error del servidor, intentá de nuevo",
        errorNetwork = "Sin conexión, revisá tu internet",
    )

    override val habitCategoryTexts = HabitCategoryTexts(
        hydration = "Hidratación",
        meditation = "Meditación",
        reading = "Lectura",
        exercise = "Ejercicio",
        sleep = "Sueño",
        nutrition = "Nutrición",
        learning = "Aprendizaje",
        mindfulness = "Mindfulness",
        social = "Social",
        creativity = "Creatividad",
        productivity = "Productividad",
        health = "Salud",
        custom = "Personalizado",
    )

    override val createHabitTexts = CreateHabitTexts(
        screenTitle = "Nuevo hábito",
        typeBadge = "HÁBITO",
        saveButton = "Guardar hábito",
        successTitle = "Hábito creado con éxito",
        categorySectionTitle = "Categoría",
        categoryPlaceholder = "Seleccionar categoría",
        goalSectionTitle = "Meta diaria",
        targetValuePlaceholder = "Ej: 8",
        unitPlaceholder = "Ej: vasos",
        errorNameEmpty = "El nombre no puede estar vacío",
        errorNoDays = "Seleccioná al menos un día",
        errorNoCategory = "Seleccioná una categoría",
        errorInvalidGoal = "La meta debe ser mayor a 0",
        errorUnitEmpty = "La unidad no puede estar vacía",
        errorEndBeforeStart = "La fecha de fin no puede ser anterior al inicio",
        errorServer = "Error del servidor, intentá de nuevo",
        errorNetwork = "Sin conexión, revisá tu internet",
    )

    override val splashTexts = SplashTexts(
        tapToContinue = "Toca para continuar",
    )
}
