package com.example.myapplication12

import androidx.constraintlayout.utils.widget.MockView
import androidx.test.espresso.Espresso.onView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


   // @get:Rule
   // val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testMyActivity() {

    }

/*
     @Test
    fun testMediaSource() {
        val mockDataSourceFactory = MockView.mock(DataSource.Factory::class.java)
        val mediaItem = MediaItem.fromUri("https://example.com/video.mp4")
        val mediaSource = ProgressiveMediaSource.Factory(mockDataSourceFactory)
            .createMediaSource(mediaItem)
       assertNotNull(mediaSource)
    }
*/
}