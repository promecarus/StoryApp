package com.promecarus.storyapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.data.repository.SettingRepository
import com.promecarus.storyapp.data.repository.StoryRepository
import com.promecarus.storyapp.ui.viewmodel.AddViewModel
import com.promecarus.storyapp.ui.viewmodel.LoginViewModel
import com.promecarus.storyapp.ui.viewmodel.MainViewModel
import com.promecarus.storyapp.ui.viewmodel.MapsViewModel
import com.promecarus.storyapp.ui.viewmodel.RegisterViewModel
import com.promecarus.storyapp.ui.viewmodel.SettingViewModel
import com.promecarus.storyapp.utils.Injection.provideAuthRepository
import com.promecarus.storyapp.utils.Injection.provideSettingRepository
import com.promecarus.storyapp.utils.Injection.provideStoryRepository

class ViewModelFactory(
    private val storyRepository: StoryRepository,
    private val authRepository: AuthRepository,
    private val settingRepository: SettingRepository,
) : NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = when {
        modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
            RegisterViewModel(authRepository) as T
        }

        modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
            LoginViewModel(authRepository) as T
        }

        modelClass.isAssignableFrom(MainViewModel::class.java) -> {
            MainViewModel(authRepository, storyRepository) as T
        }

        modelClass.isAssignableFrom(AddViewModel::class.java) -> {
            AddViewModel(storyRepository) as T
        }

        modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
            MapsViewModel(storyRepository) as T
        }

        modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
            SettingViewModel(settingRepository) as T
        }

        else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory = INSTANCE ?: synchronized(this) {
            INSTANCE ?: ViewModelFactory(
                provideStoryRepository(context),
                provideAuthRepository(context),
                provideSettingRepository(context)
            )
        }.also { INSTANCE = it }
    }
}
