package com.promecarus.storyapp.ui

import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.promecarus.storyapp.R.string.value_id
import com.promecarus.storyapp.R.string.value_lat_lon
import com.promecarus.storyapp.data.remote.response.Story
import com.promecarus.storyapp.databinding.ActivityDetailBinding
import com.promecarus.storyapp.utils.HelperUtils.getCity
import com.promecarus.storyapp.utils.HelperUtils.parseDate

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFinishAfterTransition()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

        binding.topAppBar.setNavigationOnClickListener { callback.handleOnBackPressed() }

        if (SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_STORY, Story::class.java)
        } else {
            @Suppress("DEPRECATION") intent.getParcelableExtra(EXTRA_STORY)
        }?.apply {
            binding.topAppBar.title = getString(value_id, id)
            binding.tvDetailName.text = name
            binding.tvDetailCreated.text = buildString {
                append(parseDate(createdAt).toString())
                append(getCity(
                    this@DetailActivity, lat, lon, true
                )?.let { getString(value_lat_lon, it) } ?: "")
            }
            Glide.with(this@DetailActivity).load(photoUrl).into(binding.ivDetailPhoto)
            binding.tvDetailDescription.text = description
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}
