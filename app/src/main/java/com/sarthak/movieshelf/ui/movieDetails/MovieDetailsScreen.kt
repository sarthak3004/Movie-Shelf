@file:OptIn(ExperimentalMaterial3Api::class)

package com.sarthak.movieshelf.ui.movieDetails

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.sarthak.movieshelf.R
import com.sarthak.movieshelf.domain.model.MovieItem
import com.sarthak.movieshelf.domain.model.VideoItem
import com.sarthak.movieshelf.domain.model.getDirectorsList
import com.sarthak.movieshelf.domain.model.getReleaseYear
import com.sarthak.movieshelf.domain.model.getYoutubeVideoItems
import com.sarthak.movieshelf.ui.ErrorScreen
import com.sarthak.movieshelf.ui.LoadingScreen
import com.sarthak.movieshelf.ui.theme.MovieShelfTheme
import com.sarthak.movieshelf.utils.IMAGE_BASE_URL

@Composable
fun MovieDetailsScreen(navController: NavHostController, id: Int) {
    val movieDetailsViewModel: MovieDetailsViewModel = hiltViewModel()
    val movieDetailsState = movieDetailsViewModel.state.collectAsState()
    if (movieDetailsState.value.isError) {
        ErrorScreen()
    } else if (movieDetailsState.value.isLoading) {
        LoadingScreen()
    } else {
        if(movieDetailsState.value.movieItem.id != -1)
            MovieDetails(movieDetailsState.value.movieItem, navController)
    }
}

