package com.example.myapplication12

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication12.adapters.ItemsMedia
import com.example.myapplication12.adapters.OnItemClickCallback
import com.example.myapplication12.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() , OnItemClickCallback {

    public lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private val itemAdapterOffline = ItemsMedia(this)
    private var _binding: ActivityMainBinding? = null
    private val viewBinding get() = _binding!!
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    lateinit var exoPlayer: ExoPlayer
    private var mediaFilesFinal: List<MediaFile> = emptyList()
    private var pagingListMediaFinal: List<MediaFile> = emptyList()

    private var currentPage = 0
    private val itemsPerPage = 10
    private var isDataLoading = false
    private var isLoading = false

    private var isAllDataLoaded = false
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        exoPlayer = ExoPlayer.Builder(this).build()
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES),
                1)
        } else {
            uiScope.launch (Dispatchers.IO){
                mediaFilesFinal = getMediaFiles()
                withContext(Dispatchers.Main){
                    val initialItems = getMediaFilesForPage(0)
                    itemAdapterOffline.updateList(initialItems)
                    setUpCategoriesNameRecyclerView()
                }
            }
        }
        viewBinding.rvMediaFilesInOffline.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (isLoading || isAllDataLoaded) {
                    return
                }

                val layoutManager = viewBinding.rvMediaFilesInOffline.layoutManager as LinearLayoutManager?
                val totalItemCount = layoutManager!!.itemCount
                val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = layoutManager!!.findLastVisibleItemPosition()

                if (firstVisibleItemPosition == 0 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // User is scrolling up and reached the top of the list
                    Log.i("scroll", "up is call")
                    if (currentPage > 1) {
                        isLoading = true
                        currentPage--
                        val previousItems = getMediaFilesForPage(currentPage)
                        if (previousItems.isNotEmpty()) {
                            itemAdapterOffline.addItems(previousItems)
                            layoutManager.scrollToPositionWithOffset(previousItems.size, 0)
                        } else {
                            // No previous data available
                        }
                        isLoading = false
                    } else {
                        // No more previous data available
                    }
                } else if (lastVisibleItemPosition == totalItemCount - 1 && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i("scroll", "down is call")
                    // User is scrolling down and reached the end of the list
                    isLoading = true
                    currentPage++
                    val newItems = getMediaFilesForPage(currentPage)
                    if (newItems.isNotEmpty()) {
                        itemAdapterOffline.addItems(newItems)
                    } else {
                        isAllDataLoaded = true

                        // Display a message or perform any other action to indicate that there is no more data
                    }
                    isLoading = false
                }

            }
        })

        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES),
            1)

    }

    private fun loadMoreItems() {
        if (isLoading || isAllDataLoaded) {
            return
        }

        val layoutManager = viewBinding.rvMediaFilesInOffline.layoutManager as LinearLayoutManager?
        val visibleItemCount = layoutManager!!.childCount
        val totalItemCount = layoutManager!!.itemCount
        val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = firstVisibleItemPosition + visibleItemCount

        if (lastVisibleItemPosition >= totalItemCount - 1) {
            isLoading = true
            currentPage++
            val newItems = getMediaFilesForPage(currentPage)
            if (!newItems.isEmpty()) {
                itemAdapterOffline.addItems(newItems)
            } else {
                isAllDataLoaded = true
                Toast.makeText(this, "Your message here", Toast.LENGTH_SHORT).show()

                // Display a message or perform any other action to indicate that there is no more data
            }
            isLoading = false
        }
    }


    private fun loadMoreItems1() {
        if (isLoading || isAllDataLoaded) {
            return
        }

        val layoutManager = viewBinding.rvMediaFilesInOffline.layoutManager as LinearLayoutManager?
        val visibleItemCount = layoutManager!!.childCount
        val totalItemCount = layoutManager!!.itemCount
        val firstVisibleItemPosition = layoutManager!!.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = firstVisibleItemPosition + visibleItemCount

        if (firstVisibleItemPosition == 0) {
            // User is scrolling up and reached the top of the list
            if (currentPage > 1) {
                isLoading = true
                currentPage--
                val previousItems = getMediaFilesForPage(currentPage)
                if (previousItems.isNotEmpty()) {
                    itemAdapterOffline.addItems(previousItems)
                    layoutManager.scrollToPosition(previousItems.size)
                } else {
                    // No previous data available
                }
                isLoading = false
            } else {
                // No more previous data available
            }
        } else if (lastVisibleItemPosition >= totalItemCount - 1) {
            // User is scrolling down and reached the end of the list
            isLoading = true
            currentPage++
            val newItems = getMediaFilesForPage(currentPage)
            if (newItems.isNotEmpty()) {
                itemAdapterOffline.addItems(newItems)
            } else {
                isAllDataLoaded = true
                Toast.makeText(this, "Your message here", Toast.LENGTH_SHORT).show()

                // Display a message or perform any other action to indicate that there is no more data
            }
            isLoading = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {

            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
            } else {
                uiScope.launch (Dispatchers.IO){
                    mediaFilesFinal = getMediaFiles()
                    withContext(Dispatchers.Main){
                        val initialItems = getMediaFilesForPage(0)
                        itemAdapterOffline.updateList(initialItems)
                        setUpCategoriesNameRecyclerView()
                    }
                }
            }

        }

    }

    private fun getMediaFilesForPage(page: Int): List<MediaFile> {
        val startIndex = page * itemsPerPage
        val endIndex = startIndex + itemsPerPage
        if (startIndex < mediaFilesFinal.size) {
            return mediaFilesFinal.subList(startIndex, endIndex.coerceAtMost(mediaFilesFinal.size))
        }
        return emptyList()
    }



    private fun getMediaFiles(): List<MediaFile> {
        val mediaFiles = mutableListOf<MediaFile>()


        // Get all images
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.RELATIVE_PATH
        )
        val imageCursor = this@MainActivity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            null
        )
        imageCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(pathColumn)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val mediaFile = MediaFile(id, imageUri.toString(), name, path,MediaType.IMAGE)
                mediaFiles.add(mediaFile)
            }
        }

        // Get all videos
        val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.RELATIVE_PATH
        )
        val videoCursor = this@MainActivity.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoProjection,
            null,
            null,
            null
        )
        videoCursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RELATIVE_PATH)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val path = cursor.getString(pathColumn)
                val videoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                val mediaFile = MediaFile(id, videoUri.toString(), name,path,MediaType.VIDEO)
                mediaFiles.add(mediaFile)
            }
        }

        return mediaFiles
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
        job.cancel()
    }

    private fun setUpCategoriesNameRecyclerView() {
        val layoutManager =
            LinearLayoutManager(this, GridLayoutManager.VERTICAL, false)
        viewBinding.rvMediaFilesInOffline.apply {
            this.layoutManager = layoutManager
            adapter = itemAdapterOffline
        }
    }


    override fun onItemClick(name: String,isVideoType: Boolean) {
        if (isVideoType){
            val videoUri = Uri.parse(name)
            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSourceFactory(this, "exoplayer-sample")
            ).createMediaSource(MediaItem.fromUri(videoUri))
            exoPlayer.playWhenReady = true
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.play()
            viewBinding.playerView.player = exoPlayer
            viewBinding.playerView.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.release()
    }



}