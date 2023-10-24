package com.promecarus.storyapp.data.repository

import com.google.gson.Gson
import com.promecarus.storyapp.data.local.preference.SessionPreference
import com.promecarus.storyapp.data.remote.ApiService
import com.promecarus.storyapp.data.remote.response.Common
import com.promecarus.storyapp.utils.State.Default
import com.promecarus.storyapp.utils.State.Error
import com.promecarus.storyapp.utils.State.Loading
import com.promecarus.storyapp.utils.State.Success
import kotlinx.coroutines.flow.flow
import java.io.IOException

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val sessionPreference: SessionPreference,
) {
    fun getSession() = sessionPreference.getSession()

    suspend fun clearSession() {
        sessionPreference.clearSession()
    }

    suspend fun register(name: String, email: String, password: String) = flow {
        emit(Loading)
        try {
            apiService.register(name, email, password).let {
                emit(Default)
                if (it.isSuccessful) it.body()?.also { data ->
                    emit(Success(data.message))
                } else emit(
                    Error(Gson().fromJson(it.errorBody()?.string(), Common::class.java).message)
                )
            }
        } catch (e: IOException) {
            emit(Error(e.message.toString()))
        }
    }

    suspend fun login(email: String, password: String) = flow {
        emit(Loading)
        try {
            apiService.login(email, password).let {
                emit(Default)
                if (it.isSuccessful) it.body()?.also { data ->
                    sessionPreference.setSession(data.session)
                    emit(Success(data.message))
                } else emit(
                    Error(Gson().fromJson(it.errorBody()?.string(), Common::class.java).message)
                )
            }
        } catch (e: IOException) {
            emit(Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthRepository? = null

        fun getInstance(apiService: ApiService, sessionPreference: SessionPreference) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthRepository(apiService, sessionPreference)
            }.also { INSTANCE = it }
    }
}
