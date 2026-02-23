package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

/**
 * Sistema de internacionalización KMP-ready.
 *
 * Centraliza TODOS los textos de la app en pure Kotlin, sin Android Context.
 * Cada idioma implementa todas las propiedades.
 *
 * ## Agregar nuevo idioma:
 * 1. Crear un nuevo `data object` que implemente `AppLanguage`
 * 2. Implementar TODAS las propiedades (el compilador valida exhaustividad)
 *
 * ## Selección automática:
 * En `AppModule.kt`, se detecta el locale del dispositivo y se mapea al
 * `AppLanguage` correspondiente.
 *
 * @see DailyItemLabels
 * @see ValidationTexts
 * @see ErrorTexts
 * @see DailyTexts
 * @see MainScaffoldTexts
 * @see HomeTexts
 * @see AuthTexts
 * @see AccessibilityTexts
 */
sealed interface AppLanguage {
    val monthNames: MonthNames
    val dayNamesShort: DayOfWeekNames
    val formats: DateFormats
    val relativeTexts: RelativeDateTexts
    val dailyItemLabels: DailyItemLabels
    val validationTexts: ValidationTexts
    val errorTexts: ErrorTexts
    val dailyTexts: DailyTexts
    val mainScaffoldTexts: MainScaffoldTexts
    val homeTexts: HomeTexts
    val authTexts: AuthTexts
    val accessibilityTexts: AccessibilityTexts

