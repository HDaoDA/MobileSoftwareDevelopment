package com.example.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface BookshelfUiState {
    object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}

class BookshelfViewModel(private val repo: BooksRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState

    private val _selected = MutableStateFlow<Book?>(null)
    val selected = _selected

    init {
        load()
    }

    fun load() {
        _uiState.value = BookshelfUiState.Loading
        viewModelScope.launch {
            try {
                val books = repo.getBooks()
                _uiState.value = BookshelfUiState.Success(books)
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun retry() = load()

    fun select(book: Book) {
        _selected.value = book
    }

    fun closeDetails() {
        _selected.value = null
    }

    class Factory(private val repo: BooksRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return BookshelfViewModel(repo) as T
        }
    }
}
