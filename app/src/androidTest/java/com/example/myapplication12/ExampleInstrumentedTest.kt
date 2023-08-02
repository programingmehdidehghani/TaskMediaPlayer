package com.example.myapplication12

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication12.adapters.ItemsMedia
import com.google.common.base.CharMatcher.`is`

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    private lateinit var testMediaList: List<MediaModel>
    private lateinit var adapter: ItemsMedia

    @Before
    fun setUp() {
        testMediaList = listOf(
            MediaModel("Photo 1", "content://media/external/images/media/1", null),
            MediaModel("Photo 2", "content://media/external/images/media/2", null),
            MediaModel("Video 1", "content://media/external/video/media/1", null)
        )
        adapter = ItemsMedia()
    }

    @Test
    fun adapterItemCount() {
        assertThat(adapter.itemCount, `is`(3))
    }

    @Test
    fun adapterViewHolder() {
        val parent = mock(ViewGroup::class.java)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        val viewHolder = adapter.ViewHolder(view)
        assertThat(viewHolder.itemView, `is`(view))
        assertThat(viewHolder.imageView, `is`(view.findViewById(R.id.imageView)))
        assertThat(viewHolder.textView, `is`(view.findViewById(R.id.textView)))
    }

    @Test
    fun adapterBindViewHolderPhoto() {
        val parent = mock(ViewGroup::class.java)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        val viewHolder = adapter.ViewHolder(view)
        adapter.onBindViewHolder(viewHolder, 0)
        assertThat(viewHolder.textView.text, `is`("Photo 1"))
    }

    @Test
    fun adapterBindViewHolderVideo() {
        val parent = mock(ViewGroup::class.java)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        val viewHolder = adapter.ViewHolder(view)
        adapter.onBindViewHolder(viewHolder, 2)
        assertThat(viewHolder.textView.text, `is`("Video 1"))
    }

  //  @get:Rule
  //  val activityRule = ActivityTestRule(MainActivity::class.java)*/

    @Test
    fun testMyActivity() {

    }

     /*@Test
    fun testMediaSource() {
        val mockDataSourceFactory = Mockito.mock(DataSource.Factory::class.java)
        val mediaItem = MediaItem.fromUri("https://example.com/video.mp4")
        val mediaSource = ProgressiveMediaSource.Factory(mockDataSourceFactory)
            .createMediaSource(mediaItem)
       assertNotNull(mediaSource)
    }*/
}