package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(
        val books: List<Book>,
        val selectedBook: Book? = null,
    ) : BookshelfUiState
    data class Error(val message: String) : BookshelfUiState
}

class BookshelfViewModel(
    private val booksRepository: BooksRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    fun loadBooks() {
        _uiState.value = BookshelfUiState.Loading
        viewModelScope.launch {
            _uiState.value = try {
                val books = booksRepository.getBooks()
                BookshelfUiState.Success(books = books)
            } catch (e: Exception) {
                BookshelfUiState.Error(
                    message = e.message ?: "图片数据加载失败，请稍后重试。"
                )
            }
        }
    }

    fun showBookDetails(book: Book) {
        val currentState = _uiState.value
        if (currentState is BookshelfUiState.Success) {
            _uiState.value = currentState.copy(selectedBook = book)
        }
    }

    fun closeBookDetails() {
        val currentState = _uiState.value
        if (currentState is BookshelfUiState.Success) {
            _uiState.value = currentState.copy(selectedBook = null)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as BookshelfApplication
                BookshelfViewModel(
                    booksRepository = application.container.booksRepository,
                )
            }
        }
    }
}
