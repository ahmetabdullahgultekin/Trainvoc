package com.gultekinahmetabdullah.trainvoc.di

import com.gultekinahmetabdullah.trainvoc.api.DictionaryApiService
import com.gultekinahmetabdullah.trainvoc.auth.AuthApiService
import com.gultekinahmetabdullah.trainvoc.auth.FirebaseAuthRepository
import com.gultekinahmetabdullah.trainvoc.multiplayer.data.MultiplayerApi
import com.gultekinahmetabdullah.trainvoc.offline.SyncApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Network Module for Hilt Dependency Injection
 *
 * Provides:
 * - Retrofit instance configured for Free Dictionary API
 * - Retrofit instance configured for Trainvoc Backend API (with Firebase auth)
 * - OkHttpClient with logging and auth interceptor
 * - DictionaryApiService
 * - AuthApiService
 * - MultiplayerApi
 * - SyncApiService
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val DICTIONARY_BASE_URL = "https://api.dictionaryapi.dev/api/v2/"
    private const val TIMEOUT_SECONDS = 30L

    // Backend URL from BuildConfig (configurable per build type)
    private val TRAINVOC_BASE_URL: String
        get() = com.gultekinahmetabdullah.trainvoc.BuildConfig.API_BASE_URL

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
     * Provides Firebase Auth Repository
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthRepository(): FirebaseAuthRepository {
        return FirebaseAuthRepository()
    }

    /**
     * Provides OkHttpClient with logging interceptor (for Dictionary API - no auth needed)
     */
    @Provides
    @Singleton
    @Named("dictionary")
    fun provideDictionaryOkHttpClient(): OkHttpClient {
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
     * Provides OkHttpClient with Firebase auth interceptor (for Trainvoc Backend)
     */
    @Provides
    @Singleton
    @Named("trainvoc")
    fun provideTrainvocOkHttpClient(
        firebaseAuthRepository: FirebaseAuthRepository
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (com.gultekinahmetabdullah.trainvoc.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // Auth interceptor that adds Firebase ID token to requests
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            // Skip auth for public endpoints
            val path = originalRequest.url.encodedPath
            val requiresAuth = !path.contains("/api/v1/auth/login") &&
                    !path.contains("/api/v1/auth/register") &&
                    !path.contains("/api/v1/auth/check-")

            val request = if (requiresAuth) {
                // Get Firebase ID token (blocking call, but interceptor is on IO thread)
                val token = runBlocking {
                    firebaseAuthRepository.getIdToken()
                }

                if (token != null) {
                    originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    originalRequest
                }
            } else {
                originalRequest
            }

            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
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
    @Named("dictionary")
    fun provideDictionaryRetrofit(
        @Named("dictionary") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DICTIONARY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides Retrofit instance for Trainvoc Backend API
     */
    @Provides
    @Singleton
    @Named("trainvoc")
    fun provideTrainvocRetrofit(
        @Named("trainvoc") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(TRAINVOC_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides DictionaryApiService instance
     */
    @Provides
    @Singleton
    fun provideDictionaryApiService(@Named("dictionary") retrofit: Retrofit): DictionaryApiService {
        return retrofit.create(DictionaryApiService::class.java)
    }

    /**
     * Provides SyncApiService instance
     */
    @Provides
    @Singleton
    fun provideSyncApiService(@Named("trainvoc") retrofit: Retrofit): SyncApiService {
        return retrofit.create(SyncApiService::class.java)
    }

    /**
     * Provides AuthApiService instance
     */
    @Provides
    @Singleton
    fun provideAuthApiService(@Named("trainvoc") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    /**
     * Provides MultiplayerApi instance
     */
    @Provides
    @Singleton
    fun provideMultiplayerApi(@Named("trainvoc") retrofit: Retrofit): MultiplayerApi {
        return retrofit.create(MultiplayerApi::class.java)
    }
}
