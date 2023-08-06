package com.example.myapplication12

data class MediaFile(
    val id: Long,val image: String, val name: String, val path: String,val type: MediaType

)
enum class MediaType {
    IMAGE,
    VIDEO
}

