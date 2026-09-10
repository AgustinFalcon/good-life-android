package com.agusstkd.goodlife.presentation.screen.tabs.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.domain.usecase.media.UploadProfilePhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val isUploading: Boolean = false,
    val uploadError: String? = null
)

class ProfileViewModel(
    private val userDao: UserDao,
    private val uploadProfilePhotoUseCase: UploadProfilePhotoUseCase
) : ViewModel() {

    private val _uploadState = MutableStateFlow(ProfileUploadState())

    val uiState = combine(userDao.observeUser(), _uploadState) { entity, upload ->
        ProfileUiState(
            name = entity?.name ?: "",
            email = entity?.email ?: "",
            profileImageUrl = entity?.profileImageUrl,
            isUploading = upload.isUploading,
            uploadError = upload.error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun uploadPhoto(imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = ProfileUploadState(isUploading = true)
            when (val result = uploadProfilePhotoUseCase(imageUri)) {
                is Result.Success -> {
                    val current = userDao.getUser() ?: return@launch
                    userDao.insertUser(current.copy(profileImageUrl = result.data))
                    _uploadState.value = ProfileUploadState()
                }
                is Result.Error -> {
                    _uploadState.value = ProfileUploadState(
                        error = result.exception.message ?: "Error al subir la foto"
                    )
                }
            }
        }
    }

    fun clearError() { _uploadState.update { it.copy(error = null) } }

    private data class ProfileUploadState(
        val isUploading: Boolean = false,
        val error: String? = null
    )
}