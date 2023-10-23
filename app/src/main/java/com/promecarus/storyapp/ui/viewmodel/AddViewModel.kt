package com.promecarus.storyapp.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promecarus.storyapp.data.repository.StoryRepository
import com.promecarus.storyapp.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    private val _state = MutableStateFlow<State<String>>(State.Default)
    val state: StateFlow<State<String>> = _state

    fun addStory(context: Context, description: String, uri: Uri) {
        viewModelScope.launch {
            storyRepository.addStory(context, description, uri).collect { _state.value = it }
        }
    }
}
