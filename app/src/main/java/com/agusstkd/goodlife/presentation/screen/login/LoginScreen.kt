package com.agusstkd.goodlife.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.presentation.components.BackgroundGradientComponent
import com.agusstkd.goodlife.presentation.components.ButtonComponent
import com.agusstkd.goodlife.presentation.components.ButtonParams
import com.agusstkd.goodlife.presentation.components.ButtonVariant
import com.agusstkd.goodlife.presentation.components.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.TextFieldParams
import com.agusstkd.goodlife.presentation.components.TextFieldType
import com.agusstkd.goodlife.presentation.components.TitleComponent
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiAction
import com.agusstkd.goodlife.presentation.screen.login.model.LoginUiState
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.DividerColor
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.GoodLifeTypography
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.TextLink
import com.agusstkd.goodlife.presentation.theme.TextSecondary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import com.agusstkd.goodlife.presentation.components.CheckboxComponent
import com.agusstkd.goodlife.presentation.components.CheckboxParams
import com.agusstkd.goodlife.presentation.components.CheckboxParamsId

/**
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 * LOGIN SCREEN - UI PURA (sin dependencias)
 * ═══════════════════════════════════════════════════════════════════════════════════════════
 *
 * Renderiza el formulario de login según el estado:
 * - **Loading** → Spinner de carga
 * - **Content** → Formulario completo
 * - **Error** → Formulario con estado de error
 * - **Success** → Se maneja en el Owner (navegación)
 *
 * ## Estructura:
 * - Header: Logo + Título + Subtítulo
 * - Formulario: Inputs + Remember + Forgot Password
 * - Botones: Login + Register
 * - Social: Google + Apple
 * - Footer: Términos + Privacidad
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BackgroundGradientComponent(modifier = modifier) {
        when (uiState) {
            is LoginUiState.Loading -> {
                LoadingView()
            }
            is LoginUiState.Content -> {
                LoginContent(
                    email = uiState.email,
                    password = uiState.password,
                    rememberUser = uiState.rememberUser,
                    isEmailError = uiState.isEmailError,
                    isPasswordError = uiState.isPasswordError,
                    isLoading = uiState.isLoading,
                    isBiometricAvailable = uiState.isBiometricAvailable,
                    isBiometricEnabled = uiState.isBiometricEnabled,
                    onAction = onAction
                )
            }
            is LoginUiState.Error -> {
                LoginContent(
                    email = "",
                    password = "",
                    rememberUser = false,
                    isEmailError = true,
                    isPasswordError = true,
                    isLoading = false,
                    isBiometricAvailable = false,
                    isBiometricEnabled = false,
                    onAction = onAction
                )
            }
            is LoginUiState.Success -> {
                // El Owner maneja la navegación
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// LOADING VIEW
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = DarkGreen,
            modifier = Modifier.size(48.dp)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// LOGIN CONTENT
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoginContent(
    email: String,
    password: String,
    rememberUser: Boolean,
    isEmailError: Boolean,
    isPasswordError: Boolean,
    isLoading: Boolean,
    isBiometricAvailable: Boolean,
    isBiometricEnabled: Boolean,
    onAction: (LoginUiAction) -> Unit
) {
    val isFormValid = email.isNotBlank() && password.isNotBlank()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // ═══════════════════════════════════════════════════════════════════════════════
        // HEADER: Logo + Título + Subtítulo
        // ═══════════════════════════════════════════════════════════════════════════════
        LoginHeader()

        Spacer(modifier = Modifier.height(40.dp))

        // ═══════════════════════════════════════════════════════════════════════════════
        // FORMULARIO: Email + Password
        // ═══════════════════════════════════════════════════════════════════════════════
        LoginForm(
            email = email,
            password = password,
            isEmailError = isEmailError,
            isPasswordError = isPasswordError,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ═══════════════════════════════════════════════════════════════════════════════
        // BIOMETRÍA (solo mostrar si está disponible)
        // ═══════════════════════════════════════════════════════════════════════════════
        if (isBiometricAvailable) {
            CheckboxComponent(
                params = CheckboxParams(
                    text = "Activar login con huella",
                    checked = isBiometricEnabled,
                    id = CheckboxParamsId.ENABLE_FINGER_PRINT,
                    endIcon = Icons.Default.Fingerprint
                ),
                onClick = { onAction(LoginUiAction.OnBiometricToggle) },
                onEndIconClick = { onAction(LoginUiAction.OnBiometricIconClick) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ═══════════════════════════════════════════════════════════════════════════════
        // BOTONES: Login + Register
        // ═══════════════════════════════════════════════════════════════════════════════
        LoginButtons(
            isFormValid = isFormValid,
            isLoading = isLoading,
            onAction = onAction
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════════════════════════════════════════════════════════════════════
        // SOCIAL LOGIN: Google + Apple
        // ═══════════════════════════════════════════════════════════════════════════════
        SocialLoginSection(onAction = onAction)

        Spacer(modifier = Modifier.height(24.dp))

        // ═══════════════════════════════════════════════════════════════════════════════
        // FOOTER: Términos + Privacidad
        // ═══════════════════════════════════════════════════════════════════════════════
        LoginFooter(onAction = onAction)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// HEADER
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Logo
        Image(
            painter = painterResource(R.drawable.good_life_logo),
            contentDescription = "Logo GoodLife",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título con gradiente
        TitleComponent(text = "GoodLife")

        Spacer(modifier = Modifier.height(4.dp))

        // Subtítulo
        Text(
            text = "Tu bienestar, tu ritmo",
            style = GoodLifeTypography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// FORM
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoginForm(
    email: String,
    password: String,
    isEmailError: Boolean,
    isPasswordError: Boolean,
    onAction: (LoginUiAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Email/Usuario
        TextFieldComponent(
            params = TextFieldParams(
                value = email,
                placeholder = "Correo electrónico o usuario",
                type = TextFieldType.USER,
                isError = isEmailError
            ),
            onValueChange = { onAction(LoginUiAction.OnEmailChange(it)) }
        )

        // Contraseña
        TextFieldComponent(
            params = TextFieldParams(
                value = password,
                placeholder = "Contraseña",
                type = TextFieldType.PASSWORD,
                isError = isPasswordError
            ),
            onValueChange = { onAction(LoginUiAction.OnPasswordChange(it)) }
        )
    }
}


// ═══════════════════════════════════════════════════════════════════════════════════════════
// REMEMBER + FORGOT PASSWORD
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun RememberAndForgotRow(
    rememberUser: Boolean,
    onAction: (LoginUiAction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Switch de recordar usuario
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onAction(LoginUiAction.OnRememberUserToggle)
            }
        ) {
            Switch(
                checked = rememberUser,
                onCheckedChange = { onAction(LoginUiAction.OnRememberUserToggle) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = LightGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = DividerColor
                ),
                modifier = Modifier.height(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Recordar",
                style = GoodLifeTypography.bodySmall,
                color = TextSecondary
            )
        }

        // Link olvidaste contraseña
        Text(
            text = "¿Olvidaste tu contraseña?",
            style = GoodLifeTypography.bodySmall.copy(
                textDecoration = TextDecoration.None
            ),
            color = TextLink,
            modifier = Modifier.clickable {
                onAction(LoginUiAction.OnForgotPasswordClick)
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// BUTTONS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoginButtons(
    isFormValid: Boolean,
    isLoading: Boolean,
    onAction: (LoginUiAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón Login con loading
        ButtonComponent(
            params = ButtonParams(
                text = "Iniciar Sesión",
                enabled = isFormValid,
                isLoading = isLoading,
                variant = ButtonVariant.PRIMARY,
                showTrailingIcon = !isLoading // Ocultar flecha cuando está loading
            ),
            onClick = { onAction(LoginUiAction.OnLoginClick) }
        )

        // Separador con círculo
        SeparatorWithCircle()

        // Botón Register (deshabilitado durante loading)
        ButtonComponent(
            params = ButtonParams(
                text = "Crear Cuenta",
                enabled = !isLoading,
                variant = ButtonVariant.OUTLINE
            ),
            onClick = { onAction(LoginUiAction.OnRegisterClick) }
        )
    }
}

@Composable
private fun SeparatorWithCircle() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DividerColor,
            thickness = 1.dp
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(24.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "o",
                style = GoodLifeTypography.bodySmall,
                color = TextSecondary
            )
        }

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = DividerColor,
            thickness = 1.dp
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// SOCIAL LOGIN
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun SocialLoginSection(onAction: (LoginUiAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón Google
        SocialButton(
            iconRes = R.drawable.good_life_logo, // TODO: Agregar icono de Google
            contentDescription = "Login con Google",
            onClick = { onAction(LoginUiAction.OnGoogleLoginClick) }
        )

        Spacer(modifier = Modifier.width(24.dp))

        // Botón Apple
        SocialButton(
            iconRes = R.drawable.good_life_logo, // TODO: Agregar icono de Apple
            contentDescription = "Login con Apple",
            onClick = { onAction(LoginUiAction.OnAppleLoginClick) }
        )
    }
}

@Composable
private fun SocialButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// FOOTER
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Composable
private fun LoginFooter(onAction: (LoginUiAction) -> Unit) {
    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = TextSecondary)) {
            append("Al continuar, aceptas nuestros ")
        }

        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            SpanStyle(
                color = TextLink,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Términos")
        }
        pop()

        withStyle(SpanStyle(color = TextSecondary)) {
            append(" y ")
        }

        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(
            SpanStyle(
                color = TextLink,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Privacidad")
        }
        pop()

        withStyle(SpanStyle(color = TextSecondary)) {
            append(".")
        }
    }

    Text(
        text = annotatedString,
        style = GoodLifeTypography.bodySmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // TODO: Implementar detección de click en cada link
                onAction(LoginUiAction.OnTermsClick)
            }
    )
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "Loading")
@Composable
fun LoginScreenLoadingPreview() {
    GoodLifeTheme {
        LoginScreen(
            uiState = LoginUiState.Loading,
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Empty")
@Composable
fun LoginScreenEmptyPreview() {
    GoodLifeTheme {
        LoginScreen(
            uiState = LoginUiState.Content(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - Filled")
@Composable
fun LoginScreenFilledPreview() {
    GoodLifeTheme {
        LoginScreen(
            uiState = LoginUiState.Content(
                email = "agustin@gmail.com",
                password = "1234",
                rememberUser = true
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Content - With Errors")
@Composable
fun LoginScreenWithErrorsPreview() {
    GoodLifeTheme {
        LoginScreen(
            uiState = LoginUiState.Content(
                email = "invalid",
                password = "12",
                isEmailError = true,
                isPasswordError = true
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun LoginScreenErrorPreview() {
    GoodLifeTheme {
        LoginScreen(
            uiState = LoginUiState.Error,
            onAction = {}
        )
    }
}
