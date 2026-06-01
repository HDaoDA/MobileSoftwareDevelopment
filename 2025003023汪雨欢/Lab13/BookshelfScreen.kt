package com.example.bookshelf.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.bookshelf.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfScreen(viewModel: BookshelfViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val selectedBook = viewModel.selectedBook.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📚 网络书架") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {
                is BookshelfUiState.Loading -> LoadingScreen()
                is BookshelfUiState.Success -> BookGrid(
                    books = uiState.books,
                    onBookClick = { viewModel.selectBook(it) }
                )
                is BookshelfUiState.Error -> ErrorScreen { viewModel.getBookList() }
            }

            // 详情弹窗
            selectedBook?.let {
                BookDetailDialog(
                    book = it,
                    onDismiss = { viewModel.closeDetail() }
                )
            }
        }
    }
}

// 书籍网格
@Composable
fun BookGrid(books: List<Book>, onBookClick: (Book) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(books, key = { it.id }) { book ->
            BookItem(book = book, onClick = { onBookClick(book) })
        }
    }
}

// 单个书籍卡片
@Composable
fun BookItem(book: Book, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = book.coverUrl,
            contentDescription = "书籍封面",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}

// 加载中
@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// 错误页面
@Composable
fun ErrorScreen(retry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("加载失败", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Button(onClick = retry) {
                Text("重试")
            }
        }
    }
}

// 详情弹窗
@Composable
fun BookDetailDialog(book: Book, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = "大图",
                    modifier = Modifier.size(300.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
                Text("书籍 ID：${book.id}")
            }
        }
    )
}