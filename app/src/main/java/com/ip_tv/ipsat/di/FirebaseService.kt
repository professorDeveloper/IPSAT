/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.di

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.ip_tv.ipsat.domain.model.AppUpdate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
    class FirebaseService @Inject constructor(
        private val firebaseDatabase: FirebaseDatabase
    ) {
        private val appUpdateRef = firebaseDatabase.reference.child("appUpdate")

        fun getAppUpdateInfo(): LiveData<AppUpdate?> {
            val appUpdateLiveData = MutableLiveData<AppUpdate?>()

            appUpdateRef.get().addOnSuccessListener { snapshot ->
                val appUpdate = snapshot.getValue(AppUpdate::class.java)
                Log.d("GGG", "compareVersions:${appUpdate?.version} ")
                appUpdateLiveData.postValue(appUpdate)
            }.addOnFailureListener { exception ->
                appUpdateLiveData.postValue(null)
            }

            return appUpdateLiveData
        }
    }