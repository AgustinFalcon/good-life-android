package com.agusstkd.goodlife.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.usecase.home.GetCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.home.LogoutUseCase
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.navigateToLoginFromMain
import com.agusstkd.goodlife.presentation.screen.home.model.HomeUiAction
import com.agusstkd.goodlife.presentation.screen.home.model.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla Home.
 *
 * Responsabilidades:
 * - Cargar datos del usuario logueado (via GetCurrentUserUseCase)
 * - Manejar el logout (via LogoutUseCase)
 *
 * Sigue Clean Architecture: ViewModel → UseCase → Repository
 */
class HomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val navigationController: ComposeNavigationController
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    /**
     * Carga los datos del usuario usando el UseCase.
     */
    private fun loadUserData() {
        viewModelScope.launch {
            val result = getCurrentUserUseCase()

            _uiState.value = when (result) {
                is Result.Success -> {
                    HomeUiState.Content(
                        userName = result.data.name,
                        userEmail = result.data.email
                    )
                }
                is Result.Error -> {
                    HomeUiState.Error(result.message ?: "Error al cargar usuario")
                }
                is Result.Loading -> HomeUiState.Loading
            }
        }
    }

    /**
     * Procesa acciones del usuario.
     */
    fun onAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.OnLogoutClick -> performLogout()
            is HomeUiAction.OnRetryClick -> loadUserData()
        }
    }

    /**
     * Ejecuta el logout usando el UseCase y navega a Login.
     */
    private fun performLogout() {
        viewModelScope.launch {
            logoutUseCase()
            navigationController.navigateToLoginFromMain()
        }
    }
}
