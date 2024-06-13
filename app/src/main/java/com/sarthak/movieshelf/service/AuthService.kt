package com.sarthak.movieshelf.service

import com.google.firebase.auth.FirebaseUser
import com.sarthak.movieshelf.utils.FetchResult
import kotlinx.coroutines.flow.Flow

interface AuthService {
    suspend fun getCurrentUser(): FirebaseUser?
    suspend fun signUp(email: String, password: String, username: String): Flow<FetchResult<Unit>>
    suspend fun signIn(email: String, password: String): Flow<FetchResult<Unit>>
    suspend fun sendRecoveryEmail(email: String)
    suspend fun deleteAccount()
    suspend fun signOut()
    suspend fun getUsername(): String
}