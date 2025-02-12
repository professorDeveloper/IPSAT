package com.ip_tv.ipsat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ip_tv.ipsat.utils.readData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
) : ViewModel() {
    private var _currentTheme = MutableLiveData<Int>()
    val currentTheme get() = _currentTheme

    private var _isAppLocked = MutableLiveData<Boolean>()
    val isAppLocked get() = _isAppLocked

    init {

        _currentTheme.value = readData("current_theme", toast = false) ?: 0
        _isAppLocked.value = readData("is_app_locked", toast = false) ?: false
    }
}