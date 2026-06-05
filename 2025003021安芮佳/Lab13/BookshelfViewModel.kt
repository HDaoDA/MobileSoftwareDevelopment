package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.model.Book
import com.example.bookshelf.repository.BooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}

class BookshelfViewModel(
    private val booksRepository: BooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook: StateFlow<Book?> = _selectedBook.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = BookshelfUiState.Loading
            try {
                val books = booksRepository.getBooks()
                if (books.isEmpty()) {
                    _uiState.value = BookshelfUiState.Error("No books available")
                } else {
                    _uiState.value = BookshelfUiState.Success(books)
                }
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error("Failed to load books: ${e.message}")
            }
        }
    }

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    fun dismissDialog() {
        _selectedBook.value = null
    }

    fun retry() {
        loadBooks()
    }
}