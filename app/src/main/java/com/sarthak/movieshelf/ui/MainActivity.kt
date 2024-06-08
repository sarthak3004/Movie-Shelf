package com.sarthak.movieshelf.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sarthak.movieshelf.ui.basicSearch.BasicSearchScreen
import com.sarthak.movieshelf.ui.home.HomeScreen
import com.sarthak.movieshelf.ui.movieDetails.MovieDetailsScreen
import com.sarthak.movieshelf.ui.theme.MovieShelfTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieShelfTheme {
                Navigation()
            }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.HOME_SCREEN
    ) {

        composable(Route.HOME_SCREEN) {
            HomeScreen(
                navController = navController,
            )
        }

        composable(
            "${Route.MOVIE_DETAILS_SCREEN}?id={id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType },
            )
        ) {
            val id = it.arguments?.getInt("id") ?: 0
                MovieDetailsScreen(
                    navController = navController,
                    id = id
                )
        }

        composable(Route.BASIC_SEARCH_SCREEN) {
            BasicSearchScreen(navController = navController)
        }

    }
}
