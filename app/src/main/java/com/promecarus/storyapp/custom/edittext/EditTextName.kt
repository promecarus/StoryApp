package com.promecarus.storyapp.custom.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.promecarus.storyapp.R.drawable.ic_person
import com.promecarus.storyapp.R.string.error_name_empty
import com.promecarus.storyapp.R.string.name
import com.promecarus.storyapp.utils.EditTextUtils.initEditText
import com.promecarus.storyapp.utils.EditTextUtils.isInputEmpty

@SuppressLint("InlinedApi")
class EditTextName(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    init {
        initEditText(
            autofillHint = AUTOFILL_HINT_NAME,
            context = context,
            editText = this,
            hintResId = name,
            iconResId = ic_person,
            inputType = TYPE_TEXT_VARIATION_PERSON_NAME,
        )

        addTextChangedListener {
            error = when {
                isInputEmpty(it) -> context.getString(error_name_empty)

                else -> null
            }
        }
    }
}
