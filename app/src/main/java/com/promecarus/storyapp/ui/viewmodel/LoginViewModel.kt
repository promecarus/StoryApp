package com.promecarus.storyapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.promecarus.storyapp.data.repository.AuthRepository
import com.promecarus.storyapp.utils.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _state = MutableStateFlow<State<String>>(State.Default)
    val state: StateFlow<State<String>> = _state

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password).collect { _state.value = it }
        }
    }
}
