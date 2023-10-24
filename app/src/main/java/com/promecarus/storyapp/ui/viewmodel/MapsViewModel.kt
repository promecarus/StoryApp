package com.promecarus.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promecarus.storyapp.data.model.Story
import com.promecarus.storyapp.data.repository.StoryRepository
import com.promecarus.storyapp.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _state = MutableStateFlow<State<List<Story>>>(State.Default)
    val state: StateFlow<State<List<Story>>> = _state

    init {
        viewModelScope.launch { storyRepository.getStories(1).collect { _state.value = it } }
    }
}
