package com.haneet.assignment.ui

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.haneet.assignment.R
import com.haneet.assignment.ui.onboarding.LoginActivity
import junit.framework.TestCase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matcher
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MainActivityTest {

    @InternalCoroutinesApi
    @Rule
    @JvmField
    var activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun A_isViewsVisible() {

        Espresso.onView(ViewMatchers.withId(R.id.url))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btn))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }


    @Test
    fun B_start() {
        Espresso.onView(ViewMatchers.withId(R.id.url)).perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.url))
            .perform(ViewActions.typeText("https://www.eurofound.europa.eu/sites/default/files/ef_publication/field_ef_document/ef1710en.pdf"));
        Espresso.onView(ViewMatchers.withId(R.id.btn))
            .perform(ClickOnButtonView(R.id.btn))
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
            click.perform(uiController, view.findViewById(R.id.btn))
        }
    }

}