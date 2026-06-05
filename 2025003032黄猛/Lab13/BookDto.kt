package com.example.bookshelf

import com.google.gson.annotations.SerializedName

data class BookDto(
    val id: String = "",
    @SerializedName("img_src")
    val imgSrc: String = ""
) {
    fun isValid(): Boolean {
        return id.isNotBlank() && imgSrc.isNotBlank()
    }

    fun description(): String {
        return "BookDto(id=$id)"
    }
}
