package com.promecarus.storyapp.utils

import android.content.Context
import com.promecarus.storyapp.data.local.database.StoryDatabase
import com.promecarus.storyapp.data.local.preference.SessionPreference
import com.promecarus.storyapp.data.local.preference.SettingPreference
import com.promecarus.storyapp.data.remote.ApiConfig.apiService
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.data.repository.SettingRepository
import com.promecarus.storyapp.data.repository.StoryRepository

object Injection {
    fun provideAuthRepository(context: Context) = AuthRepository.getInstance(
        apiService, SessionPreference.getInstance(context.session)
    )

    fun provideStoryRepository(context: Context) = StoryRepository.getInstance(
        apiService,
        SessionPreference.getInstance(context.session),
        SettingPreference.getInstance(context.setting),
        StoryDatabase.getDatabase(context)
    )

    fun provideSettingRepository(context: Context) = SettingRepository.getInstance(
        SettingPreference.getInstance(context.setting)
    )
}
