package com.example.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelf.data.BooksRepository
import com.example.bookshelf.model.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookshelfUiState {
    object Loading : BookshelfUiState
    data class Success(val books: List<Book>) : BookshelfUiState
    object Error : BookshelfUiState
}

class BookshelfViewModel(private val repo: BooksRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    private val _selectBook = MutableStateFlow<Book?>(null)
    val selectBook: StateFlow<Book?> = _selectBook.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = BookshelfUiState.Loading
        viewModelScope.launch {
            try {
                val list = repo.getBooks()
                _uiState.value = BookshelfUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error
            }
        }
    }

    fun openDetail(book: Book) {
        _selectBook.value = book
    }

    fun closeDetail() {
        _selectBook.value = null
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BookshelfApplication
                BookshelfViewModel(app.container.booksRepository)
            }
        }
    }
}