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
