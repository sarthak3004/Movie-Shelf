package com.sarthak.movieshelf.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sarthak.movieshelf.ui.EmailTextField
import com.sarthak.movieshelf.ui.ErrorCard
import com.sarthak.movieshelf.ui.LoadingScreen
import com.sarthak.movieshelf.ui.PasswordTextField
import com.sarthak.movieshelf.ui.Route

@Composable
fun SignUpScreen(navController: NavController) {
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val state = signUpViewModel.state.collectAsState()
    val email = state.value.email
    val password = state.value.password
    val username = state.value.username
    val repeatPassword = state.value.repeatPassword
    val userError = signUpViewModel.userError.collectAsState()


    Box {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {

                Text(
                    text = "Movie Shelf",
                    style = MaterialTheme.typography.displayMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 32.dp)
                )
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 32.dp)
                )
                TextField(
                    value = username,
                    onValueChange = signUpViewModel::onUsernameChange,
                    label = {
                        Text(
                            text = "Username",
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
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                )

                EmailTextField(
                    email = email,
                    onEmailChange = signUpViewModel::onEmailChange,
                    labelText = "Email",
                    imeAction = ImeAction.Next,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                )
                PasswordTextField(
                    password = password,
                    onPasswordChange = signUpViewModel::onPasswordChange,
                    labelText = "Password",
                    imeAction = ImeAction.Next,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                )
                PasswordTextField(
                    password = repeatPassword,
                    onPasswordChange = signUpViewModel::onRepeatPasswordChange,
                    labelText = "Confirm Password",
                    imeAction = ImeAction.Done,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 12.dp
                        )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Button(
                        onClick = {
                            signUpViewModel.onSignUpClick {
                                navController.navigate(Route.HOME_SCREEN) {
                                    popUpTo(Route.SIGNUP_SCREEN) { inclusive = true }
                                }
                            }
                        },
                        enabled = email.isNotBlank() && password.isNotBlank() && repeatPassword.isNotBlank() && username.isNotBlank(),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Go",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Button(
                        onClick = {
                            navController.navigate(Route.LOGIN_SCREEN) {
                                popUpTo(Route.SIGNUP_SCREEN) { inclusive = true}
                            }
                        },
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                if (userError.value.isNotBlank()) {
                    ErrorCard(error = userError.value, modifier = Modifier.padding(8.dp))
                }
                if(state.value.isError && state.value.errorMessage.isNotBlank()) {
                    ErrorCard(error = state.value.errorMessage, modifier = Modifier.padding(8.dp))
                }
            }
        }
        if(state.value.isLoading) {
            Surface(
                color = Color.Black.copy(alpha = 0.5F),
                modifier = Modifier.fillMaxSize()
            ) {
                LoadingScreen()
            }
        }
    }
}

