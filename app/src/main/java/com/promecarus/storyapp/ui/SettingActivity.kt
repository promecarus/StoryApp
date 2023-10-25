package com.promecarus.storyapp.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.promecarus.storyapp.databinding.ActivitySettingBinding
import com.promecarus.storyapp.ui.viewmodel.SettingViewModel
import com.promecarus.storyapp.utils.Setting
import com.promecarus.storyapp.utils.ViewModelFactory.Companion.getInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val viewModel by viewModels<SettingViewModel> { getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
    }

    private fun setupView() {
        val setting = runBlocking { viewModel.setting.first() }
        binding.edSize.setText(setting.size.toString())
        binding.smWithLocation.isChecked = setting.location == true
    }

    private fun setupAction() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@SettingActivity, MainActivity::class.java).apply {
                    flags =
                        FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NO_ANIMATION
                })
                viewModel.setSetting(
                    Setting(
                        if (binding.edSize.error == null) binding.edSize.text.toString()
                            .toInt() else 1,
                        binding.smWithLocation.isChecked
                    )
                )
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        binding.topAppBar.setNavigationOnClickListener { callback.handleOnBackPressed() }
    }
}
