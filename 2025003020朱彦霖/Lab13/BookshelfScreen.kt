package com.example.bookshelf.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bookshelf.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfScreen(
    viewModel: BookshelfViewModel = viewModel(factory = BookshelfViewModel.Factory)
) {
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Bookshelf 网络书架")
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is BookshelfUiState.Loading -> {
                LoadingScreen(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }

            is BookshelfUiState.Success -> {
                BooksGridScreen(
                    books = uiState.books,
                    selectedBook = uiState.selectedBook,
                    onBookClick = viewModel::selectBook,
                    onDismissDialog = viewModel::closeBookDetail,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }

            is BookshelfUiState.Error -> {
                ErrorScreen(
                    message = uiState.message,
                    onRetry = viewModel::getBooks,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "加载失败",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry
            ) {
                Text(text = "重试")
            }
        }
    }
}

@Composable
fun BooksGridScreen(
    books: List<Book>,
    selectedBook: Book?,
    onBookClick: (Book) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = books,
                key = { book -> book.id }
            ) { book ->
                BookCard(
                    book = book,
                    onClick = {
                        onBookClick(book)
                    }
                )
            }
        }

        if (selectedBook != null) {
            BookDetailDialog(
                book = selectedBook,
                onDismiss = onDismissDialog
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun BookDetailDialog(
    book: Book,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = book.title)
        },
        text = {
            Column {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "编号：${book.id}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "图片地址：${book.coverUrl}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = "关闭")
            }
        }
    )
}