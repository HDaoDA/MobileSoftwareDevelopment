package com.example.bookshelf.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bookshelf.R
import com.example.bookshelf.model.Book
import com.example.bookshelf.ui.theme.BookshelfTheme

@Composable
fun BookshelfApp(
    modifier: Modifier = Modifier,
    viewModel: BookshelfViewModel = viewModel(factory = BookshelfViewModel.Factory),
) {
    val uiState by viewModel.uiState.collectAsState()

    BookshelfScreen(
        uiState = uiState,
        onRetryClick = viewModel::loadBooks,
        onBookClick = viewModel::showBookDetails,
        onCloseDialog = viewModel::closeBookDetails,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfScreen(
    uiState: BookshelfUiState,
    onRetryClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onCloseDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Bookshelf") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        when (uiState) {
            BookshelfUiState.Loading -> LoadingScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is BookshelfUiState.Error -> ErrorScreen(
                message = uiState.message,
                onRetryClick = onRetryClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            )

            is BookshelfUiState.Success -> {
                BooksGrid(
                    books = uiState.books,
                    onBookClick = onBookClick,
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize(),
                )

                uiState.selectedBook?.let { book ->
                    BookDetailDialog(
                        book = book,
                        onDismissRequest = onCloseDialog,
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(24.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetryClick) {
            Text(text = "重试")
        }
    }
}

@Composable
fun BooksGrid(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        items(
            items = books,
            key = { book -> book.id },
        ) { book ->
            BookCard(
                book = book,
                onClick = { onBookClick(book) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier,
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                placeholder = painterResource(R.drawable.book_placeholder),
                error = painterResource(R.drawable.book_placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(12.dp),
            )
        }
    }
}

@Composable
fun BookDetailDialog(
    book: Book,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "关闭")
            }
        },
        title = { Text(text = book.title) },
        text = {
            Column {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.title,
                    placeholder = painterResource(R.drawable.book_placeholder),
                    error = painterResource(R.drawable.book_placeholder),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "编号：${book.id}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "图片地址：${book.coverUrl}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun BookshelfScreenPreview() {
    BookshelfTheme {
        BookshelfScreen(
            uiState = BookshelfUiState.Success(
                books = listOf(
                    Book("1", "Book #1", "https://picsum.photos/id/10/800/600"),
                    Book("2", "Book #2", "https://picsum.photos/id/11/800/600"),
                    Book("3", "Book #3", "https://picsum.photos/id/12/800/600"),
                ),
            ),
            onRetryClick = {},
            onBookClick = {},
            onCloseDialog = {},
        )
    }
}
