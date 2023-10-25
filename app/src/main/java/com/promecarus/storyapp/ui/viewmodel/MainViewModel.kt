package com.promecarus.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository,
) : ViewModel() {
    val session = authRepository.getSession()

    fun logout() {
        viewModelScope.launch { authRepository.clearSession() }
    }

    fun getStories() = storyRepository.getStories().cachedIn(viewModelScope)
}
