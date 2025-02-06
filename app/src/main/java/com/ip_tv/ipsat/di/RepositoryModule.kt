package com.ip_tv.ipsat.di

import com.ip_tv.ipsat.data.repository.AuthRepositoryImpl
import com.ip_tv.ipsat.data.repository.DetailRepositoryImpl
import com.ip_tv.ipsat.data.repository.HomeRepositoryImpl
import com.ip_tv.ipsat.domain.repository.AuthRepository
import com.ip_tv.ipsat.domain.repository.DetailRepository
import com.ip_tv.ipsat.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
    @Binds
    abstract fun bindHomeRepository(
        homeRepository: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    abstract fun bindDetailRepository(
        detailRepository: DetailRepositoryImpl
    ): DetailRepository

}