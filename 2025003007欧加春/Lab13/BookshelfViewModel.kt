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
import java.io.IOException

/**
 * UI 状态：加载中、加载成功、加载失败
 */
sealed interface BookshelfUiState {
    data object Loading : BookshelfUiState

    data class Success(
        val books: List<Book>,
        val selectedBook: Book? = null,
    ) : BookshelfUiState

    data class Error(
        val message: String,
    ) : BookshelfUiState
}

/**
 * ViewModel：管理书架数据与 UI 状态
 */
class BookshelfViewModel(
    private val booksRepository: BooksRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    init {
        getBooks()
    }

    /**
     * 从 Repository 获取书籍列表
     */
    fun getBooks() {
        viewModelScope.launch {
            _uiState.value = BookshelfUiState.Loading
            try {
                val books = booksRepository.getBooks()
                _uiState.value = BookshelfUiState.Success(books = books)
            } catch (e: IOException) {
                _uiState.value = BookshelfUiState.Error(
                    message = "网络请求失败：${e.message}"
                )
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error(
                    message = "加载失败：${e.message}"
                )
            }
        }
    }

    /**
     * 点按条目时显示详情
     */
    fun selectBook(book: Book) {
        _uiState.update { currentState ->
            if (currentState is BookshelfUiState.Success) {
                currentState.copy(selectedBook = book)
            } else {
                currentState
            }
        }
    }

    /**
     * 关闭详情弹窗
     */
    fun dismissDetail() {
        _uiState.update { currentState ->
            if (currentState is BookshelfUiState.Success) {
                currentState.copy(selectedBook = null)
            } else {
                currentState
            }
        }
    }
}

/**
 * ViewModel 工厂，用于注入 Repository
 */
class BookshelfViewModelFactory(
    private val booksRepository: BooksRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BookshelfViewModel::class.java)) {
            return BookshelfViewModel(booksRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
