package com.agusstkd.goodlife.presentation.screen.tabs.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.data.local.entity.toDomain
import com.agusstkd.goodlife.data.local.entity.toEntity
import com.agusstkd.goodlife.domain.usecase.media.UploadProfilePhotoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    private val _uploadState = MutableStateFlow(false to (null as String?))

    val uiState = userDao.observeUser()
        .map { entity ->
            ProfileUiState(
                name = entity?.name ?: "",
                email = entity?.email ?: "",
                profileImageUrl = entity?.profileImageUrl,
                isUploading = _uploadState.value.first,
                uploadError = _uploadState.value.second
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    fun uploadPhoto(imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.update { true to null }
            when (val result = uploadProfilePhotoUseCase(imageUri)) {
                is Result.Success -> {
                    val current = userDao.getUser() ?: return@launch
                    userDao.insertUser(current.copy(profileImageUrl = result.data))
                    _uploadState.update { false to null }
                }
                is Result.Error -> {
                    _uploadState.update { false to (result.exception.message ?: "Error al subir la foto") }
                }
            }
        }
    }

    fun clearError() = _uploadState.update { _uploadState.value.first to null }
}
