package com.example.myapplication12

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val mediaList : ArrayList<MediaModel> = arrayListOf()
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    public lateinit var exoPlayer: ExoPlayer
    var mediaFilesFinal: List<MediaFile> = emptyList()
    private var currentPage = 0
    private val itemsPerPage = 10

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
                    itemAdapterOffline.updateList(mediaFilesFinal)
                    setUpCategoriesNameRecyclerView()
                }
            }
        }
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES),
            1)

    }

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
                        itemAdapterOffline.updateList(mediaFilesFinal)
                        setUpCategoriesNameRecyclerView()
                    }
                }
              /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getAllMedia()
                }*/
            }

        }

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