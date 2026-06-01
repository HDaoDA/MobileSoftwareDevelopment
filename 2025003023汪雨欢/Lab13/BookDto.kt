package com.example.bookshelf.model

import com.google.gson.annotations.SerializedName

// 网络传输对象 DTO
data class BookDto(
    val id: String = "",
    @SerializedName("img_src") val imgSrc: String = ""
)

// DTO 转 领域模型
fun BookDto.toBook(): Book {
    return Book(
        id = this.id,
        coverUrl = this.imgSrc
    )
}

// 列表批量转换
fun List<BookDto>.toBookList(): List<Book> {
    return this.map { it.toBook() }
}