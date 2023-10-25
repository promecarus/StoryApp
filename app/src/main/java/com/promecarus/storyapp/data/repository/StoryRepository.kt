package com.promecarus.storyapp.data.repository

import android.content.Context
import android.net.Uri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.gson.Gson
import com.promecarus.storyapp.data.local.database.StoryDatabase
import com.promecarus.storyapp.data.local.preference.SessionPreference
import com.promecarus.storyapp.data.local.preference.SettingPreference
import com.promecarus.storyapp.data.model.Common
import com.promecarus.storyapp.data.remote.ApiService
import com.promecarus.storyapp.utils.ImageUtils.uriToFile
import com.promecarus.storyapp.utils.State.Default
import com.promecarus.storyapp.utils.State.Error
import com.promecarus.storyapp.utils.State.Loading
import com.promecarus.storyapp.utils.State.Success
import com.promecarus.storyapp.utils.StoryRemoteMediator
import com.promecarus.storyapp.utils.reduceFileImage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val sessionPreference: SessionPreference,
    private val settingPreference: SettingPreference,
    private val storyDatabase: StoryDatabase,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStories() =
        Pager(config = PagingConfig(pageSize = 5), remoteMediator = StoryRemoteMediator(
            apiService, sessionPreference, settingPreference, storyDatabase
        ), pagingSourceFactory = { storyDatabase.storyDao().getAllStory() }).flow

    suspend fun getStoriesWithLocation() = flow {
        emit(Loading)
        try {
            apiService.getStories(
                "Bearer ${sessionPreference.getSession().first().token}",
                1,
                settingPreference.getSetting().first().size,
                1
            ).let {
                emit(Default)
                if (it.isSuccessful) it.body()?.also { data ->
                    emit(Success(data.listStory))
                } else emit(
                    Error(Gson().fromJson(it.errorBody()?.string(), Common::class.java).message)
                )
            }
        } catch (e: IOException) {
            emit(Error(e.message.toString()))
        }
    }

    suspend fun addStory(context: Context, description: String, uri: Uri) = flow {
        emit(Loading)
        val photo = uriToFile(context, uri).reduceFileImage()
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val multiPartBodyPart = MultipartBody.Part.createFormData(
            "photo", photo.name, photo.asRequestBody("image/jpeg".toMediaType())
        )
        try {
            apiService.addStory(
                "Bearer ${sessionPreference.getSession().first().token}",
                requestBody,
                multiPartBodyPart
            ).let {
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

    companion object {
        @Volatile
        private var INSTANCE: StoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            sessionPreference: SessionPreference,
            settingPreference: SettingPreference,
            storyDatabase: StoryDatabase,
        ) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: StoryRepository(
                apiService, sessionPreference, settingPreference, storyDatabase
            )
        }.also { INSTANCE = it }
    }
}
