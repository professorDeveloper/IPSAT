package com.ip_tv.ipsat.di

import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ip_tv.ipsat.data.local.dao.MovieDao
import com.ip_tv.ipsat.data.local.database.AppDatabase
import com.ip_tv.ipsat.data.remote.AuthService
import com.ip_tv.ipsat.data.remote.DetailService
import com.ip_tv.ipsat.data.remote.LiveTvService
import com.ip_tv.ipsat.data.remote.MovieService
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://141.94.26.102:8000/"


    @[Provides Singleton]
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        appReference: UserPreferenceManager,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .retryOnConnectionFailure(true) // Agar bog‘lanish uzilsa, qayta urinish
            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .connectionPool(ConnectionPool(1, 1, TimeUnit.NANOSECONDS)) // Keep-Alive ni o‘chiradi
            .connectTimeout(60, TimeUnit.SECONDS) // Default: 10s
            .readTimeout(60, TimeUnit.SECONDS)    // Default: 10s
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }


    @[Provides Singleton]
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @[Provides Singleton]
    fun provideAuthApi(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @[Provides Singleton]
    fun provideLiveTvApi(retrofit: Retrofit): LiveTvService =
        retrofit.create(LiveTvService::class.java)


    @[Provides Singleton]
    fun provideHomeApi(retrofit: Retrofit): MovieService =
        retrofit.create(MovieService::class.java)


    @[Provides Singleton]
    fun provideDetailApi(retrofit: Retrofit): DetailService =
        retrofit.create(DetailService::class.java)


}