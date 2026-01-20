package com.agusstkd.goodlife

import android.app.Application
import com.agusstkd.goodlife.di.appModule
import com.agusstkd.goodlife.di.biometricModule
import com.agusstkd.goodlife.di.databaseModule
import com.agusstkd.goodlife.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Clase Application de GoodLife.
 *
 * Inicializa Koin y otras librerías globales.
 * Debe registrarse en AndroidManifest.xml con android:name.
 */
class GoodLifeApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    /**
     * Inicializa el contenedor de dependencias Koin.
     *
     * Módulos:
     * - [networkModule]: Retrofit, OkHttp, ApiService, TokenManager
     * - [databaseModule]: Room Database, DAOs
     * - [biometricModule]: BiometricAuthenticator, SecureCredentialsStorage
     * - [appModule]: Repositories, UseCases, ViewModels
     */
    private fun initKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@GoodLifeApp)
            modules(
                networkModule,
                databaseModule,
                biometricModule,
                appModule
            )
        }
    }
}
