package com.example.bookshelf.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.BookshelfApplication
import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.launch

sealed interface BookshelfUiState {

    data object Loading : BookshelfUiState

    data class Success(
        val books: List<Book>,
        val selectedBook: Book? = null
    ) : BookshelfUiState

    data class Error(
        val message: String
    ) : BookshelfUiState
}

class BookshelfViewModel(
    private val booksRepository: BooksRepository
) : ViewModel() {

    var uiState: BookshelfUiState by mutableStateOf(BookshelfUiState.Loading)
        private set

    init {
        getBooks()
    }

    fun getBooks() {
        viewModelScope.launch {
            uiState = BookshelfUiState.Loading

            uiState = try {
                val books = booksRepository.getBooks()

                if (books.isEmpty()) {
                    BookshelfUiState.Error("没有获取到书架数据")
                } else {
                    BookshelfUiState.Success(books = books)
                }
            } catch (e: Exception) {
                BookshelfUiState.Error(
                    message = e.message ?: "加载失败，请检查网络连接"
                )
            }
        }
    }

    fun selectBook(book: Book) {
        val currentState = uiState

        if (currentState is BookshelfUiState.Success) {
            uiState = currentState.copy(
                selectedBook = book
            )
        }
    }

    fun closeBookDetail() {
        val currentState = uiState

        if (currentState is BookshelfUiState.Success) {
            uiState = currentState.copy(
                selectedBook = null
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as BookshelfApplication
                BookshelfViewModel(
                    booksRepository = application.container.booksRepository
                )
            }
        }
    }
}