package com.promecarus.storyapp.custom.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.promecarus.storyapp.R.string.description
import com.promecarus.storyapp.R.string.error_description_cannot_be_empty
import com.promecarus.storyapp.utils.EditTextUtils.initEditText
import com.promecarus.storyapp.utils.EditTextUtils.isInputEmpty

@SuppressLint("InlinedApi")
class EditTextDescription(context: Context, attrs: AttributeSet) :
    AppCompatEditText(context, attrs) {
    init {
        initEditText(
            autofillHint = "",
            context = context,
            editText = this,
            hintResId = description,
            iconResId = null,
            inputType = TYPE_TEXT_FLAG_MULTI_LINE,
        )

        this.setLines(5)
        this.gravity = android.view.Gravity.START or android.view.Gravity.TOP
        this.foregroundGravity = android.view.Gravity.START or android.view.Gravity.TOP
        this.isHorizontalScrollBarEnabled = false

        addTextChangedListener {
            error = when {
                isInputEmpty(it) -> context.getString(error_description_cannot_be_empty)

                else -> null
            }
        }
    }
}
