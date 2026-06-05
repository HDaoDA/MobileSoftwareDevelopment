package com.example.bookshelf

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookshelfScreen(viewModel: BookshelfViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    val selected = viewModel.selected.collectAsState()

    Scaffold(topBar = { SmallTopAppBar(title = { Text("Bookshelf") }) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val s = uiState.value) {
                is BookshelfUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is BookshelfUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${s.message}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.retry() }) { Text("Retry") }
                    }
                }

                is BookshelfUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(s.books, key = { it.id }) { book ->
                            Card(modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable { viewModel.select(book) }) {
                                Column {
                                    AsyncImage(
                                        model = book.coverUrl,
                                        contentDescription = book.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .height(150.dp)
                                            .fillMaxWidth()
                                    )
                                    Text(
                                        book.title,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            selected.value?.let { book ->
                AlertDialog(
                    onDismissRequest = { viewModel.closeDetails() },
                    confirmButton = {
                        TextButton(onClick = { viewModel.closeDetails() }) { Text("Close") }
                    },
                    title = { Text(book.title) },
                    text = {
                        AsyncImage(
                            model = book.coverUrl,
                            contentDescription = book.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                )
            }
        }
    }
}
