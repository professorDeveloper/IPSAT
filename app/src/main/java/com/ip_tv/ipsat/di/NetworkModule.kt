package com.ip_tv.ipsat.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.ip_tv.ipsat.data.remote.AuthService
import com.ip_tv.ipsat.data.remote.DetailService
import com.ip_tv.ipsat.data.remote.MovieService
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(ChuckerInterceptor.Builder(context).build())
            .build()
    }

    @[Provides Singleton ]
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @[Provides Singleton]
    fun provideAuthApi(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)


    @[Provides Singleton]
    fun provideHomeApi(retrofit: Retrofit): MovieService =
        retrofit.create(MovieService::class.java)


    @[Provides Singleton]
    fun provideDetailApi(retrofit: Retrofit): DetailService =
        retrofit.create(DetailService::class.java)


}