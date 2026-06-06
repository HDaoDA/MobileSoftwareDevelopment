package com.example.bookshelf.model
import com.google.gson.annotations.SerializedName

data class BookDto(
    val id: String = "",
    @SerializedName("img_src") val imgSrc: String = ""
)
fun BookDto.toBook(): Book = Book(id = id, coverUrl = imgSrc, title = "书籍 #$id")