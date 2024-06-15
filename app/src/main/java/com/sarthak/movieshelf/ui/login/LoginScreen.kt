package com.sarthak.movieshelf.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sarthak.movieshelf.ui.EmailTextField
import com.sarthak.movieshelf.ui.ErrorCard
import com.sarthak.movieshelf.ui.LoadingScreen
import com.sarthak.movieshelf.ui.PasswordTextField
import com.sarthak.movieshelf.ui.Route

@Composable
fun LoginScreen(navController: NavController) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val state = loginViewModel.state.collectAsState()
    val email = state.value.email
    val password = state.value.password
    val error = loginViewModel.error.collectAsState()


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Text(
                text = "Login to your Movie Shelf",
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 32.dp)
            )
            EmailTextField(
                email = email,
                onEmailChange = loginViewModel::onEmailChange,
                labelText = "Email",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            )
            PasswordTextField(
                password = password,
                onPasswordChange = loginViewModel::onPasswordChange,
                labelText = "Password",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
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
                        loginViewModel.onLoginClick {
                            navController.navigate(Route.HOME_SCREEN) {
                                popUpTo(Route.LOGIN_SCREEN) { inclusive = true}
                            }
                        }
                    },
                    enabled = email.isNotBlank() && password.isNotBlank(),
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
                        navController.navigate(Route.SIGNUP_SCREEN) {
                            popUpTo(Route.LOGIN_SCREEN) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp),
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            if (error.value.isNotBlank()) {
                ErrorCard(error = error.value, modifier = Modifier.padding(8.dp))
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
