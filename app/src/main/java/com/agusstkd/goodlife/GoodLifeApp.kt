package com.agusstkd.goodlife

import android.app.Application
import com.agusstkd.goodlife.di.authModule
import com.agusstkd.goodlife.di.biometricModule
import com.agusstkd.goodlife.di.coreModule
import com.agusstkd.goodlife.di.dailyModule
import com.agusstkd.goodlife.di.databaseModule
import com.agusstkd.goodlife.di.networkModule
import com.agusstkd.goodlife.di.habitModule
import com.agusstkd.goodlife.di.taskModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Clase Application de GoodLife.
 *
 * Inicializa Koin con todos los módulos de la app.
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
     * ## Orden de módulos
     * El orden importa: un módulo solo puede resolver dependencias de módulos
     * que aparezcan ANTES en la lista.
     *
     * | Módulo          | Qué provee                                        |
     * |-----------------|---------------------------------------------------|
     * | [networkModule] | Retrofit, OkHttp, ApiServices, TokenManager        |
     * | [databaseModule]| Room, DAOs                                         |
     * | [biometricModule]| BiometricAuthenticator, SecureCredentialsStorage  |
     * | [coreModule]    | DispatcherProvider, Navigation, DateProvider, Lang |
     * | [authModule]    | Auth DataSource/Repository/UseCases/ViewModels    |
     * | [dailyModule]   | Daily DataSource/Repository/UseCases/ViewModel    |
     * | [taskModule]    | Task DataSource/Repository/UseCase/ViewModel      |
     * | [habitModule]   | Habit DataSource/Repository/UseCase/ViewModel     |
     */
    private fun initKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@GoodLifeApp)
            modules(
                networkModule,
                databaseModule,
                biometricModule,
                coreModule,
                authModule,
                dailyModule,
                taskModule,
                habitModule,
            )
        }
    }
}
