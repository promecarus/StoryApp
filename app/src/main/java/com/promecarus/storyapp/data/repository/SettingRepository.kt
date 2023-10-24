package com.promecarus.storyapp.data.repository

import com.promecarus.storyapp.data.local.preference.SettingPreference
import com.promecarus.storyapp.utils.Setting

class SettingRepository private constructor(
    private val settingPreference: SettingPreference,
) {
    fun getSetting() = settingPreference.getSetting()

    suspend fun setSetting(setting: Setting) = settingPreference.setSetting(setting)

    companion object {
        @Volatile
        private var INSTANCE: SettingRepository? = null

        fun getInstance(settingPreference: SettingPreference) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SettingRepository(settingPreference)
        }.also { INSTANCE = it }
    }
}
