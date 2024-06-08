package com.sarthak.movieshelf.ui.basicSearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.getReleaseYear
import com.sarthak.movieshelf.ui.MoviePosterForList
import com.sarthak.movieshelf.ui.Route

@Composable
fun BasicSearchScreen(navController: NavHostController) {
    val basicSearchViewModel: BasicSearchViewModel = hiltViewModel()
    val basicSearchState = basicSearchViewModel.state.collectAsState()
    val localFocusManager = LocalFocusManager.current
    val searchQuery = basicSearchViewModel.searchQuery.collectAsState()
    val suggestions = basicSearchState.value.movieListResponseItem.moviesList
    Scaffold(
        topBar = {BasicSearchTopAppBar(navController)}
    ) {

        Column {
            BasicSearchBar(
                searchQuery = searchQuery.value,
                onSearchQueryChange = { newSearchQuery ->
                    basicSearchViewModel.updateSearchQuery(
                        newSearchQuery
                    )
                },
                localFocusManager = localFocusManager,
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 12.dp)
            )
            if(suggestions.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(suggestions.size) { index ->
                        if (index == suggestions.size - 1) {
                            basicSearchViewModel.loadNextPage()
                        }
                        val suggestion = suggestions[index]
                        SuggestionItem(
                            movieItem = suggestion,
                            onItemClick = { navController.navigate("${Route.MOVIE_DETAILS_SCREEN}?id=${suggestion.id}") },
                            localFocusManager = localFocusManager,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BasicSearchBar(
    searchQuery: String,
    onSearchQueryChange:(String) -> Unit,
    localFocusManager: FocusManager,
    modifier: Modifier = Modifier
) {

    TextField(
        value = searchQuery,
        onValueChange = { onSearchQueryChange(it) },
        singleLine = true,
        placeholder = {
            Text(
                text = "Search...",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "searchIcon"
            )
        },
        trailingIcon = {
            if(searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "clearIcon"
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp),
        keyboardOptions = KeyboardOptions(
            autoCorrect = true,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                if(searchQuery.isNotEmpty()) {
                    localFocusManager.clearFocus()
                }
            }
        )
    )
}

@Composable
fun SuggestionItem(
    movieItem: MinimalMovieItem,
    onItemClick: (Int) -> Unit,
    localFocusManager: FocusManager,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height((screenHeight * 0.1).dp)
            .padding(horizontal = 4.dp)
            .clickable {
                onItemClick(movieItem.id)
                localFocusManager.clearFocus()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicSearchTopAppBar(navController: NavHostController, modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(
                text = "Search Movies",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back Icon",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        modifier = modifier
    )
}