package com.sarthak.movieshelf.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.movieshelf.service.AuthService
import com.sarthak.movieshelf.service.FireStoreService
import com.sarthak.movieshelf.utils.FetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import isValidEmail
import isValidPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import passwordMatches
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService,
    private val storeService: FireStoreService
): ViewModel() {
    private val _state = MutableStateFlow(SignUpUiState())
    val state: StateFlow<SignUpUiState> = _state

    private val _userError = MutableStateFlow("")
    val userError: StateFlow<String> = _userError

    fun onEmailChange(newValue: String) {
        _state.value = _state.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        _state.value = _state.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        _state.value = _state.value.copy(repeatPassword = newValue)
    }

    fun onUsernameChange(newValue: String) {
        _state.value = _state.value.copy(username = newValue)
    }

    fun onSignUpClick(navigate:() -> Unit) {
        if (!_state.value.email.isValidEmail()) {
            _userError.value = "Insert a valid email address."
            return
        }

        if (!_state.value.password.isValidPassword()) {
            _userError.value = "Your password should have at least six digits and include one digit, one lower case letter and one upper case letter."
            return
        }

        if (!_state.value.password.passwordMatches(_state.value.repeatPassword)) {
            _userError.value = "Passwords don't match."
            return
        }

        if (_state.value.username.isBlank()) {
            _userError.value = "Username is empty"
            return
        }
        _userError.value = ""

        viewModelScope.launch {
            try {
                authService.signUp(_state.value.email, _state.value.password, _state.value.username).collect{fetchResult ->
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
                _userError.value = e.toString()
            }
        }
    }
}

data class SignUpUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val username: String = "",
    val isError: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)