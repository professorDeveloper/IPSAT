/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.ip_tv.ipsat.R
import com.tapadoo.alerter.Alerter

object DialogUtils {

    fun createTapadooDialog(
        activity: Activity,
        title: String? = null,
        message: String? = null,
        color: Int
    ) {

        if (title != null) {
            Alerter.create(activity = activity)
                .setTitle(title = title)
                .setText(message.toString())
                .setBackgroundColorRes(colorResId = color)
                .show()
        }
    }

}