package com.example.bookshelf.model

import com.google.gson.annotations.SerializedName

/**
 * 从 API 接收的原始数据格式
 */
data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = "",
)