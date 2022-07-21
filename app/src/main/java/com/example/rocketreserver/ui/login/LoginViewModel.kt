package com.example.rocketreserver.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.rocketreserver.LoginMutation
import com.example.rocketreserver.core.Resource
import com.example.rocketreserver.core.http
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apolloClient: ApolloClient
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<Resource<LoginMutation.Login>>(Resource.Success(LoginMutation.Login(null)))
    val uiState: SharedFlow<Resource<LoginMutation.Login>> = _uiState

    fun login(email: String) {
        viewModelScope.launch {
            if (email.trim().isEmpty() || !email.contains("@")) {
                _uiState.emit(Resource.Error(EmailInvalidException()))
                return@launch
            }
            http(
                call = apolloClient.mutation(LoginMutation(Optional.presentIfNotNull(email))),
                flow = _uiState,
                map = { it.login }
            )
        }
    }
}

class EmailInvalidException : Exception()