/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.dialogs

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.ExitDialogBinding
import com.ip_tv.ipsat.domain.model.SubscriptionResponse
import com.ip_tv.ipsat.utils.toDateFromIso8601ForTxt

class ExitDialog(val data: SubscriptionResponse, val subCode: String) : DialogFragment() {

    private var _binding: ExitDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var noClearListener: () -> Unit

    private lateinit var yesContinueListener: () -> Unit

    fun setNoClearListener(listener: () -> Unit) {
        noClearListener = listener
    }

    fun setYesContinueListener(listener: () -> Unit) {
        yesContinueListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ExitDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog!!.window?.setWindowAnimations(R.style.DialogAnimation)
        binding.notNowBtn.setOnClickListener {
            noClearListener.invoke()
        }
        binding.yesExit.setOnClickListener {
            yesContinueListener.invoke()
        }
        binding.subCode.text = subCode
        binding.timeLast.text = "Expire date: ${data.endDate.toString().toDateFromIso8601ForTxt()}"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
