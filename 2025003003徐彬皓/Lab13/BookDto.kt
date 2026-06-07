package com.example.bookshelf.network

import com.example.bookshelf.model.Book
import com.google.gson.annotations.SerializedName

data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = ""
)

fun BookDto.asExternalModel(): Book {
    return Book(
        id = id,
        coverUrl = imgSrc,
        title = "Book $id"
    )
}