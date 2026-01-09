package com.agusstkd.goodlife.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.navigateToLogin
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiAction
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val navigationController: ComposeNavigationController
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        simulateLoading()
    }

    private fun simulateLoading() {
        viewModelScope.launch {
            delay(2000L)
            _uiState.value = SplashUiState.Ready
        }
    }

    /**
     * Maneja las acciones del usuario.
     */
    fun onAction(action: SplashUiAction) {
        when (action) {
            is SplashUiAction.ContinueClicked -> {
                navigationController.navigateToLogin()
            }
        }
    }
}
