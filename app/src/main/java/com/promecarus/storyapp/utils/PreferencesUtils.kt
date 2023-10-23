package com.promecarus.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore

val Context.session: DataStore<Preferences> by preferencesDataStore(name = "session_unique_unique")
val Context.setting: DataStore<Preferences> by preferencesDataStore(name = "setting_unique_unique")

object PreferencesManager {
    suspend fun <T> setPreference(
        dataStore: DataStore<Preferences>,
        key: Preferences.Key<T>,
        value: T,
    ) {
        dataStore.edit { it[key] = value }
    }
}