@Composable
fun MovieDetails(movieItem: MovieItem, navController: NavHostController) {
    val scrollState = rememberLazyListState()
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var imageHeightPx by remember { mutableIntStateOf(0) }
    var topAppBarAlpha by remember { mutableFloatStateOf(0F) }
    var isBackdropPresent by remember { mutableStateOf(false) }

    var detailsPadding by remember { mutableStateOf(PaddingValues(0.dp)) }
    Scaffold(
        topBar = {
            MovieTopAppBar(
                movieItem.title,
                alpha = topAppBarAlpha,
                navController
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {}

        LazyColumn(state = scrollState) {
            item {
                Box {
                    if(movieItem.backdropPath.isNotBlank()) {
                        val imgUrl = "${IMAGE_BASE_URL}w1280/${movieItem.backdropPath}"
                        val backdropPainter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imgUrl)
                                .size(Size.ORIGINAL)
                                .build()
                        )
                        val imageState = backdropPainter.state

                        if(imageState is AsyncImagePainter.State.Success) {
                            val imageBitmap = imageState.result.drawable.toBitmap()
                            isBackdropPresent = true
                            Image(
                                bitmap = imageBitmap.asImageBitmap(),
                                contentDescription = "Backdrop Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onGloballyPositioned { coordinates ->
                                        imageOffset = coordinates.localToWindow(Offset(0F, 0F))
                                        imageHeightPx = coordinates.size.height
                                    }
                                    .clickable {

                                    }

                            )
                            Divider(
                                thickness = 16.dp,
                                color = Color.Transparent,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.background
                                            )
                                        )
                                    )
                            )
                            detailsPadding = PaddingValues(0.dp)
                        } else {
                            detailsPadding = innerPadding
                            topAppBarAlpha = 1F
                        }
                    } else {
                        detailsPadding = innerPadding
                        topAppBarAlpha = 1F
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier.padding(detailsPadding)
                ) {

                    Row(modifier = Modifier.padding(bottom = 12.dp, start = 12.dp, end = 12.dp)) {
                        MovieMetaDataComponent(
                            movieItem,
                            modifier = Modifier
                                .weight(2.5F)
                                .padding(end = 8.dp)
                        )
                        Poster(movieItem, modifier = Modifier.weight(1F))
                        //TODO - Change weights for landscape mode
                    }

                    MovieDescription(movieItem, modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                    CustomDivider()
                    RatingComponent(movieItem, modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                    CustomDivider()
                    TabRowComponent(movieItem)
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
        if(isBackdropPresent) {
            LaunchedEffect(scrollState) {
                snapshotFlow { scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset }
                    .collect {
                        topAppBarAlpha = when {
                            imageOffset.y >= 0 -> 0F
                            imageOffset.y < 0 && imageOffset.y > -imageHeightPx -> -1 * (imageOffset.y / imageHeightPx.toFloat())
                            else -> 1F
                        }
                    }
            }
        }
    }
}

@Composable
fun TabRowComponent(movieItem: MovieItem) {
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val tabs = listOf("CAST + CREW", "RELATED VIDEOS", "OTHER DETAILS")

    Column {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex.intValue
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex.intValue == index,
                    onClick = { selectedTabIndex.intValue = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        when (selectedTabIndex.intValue) {
            0 -> CastAndCrewScreen(movieItem)
            1 -> RelatedVideosScreen(movieItem)
            2 -> OtherDetailsScreen(movieItem)
        }
    }
}

@Composable
fun OtherDetailsScreen(movieItem: MovieItem) {
    Column(modifier = Modifier.padding(top = 12.dp, start = 12.dp)) {
        if(movieItem.originCountry.isNotEmpty()) {
            OtherDetailsElement(
                title = "Origin Country",
                detailsList = movieItem.originCountry
            )
            CustomDivider()
        }
        if(movieItem.originalLanguage.isNotEmpty()) {
            OtherDetailsElement(
                title = "Original Language",
                detailsList = listOf(movieItem.originalLanguage)
            )
            CustomDivider()
        }
        if(movieItem.spokenLanguages.isNotEmpty()) {
            OtherDetailsElement(
                title = "Spoken Languages",
                detailsList = movieItem.spokenLanguages
            )
            CustomDivider()
        }

    }
}

@Composable
fun OtherDetailsElement(title: String, detailsList: List<String>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        for(item in detailsList) {
            Text(
                text = item,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RelatedVideosScreen(movieItem: MovieItem) {
    val videoItemList = movieItem
        .getYoutubeVideoItems()
        .sortedWith(
            compareBy (
                { it.type },
                { it.name }
            )
        )
    FlowRow(
        verticalArrangement = Arrangement.Top,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(top = 12.dp, start = 12.dp)
    ) {
        for(videoItem in videoItemList) {
            VideoComponent(videoItem = videoItem)
        }
    }
}

@Composable
fun VideoComponent(videoItem: VideoItem) {
    val context = LocalContext.current
    val url = "https://www.youtube.com/watch?v=${videoItem.key}"

    Card(
        modifier = Modifier
            .padding(end = 4.dp, bottom = 4.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
    ) {
        Text(
            text = videoItem.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun CastAndCrewScreen(movieItem: MovieItem, modifier: Modifier = Modifier) {

    Column(modifier) {
        Text(
            text = "CAST",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
        )
        LazyRow {
            items(movieItem.cast) {
                val imgUrl = if(it.profilePath.isNotBlank()) {
                    "${IMAGE_BASE_URL}w185/${it.profilePath}"
                } else ""
                PersonInfoElement(imgUrl = imgUrl, name = it.name, role = it.character)
            }
        }
        CustomDivider()
        Text(
            text = "CREW",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
        )
        LazyRow {
            items(movieItem.crew) {
                val imgUrl = if(it.profilePath.isNotBlank()) {
                    "${IMAGE_BASE_URL}w185/${it.profilePath}"
                } else ""
                PersonInfoElement(imgUrl = imgUrl, name = it.name, role = it.job)
            }
        }
    }
}

@Composable
fun PersonInfoElement(imgUrl: String, name: String, role: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(12.dp)
            .width(80.dp)
    ) {
        val imageModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1F)
            .clip(RoundedCornerShape(40.dp))
            .clickable {

            }
        if(imgUrl.isNotBlank()) {
            val personPainter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imgUrl)
                    .size(Size.ORIGINAL)
                    .build()
            )
            val imageState = personPainter.state
            if(imageState is AsyncImagePainter.State.Success) {
                val imageBitmap = imageState.result.drawable.toBitmap()
                Image(
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = "Person Image",
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.default_person),
                    contentDescription = null,
                    modifier = imageModifier.background(MaterialTheme.colorScheme.primaryContainer)
                )
            }
        } else {
            Icon(
                painter = painterResource(id = R.drawable.default_person),
                contentDescription = null,
                modifier = imageModifier.background(MaterialTheme.colorScheme.primaryContainer)
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = role,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun RatingComponent(movieItem: MovieItem, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.ratings),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        val tmdbRating = if(movieItem.voteAverage > 0) {
            movieItem.voteAverage / 2
        } else movieItem.voteAverage
        RatingCard("TMDB", tmdbRating, movieItem.voteCount)
    }
}

@Composable
fun RatingCard(source: String, averageRating: Double, voteCount: Int, modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Text(
                text = source,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1F)
                    .padding(8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1F)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star_full),
                    contentDescription = "Star Icon",
                    Modifier.size(8.dp)
                )
                Text(
                    text = averageRating.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                repeat(
                    times = 5,
                    action = {
                        Icon(
                            painter = painterResource(id = R.drawable.star_full),
                            contentDescription = "Star Icon",
                            Modifier.size(8.dp)
                        )
                    }
                )
            }
            Text(
                text = "$voteCount Ratings",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MovieDescription(movieItem: MovieItem, modifier: Modifier = Modifier) {
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }
    val descriptionIcon = when(isDescriptionExpanded) {
        false -> Icons.Default.ArrowDropDown
        true -> Icons.Default.KeyboardArrowUp
    }
    val minLines = 3
    val maxLines = when (isDescriptionExpanded) {
        false -> minLines
        true -> Int.MAX_VALUE
    }
    var showDescriptionIcon by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = movieItem.tagline.uppercase(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isDescriptionExpanded = !isDescriptionExpanded
                }
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh
                    )
                )
        ) {
            Text(
                text = movieItem.overview,
                style = MaterialTheme.typography.bodySmall,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult: TextLayoutResult ->
                    if (textLayoutResult.lineCount > minLines - 1) {
                        if (textLayoutResult.isLineEllipsized(minLines - 1)) showDescriptionIcon = true
                    }
                }
            )
            if(showDescriptionIcon) {
                Icon(
                    imageVector = descriptionIcon,
                    contentDescription = "Expand/Contract Text Icon",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreList(movieItem: MovieItem) {
    val genres = movieItem.genres
    FlowRow(
        horizontalArrangement = Arrangement.Start,
        verticalArrangement = Arrangement.Center
    ) {
        for (i in genres) {
            Card(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
            ) {
                Text(
                    text = i,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun Poster(movieItem: MovieItem, modifier: Modifier = Modifier) {
    if(movieItem.posterPath.isNotBlank()) {
        val imgUrl = "${IMAGE_BASE_URL}w500/${movieItem.posterPath}"
        val posterPainter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imgUrl)
                .size(Size.ORIGINAL)
                .build()
        )
        val imageState = posterPainter.state

        if(imageState is AsyncImagePainter.State.Success) {
            val imageBitmap = imageState.result.drawable.toBitmap()
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Poster Image",
                contentScale = ContentScale.FillWidth,
                modifier = modifier
                    .fillMaxWidth()
                    .clickable {

                    }
            )
        }
    }
}

@Composable
fun MovieMetaDataComponent(movieItem: MovieItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = movieItem.title,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = stringResource(R.string.directed_by),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = movieItem.getDirectorsList().joinToString(","),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Text(
                text = movieItem.getReleaseYear(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 16.dp, bottom = 12.dp)
            )
            Text(
                text = "${movieItem.runtime} mins",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 48.dp)
            )
        }
        GenreList(movieItem)

    }
}

@Composable
fun CustomDivider() {
    Divider(
        thickness = 2.dp,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopAppBar(title: String, alpha: Float, navController: NavHostController) {
    val topAppBarContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back Icon",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            if(alpha > 0.5F) {
                Text(text = title)
            }
        },
        colors = topAppBarColors(containerColor = topAppBarContainerColor),
    )
}

@Preview
@Composable
fun MyPreview()
{
    MovieShelfTheme {

    }
}
