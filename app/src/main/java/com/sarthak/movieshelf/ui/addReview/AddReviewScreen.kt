package com.sarthak.movieshelf.ui.addReview

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.getReleaseYear
import com.sarthak.movieshelf.ui.LoadingScreen
import com.sarthak.movieshelf.ui.movieDetails.CustomDivider
import com.sarthak.movieshelf.utils.IMAGE_BASE_URL
import com.sarthak.movieshelf.utils.PastOrPresentSelectableDates
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewScreen(minimalMovieItem: MinimalMovieItem, navController: NavHostController) {
    val addReviewViewModel: AddReviewViewModel = hiltViewModel()
    val state = addReviewViewModel.state.collectAsState()
    Box {
        Scaffold(
            topBar = {
                AddReviewTopBar(
                    goBack = { navController.popBackStack() },
                    sendReview = { addReviewViewModel.sendReview() { navController.popBackStack() } }
                )
            }
        ) {
            val datePickerState = rememberDatePickerState(
                selectableDates = PastOrPresentSelectableDates,
                initialSelectedDateMillis = System.currentTimeMillis()
            )
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 12.dp)
            ) {
                MovieDetails(
                    title = minimalMovieItem.title,
                    year = minimalMovieItem.getReleaseYear(),
                    posterPath = minimalMovieItem.posterPath
                )
                CustomDivider()
                DatePickerScreen(state.value.dateInMillis, datePickerState) { newDate ->
                    addReviewViewModel.onDateChange(newDate)
                }
                CustomDivider()
                RatingScreen(state.value.rating) { newRating ->
                    addReviewViewModel.onRatingChange(newRating)
                }
                CustomDivider()
                ReviewScreen(state.value.review) { newReview ->
                    addReviewViewModel.onReviewChange(newReview)
                }
                if(state.value.isError && state.value.errorMessage.isNotBlank()) {
                    Toast.makeText(LocalContext.current, state.value.errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
        if(state.value.isLoading) {
            Surface(
                color = Color.Black.copy(alpha = 0.7F)
            ) {
                LoadingScreen()
            }
        }
    }
}

@Composable
fun ReviewScreen(review: String, onReviewChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = review,
            onValueChange = { onReviewChange(it) },
            placeholder = {
                Text(
                    text = "Add review..."
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}

@Composable
fun RatingScreen(selectedRating: Float, onRatingSelected: (Float) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Rating",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Slider(
            value = selectedRating,
            onValueChange = {
                onRatingSelected(round(it * 10) / 10)
            },
            valueRange = 0F..5F,
            steps = 9,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = selectedRating.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerScreen(selectedDateInMillis: Long, datePickerState: DatePickerState, onDateSelected: (Long) -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    var showDatePicker by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {

        Text(
            text = "Date",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(end = 16.dp)
        )
        IconButton(
            onClick = {
                showDatePicker = true
            },
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Date Picker Icon",
                modifier = Modifier
                    .padding(end = 16.dp)
            )
        }
        Text(
            text = if(selectedDateInMillis < 0) "NO DATE SELECTED" else formatter.format(selectedDateInMillis),
            modifier = Modifier
        )
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { /*TODO*/ },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                        }
                    ) { Text("OK") }
                },
                dismissButton = { TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) { Text("Cancel") } }
            )
            {
                DatePicker(
                    state = datePickerState
                )
            }
        }

    }
}

@Composable
fun MovieDetails(title: String, year: String, posterPath: String) {
    val imgUrl = "${IMAGE_BASE_URL}w500/${posterPath}"
    val screenHeight = LocalConfiguration.current.screenHeightDp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp, end = 8.dp)
            )
            Text(
                text = year,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(bottom = 4.dp, end = 8.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(imgUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height((screenHeight * 0.1).dp)
                .padding(2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewTopBar(sendReview: () -> Unit, goBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "I watched",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1F).padding(end = 8.dp)
                )
                IconButton(
                    onClick = { sendReview() },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Review Icon",
                    )
                }

            }
        },
        navigationIcon = {
            IconButton(onClick = { goBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go Back Icon",
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
}
