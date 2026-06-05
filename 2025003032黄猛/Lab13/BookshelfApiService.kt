package com.example.bookshelf

import retrofit2.http.GET

/**
 * Retrofit service for the Apifox mock photos endpoint.
 * The API returns a list of image records used by the bookshelf UI.
 */
interface BookshelfApiService {
    companion object {
        const val PATH_PHOTOS = "photos"
    }

    @GET(PATH_PHOTOS)
    suspend fun getBooks(): List<BookDto>

    fun endpointName(): String {
        return PATH_PHOTOS
    }
}
