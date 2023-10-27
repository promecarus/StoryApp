package com.promecarus.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.promecarus.storyapp.DataDummy
import com.promecarus.storyapp.MainDispatcherRule
import com.promecarus.storyapp.custom.adapter.StoryAdapter.Companion.DIFF_CALLBACK
import com.promecarus.storyapp.data.model.Story
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Success Should Return Not Null`() = runTest {
        val expected = DataDummy.generateDummyStories()

        `when`(storyRepository.getStories()).thenReturn(flow { emit(PagingData.from(expected)) })

        val actual = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        ).apply {
            submitData(MainViewModel(authRepository, storyRepository).getStories().first())
        }.snapshot()

        assertNotNull(actual)
    }

    @Test
    fun `when Get Stories Success Should Return The Same Amount Of Data`() = runTest {
        val expected = DataDummy.generateDummyStories()

        `when`(storyRepository.getStories()).thenReturn(flow { emit(PagingData.from(expected)) })

        val actual = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        ).apply {
            submitData(MainViewModel(authRepository, storyRepository).getStories().first())
        }.snapshot()

        assertEquals(actual.size, expected.size)
    }

    @Test
    fun `when Get Stories Success Should Return The Same First Data`() = runTest {
        val expected = DataDummy.generateDummyStories()

        `when`(storyRepository.getStories()).thenReturn(flow { emit(PagingData.from(expected)) })

        val actual = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        ).apply {
            submitData(MainViewModel(authRepository, storyRepository).getStories().first())
        }.snapshot()

        assertEquals(actual[0], expected[0])
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val expected = emptyList<Story>()

        `when`(storyRepository.getStories()).thenReturn(flow { emit(PagingData.from(expected)) })

        val actual = AsyncPagingDataDiffer(
            diffCallback = DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        ).apply {
            submitData(MainViewModel(authRepository, storyRepository).getStories().first())
        }.snapshot()

        assertEquals(actual.size, 0)
    }

    companion object {
        val noopListUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }
}
