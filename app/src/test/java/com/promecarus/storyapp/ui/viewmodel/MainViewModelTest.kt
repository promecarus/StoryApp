package com.promecarus.storyapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingData
import androidx.paging.map
import com.promecarus.storyapp.DataDummy
import com.promecarus.storyapp.MainDispatcherRule
import com.promecarus.storyapp.data.model.Story
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
@Suppress("USELESS_IS_CHECK")
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
    fun `when Get Stories Has Data Should Return Not Null and Type of PagingData Story`() =
        runTest {
            val data = DataDummy.generateDummyStories()

            val expected = PagingData.from(data)

            `when`(storyRepository.getStories()).thenReturn(flow { emit(expected) })

            val actual = MainViewModel(authRepository, storyRepository).getStories().first()

            assertNotNull(actual)
            assert(actual is PagingData<Story>)
        }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data = emptyList<Story>()

        val expected = PagingData.from(data)

        `when`(storyRepository.getStories()).thenReturn(flow { emit(expected) })

        var actual = 0

        MainViewModel(authRepository, storyRepository).getStories().first().map { actual += 1 }

        assert(actual == data.size)
    }
}
