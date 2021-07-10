package com.haneet.assignment.ui

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.haneet.assignment.R
import com.haneet.assignment.ui.onboarding.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LoginActivityTest {


    @Rule
    @JvmField
    var activityRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun A_isViewsVisible() {
        onView(withId(R.id.code)).check(matches(isDisplayed()))
        onView(withId(R.id.number)).check(matches(isDisplayed()))
        onView(withId(R.id.continueBtn)).check(matches(isDisplayed()))
    }

    @Test
    fun B_generateOTP() {
        onView(withId(R.id.number)).perform(typeText("9041422652"));
        onView(withId(R.id.continueBtn))
            .perform(ClickOnButtonView(R.id.continueBtn))
    }

    @Test
    fun C_verifyOTP() {
        runBlocking { delay(2000) }
        onView(withId(R.id.number)).perform(typeText("1234"));
        onView(withId(R.id.continueBtn))
            .perform(ClickOnButtonView(R.id.continueBtn))


    }





    inner class ClickOnButtonView(continueBtn: Int) : ViewAction {
        internal var click = ViewActions.click()

        override fun getConstraints(): Matcher<View> {
            return click.constraints
        }

        override fun getDescription(): String {
            return " click on custom button view"
        }

        override fun perform(uiController: UiController, view: View) {
            //btnClickMe -> Custom row item view button
            click.perform(uiController, view.findViewById(R.id.continueBtn))
        }
    }
}