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
import androidx.paging.LoadState
import com.promecarus.storyapp.R.id.action_language
import com.promecarus.storyapp.R.id.action_logout
import com.promecarus.storyapp.R.id.action_map
import com.promecarus.storyapp.R.id.action_settings
import com.promecarus.storyapp.R.string.error_no_stories
import com.promecarus.storyapp.custom.adapter.LoadingAdapter
import com.promecarus.storyapp.custom.adapter.StoryAdapter
import com.promecarus.storyapp.databinding.ActivityMainBinding
import com.promecarus.storyapp.ui.viewmodel.MainViewModel
import com.promecarus.storyapp.utils.ActivityUtils.handleMessage
import com.promecarus.storyapp.utils.ViewModelFactory.Companion.getInstance
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> { getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeViewModel()
    }

    private fun setupAction() {
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

        val adapter = StoryAdapter(this)

        binding.recyclerView.apply {
            this.adapter = adapter.withLoadStateFooter(footer = LoadingAdapter { adapter.retry() })
        }

        lifecycleScope.launch {
            viewModel.getStories().collect {
                adapter.submitData(lifecycle, it)
            }
        }

        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    binding.textViewEmpty.visibility = GONE
                    binding.recyclerView.visibility = GONE
                    binding.progressBar.visibility = VISIBLE
                }

                is LoadState.NotLoading -> {
                    binding.progressBar.visibility = GONE
                    if (adapter.itemCount == 0) {
                        binding.recyclerView.visibility = GONE
                        binding.textViewEmpty.visibility = VISIBLE
                        binding.textViewEmpty.text = getString(error_no_stories)
                    } else {
                        binding.textViewEmpty.visibility = GONE
                        binding.recyclerView.visibility = VISIBLE
                    }
                }

                is LoadState.Error -> {
                    binding.progressBar.visibility = GONE
                    handleMessage(this, (it.refresh as LoadState.Error).error.message.toString())
                }
            }
        }
    }
}
