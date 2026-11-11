package com.agusstkd.goodlife.presentation.screen.tabs.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agusstkd.goodlife.core.datetime.language.Spanish
import com.agusstkd.goodlife.core.dispatcher.TestDispatcherProvider
import com.agusstkd.goodlife.core.result.Result
import com.agusstkd.goodlife.domain.model.auth.User
import com.agusstkd.goodlife.domain.model.media.LocalImageRef
import com.agusstkd.goodlife.domain.usecase.home.ObserveCurrentUserUseCase
import com.agusstkd.goodlife.domain.usecase.media.UploadProfilePhotoUseCase
import com.agusstkd.goodlife.fake.FakeAuthRepository
import com.agusstkd.goodlife.fake.FakeMediaRepository
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private lateinit var authRepository: FakeAuthRepository
    private lateinit var mediaRepository: FakeMediaRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        authRepository = FakeAuthRepository()
        mediaRepository = FakeMediaRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun vm() = ProfileViewModel(
        observeCurrentUser = ObserveCurrentUserUseCase(authRepository),
        uploadProfilePhotoUseCase = UploadProfilePhotoUseCase(
            mediaRepository = mediaRepository,
            authRepository = authRepository,
            dispatcher = TestDispatcherProvider(dispatcher),
        ),
        language = Spanish,
    )

    @Test
    fun `maps current user name and email`() = runTest(dispatcher) {
        authRepository.storedUser.value = User(
            id = "user-9",
            email = "ada@goodlife.app",
            name = "Ada Lovelace",
            profileImageUrl = "https://cdn.goodlife.app/ada.jpg",
        )
        val viewModel = vm()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Ada Lovelace", state.name)
        assertEquals("ada@goodlife.app", state.email)
        assertEquals("https://cdn.goodlife.app/ada.jpg", state.profileImageUrl)
    }

    @Test
    fun `upload success updates image via repository`() = runTest(dispatcher) {
        mediaRepository.uploadResult = Result.Success("https://cdn.goodlife.app/new.jpg")
        val viewModel = vm()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.uploadPhoto(LocalImageRef("content://media/picker/1"))
        advanceUntilIdle()

        assertEquals("https://cdn.goodlife.app/new.jpg", viewModel.uiState.value.profileImageUrl)
        assertEquals("https://cdn.goodlife.app/new.jpg", authRepository.storedUser.value?.profileImageUrl)
        assertEquals("content://media/picker/1", mediaRepository.lastImage?.value)
        assertEquals("profile", mediaRepository.lastFolder)
        assertNull(viewModel.uiState.value.uploadError)
        assertEquals(false, viewModel.uiState.value.isUploading)
    }

    @Test
    fun `upload error uses localized copy not exception message`() = runTest(dispatcher) {
        val backendDetail = "Cloudinary 500 stacktrace"
        mediaRepository.uploadResult = Result.Error(Exception(backendDetail))
        val viewModel = vm()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.uploadPhoto(LocalImageRef("content://media/picker/1"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(Spanish.errorTexts.dataLoadError, state.uploadError)
        assertTrue(state.uploadError != backendDetail)
        assertNull(authRepository.storedUser.value?.profileImageUrl)
    }

    @Test
    fun `network upload failure uses connection copy`() = runTest(dispatcher) {
        mediaRepository.uploadResult = Result.Error(IOException("offline"))
        val viewModel = vm()
        backgroundScope.launch { viewModel.uiState.collect {} }
        advanceUntilIdle()

        viewModel.uploadPhoto(LocalImageRef("content://media/picker/1"))
        advanceUntilIdle()

        assertEquals(Spanish.errorTexts.connectionError, viewModel.uiState.value.uploadError)
    }
}
