package com.promecarus.storyapp.utils

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.text.Editable
import android.text.InputType.TYPE_CLASS_TEXT
import android.util.Patterns.EMAIL_ADDRESS
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatEditText
import com.promecarus.storyapp.R.dimen.padding_small
import com.promecarus.storyapp.R.drawable.background_edit_text

object EditTextUtils {
    fun initEditText(
        autofillHint: String,
        context: Context,
        editText: AppCompatEditText,
        hintResId: Int,
        iconResId: Int? = null,
        inputType: Int,
    ) {
        if (SDK_INT >= O) editText.setAutofillHints(autofillHint)
        editText.background = getDrawable(context, background_edit_text)
        editText.hint = context.getString(hintResId)
        editText.setCompoundDrawablesWithIntrinsicBounds(iconResId ?: 0, 0, 0, 0)
        editText.setPadding(
            context.resources.getDimensionPixelSize(padding_small),
            editText.paddingTop,
            context.resources.getDimensionPixelSize(padding_small),
            editText.paddingBottom
        )
        editText.compoundDrawablePadding = context.resources.getDimensionPixelSize(padding_small)
        editText.inputType = TYPE_CLASS_TEXT or inputType
    }

    fun isInputEmpty(input: Editable?) = input.toString().isEmpty()

    fun isInputMoreThan(input: Editable?, number: Int) = input.toString().toInt() > number

    fun isEmailNotValid(email: Editable?) = !EMAIL_ADDRESS.matcher(email.toString()).matches()

    fun isInputLessThan(input: Editable?, length: Int) = input.toString().length < length

    fun isInputNotContainChar(input: Editable?, pattern: String) =
        !input.toString().matches(Regex(pattern))
}
