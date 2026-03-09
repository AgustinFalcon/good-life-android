package com.agusstkd.goodlife.di

import com.agusstkd.goodlife.data.local.database.GoodLifeDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single { GoodLifeDatabase.getInstance(androidContext()) }

    single { get<GoodLifeDatabase>().userDao() }

    single { get<GoodLifeDatabase>().dailyDao() }
}
