package com.filip.babic.newgamesplus

import android.app.Application
import com.filip.babic.data.di.networkingModule
import com.filip.babic.device.di.appModule
import com.filip.babic.newgamesplus.di.module.repositoryModule
import com.filip.babic.newgamesplus.di.module.useCaseModule
import com.filip.babic.newgamesplus.di.module.viewModelModule
import com.filip.babic.newgamesplus.lifecycle.CustomLifecycleHandler
import com.squareup.leakcanary.LeakCanary
import org.koin.android.ext.android.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin(this, listOf(appModule(), networkingModule(BuildConfig.DEBUG), repositoryModule, useCaseModule, viewModelModule))

        registerActivityLifecycleCallbacks(CustomLifecycleHandler())

        LeakCanary.install(this)
    }
}