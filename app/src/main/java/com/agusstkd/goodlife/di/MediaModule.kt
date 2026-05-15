package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.local.dao.UserDao
import com.agusstkd.goodlife.data.remote.api.media.MediaApiService
import com.agusstkd.goodlife.data.repository.MediaRepositoryImpl
import com.agusstkd.goodlife.domain.repository.MediaRepository
import com.agusstkd.goodlife.domain.usecase.media.UploadProfilePhotoUseCase
import com.agusstkd.goodlife.presentation.screen.tabs.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val mediaModule = module {
    single<MediaRepository> {
        MediaRepositoryImpl(
            mediaApiService = get<Retrofit>().create(MediaApiService::class.java),
            context = androidContext()
        )
    }
    factory { UploadProfilePhotoUseCase(get()) }
    viewModel { ProfileViewModel(userDao = get<UserDao>(), uploadProfilePhotoUseCase = get()) }
}
