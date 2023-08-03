package com.example.myapplication12

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication12.adapters.ItemsMedia
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.common.base.CharMatcher.`is`

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {



     @Test
    fun testMediaSource() {
        val mockDataSourceFactory = Mockito.mock(DataSource.Factory::class.java)
        val mediaItem = MediaItem.fromUri("https://example.com/video.mp4")
        val mediaSource = ProgressiveMediaSource.Factory(mockDataSourceFactory)
            .createMediaSource(mediaItem)
       assertNotNull(mediaSource)
    }
}