package com.agusstkd.goodlife.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.domain.usecase.session.CheckSessionUseCase
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.route.navigateToLogin
import com.agusstkd.goodlife.presentation.navigation.route.navigateToMainFromSplash
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiAction
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val navigationController: ComposeNavigationController,
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            if (checkSessionUseCase()) {
                navigationController.navigateToMainFromSplash()
            } else {
                _uiState.value = SplashUiState.Ready
            }
        }
    }

    fun onAction(action: SplashUiAction) {
        when (action) {
            is SplashUiAction.ContinueClicked -> {
                navigationController.navigateToLogin()
            }
        }
    }
}
