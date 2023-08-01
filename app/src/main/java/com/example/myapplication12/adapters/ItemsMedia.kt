package com.example.myapplication12.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication12.ImageLoader
import com.example.myapplication12.MediaModel
import com.example.myapplication12.databinding.ItemsMediaBinding
import com.google.android.exoplayer2.ExoPlayer


interface OnItemClickCallback {
    fun onItemClick(name: String)
}

class ItemsMedia(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<ItemsMedia.MediaViewHolder>() {
    private val fileMedia: ArrayList<MediaModel> = arrayListOf()

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
    fun updateList(list: List<MediaModel>) {
        this.fileMedia.clear()
        this.fileMedia.addAll(list)
        notifyDataSetChanged()
    }



    inner class MediaViewHolder(private val binding: ItemsMediaBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(model: MediaModel, onItemClickCallback: OnItemClickCallback) {
            binding.tvNameMusic.text = model.displayName
            ImageLoader.loadImage(
                binding.ivPictureSong,
                model.path
            )
            itemView.setOnClickListener {
                onItemClickCallback.onItemClick(
                    model.path
                )
            }

        }
    }


}