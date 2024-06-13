package com.sarthak.movieshelf.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.service.AuthService
import com.sarthak.movieshelf.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
): ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun onEmailChange(newValue: String) {
        _state.value = _state.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _state.value = _state.value.copy(password = newValue)
    }

    fun onLoginClick(navigate: () -> Unit) {

        viewModelScope.launch {
            try {
                authService.signIn(_state.value.email, _state.value.password).collect{fetchResult ->
                    when(fetchResult) {
                        is FetchResult.Error -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = fetchResult.message
                            )
                        }
                        is FetchResult.Loading -> {
                            _state.value = _state.value.copy(
                                isLoading = true,
                                isError = false,
                                errorMessage = ""
                            )
                        }
                        is FetchResult.Success -> {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                isError = false,
                                errorMessage = ""
                            )
                            navigate()
                        }
                    }
                }
            } catch (e: Exception) {
                _error.value = e.toString()
            }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)