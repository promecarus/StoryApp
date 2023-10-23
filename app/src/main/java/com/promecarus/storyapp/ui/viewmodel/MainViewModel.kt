package com.promecarus.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promecarus.storyapp.data.remote.response.Story
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.data.repository.StoryRepository
import com.promecarus.storyapp.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val storyRepository: StoryRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<State<List<Story>>>(State.Default)
    val state: StateFlow<State<List<Story>>> = _state

    val session = authRepository.getSession()

    fun logout() {
        viewModelScope.launch { authRepository.clearSession() }
    }

    fun getStories() {
        viewModelScope.launch { storyRepository.getStories().collect { _state.value = it } }
    }
}
