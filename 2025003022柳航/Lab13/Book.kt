package com.example.bookshelf.model

data class Book(
    val id: String,
    val coverUrl: String
)

fun BookDto.asDomain(): Book {
    return Book(id = id, coverUrl = imgSrc)
}