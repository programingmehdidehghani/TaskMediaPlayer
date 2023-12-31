package com.example.myapplication12

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import org.junit.Test


import org.junit.Assert.*
import org.mockito.Mockito


class ExampleUnitTest {

    @Test
    fun get(){
        val mockDataSourceFactory = Mockito.mock(DataSource.Factory::class.java)
        val mediaItem = MediaItem.fromUri("https://devstreaming-cdn.apple.com/videos/streaming/examples/img_bipbop_adv_example_ts/master.m3u8")
        val mediaSource = ProgressiveMediaSource.Factory(mockDataSourceFactory)
            .createMediaSource(mediaItem)
        assertNotNull(mediaSource)
    }




}