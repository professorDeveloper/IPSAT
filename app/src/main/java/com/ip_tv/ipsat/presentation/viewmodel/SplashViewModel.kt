package com.ip_tv.ipsat.presentation.viewmodel

import Resource
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.app.App
import com.ip_tv.ipsat.di.FirebaseService
import com.ip_tv.ipsat.domain.model.AppUpdate
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.domain.preference.UserPreferenceManager
import com.ip_tv.ipsat.domain.usecase.CheckSubscribeUseCase
import com.ip_tv.ipsat.utils.AppUtils
import com.ip_tv.ipsat.utils.AuthState
import com.ip_tv.ipsat.utils.currContext
import com.ip_tv.ipsat.utils.readData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val useCase: CheckSubscribeUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val firebaseService: FirebaseService
) : ViewModel() {
    private val _initSplash = MutableLiveData<Resource<SubscriptionResponse>>(Resource.Idle)
    private val _isFirst = MutableLiveData<Unit>()
    val initSplash = _initSplash
    val isFirst = _isFirst

    private val _isLocked = MutableLiveData<Boolean>()
    val isLocked = _isLocked
    val isUpdateAvailableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val getAppUpdateInfo: MutableLiveData<AppUpdate> = MutableLiveData()

    init {
        checkForAppUpdate()
        loadLock()
    }

    private fun checkForAppUpdate() {
        firebaseService.getAppUpdateInfo().observeForever { appUpdate ->
            if (appUpdate != null) {
                if (isUpdateAvailable(
                        AppUtils.getAppVersion(context = App.currentContext()!!)!!,
                        appUpdate.version
                    )
                ) {

                    isUpdateAvailableLiveData.postValue(true)
                } else {
                    isUpdateAvailableLiveData.postValue(false)
                }
            } else {
                isUpdateAvailableLiveData.postValue(false)
            }
        }
    }

    fun getAppUpdateInfo() {

        firebaseService.getAppUpdateInfo().observeForever { appUpdate ->
            appUpdate?.let {
                getAppUpdateInfo.postValue(appUpdate!!)
            }
        }
    }

    private fun isUpdateAvailable(currentVersion: String, newVersion: String?): Boolean {
        return if (newVersion != null) {
            compareVersions(currentVersion, newVersion) < 0
        } else {
            false
        }
    }

    private fun compareVersions(version1: String, version2: String): Int {
        val version1Parts = version1.split(".").map { it.toInt() }
        val version2Parts = version2.split(".").map { it.toInt() }

        for (i in 0 until Math.min(version1Parts.size, version2Parts.size)) {
            val comparison = version1Parts[i].compareTo(version2Parts[i])
            if (comparison != 0) {
                return comparison
            }
        }
        Log.d("GGG", "compareVersions:${version1Parts} ${version2Parts} ")
        return version1Parts.size.compareTo(version2Parts.size)
    }


    private fun loadLock() {
        _isLocked.value = readData("isLocked", currContext()) ?: false
    }


    fun checkSubscribe() {
        if (userPreferenceManager.isLogged) {
            _initSplash.value = Resource.Loading
            useCase.checkSubscribe().onEach {
                it.onFailure {
                    _initSplash.value = Resource.Error(
                        Exception(it.message)
                    )
                }

                it.onSuccess {
                    _initSplash.value = Resource.Success(it)

                }
            }.launchIn(viewModelScope)
        } else {
            isFirst.postValue(Unit)
        }
    }

}