package com.sarthak.movieshelf.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sarthak.movieshelf.R
import com.sarthak.movieshelf.ui.ErrorScreen
import com.sarthak.movieshelf.ui.LoadingScreen
import com.sarthak.movieshelf.ui.MoviePosterList
import com.sarthak.movieshelf.ui.theme.MovieShelfTheme

@Composable
fun HomeScreen(navController: NavHostController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState = homeViewModel.state.collectAsState()

    Scaffold(
        topBar = { HomeScreenTopBar() },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (homeState.value.trendingState.isError) {
                ErrorScreen()
            } else if (homeState.value.trendingState.isLoading) {
                LoadingScreen()
            } else {
                MoviePosterList(
                    moviesList = homeState.value.trendingState.trendingMovies,
                    navController,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.weight(1F)
                )
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon Button"
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu Icon Button"
                )
            }
        }
    )
}

@Preview
@Composable
fun MyPreview() {
    MovieShelfTheme {
        HomeScreenTopBar()
    }
}
