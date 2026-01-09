package com.agusstkd.goodlife.presentation.navigation.route

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * Define el grafo de navegación a nivel de aplicación.
 *
 * Contiene las rutas principales: Splash, Login, Register, Main.
 * Se usa en SmartNavHost como el builder del grafo.
 */
fun NavGraphBuilder.addAppGraph() {

    composable<AppRoute.Splash> {
        // TODO: Reemplazar con SplashScreenOwner()
        PlaceholderScreen("Splash")
    }

    composable<AppRoute.Login> {
        // TODO: Reemplazar con LoginScreenOwner()
        PlaceholderScreen("Login")
    }

    composable<AppRoute.Register> {
        // TODO: Reemplazar con RegisterScreenOwner()
        PlaceholderScreen("Register")
    }

    composable<AppRoute.Main> {
        // TODO: Reemplazar con MainScaffoldScreen()
        PlaceholderScreen("Main (con tabs)")
    }
}

/**
 * Pantalla placeholder temporal para desarrollo.
 */
@Composable
private fun PlaceholderScreen(name: String) {
    Text(text = "Pantalla: $name")
}
