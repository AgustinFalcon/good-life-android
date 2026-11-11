package com.agusstkd.goodlife.presentation.screen.tabs.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.datetime.language.AppLanguage
import com.agusstkd.goodlife.core.datetime.language.ProfileTexts
import com.agusstkd.goodlife.domain.model.media.LocalImageRef
import com.agusstkd.goodlife.domain.usecase.home.ObserveCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.media.UploadProfilePhotoUseCase
import com.agusstkd.goodlife.domain.usecase.media.result.UploadProfilePhotoResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de perfil.
 *
 * @property name Nombre del usuario autenticado.
 * @property email Correo del usuario autenticado.
 * @property profileImageUrl URL de la foto de perfil, o `null` si no hay.
 * @property isUploading `true` mientras se sube una foto.
 * @property uploadError Copy localizado del último error de subida, o `null`.
 */
@Immutable
data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null
)

/**
 * ViewModel de perfil: observa el usuario autenticado y sube la foto de perfil.
 *
 * No accede a repositorios. El usuario llega por [ObserveCurrentUserUseCase].
 */
class ProfileViewModel(
    private val observeCurrentUser: ObserveCurrentUserUseCase,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase,
    private val language: AppLanguage,
) : ViewModel() {

    /** Copy localizado de la pantalla de perfil. */
    val profileTexts: ProfileTexts get() = language.profileTexts

    private val _uploadState = MutableStateFlow(ProfileUploadState())

    /** Estado combinado del usuario persistido y de la subida de foto. */
    val uiState = combine(observeCurrentUser(), _uploadState) { user, upload ->
        ProfileUiState(
            name = user?.name ?: "",
            email = user?.email ?: "",
            profileImageUrl = user?.profileImageUrl,
            isUploading = upload.isUploading,
            uploadError = upload.error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    /**
     * Sube la foto seleccionada y persiste la URL resultante en el usuario local.
     *
     * @param image Referencia local a la imagen (content URI como string).
     */
    fun uploadPhoto(image: LocalImageRef) {
        viewModelScope.launch {
            _uploadState.value = ProfileUploadState(isUploading = true)
            _uploadState.value = when (uploadProfilePhotoUseCase(image)) {
                UploadProfilePhotoResult.Success -> ProfileUploadState()
                UploadProfilePhotoResult.NetworkError -> ProfileUploadState(
                    error = language.errorTexts.connectionError,
                )
                UploadProfilePhotoResult.ServerError -> ProfileUploadState(
                    error = language.errorTexts.dataLoadError,
                )
            }
        }
    }

    /**
     * Limpia el error de subida después de mostrarlo.
     */
    fun clearError() { _uploadState.update { it.copy(error = null) } }

    private data class ProfileUploadState(
        val isUploading: Boolean = false,
        val error: String? = null
    )
}
