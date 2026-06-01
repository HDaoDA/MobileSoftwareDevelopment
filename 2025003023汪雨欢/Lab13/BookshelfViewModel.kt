package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.AppContainer
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// UI 状态：加载中 / 成功 / 失败
sealed interface BookshelfUiState {
    object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    object Error : BookshelfUiState
}

class BookshelfViewModel : ViewModel() {
    private val repository = AppContainer.booksRepository

    // UI 状态
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    // 当前点击查看详情的书籍
    private val _selectedBook = MutableStateFlow<Book?>(null)
    val selectedBook = _selectedBook.asStateFlow()

    init {
        getBookList()
    }

    // 获取书籍数据
    fun getBookList() {
        _uiState.value = BookshelfUiState.Loading
        viewModelScope.launch {
            try {
                val list = repository.getBooks()
                _uiState.value = BookshelfUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error
            }
        }
    }

    // 选择书籍（打开弹窗）
    fun selectBook(book: Book) {
        _selectedBook.value = book
    }

    // 关闭弹窗
    fun closeDetail() {
        _selectedBook.value = null
    }
}