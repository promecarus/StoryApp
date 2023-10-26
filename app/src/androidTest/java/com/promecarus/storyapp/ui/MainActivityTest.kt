package com.promecarus.storyapp.ui

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.promecarus.storyapp.EspressoIdlingResource
import com.promecarus.storyapp.R
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun logout_Success() {
        onView(withId(R.id.ed_login_email)).perform(click(), typeText("slimut@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(click(), typeText("aaaaaaaaA1!"))
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(6000)
        onView(withId(R.id.app_bar_layout)).check(matches(isDisplayed()))
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withId(R.id.action_logout)).perform(click())
        Thread.sleep(2000)
        onView(withId(R.id.app_bar_layout)).check(matches(not(isDisplayed())))
    }

    @Test
    fun login_Success() {
        onView(withId(R.id.ed_login_email)).perform(click(), typeText("slimut@gmail.com"))
        onView(withId(R.id.ed_login_password)).perform(click(), typeText("aaaaaaaaA1!"))
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(6000)
        onView(withId(R.id.app_bar_layout)).check(matches(isDisplayed()))
    }
}
