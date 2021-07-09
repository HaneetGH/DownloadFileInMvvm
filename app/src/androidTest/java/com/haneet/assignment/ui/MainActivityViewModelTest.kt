package com.haneet.assignment.ui


import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.haneet.assignment.constant.Task
import com.haneet.assignment.data.remote.ASynchronousApi
import com.haneet.assignment.data.repository.MainActivityRepository
import com.haneet.assignment.domain.DataState

import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class MainActivityViewModelTest : TestCase()  {
    @Rule
    @JvmField
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()
    val context = ApplicationProvider.getApplicationContext<Context>()

    @Mock
    var apiEndPoint: ASynchronousApi? = null


    var apiClient: MainActivityRepository? = null
    private var viewModel: MainActivityViewModel? = null

    @Mock
    var observer: Observer<DataState>? = null

    @Mock
    var lifecycleOwner: LifecycleOwner? = null
    var lifecycle: Lifecycle? = null

    @Before
    @Throws(Exception::class)
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        lifecycle = LifecycleRegistry(lifecycleOwner!!)
        viewModel = apiClient?.let { MainActivityViewModel(it) }
        viewModel!!.uiStateFeatured.observeForever(observer!!)
    }

    @Test
    fun testNull() {

        runBlocking {

            apiClient = MainActivityRepository(context, apiEndPoint!!)
            launch(Dispatchers.IO) {  // Will be launched in the mainThreadSurrogate dispatcher
                `when`(apiClient!!.getFeaturedMoviesApi()).thenReturn(null)
            }

            assertNotNull(viewModel!!.setStateEvent(MainStateEvent.FetchFeaturedMoviesList))
            assertTrue(viewModel!!.uiStateFeatured.hasObservers())
        }
    }

    @Test
    fun testApiFetchDataSuccess() {
        // Mock API response
        runBlocking {


            viewModel!!.setStateEvent(MainStateEvent.FetchFeaturedMoviesList)
            verify(observer)!!.onChanged(DataState.Loading(Task.FEATURED_LIST))
            //verify(observer)!!.onChanged(DataState.Success())
        }

    }

    @Test
    fun testFakeFetchDataSuccess() {
        // Mock API response
        runBlocking {


            viewModel!!.setStateEvent(MainStateEvent.FetchFeaturedMoviesList)
            verify(observer)!!.onChanged(DataState.Loading(Task.FEATURED_LIST))
            //verify(observer)!!.onChanged(DataState.Success())
        }

    }
}