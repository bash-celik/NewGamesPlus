package com.filip.babic.newgamesplus

import android.app.Application
import com.babic.filip.core.di.coreModule
import com.babic.filip.gamedetails.di.gameDetailsModule
import com.babic.filip.login.di.loginModule
import com.babic.filip.main.di.mainModule
import com.babic.filip.networking.data.di.networkingModule
import com.babic.filip.register.di.registerModule
import com.babic.filip.splash.di.splashModule
import com.filip.babic.device.di.deviceModule
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

        startKoin(this, coreModules + featureModules)

        registerActivityLifecycleCallbacks(CustomLifecycleHandler())
        LeakCanary.install(this)
    }

    private val coreModules = listOf(
            coreModule,
            networkingModule(BuildConfig.DEBUG)
    )

    private val featureModules = listOf(
            registerModule,
            splashModule,
            loginModule,
            deviceModule,
            mainModule,
            gameDetailsModule
    )
}