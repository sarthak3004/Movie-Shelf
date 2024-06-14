package com.sarthak.movieshelf.ui.watchlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sarthak.movieshelf.R
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.getReleaseYear
import com.sarthak.movieshelf.ui.MoviePosterForList
import com.sarthak.movieshelf.ui.Route
import com.sarthak.movieshelf.ui.basicSearch.SuggestionItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WatchlistScreen(navController: NavHostController) {
    val watchlistViewModel: WatchlistViewModel = hiltViewModel()
    val watchlistState = watchlistViewModel.state.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = { WatchListScreenTopBar(navController = navController) }
        ) {
            Column(modifier = Modifier.padding(it)) {
                if(watchlistState.value.minimalMoviesItemList.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(watchlistState.value.minimalMoviesItemList.size) { index ->
                            val movie = watchlistState.value.minimalMoviesItemList[index]
                            MovieItemRow(
                                movieItem = movie,
                                onItemClick = { navController.navigate("${Route.MOVIE_DETAILS_SCREEN}?id=${movie.id}") },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchListScreenTopBar(
    navController: NavHostController,
) {
    TopAppBar(
        title = {
            Text(
                text = "Watchlist",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back Button"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
}

@Composable
fun MovieItemRow(
    movieItem: MinimalMovieItem,
    onItemClick: (Int) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height((screenHeight * 0.2).dp)
            .padding(horizontal = 4.dp)
            .clickable {
                onItemClick(movieItem.id)
            }
    ) {
        MoviePosterForList(item = movieItem, navController = navController, modifier = Modifier.padding(4.dp))
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1F)
        ) {
            Text(
                text = movieItem.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = movieItem.getReleaseYear(),
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}