package com.promecarus.storyapp.custom.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.promecarus.storyapp.R.drawable.ic_email
import com.promecarus.storyapp.R.string.email
import com.promecarus.storyapp.R.string.error_email_address_cannot_be_empty
import com.promecarus.storyapp.R.string.error_email_address_invalid
import com.promecarus.storyapp.utils.EditTextUtils.initEditText
import com.promecarus.storyapp.utils.EditTextUtils.isEmailNotValid
import com.promecarus.storyapp.utils.EditTextUtils.isInputEmpty

@SuppressLint("InlinedApi")
class EditTextEmail(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    init {
        initEditText(
            autofillHint = AUTOFILL_HINT_EMAIL_ADDRESS,
            context = context,
            editText = this,
            hintResId = email,
            iconResId = ic_email,
            inputType = TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
        )

        addTextChangedListener {
            error = when {
                isInputEmpty(it) -> context.getString(error_email_address_cannot_be_empty)

                isEmailNotValid(it) -> context.getString(error_email_address_invalid)

                else -> null
            }
        }
    }
}
