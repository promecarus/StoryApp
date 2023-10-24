package com.promecarus.storyapp.data.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.promecarus.storyapp.data.remote.response.Session
import com.promecarus.storyapp.utils.PreferencesManager.setPreference
import kotlinx.coroutines.flow.map

class SessionPreference private constructor(private val dataStore: DataStore<Preferences>) {
    suspend fun setSession(session: Session) {
        setPreference(dataStore, stringPreferencesKey(KEY_TOKEN), session.token)
    }

    fun getSession() = dataStore.data.map { Session(it[stringPreferencesKey(KEY_TOKEN)] ?: "") }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    companion object {
        const val KEY_TOKEN = "token"

        @Volatile
        private var INSTANCE: SessionPreference? = null

        fun getInstance(session: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: SessionPreference(session).also { INSTANCE = it }
        }
    }
}
