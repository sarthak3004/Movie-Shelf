@file:OptIn(ExperimentalMaterial3Api::class)

package com.sarthak.movieshelf.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarthak.movieshelf.R
import com.sarthak.movieshelf.ui.theme.MovieShelfTheme

@Composable
fun MovieScreen() {
//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val scrollState = rememberLazyListState()
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var imageHeightPx by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    var topAppBarAlpha by remember { mutableFloatStateOf(0F) }
    Scaffold(
        topBar = {
            MovieTopAppBar(
//                scrollBehavior.state.collapsedFraction, scrollBehavior,
                alpha = topAppBarAlpha,
                modifier = Modifier
                    .animateContentSize(
                        spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessHigh
                        )
                    )
            )
        },
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
            innerPadding ->
        if(false) {
            Column(modifier = Modifier.padding(innerPadding)) {

            }
        }
        LazyColumn(state = scrollState) {
            item {
                Box() {
                    Image(
                        painter = painterResource(
                            id = R.drawable.mockbackdrop1280
                        ),
                        contentDescription = "Movie Backdrop",
                        contentScale = ContentScale.Crop,
//                        alpha = 1 - collapsedFraction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                imageOffset = coordinates.localToWindow(Offset(0F, 0F))
                                imageHeightPx = coordinates.size.height
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
                }
            }
            item {
                Column(
                    modifier = Modifier
                ) {

                    Row(modifier = Modifier.padding(bottom = 12.dp, start = 12.dp, end = 12.dp)) {
                        MovieMetaDataComponent(
                            modifier = Modifier
                                .weight(2.5F)
                                .padding(end = 8.dp)
                        )
                        Poster(modifier = Modifier.weight(1F))
                    }

                    MovieDescription(modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                    CustomDivider()
                    RatingComponent(modifier = Modifier.padding(start = 12.dp, end = 12.dp))
                    CustomDivider()
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    Text("Composable", fontSize = 24.sp)
                    TabRowComponent()
                }
            }
        }
        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.firstVisibleItemIndex to scrollState.firstVisibleItemScrollOffset }
                .collect {
                    topAppBarAlpha = when {
                        imageOffset.y >= 0 -> 0F
                        imageOffset.y < 0 && imageOffset.y > -imageHeightPx ->  -1 * (imageOffset.y / imageHeightPx.toFloat())
                        else -> 1F
                    }
                }
        }
    }
}

@Composable
fun TabRowComponent() {
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val tabs = listOf("CAST + CREW", "DETAILS", "GENRE")

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
            0 -> CastAndCrewScreen()
            1 -> OtherDetailsScreen()
            2 -> GenreScreen()
        }
    }
}

@Composable
fun GenreScreen() {
    Text(text = "Genre Screen")
}

@Composable
fun OtherDetailsScreen() {
    Text(text = "Details Screen")
}

@Composable
fun CastAndCrewScreen() {
    Text(text = "Cast and Crew Screen")
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

@Composable
fun RatingComponent(modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.ratings),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RatingCard()
    }
}

@Composable
fun RatingCard(modifier: Modifier = Modifier) {
    Card(
        shape = MaterialTheme.shapes.extraSmall,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
        ) {
            Text(
                text = "TMDB",
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
                    text = "3.7",
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
                text = "100 Ratings",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun MovieDescription(modifier: Modifier = Modifier) {
    var isDescriptionExpanded by remember {
        mutableStateOf(false)
    }
    val descriptionIcon = when(isDescriptionExpanded) {
        false -> Icons.Default.ArrowDropDown
        true -> Icons.Default.KeyboardArrowUp
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Tagline Tagline Tagline".uppercase(),
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
                text = "Description Description Description Description Description Description Description Description Description Description Description Description Description Description. Description Description Description Description Description Description Description Description Description Description Description Description Description Description.",
                style = MaterialTheme.typography.bodySmall,
                maxLines = when (isDescriptionExpanded) {
                    false -> 3
                    true -> Int.MAX_VALUE
                },
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = descriptionIcon,
                contentDescription = "Expand Text Icon",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun Poster(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.mockposter500),
        contentDescription = "Movie Poster",
        contentScale = ContentScale.FillWidth,
        modifier = modifier
            .fillMaxWidth(),
    )
}

@Composable
fun MovieMetaDataComponent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Movie Title Movie Title Movie Title",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = stringResource(R.string.directed_by),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Director Name",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Text(
                text = "YEAR",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 24.dp)
            )
            Text(
                text = "XXX mins",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 48.dp)
            )
            Trailer()
        }
    }
}

@Composable
fun Trailer() {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        pushStringAnnotation(tag = "URL", annotation = "https://www.youtube.com/watch?v=lmN1Op8ygno")
        append("TRAILER")
        pop()
    }
    val trailerTextStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        },
        style = trailerTextStyle,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieTopAppBar(
    alpha: Float,
    modifier: Modifier = Modifier)
{
    val topAppBarContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha)

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back Icon",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        title = {
            if(alpha > 0.5F) {
                Text("Movie Title Movie Title Movie Title")
            }
        },
        colors = topAppBarColors(containerColor = topAppBarContainerColor),
    )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyPreview() {
    MovieShelfTheme {
        MovieScreen()
    }
}
