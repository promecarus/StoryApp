package com.promecarus.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promecarus.storyapp.data.repository.SettingRepository
import com.promecarus.storyapp.utils.Setting
import kotlinx.coroutines.launch

class SettingViewModel(
    private val settingRepository: SettingRepository,
) : ViewModel() {
    val setting = settingRepository.getSetting()

    fun setSetting(setting: Setting) =
        viewModelScope.launch { settingRepository.setSetting(setting) }
}
