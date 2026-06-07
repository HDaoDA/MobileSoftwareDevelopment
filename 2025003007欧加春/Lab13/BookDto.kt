package com.example.bookshelf.model

import com.google.gson.annotations.SerializedName

/**
 * 对应 Apifox Mock 返回的单条图片数据（DTO）
 */
data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = "",
)

/**
 * 将 DTO 转换为应用内部使用的领域模型
 */
fun BookDto.asExternalModel(): Book {
    return Book(
        id = id,
        title = "Book #$id",
        coverUrl = imgSrc,
    )
}
