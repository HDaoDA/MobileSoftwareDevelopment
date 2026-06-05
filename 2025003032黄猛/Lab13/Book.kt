package com.example.bookshelf

data class Book(
    val id: String,
    val coverUrl: String,
    val title: String
) {
    fun summary(): String {
        return "$title ($id)"
    }

    fun hasCover(): Boolean {
        return coverUrl.isNotBlank()
    }

    fun displayTitle(): String {
        return if (title.isBlank()) "Book #$id" else title
    }
}

fun BookDto.asExternalModel(): Book = Book(
    id = id,
    coverUrl = imgSrc,
    title = "Book #$id"
)
