package com.example.myapplication12

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class Test {

    @Test
    fun testMediaSource() {
        val mockDataSourceFactory = Mockito.mock(DataSource.Factory::class.java)
        val mediaItem = MediaItem.fromUri("https://example.com/video.mp4")
        val mediaSource = ProgressiveMediaSource.Factory(mockDataSourceFactory)
            .createMediaSource(mediaItem)
        Assert.assertNotNull(mediaSource)
    }
}