package com.example.bookshelf.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.model.Book

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfScreen(vm: BookshelfViewModel) {
    val uiState by vm.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Bookshelf") })
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is BookshelfUiState.Loading -> CircularProgressIndicator()
                is BookshelfUiState.Error -> Column(
                    horizontalAlignment = Alignment.Center as Alignment.Horizontal
                ) {
                    Text((uiState as BookshelfUiState.Error).msg)
                    Button(onClick = vm::reload) { Text("重试") }
                }
                is BookshelfUiState.Success -> {
                    val s = uiState as BookshelfUiState.Success
                    BookshelfGrid(
                        books = s.books,
                        onClick = vm::selectBook
                    )
                    s.selected?.let {
                        DetailDialog(
                            book = it,
                            onDismiss = vm::closeDetail
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookshelfGrid(
    books: List<Book>,
    onClick: (Book) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        items(books) { book ->
            Card(
                modifier = Modifier
                    .size(150.dp)
                    .clickable { onClick(book) }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(book.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun DetailDialog(book: Book, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("图片详情") },
        text = {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "关闭")
            }
        }
    )
}