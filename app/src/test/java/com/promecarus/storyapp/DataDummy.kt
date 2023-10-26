package com.promecarus.storyapp

import com.promecarus.storyapp.data.model.Story

object DataDummy {
    fun generateDummyStories() = (1..10).map {
        Story(
            it.toString(),
            "story-$it",
            "desc$it",
            "https://story-api.dicoding.dev/images/stories/photos-$it.jpg",
            it.toString(),
            it.toString().toDouble(),
            it.toString().toDouble(),
        )
    }
}
