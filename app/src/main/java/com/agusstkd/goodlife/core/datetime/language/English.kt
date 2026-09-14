package com.agusstkd.goodlife.core.datetime.language

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

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
        tapToRetry = "Tap to retry",
        nextUp = "NEXT UP",
        inProgress = "IN PROGRESS",
        all = "All",
        task = "Task",
        habit = "Habit",
        workout = "Workout",
        meal = "Meal"
    )

    override val mainScaffoldTexts = MainScaffoldTexts(
        task = "Task",
        habit = "Habit",
        workout = "Workout",
        meal = "Meal",
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
        goToMore = "More options and settings",
        close = "Close",
        appLogo = "GoodLife logo",
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

    override val datePickerTexts = DatePickerTexts(
        title = "Select date",
        cancel = "Cancel",
        confirm = "Confirm",
        pastDaysHint = "Past days are not available",
    )

    override val createItemSharedTexts = CreateItemSharedTexts(
        fieldTitleLabel = "Title",
        fieldTitlePlaceholder = "E.g. Buy groceries",
        fieldDescriptionLabel = "Description (optional)",
        fieldDescriptionPlaceholder = "Add a note...",
        whenSectionTitle = "When?",
        modeOnce = "Once",
        modeRepeats = "Repeating",
        daysRowTitle = "Days of the week",
        fromDateLabel = "From",
        toDateLabel = "Until",
        noEndDate = "No end date",
        hasEndDate = "Set end date",
        noSpecificTime = "No specific time",
        timeLabel = "Start time",
        reminderLabel = "Reminder",
        cancelButton = "Cancel",
        confirmLabel = "Confirm",
        cancelLabel = "Cancel",
        retryLabel = "Retry",
        loadingMessage = "This will only take a moment...",
        errorTitle = "Could not save",
    )

    override val createTaskTexts = CreateTaskTexts(
        screenTitle = "New task",
        typeBadge = "TASK",
        saveButton = "Save task",
        successTitle = "Task created successfully",
        errorTitleEmpty = "Title can't be empty",
        errorNoDate = "Select a date",
        errorNoDays = "Select at least one day",
        errorEndBeforeStart = "End date can't be before start date",
        errorServer = "Server error, please try again",
        errorNetwork = "No connection, check your internet",
        loadingTitle = "Saving task...",
    )

    override val habitCategoryTexts = HabitCategoryTexts(
        hydration = "Hydration",
        meditation = "Meditation",
        reading = "Reading",
        exercise = "Exercise",
        sleep = "Sleep",
        nutrition = "Nutrition",
        learning = "Learning",
        mindfulness = "Mindfulness",
        social = "Social",
        creativity = "Creativity",
        productivity = "Productivity",
        health = "Health",
        custom = "Custom",
    )

    override val createHabitTexts = CreateHabitTexts(
        screenTitle = "New habit",
        typeBadge = "HABIT",
        saveButton = "Save habit",
        successTitle = "Habit created successfully",
        categorySectionTitle = "Category",
        categoryPlaceholder = "Select category",
        goalSectionTitle = "Daily goal",
        targetValuePlaceholder = "E.g. 8",
        unitPlaceholder = "E.g. glasses",
        errorNameEmpty = "Name can't be empty",
        errorNoDays = "Select at least one day",
        errorNoCategory = "Select a category",
        errorInvalidGoal = "Goal must be greater than 0",
        errorUnitEmpty = "Unit can't be empty",
        errorEndBeforeStart = "End date can't be before start date",
        errorServer = "Server error, please try again",
        errorNetwork = "No connection, check your internet",
        loadingTitle = "Saving habit...",
    )

    override val createRoutineTexts = CreateRoutineTexts(
        screenTitle = "New routine",
        typeBadge = "ROUTINE",
        stepOf = "Step %d of %d",
        nextButton = "Next",
        backButton = "Back",
        createButton = "Create routine",
        successTitle = "Routine created successfully",
        difficultySectionTitle = "Difficulty",
        difficultyBeginner = "Beginner",
        difficultyIntermediate = "Intermediate",
        difficultyAdvanced = "Advanced",
        goalSectionTitle = "Main goal",
        goalPlaceholder = "Select a goal",
        goalMuscleGain = "Muscle gain",
        goalWeightLoss = "Weight loss",
        goalStrength = "Strength",
        goalEndurance = "Endurance",
        goalFlexibility = "Flexibility",
        workoutsTitle = "Workouts",
        workoutsSubtitle = "Define your workout rotation",
        addWorkout = "Add workout",
        workoutNamePlaceholder = "E.g. Push Day",
        deleteWorkoutConfirm = "Delete this workout?",
        yourExercises = "Your exercises",
        catalogTitle = "Exercise catalog",
        searchPlaceholder = "Search exercises...",
        filterAll = "All",
        addExerciseButton = "Add",
        editSetsButton = "Edit",
        saveWorkoutButton = "Save workout",
        setsLabel = "Sets",
        repsLabel = "Reps",
        weightLabel = "Weight (kg)",
        addSetButton = "Add set",
        notesPlaceholder = "Exercise notes (optional)",
        summaryTitle = "Routine summary",
        activateToggle = "Activate this routine now",
        exercisesCount = "%d exercises",
        setsCount = "%d total sets",
        errorNameEmpty = "Name cannot be empty",
        errorNoDifficulty = "Please select a difficulty",
        errorNoGoal = "Please select a goal",
        errorNoDays = "Select at least one day",
        errorNoWorkouts = "Add at least one workout",
        errorWorkoutNoExercises = "Each workout needs at least one exercise",
        errorEndBeforeStart = "End date cannot be before start date",
        errorServer = "Server error, please try again",
        errorNetwork = "No connection, check your internet",
        loadingTitle = "Saving routine...",
    )

    override val workoutTexts = WorkoutTexts(
        noRoutineTitle = "No active routine",
        noRoutineDescription = "Create your first training routine to start tracking workouts.",
        createRoutine = "Create routine",
        retry = "Retry",
        emptyWorkouts = "No workouts in this routine",
        workoutSummaryFormat = "{0} exercises · {1} sets",
        dayFormat = "Day {0}",
        loading = "Loading workout...",
        back = "Back",
        exercisesTitle = "Exercises",
        noExercises = "This workout has no configured exercises.",
        notesLabel = "Notes",
        noSets = "No configured sets.",
        setFormat = "Set {0}",
        repsFormat = "{0} reps",
        repsAndWeightFormat = "{0} reps · {1} kg",
        workoutNotFound = "We could not find this workout in your active routine.",
    )
    override val mealTypeTexts = MealTypeTexts(
        breakfast = "Breakfast",
        lunch = "Lunch",
        dinner = "Dinner",
        snack = "Snack",
        preWorkout = "Pre-workout",
        postWorkout = "Post-workout",
    )

    override val createMealPlanTexts = CreateMealPlanTexts(
        // ── Header ──
        screenTitle = "New meal",
        typeBadge = "MEAL",
        stepOf = "Step %d of %d",
        nextButton = "Next",
        backButton = "Back",
        createPlanButton = "Create plan",
        successTitle = "Meal plan created successfully",
        // ── Step 1A: Catalog ──
        searchMealPlaceholder = "Search meals...",
        filterAll = "All",
        chooseMealButton = "Choose",
        createNewMealButton = "Create new meal",
        // ── Step 1B: New meal ──
        newMealNameLabel = "Meal name",
        newMealNamePlaceholder = "E.g. Protein smoothie",
        newMealDescriptionLabel = "Description (optional)",
        newMealDescriptionPlaceholder = "E.g. Post-workout shake...",
        newMealTypeLabel = "Meal type",
        // ── Step 2: Ingredients ──
        macrosSummaryTitle = "Total macros",
        macroProteinLabel = "Protein",
        macroCarbsLabel = "Carbs",
        macroFatLabel = "Fat",
        myIngredientsSectionTitle = "My ingredients",
        ingredientCatalogSectionTitle = "Ingredient catalog",
        searchIngredientPlaceholder = "Search ingredients...",
        addIngredientButton = "Add",
        createCustomIngredientButton = "Create custom ingredient",
        // ── Bottom Sheet ──
        quantitySheetQuantityLabel = "Quantity",
        quantitySheetUnitLabel = "Unit",
        quantitySheetConfirmButton = "Confirm",
        quantitySheetCancelButton = "Cancel",
        quantitySheetMacroTooltip = "Protein, carbs and fat expressed in grams (g).",
        // ── Ingredient dialog ──
        createIngredientDialogTitle = "New Ingredient",
        ingredientNameLabel = "Name",
        ingredientNamePlaceholder = "E.g. Instant oats",
        ingredientBrandLabel = "Brand (optional)",
        ingredientBrandPlaceholder = "E.g. Quaker",
        ingredientServingSizeLabel = "Reference serving",
        ingredientCaloriesLabel = "kcal",
        ingredientProteinLabel = "Protein (g)",
        ingredientCarbsLabel = "Carbs (g)",
        ingredientFatLabel = "Fat (g)",
        saveIngredientButton = "Save",
        // ── Step 3 ──
        mealSummaryTitle = "Meal summary",
        mealTypeSectionTitle = "Meal type",
        // ── Errors ──
        errorNoMealSelected = "Select a meal to continue",
        errorMealNameEmpty = "Meal name can't be empty",
        errorNoIngredients = "Add at least one ingredient",
        errorNoMealType = "Select a meal type",
        errorNoDays = "Select at least one day",
        errorEndBeforeStart = "End date can't be before start date",
        errorQuantityZero = "Quantity must be greater than 0",
        errorIngredientNameEmpty = "Ingredient name can't be empty",
        errorServer = "Server error, please try again",
        errorNetwork = "No connection, check your internet",
        loadingTitle = "Saving meal plan...",
    )

    override val splashTexts = SplashTexts(
        tapToContinue = "Tap to continue",
    )
    override val tabDetailTexts = TabDetailTexts(
        dailyTitle = "Daily detail", mealTitle = "Meal detail", back = "Back", loading = "Loading detail…",
        dailyNotFound = "We could not find this daily item", mealNotFound = "We could not find this meal plan", invalidRoute = "This detail route is invalid", retry = "Retry",
        typeLabel = "Type", statusLabel = "Status", scheduleLabel = "Schedule", descriptionLabel = "Description",
        pending = "Pending", inProgress = "In progress", completed = "Completed", skipped = "Skipped",
        mealLabel = "Meal", proteinLabel = "Protein", carbsLabel = "Carbs", fatLabel = "Fat",
        active = "Active", inactive = "Inactive", mealImageUnavailable = "Meal image unavailable",
        emptyMealsTitle = "No meal plans for today", emptyMealsDescription = "Create your first meal plan to track your daily nutrition.",
        createMealPlan = "Create meal plan", tapToRetry = "Tap to retry", mealPlanSingular = "meal plan", mealPlanPlural = "meal plans",
        calorieUnit = "kcal", gramUnit = "g",
    )

}
