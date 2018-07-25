package com.babic.filip.networking.data.di

import com.filip.babic.device.repository.UserPreferencesRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL: String = "http://api.openweathermap.org/"
private const val BASE_URL_DEBUG: String = "http://10.0.2.2:3000"

private const val KEY_AUTHORIZATION = "authorization"

fun networkingModule(isDebug: Boolean) = module {
    single { if (isDebug) BASE_URL_DEBUG else BASE_URL }

    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }

    single {
        val interceptor = Interceptor {
            val preferences = get<UserPreferencesRepository>()

            val request = it.request()

            val auth = request.newBuilder()
                    .addHeader(KEY_AUTHORIZATION, preferences.getToken())
                    .build()

            it.proceed(auth)
        }

        interceptor
    }

    single {
        OkHttpClient.Builder().apply {
            if (isDebug) {
                addInterceptor(get<HttpLoggingInterceptor>())
            }

            addInterceptor(get())
        }.build()
    }

    single { GsonConverterFactory.create() }

    single {
        Retrofit.Builder()
                .baseUrl(get<String>())
                .client(get())
                .addConverterFactory(get<GsonConverterFactory>())
                .build()
    }
}