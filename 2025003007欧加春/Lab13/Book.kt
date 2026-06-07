package com.example.bookshelf.model

/**
 * 应用内部使用的领域模型，UI 层只使用此模型
 */
data class Book(
    val id: String,
    val title: String,
    val coverUrl: String,
)
