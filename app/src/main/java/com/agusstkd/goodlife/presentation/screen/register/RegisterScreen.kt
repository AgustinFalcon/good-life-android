package com.agusstkd.goodlife.presentation.screen.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.agusstkd.goodlife.presentation.components.common.BackgroundGradientComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonComponent
import com.agusstkd.goodlife.presentation.components.common.ButtonParams
import com.agusstkd.goodlife.presentation.components.common.ButtonVariant
import com.agusstkd.goodlife.presentation.components.common.CheckboxComponent
import com.agusstkd.goodlife.presentation.components.common.CheckboxParams
import com.agusstkd.goodlife.presentation.components.common.CheckboxParamsId
import com.agusstkd.goodlife.presentation.components.common.TextFieldComponent
import com.agusstkd.goodlife.presentation.components.common.TextFieldParams
import com.agusstkd.goodlife.presentation.components.common.TextFieldType
import com.agusstkd.goodlife.presentation.components.common.TitleComponent
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiAction
import com.agusstkd.goodlife.presentation.screen.register.model.RegisterUiState
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.TextLink
import com.agusstkd.goodlife.presentation.theme.TextSecondary

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onAction: (RegisterUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BackgroundGradientComponent(modifier = modifier) {
        when (uiState) {
            is RegisterUiState.Loading -> LoadingView()
            is RegisterUiState.Content -> RegisterContent(state = uiState, onAction = onAction)
            is RegisterUiState.Success -> { }
        }
    }
}

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

@Composable
private fun RegisterContent(
    state: RegisterUiState.Content,
    onAction: (RegisterUiAction) -> Unit
) {
    val isFormValid = state.fullName.isNotBlank() &&
            state.userName.isNotBlank() &&
            state.email.isNotBlank() &&
            state.password.isNotBlank() &&
            state.confirmPassword.isNotBlank() &&
            state.password == state.confirmPassword &&
            state.acceptedTerms &&
            !state.isConfirmPasswordError

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        RegisterHeader()

        Spacer(modifier = Modifier.height(32.dp))

        RegisterForm(state = state, onAction = onAction)

        Spacer(modifier = Modifier.height(16.dp))

        TermsCheckbox(isChecked = state.acceptedTerms, onAction = onAction)

        Spacer(modifier = Modifier.height(24.dp))

        ButtonComponent(
            params = ButtonParams(
                text = "Registrarme",
                enabled = isFormValid,
                isLoading = state.isLoading,
                variant = ButtonVariant.PRIMARY,
                showTrailingIcon = !state.isLoading
            ),
            onClick = { onAction(RegisterUiAction.OnRegisterClick) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AlreadyHaveAccountLink(onAction = onAction)

        state.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            ErrorSnackbar(
                message = error,
                onDismiss = { onAction(RegisterUiAction.OnDismissError) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RegisterHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.good_life_logo),
            contentDescription = "Logo GoodLife",
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        TitleComponent(text = "Crear Cuenta")

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Crea tu cuenta para empezar tu vida saludable",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RegisterForm(
    state: RegisterUiState.Content,
    onAction: (RegisterUiAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextFieldComponent(
            params = TextFieldParams(
                value = state.fullName,
                placeholder = "Nombre completo",
                type = TextFieldType.USER,
                isError = state.isFullNameError
            ),
            onValueChange = { onAction(RegisterUiAction.OnFullNameChange(it)) }
        )

        TextFieldComponent(
            params = TextFieldParams(
                value = state.userName,
                placeholder = "Nombre de usuario",
                type = TextFieldType.USER,
                isError = state.isUserNameError
            ),
            onValueChange = { onAction(RegisterUiAction.OnUserNameChange(it)) }
        )

        TextFieldComponent(
            params = TextFieldParams(
                value = state.email,
                placeholder = "Correo electrónico",
                type = TextFieldType.EMAIL,
                isError = state.isEmailError
            ),
            onValueChange = { onAction(RegisterUiAction.OnEmailChange(it)) }
        )

        TextFieldComponent(
            params = TextFieldParams(
                value = state.password,
                placeholder = "Contraseña",
                type = TextFieldType.PASSWORD,
                isError = state.isPasswordError
            ),
            onValueChange = { onAction(RegisterUiAction.OnPasswordChange(it)) }
        )

        TextFieldComponent(
            params = TextFieldParams(
                value = state.confirmPassword,
                placeholder = "Confirmar contraseña",
                type = TextFieldType.PASSWORD,
                isError = state.isConfirmPasswordError
            ),
            onValueChange = { onAction(RegisterUiAction.OnConfirmPasswordChange(it)) }
        )
    }
}

@Composable
private fun TermsCheckbox(
    isChecked: Boolean,
    onAction: (RegisterUiAction) -> Unit
) {
    CheckboxComponent(
        params = CheckboxParams(
            text = "Acepto los ",
            linkText = "Términos y Condiciones",
            checked = isChecked,
            id = CheckboxParamsId.ACCEPT_TERMS_AND_CONDITIONS
        ),
        onClick = { onAction(RegisterUiAction.OnTermsToggle) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    )
}

@Composable
private fun AlreadyHaveAccountLink(onAction: (RegisterUiAction) -> Unit) {
    val annotatedString = buildAnnotatedString {
        withStyle(SpanStyle(color = TextSecondary)) {
            append("¿Ya tienes cuenta? ")
        }
        withStyle(SpanStyle(color = TextLink, textDecoration = TextDecoration.Underline)) {
            append("Iniciar Sesión")
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction(RegisterUiAction.OnLoginClick) }
    )
}

@Composable
private fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        action = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    ) {
        Text(message)
    }
}

// ═══════════════════════════════════════════════════════════════════════════════════════════
// PREVIEWS
// ═══════════════════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "Loading")
@Composable
private fun RegisterScreenLoadingPreview() {
    GoodLifeTheme {
        RegisterScreen(uiState = RegisterUiState.Loading, onAction = {})
    }
}

@Preview(showBackground = true, name = "Content - Empty")
@Composable
private fun RegisterScreenEmptyPreview() {
    GoodLifeTheme {
        RegisterScreen(uiState = RegisterUiState.Content(), onAction = {})
    }
}

@Preview(showBackground = true, name = "Content - Filled")
@Composable
private fun RegisterScreenFilledPreview() {
    GoodLifeTheme {
        RegisterScreen(
            uiState = RegisterUiState.Content(
                fullName = "Agustín García",
                userName = "agustin123",
                email = "agustin@email.com",
                password = "123456",
                confirmPassword = "123456",
                acceptedTerms = true
            ),
            onAction = {}
        )
    }
}
