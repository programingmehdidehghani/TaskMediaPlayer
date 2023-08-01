package com.example.myapplication12

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication12.adapters.ItemsMedia
import com.example.myapplication12.adapters.OnItemClickCallback
import com.example.myapplication12.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() , OnItemClickCallback {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private val itemAdapterOffline = ItemsMedia(this)
    private var _binding: ActivityMainBinding? = null
    private val viewBinding get() = _binding!!
    private val mediaList : ArrayList<MediaModel> = arrayListOf()
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var exoPlayer: ExoPlayer

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
            && ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES),
                1)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getAllMedia()

            }
        }
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_VIDEO,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES),
            1)

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getAllMedia()
                }
            }

        }

    }



   /* @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllMedia() {
            // Projection for video query
            val mediaList = mutableListOf<MediaModel>()

            val videoProjection = arrayOf(
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME
            )

            // Query for videos
            val videoCursor = this@MainActivity.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                null
            )

            // Projection for image query
            val imageProjection = arrayOf(
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME
            )

            // Query for images
            val imageCursor = this@MainActivity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageProjection,
                null,
                null,
                null
            )

            try {
                // Combine the results of video and image queries


                // Add videos to the media list
                videoCursor?.let { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                            val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(data)
                            val thumbnail = retriever.getFrameAtTime(
                                100,
                                MediaMetadataRetriever.OPTION_CLOSEST
                            )
                            Log.i("list","${mediaList.toString()}")
                            mediaList.add(MediaModel(data, displayName, thumbnail))
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                }

                // Add images to the media list
                imageCursor?.let { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                            val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                            val thumbnail = BitmapFactory.decodeFile(data)
                            mediaList.add(MediaModel(data, displayName, thumbnail))
                        } while (cursor.moveToNext())
                    }
                    cursor.close()
                }
                    Log.i("list","${mediaList.size}")
                    itemAdapterOffline.updateList(mediaList)
                    setUpCategoriesNameRecyclerView()
                    viewBinding.rvMediaFilesInOffline.visibility = View.VISIBLE


                // Do something with the media list
                // ...

            } catch (e: Exception) {
                e.printStackTrace()
            }



    }*/

    @RequiresApi(Build.VERSION_CODES.P)
    fun getAllMedia() {
        val projection =
            arrayOf(MediaStore.Video.VideoColumns.DATA, MediaStore.Video.Media.DISPLAY_NAME)
        val cursor = this.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        try {
            cursor?.let { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                        val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                        val retriever = MediaMetadataRetriever()
                        retriever.setDataSource(data)
                        val thumbnail = retriever.getFrameAtTime(
                            100,
                            MediaMetadataRetriever.OPTION_CLOSEST
                        )
                        mediaList.add(MediaModel(data, displayName, thumbnail))
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            itemAdapterOffline.updateList(mediaList)
            setUpCategoriesNameRecyclerView()
         //   getImage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getImage(){
        val imageProjection = arrayOf(
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        // Query for images
        val imageCursor = this@MainActivity.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            null
        )
        try {
            // Add images to the media list
            imageCursor?.let { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                        val thumbnail = BitmapFactory.decodeFile(data)
                        mediaList.add(MediaModel(data, displayName, thumbnail))
                    } while (cursor.moveToNext())
                }
                cursor.close()
            }
            Log.i("list","${mediaList.size}")
            itemAdapterOffline.updateList(mediaList)
            setUpCategoriesNameRecyclerView()
            viewBinding.rvMediaFilesInOffline.visibility = View.VISIBLE

       } catch (e: Exception) {
        e.printStackTrace()
       }
    }




    override fun onDestroy() {
        super.onDestroy()
        _binding = null;
        job.cancel()
    }

    private fun setUpCategoriesNameRecyclerView() {
        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        viewBinding.rvMediaFilesInOffline.apply {
            this.layoutManager = layoutManager
            adapter = itemAdapterOffline
        }
    }


    override fun onItemClick(name: String) {
        val videoUri = Uri.parse(name)
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSourceFactory(this, "exoplayer-sample")
        ).createMediaSource(MediaItem.fromUri(videoUri))
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer.playWhenReady = true
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.play()
        viewBinding.playerView.player = exoPlayer
        viewBinding.playerView.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.release() 
    }



}