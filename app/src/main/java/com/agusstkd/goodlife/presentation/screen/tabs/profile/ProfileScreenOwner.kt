package com.agusstkd.goodlife.presentation.screen.tabs.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
 * Owner de perfil: inyecta el ViewModel, el picker de imagen y los side-effects.
 */
@Composable
fun ProfileScreenOwner(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadPhoto(it) }
    }

    LaunchedEffect(state.uploadError) {
        state.uploadError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    ProfileScreen(
        state = state,
        onChangePhoto = { imagePicker.launch("image/*") },
        snackbarHostState = snackbarHostState,
    )
}
