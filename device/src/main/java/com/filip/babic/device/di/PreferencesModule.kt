package com.filip.babic.device.di

import android.content.Context
import org.koin.dsl.module.module

private const val KEY_PREFERENCES = "NewGamesPlus-SharedPreferences"

fun appModule() = module {
    single { get<Context>().getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE) }
}