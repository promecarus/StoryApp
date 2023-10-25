package com.promecarus.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import com.promecarus.storyapp.R.string.success_register
import com.promecarus.storyapp.databinding.ActivityRegisterBinding
import com.promecarus.storyapp.ui.viewmodel.RegisterViewModel
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

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel by viewModels<RegisterViewModel> { getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeViewModel()
    }

    private fun setupAction(
        button: Button = binding.btnRegister,
        editTexts: List<AppCompatEditText> = listOf(
            binding.edRegisterName, binding.edRegisterEmail, binding.edRegisterPassword
        ),
    ) {
        enableWhenNoEmptyAndErrors(button, editTexts)

        button.setOnClickListener {
            viewModel.register(
                binding.edRegisterName.text.toString(),
                binding.edRegisterEmail.text.toString(),
                binding.edRegisterPassword.text.toString()
            )
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFinishAfterTransition()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        binding.btnLogin.setOnClickListener { callback.handleOnBackPressed() }
    }

    private fun observeViewModel(
        buttons: List<Button> = listOf(binding.btnRegister, binding.btnLogin),
        editTexts: List<AppCompatEditText> = listOf(
            binding.edRegisterName, binding.edRegisterEmail, binding.edRegisterPassword
        ),
    ) {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is Loading -> {
                        enableEditTexts(false, editTexts)
                        enableButtons(false, buttons)
                        hideKeyboard(this@RegisterActivity, binding.root)
                        binding.progressBar.visibility = VISIBLE
                    }

                    is Success -> {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        makeText(this@RegisterActivity, success_register, LENGTH_SHORT).show()
                        finish()
                    }

                    is Error -> {
                        enableEditTexts(true, editTexts)
                        handleMessage(
                            this@RegisterActivity,
                            it.error,
                            binding.btnLogin,
                            binding.edRegisterEmail,
                            binding.edRegisterPassword
                        )
                    }

                    is Default -> binding.progressBar.visibility = GONE
                }
            }
        }
    }
}
