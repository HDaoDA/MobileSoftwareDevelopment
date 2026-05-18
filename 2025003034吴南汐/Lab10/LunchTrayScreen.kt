package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

enum class LunchTrayScreen(@StringRes val title: Int) {
    Start(R.string.start_order),
    Entree(R.string.choose_flavor),
    SideDish(R.string.choose_pickup_date),
    Accompaniment(R.string.choose_accompaniment),
    Checkout(R.string.order_summary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar(
    currentScreen: LunchTrayScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun LunchTrayApp(
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = LunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LunchTrayScreen.Start.name
    )

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(LunchTrayScreen.Start.name) {
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        viewModel.setQuantity(12)
                        navController.navigate(LunchTrayScreen.Entree.name)
                    }
                )
            }

            composable(LunchTrayScreen.Entree.name) {
                EntreeMenuScreen(
                    options = DataSource.flavors,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.Start.name, false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.SideDish.name)
                    },
                    onSelectionChanged = { viewModel.setFlavor(it) }
                )
            }

            composable(LunchTrayScreen.SideDish.name) {
                SideDishMenuScreen(
                    options = DataSource.dates,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.Start.name, false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(LunchTrayScreen.Checkout.name)
                    },
                    onSelectionChanged = { viewModel.setDate(it) }
                )
            }

            composable(LunchTrayScreen.Accompaniment.name) {
                AccompanimentMenuScreen(
                    options = emptyList(),
                    onCancelButtonClicked = {},
                    onNextButtonClicked = {},
                    onSelectionChanged = {}
                )
            }

            composable(LunchTrayScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navController.popBackStack(LunchTrayScreen.Start.name, false)
                    },
                    onSubmitButtonClicked = {
                        navController.popBackStack(LunchTrayScreen.Start.name, false)
                    }
                )
            }
        }
    }
}