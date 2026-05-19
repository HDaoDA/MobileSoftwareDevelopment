package com.example.sports.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sports.R
import com.example.sports.data.LocalSportsDataProvider
import com.example.sports.model.Sport
import com.example.sports.utils.SportsContentType

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SportsApp(
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    viewModel: SportsViewModel = viewModel()
) {
    val contentType = remember(windowWidthSizeClass) {
        when (windowWidthSizeClass) {
            WindowWidthSizeClass.Expanded -> SportsContentType.ListAndDetail
            else -> SportsContentType.ListOnly
        }
    }

    Scaffold(
        topBar = {
            SportsAppBar(
                isShowingListPage = viewModel.isShowingListPage,
                isListAndDetail = contentType == SportsContentType.ListAndDetail,
                onBackButtonClick = { viewModel.navigateToListPage() }
            )
        }
    ) { innerPadding ->
        if (contentType == SportsContentType.ListAndDetail) {
            SportsListAndDetails(
                sports = viewModel.sportsList,
                currentSport = viewModel.currentSport,
                onSportClick = viewModel::updateCurrentSport,
                contentPadding = innerPadding
            )
        } else {
            if (viewModel.isShowingListPage) {
                SportsList(
                    sports = viewModel.sportsList,
                    onClick = {
                        viewModel.updateCurrentSport(it)
                        viewModel.navigateToDetailPage()
                    },
                    contentPadding = innerPadding
                )
            } else {
                SportsDetail(
                    selectedSport = viewModel.currentSport,
                    onBackPressed = { viewModel.navigateToListPage() },
                    contentPadding = innerPadding
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsAppBar(
    isShowingListPage: Boolean,
    isListAndDetail: Boolean,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleText = if (isListAndDetail || isShowingListPage) {
        stringResource(R.string.list_fragment_label)
    } else {
        stringResource(R.string.detail_fragment_label)
    }

    TopAppBar(
        title = {
            Text(
                text = titleText,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (!isShowingListPage && !isListAndDetail) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier
    )
}

@Composable
private fun SportsListAndDetails(
    sports: List<Sport>,
    currentSport: V,
    onSportClick: (Sport) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    androidx.activity.compose.BackHandler {
        (context as Activity).finish()
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        SportsList(
            sports = sports,
            onClick = onSportClick,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(0.dp)
        )

        SportsDetail(
            selectedSport = currentSport,
            onBackPressed = {},
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.weight(2f)
        )
    }
}

@Composable
fun SportsList(
    sports: List<Sport>,
    onClick: (Sport) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        items(sports, key = { it.id }) { sport ->
            SportsListItem(
                sport = sport,
                onItemClick = onClick
            )
        }
    }
}

@Composable
fun SportsListItem(
    sport: Sport,
    onItemClick: (Sport) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { onItemClick(sport) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = sport.imageResId,
                contentDescription = stringResource(sport.nameResId),
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(sport.nameResId),
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = stringResource(sport.detailResId),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}

@Composable
fun SportsDetail(
    selectedSport: Sport,
    onBackPressed: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    androidx.activity.compose.BackHandler { onBackPressed() }

    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                AsyncImage(
                    model = selectedSport.imageResId,
                    contentDescription = stringResource(selectedSport.nameResId),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = 100f,
                                endY = 250f
                            )
                        )
                )
                Text(
                    text = stringResource(selectedSport.nameResId),
                    style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Athletes: ${selectedSport.athletesNumber}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(selectedSport.detailResId),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun AsyncImage(
    model: imageResId,
    contentDescription: String,
    modifier: Modifier,
    contentScale: ContentScale
) {
    TODO("Not yet implemented")
}

@Preview(widthDp = 900, showBackground = true)
@Composable
fun SportsListAndDetailsPreview() {
    SportsTheme {
        SportsListAndDetails(
            sports = LocalSportsDataProvider.getSportsData(),
            currentSport = LocalSportsDataProvider.getSportsData()[0],
            onSportClick = {},
            contentPadding = PaddingValues(0.dp)
        )
    }
}

@Composable
fun SportsTheme(content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}