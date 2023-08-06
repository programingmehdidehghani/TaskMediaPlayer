package com.example.myapplication12

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication12.adapters.ItemsMedia
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.common.base.CharMatcher.`is`
import net.bytebuddy.matcher.ElementMatchers.any
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockExoPlayer: ExoPlayer

    private lateinit var activity: MainActivity

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        activity = spy(MainActivity())
        doReturn(mockContext).`when`(activity).applicationContext
        doReturn(mockExoPlayer).`when`(activity).exoPlayer
        activity.requestPermissionLauncher = mock(ActivityResultLauncher::class.java) as ActivityResultLauncher<Array<String>>
    }

    @Test
    fun testOnCreateWithPermissionGranted() {
        // Mock permission granted
        val mockPackageManager: PackageManager = mock(PackageManager::class.java)
        val mockPermissionInfo: PermissionInfo = mock(PermissionInfo::class.java)
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
        val grantResults = IntArray(permissions.size) { PackageManager.PERMISSION_GRANTED }
        `when`(mockContext.checkPermission(any().toString(), anyInt(), anyInt())).thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockPackageManager.getPermissionInfo(any().toString(), anyInt())).thenReturn(mockPermissionInfo)

        // Call the method under test
        activity.onCreate(Bundle())

        // Verify that the appropriate methods are called
        verify(activity).exoPlayer
        verify(activity).getAllMedia()
    }

    @Test
    fun testOnCreateWithPermissionDenied() {
        // Mock permission denied
        val mockPackageManager: PackageManager = mock(PackageManager::class.java)
        val mockPermissionInfo: PermissionInfo = mock(PermissionInfo::class.java)
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_IMAGES
        )
        val grantResults = IntArray(permissions.size) { PackageManager.PERMISSION_DENIED }
        `when`(mockContext.checkPermission(any().toString(), anyInt(), anyInt())).thenReturn(PackageManager.PERMISSION_DENIED)
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockPackageManager.getPermissionInfo(any().toString(), anyInt())).thenReturn(mockPermissionInfo)

        // Call the method under test
        activity.onCreate(Bundle())

        // Verify that the appropriate methods are not called
        verify(activity, never()).exoPlayer
        verify(activity, never()).getAllMedia()
    }

    @After
    fun tearDown() {
        activity = null
    }
}