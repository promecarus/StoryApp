package com.promecarus.storyapp.ui

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.os.Bundle
import android.provider.Settings.ACTION_LOCALE_SETTINGS
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.promecarus.storyapp.R.id.action_language
import com.promecarus.storyapp.R.id.action_logout
import com.promecarus.storyapp.R.id.action_map
import com.promecarus.storyapp.R.id.action_settings
import com.promecarus.storyapp.R.string.error_no_stories
import com.promecarus.storyapp.custom.adapter.StoryAdapter
import com.promecarus.storyapp.databinding.ActivityMainBinding
import com.promecarus.storyapp.ui.viewmodel.MainViewModel
import com.promecarus.storyapp.utils.ActivityUtils.handleMessage
import com.promecarus.storyapp.utils.State.Default
import com.promecarus.storyapp.utils.State.Error
import com.promecarus.storyapp.utils.State.Loading
import com.promecarus.storyapp.utils.State.Success
import com.promecarus.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> { ViewModelFactory.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeViewModel()
    }

    private fun setupAction() {
        binding.topAppBar.setNavigationOnClickListener {
            viewModel.getStories()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                action_language -> {
                    startActivity(Intent(ACTION_LOCALE_SETTINGS))
                    true
                }

                action_map -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }

                action_settings -> {
                    startActivity(Intent(this, SettingActivity::class.java))
                    true
                }

                action_logout -> {
                    viewModel.logout()
                    true
                }

                else -> false
            }
        }

        binding.extendedFab.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
            finish()
        }

        binding.nestedScrollView.apply {
            setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                when {
                    scrollY == this.getChildAt(0).measuredHeight - this.measuredHeight -> binding.extendedFab.hide()
                    scrollY > oldScrollY -> binding.extendedFab.shrink()
                    else -> {
                        binding.extendedFab.show()
                        binding.extendedFab.extend()
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.session.collect {
                if (it.token.isEmpty()) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java).apply {
                        flags = FLAG_ACTIVITY_NO_ANIMATION
                    })
                    finish()
                } else {
                    binding.appBarLayout.visibility = VISIBLE
                    binding.nestedScrollView.visibility = VISIBLE
                    binding.extendedFab.visibility = VISIBLE
                    viewModel.getStories()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is Loading -> {
                        binding.recyclerView.visibility = GONE
                        binding.progressBar.visibility = VISIBLE
                    }

                    is Success -> if (it.data.isEmpty()) {
                        binding.textViewEmpty.visibility = GONE
                        binding.textViewEmpty.apply {
                            text = context.getString(error_no_stories)
                            visibility = VISIBLE
                        }
                    } else binding.recyclerView.apply {
                        adapter = StoryAdapter(this@MainActivity, it.data)
                        hasFixedSize()
                        visibility = VISIBLE
                    }

                    is Error -> {
                        binding.recyclerView.visibility = GONE
                        binding.textViewEmpty.visibility = VISIBLE
                        handleMessage(this@MainActivity, it.error)
                    }

                    is Default -> binding.progressBar.visibility = GONE
                }
            }
        }
    }
}
