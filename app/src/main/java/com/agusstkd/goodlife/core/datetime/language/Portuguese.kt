package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames

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
        tapToRetry = "Toque para tentar novamente",
        nextUp = "PRÓXIMO",
        inProgress = "EM ANDAMENTO",
        all = "Todos",
        task = "Tarefa",
        habit = "Hábito",
        workout = "Treino",
        meal = "Refeição"
    )

    override val mainScaffoldTexts = MainScaffoldTexts(
        task = "Tarefa",
        habit = "Hábito",
        workout = "Treino",
        meal = "Refeição",
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
        goToMore = "Mais opções e configurações",
        close = "Fechar",
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
        title = "Selecionar data",
        cancel = "Cancelar",
        confirm = "Confirmar",
        pastDaysHint = "Dias anteriores não estão disponíveis",
    )

    override val createItemSharedTexts = CreateItemSharedTexts(
        fieldTitleLabel = "Título",
        fieldTitlePlaceholder = "Ex: Comprar legumes",
        fieldDescriptionLabel = "Descrição (opcional)",
        fieldDescriptionPlaceholder = "Adicione uma nota...",
        whenSectionTitle = "Quando?",
        modeOnce = "Uma vez",
        modeRepeats = "Repetir",
        daysRowTitle = "Dias da semana",
        fromDateLabel = "De",
        toDateLabel = "Até",
        noEndDate = "Sem data de fim",
        hasEndDate = "Com data de fim",
        noSpecificTime = "Sem hora específica",
        timeLabel = "Hora de início",
        reminderLabel = "Lembrete",
        cancelButton = "Cancelar",
        confirmLabel = "Confirmar",
        cancelLabel = "Cancelar",
        retryLabel = "Tentar novamente",
        loadingMessage = "Isso vai levar apenas um momento...",
        errorTitle = "Não foi possível salvar",
    )

    override val createTaskTexts = CreateTaskTexts(
        screenTitle = "Nova tarefa",
        typeBadge = "TAREFA",
        saveButton = "Salvar tarefa",
        successTitle = "Tarefa criada com sucesso",
        errorTitleEmpty = "O título não pode estar vazio",
        errorNoDate = "Selecione uma data",
        errorNoDays = "Selecione pelo menos um dia",
        errorEndBeforeStart = "A data de fim não pode ser anterior ao início",
        errorServer = "Erro do servidor, tente novamente",
        errorNetwork = "Sem conexão, verifique sua internet",
        loadingTitle = "Salvando tarefa...",
    )

    override val habitCategoryTexts = HabitCategoryTexts(
        hydration = "Hidratação",
        meditation = "Meditação",
        reading = "Leitura",
        exercise = "Exercício",
        sleep = "Sono",
        nutrition = "Nutrição",
        learning = "Aprendizado",
        mindfulness = "Mindfulness",
        social = "Social",
        creativity = "Criatividade",
        productivity = "Produtividade",
        health = "Saúde",
        custom = "Personalizado",
    )

    override val createHabitTexts = CreateHabitTexts(
        screenTitle = "Novo hábito",
        typeBadge = "HÁBITO",
        saveButton = "Salvar hábito",
        successTitle = "Hábito criado com sucesso",
        categorySectionTitle = "Categoria",
        categoryPlaceholder = "Selecionar categoria",
        goalSectionTitle = "Meta diária",
        targetValuePlaceholder = "Ex: 8",
        unitPlaceholder = "Ex: copos",
        errorNameEmpty = "O nome não pode estar vazio",
        errorNoDays = "Selecione pelo menos um dia",
        errorNoCategory = "Selecione uma categoria",
        errorInvalidGoal = "A meta deve ser maior que 0",
        errorUnitEmpty = "A unidade não pode estar vazia",
        errorEndBeforeStart = "A data de fim não pode ser anterior ao início",
        errorServer = "Erro do servidor, tente novamente",
        errorNetwork = "Sem conexão, verifique sua internet",
        loadingTitle = "Salvando hábito...",
    )

    override val createRoutineTexts = CreateRoutineTexts(
        screenTitle = "Nova rotina",
        typeBadge = "ROTINA",
        stepOf = "Passo %d de %d",
        nextButton = "Próximo",
        backButton = "Voltar",
        createButton = "Criar rotina",
        successTitle = "Rotina criada com sucesso",
        difficultySectionTitle = "Dificuldade",
        difficultyBeginner = "Iniciante",
        difficultyIntermediate = "Intermediário",
        difficultyAdvanced = "Avançado",
        goalSectionTitle = "Objetivo principal",
        goalPlaceholder = "Selecionar objetivo",
        goalMuscleGain = "Ganho muscular",
        goalWeightLoss = "Perda de peso",
        goalStrength = "Força",
        goalEndurance = "Resistência",
        goalFlexibility = "Flexibilidade",
        workoutsTitle = "Treinos",
        workoutsSubtitle = "Defina a rotação de treinos",
        addWorkout = "Adicionar treino",
        workoutNamePlaceholder = "Ex: Push Day",
        deleteWorkoutConfirm = "Excluir este treino?",
        yourExercises = "Seus exercícios",
        catalogTitle = "Catálogo de exercícios",
        searchPlaceholder = "Buscar exercícios...",
        filterAll = "Todos",
        addExerciseButton = "Adicionar",
        editSetsButton = "Editar",
        saveWorkoutButton = "Salvar treino",
        setsLabel = "Sets",
        repsLabel = "Reps",
        weightLabel = "Peso (kg)",
        addSetButton = "Adicionar set",
        notesPlaceholder = "Notas do exercício (opcional)",
        summaryTitle = "Resumo da rotina",
        activateToggle = "Ativar esta rotina agora",
        exercisesCount = "%d exercícios",
        setsCount = "%d sets totais",
        errorNameEmpty = "O nome não pode estar vazio",
        errorNoDifficulty = "Selecione uma dificuldade",
        errorNoGoal = "Selecione um objetivo",
        errorNoDays = "Selecione pelo menos um dia",
        errorNoWorkouts = "Adicione pelo menos um treino",
        errorWorkoutNoExercises = "Cada treino precisa de pelo menos um exercício",
        errorEndBeforeStart = "A data de término não pode ser anterior ao início",
        errorServer = "Erro no servidor, tente novamente",
        errorNetwork = "Sem conexão, verifique sua internet",
        loadingTitle = "Salvando rotina...",
    )

    override val mealTypeTexts = MealTypeTexts(
        breakfast = "Café da manhã",
        lunch = "Almoço",
        dinner = "Jantar",
        snack = "Lanche",
        preWorkout = "Pré-treino",
        postWorkout = "Pós-treino",
    )

    override val createMealPlanTexts = CreateMealPlanTexts(
        // ── Cabeçalho ──
        screenTitle = "Nova refeição",
        typeBadge = "REFEIÇÃO",
        stepOf = "Passo %d de %d",
        nextButton = "Próximo",
        backButton = "Voltar",
        createPlanButton = "Criar plano",
        successTitle = "Plano de refeição criado com sucesso",
        // ── Passo 1A: Catálogo ──
        searchMealPlaceholder = "Buscar refeições...",
        filterAll = "Todos",
        chooseMealButton = "Escolher",
        createNewMealButton = "Criar nova refeição",
        // ── Passo 1B: Nova refeição ──
        newMealNameLabel = "Nome da refeição",
        newMealNamePlaceholder = "Ex: Vitamina de proteína",
        newMealDescriptionLabel = "Descrição (opcional)",
        newMealDescriptionPlaceholder = "Ex: Shake pós-treino...",
        newMealTypeLabel = "Tipo de refeição",
        // ── Passo 2: Ingredientes ──
        macrosSummaryTitle = "Macros totais",
        macroProteinLabel = "Proteína",
        macroCarbsLabel = "Carbos",
        macroFatLabel = "Gordura",
        myIngredientsSectionTitle = "Meus ingredientes",
        ingredientCatalogSectionTitle = "Catálogo de ingredientes",
        searchIngredientPlaceholder = "Buscar ingredientes...",
        addIngredientButton = "Adicionar",
        createCustomIngredientButton = "Criar ingrediente personalizado",
        // ── Bottom Sheet ──
        quantitySheetQuantityLabel = "Quantidade",
        quantitySheetUnitLabel = "Unidade",
        quantitySheetConfirmButton = "Confirmar",
        quantitySheetCancelButton = "Cancelar",
        quantitySheetMacroTooltip = "Proteína, carboidratos e gorduras expressos em gramas (g).",
        // ── Dialog ingrediente ──
        createIngredientDialogTitle = "Novo Ingrediente",
        ingredientNameLabel = "Nome",
        ingredientNamePlaceholder = "Ex: Aveia instantânea",
        ingredientBrandLabel = "Marca (opcional)",
        ingredientBrandPlaceholder = "Ex: Quaker",
        ingredientServingSizeLabel = "Porção de referência",
        ingredientCaloriesLabel = "kcal",
        ingredientProteinLabel = "Proteína (g)",
        ingredientCarbsLabel = "Carboidratos (g)",
        ingredientFatLabel = "Gordura (g)",
        saveIngredientButton = "Salvar",
        // ── Passo 3 ──
        mealSummaryTitle = "Resumo da refeição",
        mealTypeSectionTitle = "Tipo de refeição",
        // ── Erros ──
        errorNoMealSelected = "Selecione uma refeição para continuar",
        errorMealNameEmpty = "O nome da refeição não pode estar vazio",
        errorNoIngredients = "Adicione pelo menos um ingrediente",
        errorNoMealType = "Selecione o tipo de refeição",
        errorNoDays = "Selecione pelo menos um dia",
        errorEndBeforeStart = "A data de fim não pode ser anterior ao início",
        errorQuantityZero = "A quantidade deve ser maior que 0",
        errorIngredientNameEmpty = "O nome do ingrediente não pode estar vazio",
        errorServer = "Erro do servidor, tente novamente",
        errorNetwork = "Sem conexão, verifique sua internet",
        loadingTitle = "Salvando plano de refeição...",
    )

    override val splashTexts = SplashTexts(
        tapToContinue = "Toque para continuar",
    )
}
