package com.agusstkd.goodlife.presentation.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.agusstkd.goodlife.R
import com.agusstkd.goodlife.presentation.components.common.BackgroundGradientComponent
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiAction
import com.agusstkd.goodlife.presentation.screen.splash.model.SplashUiState
import com.agusstkd.goodlife.presentation.theme.AppTitleStyle
import com.agusstkd.goodlife.presentation.theme.DarkGreen
import com.agusstkd.goodlife.presentation.theme.GoodLifeTheme
import com.agusstkd.goodlife.presentation.theme.LightGreen
import com.agusstkd.goodlife.presentation.theme.LinkTextStyle

@Composable
fun SplashScreen(
    uiState: SplashUiState,
    onAction: (SplashUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    BackgroundGradientComponent(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.good_life_logo),
                contentDescription = "Logo GoodLife",
                modifier = Modifier.size(250.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "GoodLife",
                style = AppTitleStyle,
                color = DarkGreen,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(48.dp))

            when (uiState) {
                is SplashUiState.Loading -> {
                    CircularProgressIndicator(
                        color = DarkGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }

                is SplashUiState.Ready -> {
                    Text(
                        text = "Toca para continuar",
                        style = LinkTextStyle,
                        color = LightGreen,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            onAction(SplashUiAction.ContinueClicked)
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
fun SplashScreenLoadingPreview() {
    GoodLifeTheme {
        SplashScreen(
            uiState = SplashUiState.Loading,
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Ready")
@Composable
fun SplashScreenReadyPreview() {
    GoodLifeTheme {
        SplashScreen(
            uiState = SplashUiState.Ready,
            onAction = {}
        )
    }
}
