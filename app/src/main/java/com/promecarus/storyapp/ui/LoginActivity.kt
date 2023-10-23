package com.promecarus.storyapp.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.promecarus.storyapp.R.string.success_login
import com.promecarus.storyapp.databinding.ActivityLoginBinding
import com.promecarus.storyapp.ui.viewmodel.LoginViewModel
import com.promecarus.storyapp.utils.ActivityUtils.enableButtons
import com.promecarus.storyapp.utils.ActivityUtils.enableEditTexts
import com.promecarus.storyapp.utils.ActivityUtils.enableWhenNoEmptyAndErrors
import com.promecarus.storyapp.utils.ActivityUtils.handleMessage
import com.promecarus.storyapp.utils.ActivityUtils.hideKeyboard
import com.promecarus.storyapp.utils.State.Default
import com.promecarus.storyapp.utils.State.Error
import com.promecarus.storyapp.utils.State.Loading
import com.promecarus.storyapp.utils.State.Success
import com.promecarus.storyapp.utils.ViewModelFactory.Companion.getInstance
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> { getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeViewModel()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        @Suppress("DEPRECATION") super.onBackPressed()
        finishAffinity()
    }

    private fun setupAction(
        button: Button = binding.btnLogin,
        editTexts: List<AppCompatEditText> = listOf(binding.edLoginEmail, binding.edLoginPassword),
    ) {
        enableWhenNoEmptyAndErrors(button, editTexts)

        button.setOnClickListener {
            viewModel.login(
                binding.edLoginEmail.text.toString(), binding.edLoginPassword.text.toString()
            )
        }

        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java).apply {
                    flags = FLAG_ACTIVITY_NO_HISTORY
                }, ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(binding.tvTitle, "tv_title"),
                    Pair(binding.edLoginEmail, "ed_email"),
                    Pair(binding.edLoginPassword, "ed_password"),
                    Pair(binding.btnLogin, "btn_login"),
                    Pair(binding.btnRegister, "btn_register"),
                ).toBundle()
            )
        }
    }

    private fun observeViewModel(
        editTexts: List<AppCompatEditText> = listOf(binding.edLoginEmail, binding.edLoginPassword),
        buttons: List<Button> = listOf(binding.btnLogin, binding.btnRegister),
    ) {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is Loading -> {
                        enableEditTexts(false, editTexts)
                        enableButtons(false, buttons)
                        hideKeyboard(this@LoginActivity, binding.root)
                        binding.progressBar.visibility = VISIBLE
                    }

                    is Success -> {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        makeText(this@LoginActivity, success_login, LENGTH_SHORT).show()
                        finish()
                    }

                    is Error -> {
                        enableEditTexts(true, editTexts)
                        handleMessage(
                            this@LoginActivity,
                            it.error,
                            binding.btnRegister,
                            binding.edLoginEmail,
                            binding.edLoginPassword
                        )
                    }

                    is Default -> binding.progressBar.visibility = GONE
                }
            }
        }
    }
}
