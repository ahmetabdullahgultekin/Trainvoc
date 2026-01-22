package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.api.DictionaryApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Network Module for Hilt Dependency Injection
 *
 * Provides:
 * - Retrofit instance configured for Free Dictionary API
 * - OkHttpClient with logging
 * - DictionaryApiService
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.dictionaryapi.dev/api/v2/"
    private const val TIMEOUT_SECONDS = 30L

    /**
     * Provides Gson instance for JSON serialization/deserialization
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    /**
     * Provides OkHttpClient with logging interceptor
     *
     * Logging is enabled in DEBUG builds only for privacy
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (com.gultekinahmetabdullah.trainvoc.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Provides Retrofit instance for Free Dictionary API
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides DictionaryApiService instance
     */
    @Provides
    @Singleton
    fun provideDictionaryApiService(retrofit: Retrofit): DictionaryApiService {
        return retrofit.create(DictionaryApiService::class.java)
    }
}
