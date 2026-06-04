package com.example.bookshelf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.R
import com.example.bookshelf.model.Book

@Composable
fun BookshelfScreen(
    viewModel: BookshelfViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState
    val selectedBook = viewModel.selectedBook

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookshelf") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        when (uiState) {
            BookshelfUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is BookshelfUiState.Success -> {
                BookGrid(
                    books = (uiState as BookshelfUiState.Success).books,
                    onBookClick = { viewModel.selectBook(it) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is BookshelfUiState.Error -> {
                ErrorScreen(
                    message = (uiState as BookshelfUiState.Error).message,
                    onRetry = { viewModel.loadBooks() },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    selectedBook?.let { book ->
        BookDetailDialog(
            book = book,
            onDismiss = { viewModel.dismissBookDetail() }
        )
    }
}

@Composable
fun BookGrid(
    books: List<Book>,
    onBookClick: (Book) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = dimensionResource(R.dimen.padding_small)
    ) {
        items(books) { book ->
            BookCard(book = book, onClick = { onBookClick(book) })
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
            .size(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(140.dp)
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                imageVector = androidx.compose.material.icons.Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Error: $message",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
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
        title = { Text(book.title) },
        text = {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.coverUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(250.dp)
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}