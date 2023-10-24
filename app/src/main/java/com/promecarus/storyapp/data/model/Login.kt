package com.promecarus.storyapp.data.model

import com.google.gson.annotations.SerializedName

data class Login(
    val message: String,
    @SerializedName("loginResult") val session: Session,
)
