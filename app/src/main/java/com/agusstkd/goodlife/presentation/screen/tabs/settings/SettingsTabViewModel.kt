package com.agusstkd.goodlife.presentation.screen.tabs.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.SettingsTexts
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.domain.usecase.home.result.GetCurrentUserResult
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.navigateToLoginFromMain
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsNavigationEffect
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del tab de Settings.
 *
 * Carga el nombre del usuario autenticado, expone copy via [AppLanguage]
 * y emite [SettingsNavigationEffect] para ir al perfil.
 */
class SettingsTabViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val navigationController: ComposeNavigationController,
    private val language: AppLanguage,
) : ViewModel() {

    /** Copy localizado del tab Settings. */
    val texts: SettingsTexts get() = language.settingsTexts

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val navigationEffectsChannel = Channel<SettingsNavigationEffect>(Channel.RENDEZVOUS)
    val navigationEffects = navigationEffectsChannel.receiveAsFlow()

    init {
        loadCurrentUser()
    }

    /**
     * Punto único de entrada de acciones de la Screen.
     *
     * @param action Acción disparada por el usuario.
     */
    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.Logout -> logout()
            is SettingsUiAction.NavigateToProfile -> {
                navigationEffectsChannel.trySend(SettingsNavigationEffect.NavigateToProfile)
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is GetCurrentUserResult.Success -> _uiState.update { it.copy(username = result.user.name) }
                GetCurrentUserResult.Unavailable -> { /* mantener username vacío — no bloquear la pantalla */ }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            logoutUseCase()
            _uiState.update { it.copy(isLoading = false) }
            navigationController.navigateToLoginFromMain()
        }
    }
}
