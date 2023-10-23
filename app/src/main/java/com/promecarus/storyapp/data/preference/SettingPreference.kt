package com.promecarus.storyapp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.promecarus.storyapp.utils.PreferencesManager.setPreference
import com.promecarus.storyapp.utils.Setting
import kotlinx.coroutines.flow.map

class SettingPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun setSetting(setting: Setting) {
        setPreference(dataStore, intPreferencesKey(SIZE), setting.size)
        setPreference(dataStore, booleanPreferencesKey(LOCATION), setting.location)
    }

    fun getSetting() = dataStore.data.map {
        Setting(
            it[intPreferencesKey(SIZE)] ?: 10,
            it[booleanPreferencesKey(LOCATION)] ?: false,
        )
    }

    companion object {
        const val SIZE = "size"
        const val LOCATION = "location"

        @Volatile
        private var INSTANCE: SettingPreference? = null

        fun getInstance(setting: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SettingPreference(setting).also { INSTANCE = it }
        }
    }
}
