package com.example.bookshelf

object ApiConfig {
	const val BASE_URL = "https://m1.apifoxmock.com/m1/8321477-8085280-default/"
	const val DEFAULT_GRID_COLUMNS = 2
	const val DEFAULT_TIMEOUT_SECONDS = 15L
	const val FALLBACK_COUNT = 5
	const val PHOTOS_PATH = "photos"

	fun buildPhotoUrl(id: String): String {
		return "${BASE_URL}${PHOTOS_PATH}/$id"
	}

	fun appTag(): String {
		return "Lab13-Bookshelf"
	}

	fun retryDelayMillis(): Long {
		return DEFAULT_TIMEOUT_SECONDS * 1000L
	}
}
