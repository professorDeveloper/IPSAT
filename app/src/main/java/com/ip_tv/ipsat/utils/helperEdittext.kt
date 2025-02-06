package com.ip_tv.ipsat.utils

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.EditText

fun EditText.setupPhoneNumberEditText(
    onChangedToEnable: () -> Unit,
    onChangedToDisable: () -> Unit
) {
    val prefix = "+998 "
    var isDeleting = false
    var focusListenerEnabled = true

    val phoneMaskLength = 13
    val totalMaxLength = prefix.length + phoneMaskLength


    this.filters = arrayOf(InputFilter.LengthFilter(totalMaxLength))

    this.addTextChangedListener(object : TextWatcher {
        private var isUpdating: Boolean = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            isDeleting = count > after
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (isUpdating) return

            var input = s.toString()

            if (input.length < prefix.length && isDeleting) {
                text.clear()
                return
            }

            input = input.removePrefix(prefix)
            val cleanInput = input.replace(Regex("[^\\d]"), "")

            val formattedText = StringBuilder(prefix)

            if (cleanInput.isNotEmpty()) {
                formattedText.append(cleanInput.substring(0, Math.min(2, cleanInput.length)))
            }
            if (cleanInput.length > 2) {
                formattedText.append("-")
                    .append(cleanInput.substring(2, Math.min(5, cleanInput.length)))
            }
            if (cleanInput.length > 5) {
                formattedText.append("-")
                    .append(cleanInput.substring(5, Math.min(7, cleanInput.length)))
            }
            if (cleanInput.length > 7) {
                formattedText.append("-")
                    .append(cleanInput.substring(7, Math.min(9, cleanInput.length)))
            }

            isUpdating = true
            this@setupPhoneNumberEditText.setText(formattedText)
            this@setupPhoneNumberEditText.setSelection(formattedText.length)
            isUpdating = false

            focusListenerEnabled = if (formattedText.length == 17) {
                onChangedToEnable()
                false
            }else{
                onChangedToDisable()
                true

            }
        }

        override fun afterTextChanged(s: Editable?) {}
    })

    this.setOnFocusChangeListener { _, hasFocus ->
        if (focusListenerEnabled && hasFocus) {
            this.setText(prefix)
            this.setSelection(this.text.length)
        }

    }
}

