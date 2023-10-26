package com.promecarus.storyapp.utils

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
import androidx.room.withTransaction
import com.promecarus.storyapp.data.local.database.StoryDatabase
import com.promecarus.storyapp.data.local.preference.SessionPreference
import com.promecarus.storyapp.data.local.preference.SettingPreference
import com.promecarus.storyapp.data.model.RemoteKeys
import com.promecarus.storyapp.data.model.Story
import com.promecarus.storyapp.data.remote.ApiService
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val sessionPreference: SessionPreference,
    private val settingPreference: SettingPreference,
    private val storyDatabase: StoryDatabase,
) : RemoteMediator<Int, Story>() {
    override suspend fun initialize(): InitializeAction = LAUNCH_INITIAL_REFRESH

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Story>): MediatorResult {
        val page = when (loadType) {
            REFRESH -> {
                val remoteKeys = state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.id?.let { id ->
                        storyDatabase.remoteKeysDao().getRemoteKeysId(id)
                    }
                }
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            PREPEND -> {
                val remoteKeys =
                    state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
                        ?.let { data ->
                            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
                        }
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            APPEND -> {
                val remoteKeys = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                    ?.let { data ->
                        storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
                    }
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        return try {
            val responseData = apiService.getStories(
                "Bearer ${sessionPreference.getSession().first().token}",
                page,
                state.config.pageSize,
                if (settingPreference.getSetting().first().location) 1 else 0
            ).body()?.listStory

            val endOfPaginationReached = responseData?.isEmpty()

            storyDatabase.withTransaction {
                if (loadType == REFRESH) {
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyDatabase.storyDao().deleteAll()
                }
                val prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1
                val keys = responseData?.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                if (keys != null) storyDatabase.remoteKeysDao().insertAll(keys)
                if (responseData != null) storyDatabase.storyDao().insertStory(responseData)
            }
            MediatorResult.Success(endOfPaginationReached = (endOfPaginationReached == true))
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}
