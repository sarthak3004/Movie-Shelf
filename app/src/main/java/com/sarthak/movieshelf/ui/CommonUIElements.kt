package com.sarthak.movieshelf.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.utils.IMAGE_BASE_URL
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String = "ERROR!!!", modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}
@Composable
fun MoviePosterForList(item: MinimalMovieItem, navController: NavHostController, modifier: Modifier = Modifier) {
    val imgUrl = "${IMAGE_BASE_URL}w500/${item.posterPath}"
    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(imgUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                navController.navigate(
                    "${Route.MOVIE_DETAILS_SCREEN}?id=${item.id}"
                )
            }

    )
}

@Composable
fun MoviePosterList(
    moviesList: List<MinimalMovieItem>,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height((screenHeight * 0.2).dp)
    ) {
        items(moviesList) {item ->
            MoviePosterForList(item, navController)
        }
    }
}

@Composable
fun EmailTextField(email: String, onEmailChange: (String) -> Unit, labelText: String, modifier: Modifier = Modifier) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelSmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    )
}

@Composable
fun PasswordTextField(
    password: String,
    onPasswordChange: (String) -> Unit,
    labelText: String,
    modifier: Modifier = Modifier
) {
    var showPassword by remember { mutableStateOf(false) }
    val passwordVisualTransformation = remember { PasswordVisualTransformation() }
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        visualTransformation = if(showPassword) {
            VisualTransformation.None
        } else {
            passwordVisualTransformation
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Password,
                contentDescription = "Password Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = { showPassword = !showPassword }) {
                Icon(
                    imageVector = if(showPassword) {
                        Icons.Default.Visibility
                    } else {
                        Icons.Default.VisibilityOff
                    },
                    contentDescription = "Password Visibility Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    )
}

@Composable
fun ReviewElement(
    username: String = "",
    rating: Float = 0.0F,
    reviewText: String = "",
    viewingDateInMillis: Long? = null,
    modifier: Modifier
) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column(
                modifier = Modifier.weight(1F)
            ) {
                Text(
                    text = username,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                viewingDateInMillis?.let {
                    Text(
                        text = formatter.format(it),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(1.dp)
            ) {

                Text(
                    text = rating.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        Text(
            text = reviewText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

