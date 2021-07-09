package com.haneet.assignment.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.haneet.assignment.CustomAssertions.Companion.hasItemCount
import com.haneet.assignment.CustomMatchers.Companion.withItemCount
import com.haneet.assignment.R
import com.haneet.assignment.data.fake.FakeData

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test



class MainActivityTest {

    val LIST_ITEM_IN_TEST = 4
    val MOVIE_IN_TEST = FakeData.moviesList


    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)


    @Test
    fun test_isListFragmentVisible_onAppLaunch() {
        onView(withId(R.id.rvData)).check(matches(isDisplayed()))


    }

    @Test
    fun countPrograms() {
        runBlocking { delay(2000) }
        onView(withId(R.id.rvData))
            .check(matches(withItemCount(20)))
    }

    @Test
    fun countProgramsWithViewAssertion() {
        runBlocking { delay(2000) }
        onView(withId(R.id.rvData))
            .check(hasItemCount(20))
    }

    @Test
    fun clickEvent() {
        runBlocking { delay(1000) }
        Espresso.onView(ViewMatchers.withId(R.id.rvData)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>
                (3, ClickOnButtonView())
        )
    }

    inner class ClickOnButtonView : ViewAction {
        internal var click = ViewActions.click()

        override fun getConstraints(): Matcher<View> {
            return click.constraints
        }

        override fun getDescription(): String {
            return " click on custom button view"
        }

        override fun perform(uiController: UiController, view: View) {
            //btnClickMe -> Custom row item view button
            click.perform(uiController, view.findViewById(R.id.cl_items))
        }
    }
}