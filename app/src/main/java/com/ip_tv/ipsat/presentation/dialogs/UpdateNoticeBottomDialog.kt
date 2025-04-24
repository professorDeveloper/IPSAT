/*
 * Copyright (C) 2025 Azamov . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 */

package com.ip_tv.ipsat.presentation.dialogs

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ip_tv.ipsat.R
import com.ip_tv.ipsat.databinding.AlertUpdateProblemSheetBinding

class UpdateNoticeBottomDialog : BottomSheetDialogFragment() {

    private var _binding: AlertUpdateProblemSheetBinding? = null
    private val binding get() = _binding!!

    private var understandBtnClickCallback: (() -> Unit)? = null
    fun setUnderstandBtnClickCallback(callback: () -> Unit) {
        understandBtnClickCallback = callback
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlertUpdateProblemSheetBinding.inflate(inflater, container, false)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.okBtn.setOnClickListener {
            understandBtnClickCallback?.invoke()
        }
        applyWarningUpdateDesc()

    }

    private fun applyWarningUpdateDesc() {
        val raw = getString(R.string.warning_update_desc)
        val regex = "\\*(.*?)\\*".toRegex()
        val match = regex.find(raw)

        if (match != null) {
            val inner = match.groupValues[1]              // "Downloads/IPSAT"
            val clean = raw.replace("*$inner*", inner)    // strip the *'s
            val ss = SpannableString(clean)
            val start = clean.indexOf(inner)
            val end = start + inner.length

            ss.setSpan(
                StyleSpan(Typeface.BOLD),
                start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val color = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
            ss.setSpan(
                ForegroundColorSpan(color),
                start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.updateText.text = ss
        } else {
            binding.updateText.text = raw
        }
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
