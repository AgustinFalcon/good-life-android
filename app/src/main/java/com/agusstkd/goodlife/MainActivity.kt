package com.agusstkd.goodlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.agusstkd.goodlife.presentation.navigation.core.ComposeNavigationController
import com.agusstkd.goodlife.presentation.navigation.host.SmartNavHost
import com.agusstkd.goodlife.presentation.navigation.route.AppRoute
import com.agusstkd.goodlife.presentation.navigation.route.addAppGraph
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import org.koin.android.ext.android.inject

/**
 * Activity principal de la aplicación.
 *
 * Implementa Single Activity Architecture.
 * Toda la navegación se maneja con Compose Navigation.
 */
class MainActivity : ComponentActivity() {

    private val navigationController: ComposeNavigationController by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GoodLifeTheme {
                val navController = rememberNavController()

                SmartNavHost(
                    navController = navController,
                    navigationController = navigationController,
                    startDestination = AppRoute.Splash,
                    graphBuilder = { addAppGraph() }
                )
            }
        }
    }
}
