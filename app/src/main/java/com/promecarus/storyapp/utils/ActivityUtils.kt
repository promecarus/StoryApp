package com.promecarus.storyapp.utils

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.Button
import android.widget.EditText
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.promecarus.storyapp.R.string.connect_or_relaunch
import com.promecarus.storyapp.R.string.error
import com.promecarus.storyapp.R.string.error_bad_http_auth
import com.promecarus.storyapp.R.string.error_email_already_taken
import com.promecarus.storyapp.R.string.error_email_not_valid
import com.promecarus.storyapp.R.string.error_input
import com.promecarus.storyapp.R.string.error_login
import com.promecarus.storyapp.R.string.error_no_internet
import com.promecarus.storyapp.R.string.error_password_invalid
import com.promecarus.storyapp.R.string.error_registration
import com.promecarus.storyapp.R.string.error_user_not_found
import com.promecarus.storyapp.R.string.ok
import com.promecarus.storyapp.R.string.rto
import com.promecarus.storyapp.R.string.try_again
import com.promecarus.storyapp.ui.MainActivity

object ActivityUtils {
    fun enableEditTexts(enable: Boolean, listOfEditTexts: List<EditText>) {
        listOfEditTexts.forEach { it.isEnabled = enable }
    }

    fun enableButtons(enable: Boolean, listOfButtons: List<Button>? = null) {
        listOfButtons?.forEach { it.isEnabled = enable }
    }

    fun enableWhenNoEmptyAndErrors(button: Button, listOfEditTexts: List<EditText>) {
        listOfEditTexts.forEach { editText ->
            editText.addTextChangedListener {
                button.isEnabled = listOfEditTexts.none { it.text.isEmpty() || it.error != null }
            }
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showKeyboard(context: Context, editText: EditText) {
        (context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            editText, SHOW_IMPLICIT
        )
    }

    fun handleMessage(
        context: Context,
        message: String,
        button: Button? = null,
        editTextEmail: EditText? = null,
        editTextPassword: EditText? = null,
    ) {
        when (message) {
            "\"email\" must be a valid email" -> dialog(
                context, error_input, error_email_not_valid
            ).setPositiveButton(try_again) { _, _ ->
                handleError(context, button, editTextEmail)
            }.show()

            "Email is already taken" -> dialog(
                context, error_registration, error_email_already_taken
            ).setPositiveButton(try_again) { _, _ ->
                handleError(context, button, editTextEmail)
            }.show()

            "User not found" -> dialog(
                context, error_login, error_user_not_found
            ).setPositiveButton(try_again) { _, _ ->
                handleError(context, button, editTextEmail)
            }.show()

            "Invalid password" -> dialog(
                context, error_login, error_password_invalid
            ).setPositiveButton(try_again) { _, _ ->
                handleError(context, button, editTextPassword)
            }.show()

            "Unable to resolve host \"story-api.dicoding.dev\": No address associated with hostname",
            "Failed to connect to story-api.dicoding.dev/167.172.79.194:443",
            -> dialog(
                context, error_no_internet, connect_or_relaunch
            ).setPositiveButton(try_again) { _, _ ->
                startNewActivity(context, context::class.java, false)
            }.show()

            "Bad HTTP authentication header format" -> dialog(
                context, error, error_bad_http_auth
            ).setPositiveButton(ok) { _, _ ->
                startNewActivity(context, context::class.java, false)
            }.show()

            "timeout" -> startNewActivity(context, MainActivity::class.java, false, rto)

            else -> dialog(context, error, message).setPositiveButton(ok) { _, _ ->
                startNewActivity(context, context::class.java, false)
            }.show()
        }
    }

    private fun dialog(context: Context, title: Int, message: Int) =
        MaterialAlertDialogBuilder(context).setCancelable(false).setTitle(title).setMessage(message)

    private fun dialog(context: Context, title: Int, message: String) =
        MaterialAlertDialogBuilder(context).setCancelable(false).setTitle(title).setMessage(message)

    private fun handleError(
        context: Context, button: Button? = null, editText: EditText? = null,
    ) {
        if (button != null) enableButtons(true, listOf(button))
        editText?.apply {
            text?.clear()
            requestFocus()
            showKeyboard(context, this)
        }
    }

    private fun startNewActivity(
        context: Context,
        target: Class<*>,
        withAnimation: Boolean = true,
        message: Int = 0,
    ) {
        context.startActivity(Intent(context, target).apply {
            if (!withAnimation) flags = FLAG_ACTIVITY_NO_ANIMATION
        })
        if (context is Activity) context.finish()
        if (message != 0) makeText(context, message, LENGTH_SHORT).show()
    }
}
