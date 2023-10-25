package com.promecarus.storyapp.custom.edittext

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.promecarus.storyapp.R.drawable.ic_numbers
import com.promecarus.storyapp.R.string.cannot_be_empty
import com.promecarus.storyapp.R.string.cannot_more_than
import com.promecarus.storyapp.R.string.data_size
import com.promecarus.storyapp.utils.EditTextUtils.initEditText
import com.promecarus.storyapp.utils.EditTextUtils.isInputEmpty
import com.promecarus.storyapp.utils.EditTextUtils.isInputLessThan
import com.promecarus.storyapp.utils.EditTextUtils.isInputMoreThan

@SuppressLint("InlinedApi")
class EditTextNumber(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    init {
        initEditText(
            autofillHint = "",
            context = context,
            editText = this,
            hintResId = data_size,
            iconResId = ic_numbers,
            inputType = TYPE_CLASS_NUMBER,
        )

        this.filters = arrayOf(InputFilter { source, _, _, dest, dstart, dend ->
            val inputText =
                dest.toString().substring(0, dstart) + source.toString() + dest.toString()
                    .substring(dend)
            if (inputText.matches(Regex("[0-9]+")) && inputText.length <= 3) null else ""
        })

        addTextChangedListener {
            setSelection(text?.length ?: 0)

            error = when {
                isInputEmpty(it) -> context.getString(cannot_be_empty)

                isInputLessThan(it, 1) -> context.getString(cannot_be_empty)

                isInputMoreThan(it, LIMIT) -> context.getString(cannot_more_than, LIMIT)

                else -> null
            }
        }
    }

    companion object {
        const val LIMIT = 100
    }
}
