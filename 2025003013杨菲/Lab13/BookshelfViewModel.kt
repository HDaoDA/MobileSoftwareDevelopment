package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI 状态定义
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    data class Error(val message: String = "加载失败，请重试") : BookshelfUiState
}

class BookshelfViewModel(
    private val booksRepository: BooksRepository
) : ViewModel() {

    // UI 状态流
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    // 选中的书籍（用于弹窗）
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
                if (books.isNotEmpty()) {
                    _uiState.value = BookshelfUiState.Success(books)
                } else {
                    _uiState.value = BookshelfUiState.Error("暂无数据")
                }
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error("加载失败: ${e.message}")
            }
        }
    }

    fun retry() {
        loadBooks()
    }

    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    fun dismissDialog() {
        _selectedBook.value = null
    }
}