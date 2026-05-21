package com.example.sports.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    // 任务三：根据宽度类别确定布局类型
    val contentType = when (windowWidthSizeClass) {
        WindowWidthSizeClass.Expanded -> SportsContentType.ListAndDetail
        else -> SportsContentType.ListOnly  // ✅ 注意大写 O
    }

    val uiState by viewModel.uiState.collectAsState()
    val sports = LocalSportsDataProvider.allSports
    val currentSport = uiState.currentSport ?: sports.first()

    // 获取 Context（使用 remember 包装）
    val context = LocalContext.current

    // 点击列表项：更新选中项；小屏才跳详情页
    val onSportClick: (Sport) -> Unit = { sport ->
        viewModel.updateCurrentSport(sport)
        if (contentType == SportsContentType.ListOnly) {  // ✅ 大写 O
            viewModel.updateIsShowingListPage(false)
        }
    }

    // 返回逻辑：小屏回列表、大屏退出应用
    val onBackPressed = {
        if (contentType == SportsContentType.ListOnly) {  // ✅ 大写 O
            viewModel.updateIsShowingListPage(true)
        } else {
            (context as Activity).finish()  // ✅ 使用上面获取的 context
        }
    }

    Scaffold(
        topBar = {
            SportsAppBar(
                isShowingListPage = uiState.isShowingListPage,
                isListAndDetail = contentType == SportsContentType.ListAndDetail,
                onBackButtonClick = onBackPressed
            )
        }
    ) { innerPadding ->
        when (contentType) {
            // 小屏：保持原列表/详情切换
            SportsContentType.ListOnly -> {  // ✅ 大写 O
                if (uiState.isShowingListPage) {
                    SportsList(
                        sports = sports,
                        onClick = onSportClick,
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    SportsDetail(
                        selectedSport = currentSport,
                        onBackPressed = onBackPressed,
                        contentPadding = innerPadding,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            // 大屏：并排布局 + 返回退出
            SportsContentType.ListAndDetail -> {
                SportsListAndDetails(
                    sports = sports,
                    currentSport = currentSport,
                    onSportClick = onSportClick,
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                )
                BackHandler { onBackPressed() }
            }
        }
    }
}

// 任务四：双窗格布局（左1/3、右2/3）
@Composable
private fun SportsListAndDetails(
    sports: List<Sport>,
    currentSport: Sport,
    onSportClick: (Sport) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // 左侧列表：占1份权重
        SportsList(
            sports = sports,
            onClick = onSportClick,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.weight(1f)
        )
        // 右侧详情：占2份权重，无返回按钮
        SportsDetail(
            selectedSport = currentSport,
            onBackPressed = {},
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.weight(2f)
        )
    }
}

// 任务五：适配大屏的 AppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsAppBar(
    isShowingListPage: Boolean,
    isListAndDetail: Boolean = false,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            val title = if (isListAndDetail) {
                // 大屏固定显示 Sports
                stringResource(R.string.list_fragment_label)
            } else {
                // 小屏：列表页Sports、详情页Sport Info
                if (isShowingListPage) {
                    stringResource(R.string.list_fragment_label)
                } else {
                    stringResource(R.string.detail_fragment_label)
                }
            }
            Text(text = title)
        },
        navigationIcon = {
            // 仅小屏详情页显示返回按钮
            if (!isListAndDetail && !isShowingListPage) {
                IconButton(onClick = onBackButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun SportsList(
    sports: List<Sport>,
    onClick: (Sport) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(contentPadding),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(sports) { sport ->
            Column(
                modifier = Modifier
                    .clickable { onClick(sport) }
                    .padding(8.dp)
            ) {
                Text(
                    text = sport.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = sport.description,
                    style = MaterialTheme.typography.bodyMedium
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Text(
            text = selectedSport.name,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = selectedSport.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(text = "运动员人数：${selectedSport.athletes}")
    }
}