    // ═══════════════════════════════════════════════════════════════════
    // ESPAÑOL
    // ═══════════════════════════════════════════════════════════════════

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
            tapToRetry = "Tap para reintentar"
        )

        override val mainScaffoldTexts = MainScaffoldTexts(
            routine = "Rutina",
            nutrition = "Alimentación",
            weight = "Peso",
            supplements = "Suplementos",
            activity = "Actividad",
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
            goToMore = "Más opciones y configuración"
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
    }

    // ═══════════════════════════════════════════════════════════════════
    // ENGLISH
    // ═══════════════════════════════════════════════════════════════════

    data object English : AppLanguage {

        override val monthNames = MonthNames(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        override val dayNamesShort = DayOfWeekNames(
            "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"
        )

        override val relativeTexts = RelativeDateTexts(
            today = "Today",
            yesterday = "Yesterday",
            tomorrow = "Tomorrow"
        )

        override val dailyItemLabels = DailyItemLabels(
            task = "Task",
            habit = "Habit",
            workout = "Workout",
            meal = "Meal"
        )

        override val validationTexts = ValidationTexts(
            fieldRequired = "This field is required",
            minLengthFormat = "Must be at least {0} characters",
            invalidEmailFormat = "Invalid email format",
            passwordRequired = "Password is required",
            passwordMinLengthFormat = "Password must be at least {0} characters",
            passwordMaxLengthFormat = "Password cannot exceed {0} characters",
            passwordNeedsUppercase = "Password must contain at least one uppercase letter",
            passwordNeedsNumber = "Password must contain at least one number",
            passwordNeedsSpecialChar = "Password must contain at least one special character",
            passwordsDontMatch = "Passwords do not match",
            usernameRequired = "Username is required",
            usernameMinLengthFormat = "Username must be at least {0} characters",
            usernameInvalidChars = "Only letters, numbers and underscores",
            fullNameRequired = "Name is required",
            fullNameTooShort = "Name is too short",
            fullNameInvalidChars = "Name can only contain letters",
            invalidFullName = "Invalid name",
            invalidUsername = "Invalid username",
            invalidEmail = "Invalid email",
            invalidPassword = "Invalid password"
        )

        override val errorTexts = ErrorTexts(
            loginError = "Login failed",
            registerError = "Registration failed",
            unexpectedState = "Unexpected state",
            operationInProgress = "Operation in progress",
            userLoadError = "Failed to load user",
            logoutError = "Failed to log out",
            noAuthenticatedUser = "No authenticated user",
            dataLoadError = "Failed to load data",
            connectionError = "Connection error",
            serverErrorFormat = "Server error (code: {0})",
            unknownError = "Unknown error",
            mustAcceptTerms = "You must accept the terms and conditions"
        )

        override val dailyTexts = DailyTexts(
            dailyProgressFormat = "Daily progress: {0}%",
            completedOfFormat = "{0} of {1} completed",
            totalForDayFormat = "Day total: {0} items",
            schedulePrefix = "Schedule",
            serverError = "Server error",
            sessionExpired = "Session expired",
            noData = "No data",
            noPermissions = "No permissions",
            invalidRequest = "Invalid request",
            error = "Error",
            tapToRetry = "Tap to retry"
        )

        override val mainScaffoldTexts = MainScaffoldTexts(
            routine = "Routine",
            nutrition = "Nutrition",
            weight = "Weight",
            supplements = "Supplements",
            activity = "Activity",
            others = "Others",
            dailySummary = "Daily summary",
            breakfast = "Breakfast",
            lunch = "Lunch",
            dinner = "Dinner",
            snack = "Snack",
            preWorkout = "Pre-workout",
            postWorkout = "Post-workout",
            tabDaily = "Daily",
            tabWorkouts = "Workouts",
            tabFood = "Food",
            tabMore = "More",
            tabExercises = "Exercises",
            tabMyExercises = "My exercises",
            tabStatistics = "Statistics"
        )

        override val homeTexts = HomeTexts(
            welcome = "Welcome!",
            sessionActive = "You have logged in successfully.\nYour session is active.",
            logout = "Log out",
            error = "Error",
            retry = "Retry"
        )

        override val authTexts = AuthTexts(
            loginSubtitle = "Your wellness, your pace",
            emailOrUsername = "Email or username",
            password = "Password",
            login = "Log In",
            createAccount = "Create Account",
            or = "or",
            loginWithGoogle = "Login with Google",
            loginWithApple = "Login with Apple",
            enableBiometricLogin = "Enable biometric login",
            termsPrefix = "By continuing, you agree to our ",
            terms = "Terms",
            and = " and ",
            privacy = "Privacy",
            register = "Sign Up",
            registerSubtitle = "Create your account to start your healthy life",
            fullName = "Full name",
            username = "Username",
            email = "Email",
            confirmPassword = "Confirm password",
            termsAndConditions = "Terms and Conditions",
            alreadyHaveAccount = "Already have an account? ",
            ok = "OK",
            biometricPromptTitle = "Log in to GoodLife",
            biometricPromptSubtitle = "Use your fingerprint, face, PIN or pattern",
            biometricPromptCancel = "Cancel",
            loginSuccess = "Login Successful"
        )

        override val accessibilityTexts = AccessibilityTexts(
            hide = "Hide",
            show = "Show",
            openCalendar = "Open calendar",
            previousDay = "Previous day",
            nextDay = "Next day",
            notifications = "Notifications",
            filter = "Filter",
            add = "Add",
            goToDaily = "Go to daily tasks",
            goToWorkouts = "Go to workouts",
            goToMeals = "Go to meals",
            goToMore = "More options and settings"
        )

        override val formats = DateFormats(
            full = LocalDate.Format {
                monthName(monthNames)
                char(' ')
                dayOfMonth()
                chars(", ")
                year()
            },
            dayMonth = LocalDate.Format {
                monthName(monthNames)
                char(' ')
                dayOfMonth()
            },
            dayNameAndDate = LocalDate.Format {
                dayOfWeek(dayNamesShort)
                chars(", ")
                monthName(monthNames)
                char(' ')
                dayOfMonth()
            }
        )
    }

    // ═══════════════════════════════════════════════════════════════════
    // PORTUGUÊS
    // ═══════════════════════════════════════════════════════════════════

    data object Portuguese : AppLanguage {

        override val monthNames = MonthNames(
            "janeiro", "fevereiro", "março", "abril", "maio", "junho",
            "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"
        )

        override val dayNamesShort = DayOfWeekNames(
            "seg", "ter", "qua", "qui", "sex", "sáb", "dom"
        )

        override val relativeTexts = RelativeDateTexts(
            today = "Hoje",
            yesterday = "Ontem",
            tomorrow = "Amanhã"
        )

        override val dailyItemLabels = DailyItemLabels(
            task = "Tarefa",
            habit = "Hábito",
            workout = "Treino",
            meal = "Refeição"
        )

        override val validationTexts = ValidationTexts(
            fieldRequired = "O campo não pode estar vazio",
            minLengthFormat = "Deve ter pelo menos {0} caracteres",
            invalidEmailFormat = "Formato de email inválido",
            passwordRequired = "A senha não pode estar vazia",
            passwordMinLengthFormat = "A senha deve ter pelo menos {0} caracteres",
            passwordMaxLengthFormat = "A senha não pode exceder {0} caracteres",
            passwordNeedsUppercase = "A senha deve conter pelo menos uma maiúscula",
            passwordNeedsNumber = "A senha deve conter pelo menos um número",
            passwordNeedsSpecialChar = "A senha deve conter pelo menos um caractere especial",
            passwordsDontMatch = "As senhas não coincidem",
            usernameRequired = "O nome de usuário não pode estar vazio",
            usernameMinLengthFormat = "O nome de usuário deve ter pelo menos {0} caracteres",
            usernameInvalidChars = "Apenas letras, números e underscores",
            fullNameRequired = "O nome não pode estar vazio",
            fullNameTooShort = "O nome é muito curto",
            fullNameInvalidChars = "O nome só pode conter letras",
            invalidFullName = "Nome inválido",
            invalidUsername = "Nome de usuário inválido",
            invalidEmail = "Email inválido",
            invalidPassword = "Senha inválida"
        )

        override val errorTexts = ErrorTexts(
            loginError = "Erro ao fazer login",
            registerError = "Erro ao registrar",
            unexpectedState = "Estado inesperado",
            operationInProgress = "Operação em andamento",
            userLoadError = "Erro ao carregar usuário",
            logoutError = "Erro ao fazer logout",
            noAuthenticatedUser = "Nenhum usuário autenticado",
            dataLoadError = "Erro ao carregar dados",
            connectionError = "Erro de conexão",
            serverErrorFormat = "Erro do servidor (código: {0})",
            unknownError = "Erro desconhecido",
            mustAcceptTerms = "Você deve aceitar os termos e condições"
        )

        override val dailyTexts = DailyTexts(
            dailyProgressFormat = "Progresso diário: {0}%",
            completedOfFormat = "{0} de {1} concluídos",
            totalForDayFormat = "Total do dia: {0} itens",
            schedulePrefix = "Horário",
            serverError = "Erro do servidor",
            sessionExpired = "Sessão expirada",
            noData = "Sem dados",
            noPermissions = "Sem permissões",
            invalidRequest = "Solicitação inválida",
            error = "Erro",
            tapToRetry = "Toque para tentar novamente"
        )

        override val mainScaffoldTexts = MainScaffoldTexts(
            routine = "Rotina",
            nutrition = "Alimentação",
            weight = "Peso",
            supplements = "Suplementos",
            activity = "Atividade",
            others = "Outros",
            dailySummary = "Resumo diário",
            breakfast = "Café da manhã",
            lunch = "Almoço",
            dinner = "Jantar",
            snack = "Lanche",
            preWorkout = "Pré-treino",
            postWorkout = "Pós-treino",
            tabDaily = "Diário",
            tabWorkouts = "Treinos",
            tabFood = "Comida",
            tabMore = "Mais",
            tabExercises = "Exercícios",
            tabMyExercises = "Meus exercícios",
            tabStatistics = "Estatísticas"
        )

        override val homeTexts = HomeTexts(
            welcome = "Bem-vindo!",
            sessionActive = "Você fez login com sucesso.\nSua sessão está ativa.",
            logout = "Sair",
            error = "Erro",
            retry = "Tentar novamente"
        )

        override val authTexts = AuthTexts(
            loginSubtitle = "Seu bem-estar, seu ritmo",
            emailOrUsername = "Email ou nome de usuário",
            password = "Senha",
            login = "Entrar",
            createAccount = "Criar Conta",
            or = "ou",
            loginWithGoogle = "Entrar com Google",
            loginWithApple = "Entrar com Apple",
            enableBiometricLogin = "Ativar login biométrico",
            termsPrefix = "Ao continuar, você aceita nossos ",
            terms = "Termos",
            and = " e ",
            privacy = "Privacidade",
            register = "Cadastrar",
            registerSubtitle = "Crie sua conta para começar sua vida saudável",
            fullName = "Nome completo",
            username = "Nome de usuário",
            email = "Email",
            confirmPassword = "Confirmar senha",
            termsAndConditions = "Termos e Condições",
            alreadyHaveAccount = "Já tem uma conta? ",
            ok = "OK",
            biometricPromptTitle = "Entrar no GoodLife",
            biometricPromptSubtitle = "Use sua digital, rosto, PIN ou padrão",
            biometricPromptCancel = "Cancelar",
            loginSuccess = "Login realizado com sucesso"
        )

        override val accessibilityTexts = AccessibilityTexts(
            hide = "Ocultar",
            show = "Mostrar",
            openCalendar = "Abrir calendário",
            previousDay = "Dia anterior",
            nextDay = "Próximo dia",
            notifications = "Notificações",
            filter = "Filtrar",
            add = "Adicionar",
            goToDaily = "Ir para tarefas diárias",
            goToWorkouts = "Ir para treinos",
            goToMeals = "Ir para refeições",
            goToMore = "Mais opções e configurações"
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
    }
}
