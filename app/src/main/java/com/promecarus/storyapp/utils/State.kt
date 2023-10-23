package com.promecarus.storyapp.utils

sealed class State<out R> private constructor() {
    data object Default : State<Nothing>()
    data object Loading : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()
    data class Error(val error: String) : State<Nothing>()
}
