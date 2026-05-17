package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.presentation.screen.tabs.settings.SettingsTabViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo de Koin para la feature de Settings.
 *
 * Depende de:
 * - [authModule]: provee [LogoutUseCase] y [GetCurrentUserUseCase]
 */
val settingsModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // VIEWMODELS
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * SettingsTabViewModel — ViewModel del tab Settings.
     */
    viewModel {
        SettingsTabViewModel(
            logoutUseCase = get(),
            getCurrentUserUseCase = get()
        )
    }
}
