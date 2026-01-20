package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.domain.biometric.BiometricAuthenticator
import com.agusstkd.goodlife.domain.storage.SecureCredentialsStorage
import com.agusstkd.goodlife.platform.biometric.AndroidBiometricAuthenticator
import com.agusstkd.goodlife.platform.storage.AndroidSecureCredentialsStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Módulo Koin para Biometría - Android.
 * 
 * KMP: En iOS este módulo usaría IosBiometricAuthenticator e IosSecureCredentialsStorage.
 */
val biometricModule = module {
    single<BiometricAuthenticator> { AndroidBiometricAuthenticator(androidContext()) }
    single<SecureCredentialsStorage> { AndroidSecureCredentialsStorage(androidContext()) }
}
