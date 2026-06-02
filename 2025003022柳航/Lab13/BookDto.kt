package com.example.bookshelf.model

import com.google.gson.annotations.SerializedName

data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = ""
)