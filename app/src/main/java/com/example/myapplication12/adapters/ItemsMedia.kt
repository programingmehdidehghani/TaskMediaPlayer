package com.example.myapplication12.adapters

import android.annotation.SuppressLint
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication12.ImageLoader
import com.example.myapplication12.MediaFile
import com.example.myapplication12.MediaModel
import com.example.myapplication12.MediaType
import com.example.myapplication12.databinding.ItemsMediaBinding


interface OnItemClickCallback {
    fun onItemClick(name: String,isVideoType: Boolean)
}

class ItemsMedia(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<ItemsMedia.MediaViewHolder>() {
    private val fileMedia: ArrayList<MediaFile> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemsMediaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MediaViewHolder(binding)
    }

    override fun getItemCount(): Int =
        fileMedia.size


    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(fileMedia[position],onItemClickCallback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<MediaFile>) {
        this.fileMedia.clear()
        this.fileMedia.addAll(list)
        notifyDataSetChanged()
    }



    inner class MediaViewHolder(private val binding: ItemsMediaBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model: MediaFile, onItemClickCallback: OnItemClickCallback) {
            binding.tvNameMusic.text = model.name
            ImageLoader.loadImage(
                binding.ivPictureSong,
                model.image
            )
            itemView.setOnClickListener {
                if (model.type == MediaType.VIDEO) {
                    onItemClickCallback.onItemClick(
                        model.image,
                        isVideoType = true
                    )
                } else  {
                    onItemClickCallback.onItemClick(
                        model.image,
                        isVideoType = false
                    )
                }

            }

        }
    }


}