package com.example.bookshelf.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState
    data class Success(val books: List<Book>, val selected: Book?) : BookshelfUiState
    data class Error(val msg: String) : BookshelfUiState
}

class BookshelfViewModel(
    private val repo: BooksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        _uiState.value = BookshelfUiState.Loading
        viewModelScope.launch {
            try {
                val list = repo.getBooks()
                _uiState.value = BookshelfUiState.Success(list, null)
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error("加载失败：${e.message}")
            }
        }
    }

    fun selectBook(book: Book) {
        _uiState.update { state ->
            if (state is BookshelfUiState.Success) state.copy(selected = book) else state
        }
    }

    fun closeDetail() {
        _uiState.update { state ->
            if (state is BookshelfUiState.Success) state.copy(selected = null) else state
        }
    }

    companion object {
        // 正确实现 ViewModelProvider.Factory 接口
        fun factory(repo: BooksRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(BookshelfViewModel::class.java)) {
                        return BookshelfViewModel(repo) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}