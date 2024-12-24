package com.ip_tv.ipsat.di

import com.ip_tv.ipsat.domain.usecase.LoginUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UseCaseModule {
     @Binds
    fun getLoginUseCase(): LoginUseCase
//
//    @Binds
//    fun getAuthUseCase(imp:AuthUseCaseImp):AuthUseCase
//
//    @Binds
//    fun getContactScreenUseCase(imp: ContactScreenUseCaseImp):ContactScreenUseCase
//
//    @Binds
//    fun getProfileUseCase(imp:ProfileUseCaseImp):ProfileUseCase
//
//    @Binds
//    fun getEditProfileUseCase(imp:EditProfileUseCaseImp):EditProfileUseCase
//
//    @Binds
//    fun getTEchUseCase(allInfoUseCaseImp: AllInfoUseCaseImp):AllInfoScreenUseCase
//    @Binds
//    fun addDriverUseCase(addDriverUseCase: AddDriverUseCaseImp):AddDriverUseCase

}