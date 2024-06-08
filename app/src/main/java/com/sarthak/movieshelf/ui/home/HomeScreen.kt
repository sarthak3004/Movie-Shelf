package com.sarthak.movieshelf.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sarthak.movieshelf.R
import com.sarthak.movieshelf.ui.ErrorScreen
import com.sarthak.movieshelf.ui.LoadingScreen
import com.sarthak.movieshelf.ui.MoviePosterList
import com.sarthak.movieshelf.ui.Route
import com.sarthak.movieshelf.utils.MOVIES_LIST_TYPE

@Composable
fun HomeScreen(navController: NavHostController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState = homeViewModel.state.collectAsState()
    val trendingState = homeState.value.trendingState
    val topRatedState = homeState.value.topRatedState
    val upcomingState = homeState.value.upcomingState

    Scaffold(
        topBar = { HomeScreenTopBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
        ) {
            MovieListSection(
                moviesListType = MOVIES_LIST_TYPE.TRENDING,
                moviesListState = trendingState,
                navController = navController,
                modifier = Modifier.padding(12.dp)
            )
            MovieListSection(
                moviesListType = MOVIES_LIST_TYPE.UPCOMING,
                moviesListState = upcomingState,
                navController = navController,
                modifier = Modifier.padding(12.dp)
            )
            MovieListSection(
                moviesListType = MOVIES_LIST_TYPE.TOP_RATED,
                moviesListState = topRatedState,
                navController = navController,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun MovieListSection(moviesListType: MOVIES_LIST_TYPE, moviesListState: MoviesListState, navController: NavHostController, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "${moviesListType.listType} Movies",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (moviesListState.isError) {
            ErrorScreen()
        } else if (moviesListState.isLoading) {
            LoadingScreen()
        } else {
            MoviePosterList(
                moviesList = moviesListState.moviesResponse.moviesList,
                navController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(navController: NavHostController) {
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
                IconButton(onClick = { navController.navigate(Route.BASIC_SEARCH_SCREEN) }) {
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
