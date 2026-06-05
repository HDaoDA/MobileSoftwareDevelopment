package com.example.bookshelf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelf.model.Book

@Composable
fun BookshelfScreen(viewModel: BookshelfViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = BookshelfViewModel.Factory)){
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val detailBook by viewModel.selectBook.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Text("网络书架", Modifier.padding(16.dp), style = MaterialTheme.typography.headlineMedium)
        Box(modifier = Modifier.fillMaxSize()){
            when(ui){
                is BookshelfUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is BookshelfUiState.Success -> BookGrid((ui as BookshelfUiState.Success).books, onClick = {viewModel.openDetail(it)})
                BookshelfUiState.Error -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("加载失败")
                    Button(onClick = {viewModel.loadData()}) { Text("重试") }
                }
            }
        }
    }
    detailBook?.let {
        AlertDialog(
            onDismissRequest = {viewModel.closeDetail()},
            confirmButton = {Button({viewModel.closeDetail()}){Text("关闭")}},
            text = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(it.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = it.title,
                    modifier = Modifier.aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }
}

@Composable
fun BookGrid(list:List<Book>, onClick:(Book)->Unit){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(list,key={it.id}){
            Card(onClick={onClick(it)}, modifier=Modifier.aspectRatio(1f)){
                AsyncImage(
                    model = it.coverUrl,
                    contentDescription = it.title,
                    modifier=Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}