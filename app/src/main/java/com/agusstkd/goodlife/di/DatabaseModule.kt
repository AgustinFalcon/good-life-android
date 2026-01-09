package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.local.database.GoodLifeDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Módulo de Koin para base de datos Room.
 *
 * Provee:
 * - GoodLifeDatabase: Base de datos principal (via Singleton interno)
 * - DAOs: UserDao (y futuros DAOs)
 */
val databaseModule = module {

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DATABASE
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * GoodLifeDatabase - usa el Singleton interno de la clase.
     * Garantiza una única instancia thread-safe.
     */
    single { GoodLifeDatabase.getInstance(androidContext()) }

    // ═══════════════════════════════════════════════════════════════════════════════════════════
    // DAOs
    // ═══════════════════════════════════════════════════════════════════════════════════════════

    /**
     * UserDao - Operaciones de usuario.
     */
    single { get<GoodLifeDatabase>().userDao() }

    // TODO: Agregar más DAOs cuando se implementen
    // single { get<GoodLifeDatabase>().taskDao() }
    // single { get<GoodLifeDatabase>().habitDao() }
    // single { get<GoodLifeDatabase>().routineDao() }
    // single { get<GoodLifeDatabase>().mealDao() }
}
