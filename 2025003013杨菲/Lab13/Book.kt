package com.example.bookshelf.model

/**
 * 应用内部使用的书籍模型
 */
data class Book(
    val id: String,
    val imageUrl: String,
    val title: String = "Book $id"
)

/**
 * 将 DTO 转换为领域模型
 */
fun BookDto.asExternalModel(): Book = Book(
    id = id,
    imageUrl = imgSrc,
    title = "Book #$id"
)