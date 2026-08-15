package com.silvercat.sparkcards.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.silvercat.sparkcards.di.AppContainer
import com.silvercat.sparkcards.ui.components.BottomNavBar
import com.silvercat.sparkcards.ui.favorites.FavoriteDetailScreen
import com.silvercat.sparkcards.ui.favorites.FavoritesScreen
import com.silvercat.sparkcards.ui.home.HomeScreen
import com.silvercat.sparkcards.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    container: AppContainer,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                onItemSelected = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(repository = container.cardRepository)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    repository = container.cardRepository,
                    onCardClick = { cardId ->
                        navController.navigate(Screen.FavoriteDetail.routeFor(cardId))
                    },
                )
            }
            composable(Screen.FavoriteDetail.route) { entry ->
                val cardId = entry.arguments?.getString("cardId").orEmpty()
                FavoriteDetailScreen(
                    repository = container.cardRepository,
                    cardId = cardId,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(repository = container.cardRepository)
            }
        }
    }
}
