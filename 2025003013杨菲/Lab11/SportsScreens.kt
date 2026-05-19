package com.example.sports.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sports.R
import com.example.sports.model.Sport
import com.example.sports.ui.theme.SportsTheme
import com.example.sports.utils.SportsContentType

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun SportsApp(
    windowWidthSizeClass: WindowWidthSizeClass = WindowWidthSizeClass.Compact,
    sportsViewModel: SportsViewModel = viewModel()
) {
    val uiState by sportsViewModel.uiState.collectAsState()
    val sports = uiState.sportsList
    val currentSport = uiState.currentSport
    var isShowingListPage by remember { mutableStateOf(true) }

    val contentType = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Expanded -> SportsContentType.ListAndDetail
        else -> SportsContentType.ListOnly
    }

    val context = LocalContext.current
    BackHandler(
        enabled = contentType == SportsContentType.ListAndDetail
    ) {
        (context as Activity).finish()
    }

    when (contentType) {
        SportsContentType.ListAndDetail -> {
            SportsListAndDetails(
                sports = sports,
                currentSport = currentSport,
                onSportClick = { sport ->
                    sportsViewModel.updateCurrentSport(sport)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        SportsContentType.ListOnly -> {
            SportsSinglePane(
                sports = sports,
                currentSport = currentSport,
                isShowingListPage = isShowingListPage,
                onSportClick = { sport ->
                    sportsViewModel.updateCurrentSport(sport)
                    isShowingListPage = false
                },
                onBackClick = { isShowingListPage = true },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SportsListAndDetails(
    sports: List<Sport>,
    currentSport: Sport,
    onSportClick: (Sport) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SportsAppBar(
                isShowingListPage = true,
                isListAndDetail = true,
                onBackButtonClick = {}
            )
        },
        modifier = modifier
    ) { contentPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            // 左侧：运动列表（占1/3宽度）
            SportsList(
                sports = sports,
                onClick = onSportClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            )

            // 右侧：运动详情（占2/3宽度）
            SportsDetailContent(
                selectedSport = currentSport,
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun SportsSinglePane(
    sports: List<Sport>,
    currentSport: Sport,
    isShowingListPage: Boolean,
    onSportClick: (Sport) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            SportsAppBar(
                isShowingListPage = isShowingListPage,
                isListAndDetail = false,
                onBackButtonClick = onBackClick
            )
        },
        modifier = modifier
    ) { contentPadding ->
        if (isShowingListPage) {
            SportsList(
                sports = sports,
                onClick = onSportClick,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding
            )
        } else {
            SportsDetail(
                selectedSport = currentSport,
                onBackPressed = onBackClick,
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsAppBar(
    isShowingListPage: Boolean,
    isListAndDetail: Boolean = false,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when {
        isListAndDetail -> "Sports"
        isShowingListPage -> "Sports"
        else -> "Sport Info"
    }

    val showBackButton = !isListAndDetail && !isShowingListPage

    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        modifier = modifier              // ← 最后一项不需要逗号
    )
}


@Composable
fun SportsList(
    sports: List<Sport>,
    onClick: (Sport) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sports) { sport ->
            SportListItem(
                sport = sport,
                onClick = { onClick(sport) }
            )
        }
    }
}

@Composable
fun SportListItem(
    sport: Sport,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = sport.imageResourceId),
                contentDescription = stringResource(sport.titleResourceId),
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(sport.titleResourceId),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${sport.playerCount} athletes • ${if (sport.olympic) "Olympic" else "Not Olympic"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
    BackHandler {
        onBackPressed()
    }

    SportsDetailContent(
        selectedSport = selectedSport,
        modifier = modifier.padding(contentPadding)
    )
}

@Composable
fun SportsDetailContent(
    selectedSport: Sport,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 详情页横幅图 - 使用 sportsImageBanner
        androidx.compose.foundation.Image(
            painter = painterResource(id = selectedSport.sportsImageBanner),
            contentDescription = stringResource(selectedSport.titleResourceId),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(selectedSport.titleResourceId),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Athletes: ${selectedSport.playerCount}",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(selectedSport.sportDetails),
            fontSize = 16.sp,
            lineHeight = 24.sp
        )
    }
}

@Preview(
    name = "Tablet - List and Detail",
    device = "spec:width=800dp,height=1280dp",
    showBackground = true
)
@Composable
fun PreviewTabletListAndDetail() {
    SportsTheme {
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SportsApp(windowWidthSizeClass = WindowWidthSizeClass.Expanded)
        }
    }
}