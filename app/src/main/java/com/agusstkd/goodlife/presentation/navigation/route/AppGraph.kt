package com.agusstkd.goodlife.presentation.navigation.route

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.agusstkd.goodlife.presentation.screen.add.task.CreateTaskScreenOwner
import com.agusstkd.goodlife.presentation.screen.login.LoginScreenOwner
import com.agusstkd.goodlife.presentation.screen.main.MainScaffoldScreenOwner
import com.agusstkd.goodlife.presentation.screen.register.RegisterScreenOwner
import com.agusstkd.goodlife.presentation.screen.splash.SplashScreenOwner

/**
 * Define el grafo de navegación a nivel de aplicación.
 *
 * Contiene las rutas principales: Splash, Login, Register, Main.
 * Se usa en SmartNavHost como el builder del grafo.
 */
fun NavGraphBuilder.addAppGraph() {

    composable<AppRoute.Splash> {
        SplashScreenOwner()
    }

    composable<AppRoute.Login> {
        LoginScreenOwner()
    }

    composable<AppRoute.Register> {
        RegisterScreenOwner()
    }

    composable<AppRoute.Main> {
        MainScaffoldScreenOwner()
    }

    composable<AppRoute.CreateTask> {
        CreateTaskScreenOwner()
    }
}

/**
 * Pantalla placeholder temporal para desarrollo.
 */
@Composable
private fun PlaceholderScreen(name: String) {
    Text(text = "Pantalla: $name")
}
