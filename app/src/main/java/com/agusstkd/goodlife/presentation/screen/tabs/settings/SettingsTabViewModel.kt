package com.agusstkd.goodlife.presentation.screen.tabs.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiAction
import com.agusstkd.goodlife.presentation.screen.tabs.settings.model.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel del tab de Settings.
 *
 * Carga el nombre del usuario autenticado y ejecuta el logout.
 * La navegación (NavigateToProfile) queda delegada al Owner.
 */
class SettingsTabViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun onAction(action: SettingsUiAction) {
        when (action) {
            is SettingsUiAction.Logout -> logout()
            is SettingsUiAction.NavigateToProfile -> { /* handled by owner */ }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is Result.Success -> _uiState.update { it.copy(username = result.data.name) }
                is Result.Error -> { /* mantener username vacío — no bloquear la pantalla */ }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            logoutUseCase()
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
