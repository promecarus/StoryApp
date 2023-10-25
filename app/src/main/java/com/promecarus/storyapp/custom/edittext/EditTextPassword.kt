package com.promecarus.storyapp.custom.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.promecarus.storyapp.R.drawable.ic_lock
import com.promecarus.storyapp.R.string.error_password_digit
import com.promecarus.storyapp.R.string.error_password_empty
import com.promecarus.storyapp.R.string.error_password_less_characters
import com.promecarus.storyapp.R.string.error_password_lowercase_letter
import com.promecarus.storyapp.R.string.error_password_special_character
import com.promecarus.storyapp.R.string.error_password_uppercase_letter
import com.promecarus.storyapp.R.string.password
import com.promecarus.storyapp.R.styleable.EditTextPassword
import com.promecarus.storyapp.R.styleable.EditTextPassword_strictValidation
import com.promecarus.storyapp.utils.ActivityUtils.hideKeyboard
import com.promecarus.storyapp.utils.EditTextUtils.initEditText
import com.promecarus.storyapp.utils.EditTextUtils.isInputEmpty
import com.promecarus.storyapp.utils.EditTextUtils.isInputLengthLessThan
import com.promecarus.storyapp.utils.EditTextUtils.isInputNotContainChar

@SuppressLint("InlinedApi")
class EditTextPassword(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private var isValidationStrict: Boolean

    init {
        initEditText(
            autofillHint = AUTOFILL_HINT_PASSWORD,
            context = context,
            editText = this,
            hintResId = password,
            iconResId = ic_lock,
            inputType = TYPE_TEXT_VARIATION_PASSWORD,
        )

        context.obtainStyledAttributes(attrs, EditTextPassword).apply {
            isValidationStrict = getBoolean(EditTextPassword_strictValidation, false)
            recycle()
        }

        addTextChangedListener {
            error = when {
                isInputEmpty(it) -> context.getString(error_password_empty)

                isInputLengthLessThan(it, MIN_PASSWORD_LENGTH) -> context.getString(
                    error_password_less_characters, MIN_PASSWORD_LENGTH
                )

                isValidationStrict -> strictValidation(it)

                else -> null
            }
        }

        setOnEditorActionListener { _, i, _ ->
            if (i == IME_ACTION_DONE) {
                clearFocus()
                hideKeyboard(context, this)
                true
            } else false
        }
    }

    private fun strictValidation(input: Editable?) = when {
        isInputNotContainChar(input, REGEX_LOWERCASE_LETTER) -> context.getString(
            error_password_lowercase_letter
        )

        isInputNotContainChar(input, REGEX_UPPERCASE_LETTER) -> context.getString(
            error_password_uppercase_letter
        )

        isInputNotContainChar(input, REGEX_DIGIT) -> context.getString(
            error_password_digit
        )

        isInputNotContainChar(input, REGEX_SPECIAL_CHARACTER) -> context.getString(
            error_password_special_character
        )

        else -> null
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
        private const val REGEX_LOWERCASE_LETTER = ".*[a-z].*"
        private const val REGEX_UPPERCASE_LETTER = ".*[A-Z].*"
        private const val REGEX_DIGIT = ".*\\d.*"
        private const val REGEX_SPECIAL_CHARACTER = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"
    }
}
