package com.example.bookshelf.model

import com.google.gson.annotations.SerializedName

data class Book(
    val id: String,
    val coverUrl: String,
    val title: String
)

data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = "",
)

fun BookDto.asExternalModel(): Book {
    return Book(
        id = id,
        coverUrl = imgSrc,
        title = "Book $id"
    )
}