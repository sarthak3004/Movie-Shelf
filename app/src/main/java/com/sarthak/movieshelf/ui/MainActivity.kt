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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.sarthak.movieshelf.BuildConfig
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.ui.addReview.AddReviewScreen
import com.sarthak.movieshelf.ui.basicSearch.BasicSearchScreen
import com.sarthak.movieshelf.ui.home.HomeScreen
import com.sarthak.movieshelf.ui.login.LoginScreen
import com.sarthak.movieshelf.ui.movieDetails.MovieDetailsScreen
import com.sarthak.movieshelf.ui.signup.SignUpScreen
import com.sarthak.movieshelf.ui.theme.MovieShelfTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (BuildConfig.DEBUG) {
//            Firebase.auth.useEmulator("10.0.2.2", 9099)
//            Firebase.firestore.useEmulator("10.0.2.2", 8080)
//        }
        val auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            MovieShelfTheme {
                Navigation(auth.currentUser != null)
            }
        }
    }
}

@Composable
fun Navigation(isLoggedIn: Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) {
            Route.HOME_SCREEN
        } else Route.LOGIN_SCREEN
    ) {

        composable(Route.LOGIN_SCREEN) {
            LoginScreen(navController = navController)
        }

        composable(Route.SIGNUP_SCREEN) {
            SignUpScreen(navController = navController)
        }

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
            val id = it.arguments?.getInt("id") ?: -1
                MovieDetailsScreen(
                    navController = navController,
                    id = id
                )
        }

        composable(Route.BASIC_SEARCH_SCREEN) {
            BasicSearchScreen(navController = navController)
        }

        composable(
            "${Route.ADD_REVIEW_SCREEN}?id={id},posterPath={posterPath},title={title},releaseDate={releaseDate}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("posterPath") { type = NavType.StringType },
                    navArgument("title") { type = NavType.StringType },
                    navArgument("releaseDate") { type = NavType.StringType }
                )
        ) {
            val id = it.arguments?.getInt("id") ?: -1
            val posterPath = it.arguments?.getString("posterPath") ?: ""
            val title = it.arguments?.getString("title") ?: ""
            val releaseDate = it.arguments?.getString("releaseDate") ?: ""
            AddReviewScreen(MinimalMovieItem(
                id = id,
                posterPath = posterPath,
                title = title,
                releaseDate = releaseDate
            ), navController)
        }
    }
}