package com.mad411.habittracker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mad411.habittracker.ui.screens.AddHabitScreen
import com.mad411.habittracker.ui.screens.HabitListScreen
import com.mad411.habittracker.ui.screens.QuoteScreen
import com.mad411.habittracker.viewmodel.HabitViewModel

//nav routes defined as constants so we dont typo them all over the place
//string based routes are the standard approach for Navigation Compose
object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val QUOTE = "quote"
}

// nav graph for the whole app


@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    // viewModel() here is outside any composable{} destination so it resolves to the Activity
    // as its ViewModelStoreOwner — that means both screens share the same HabitViewModel
    // instance. Without this, each destination gets its own copy and changes made in
    // AddHabitScreen are invisible to HabitListScreen.
    val habitViewModel: HabitViewModel = viewModel()

    NavHost(navController = navController, startDestination = Routes.LIST) {

        composable(Routes.LIST) {
            HabitListScreen(
                onAddHabitClick = { navController.navigate(Routes.ADD) },
                onShowQuoteClick = { navController.navigate(Routes.QUOTE) },
                viewModel = habitViewModel
            )
        }

        composable(Routes.ADD) {
            AddHabitScreen(
                onHabitSaved = { navController.popBackStack() },
                viewModel = habitViewModel
            )
        }

        composable(Routes.QUOTE) {
            QuoteScreen()
        }
    }
}
