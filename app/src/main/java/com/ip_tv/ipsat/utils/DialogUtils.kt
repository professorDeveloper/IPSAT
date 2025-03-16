/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.utils

import android.app.ActionBar
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import com.ip_tv.ipsat.R
import com.tapadoo.alerter.Alerter

object DialogUtils {

    fun loadingDialog(ctx: Context): Dialog {
        val dialog = Dialog(ctx)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setLayout(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
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