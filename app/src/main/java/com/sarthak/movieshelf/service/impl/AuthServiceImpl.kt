package com.sarthak.movieshelf.service.impl

import com.google.firebase.auth.FirebaseAuth
import com.sarthak.movieshelf.domain.model.User
import com.sarthak.movieshelf.service.AuthService
import com.sarthak.movieshelf.utils.FetchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(private val auth: FirebaseAuth, private val fireStoreServiceImpl: FireStoreServiceImpl): AuthService {
    override suspend fun getCurrentUser() = auth.currentUser

    override suspend fun signUp(email: String, password: String, username: String): Flow<FetchResult<Unit>> = flow {
        try {
            emit(FetchResult.Loading())
            if (fireStoreServiceImpl.isUsernameExists(username)) {
                emit(FetchResult.Error("Username already exists."))
                return@flow
            }

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: ""

            if(userId.isBlank()){
                emit(FetchResult.Error("Could not create user."))
                return@flow
            }

            val user = User(
                username = username,
                watchlist = emptyList()
            )

            fireStoreServiceImpl.addUser(userId, user)
            emit(FetchResult.Success(null))
        } catch (e: Exception) {
            emit(FetchResult.Error(e.toString()))
        }
    }

    override suspend fun signIn(email: String, password: String): Flow<FetchResult<Unit>> = flow {
        try {
            emit(FetchResult.Loading())
            auth.signInWithEmailAndPassword(email, password).await()
            emit(FetchResult.Success(null))
        } catch (e: Exception) {
            emit(FetchResult.Error(e.toString()))
        }
    }

    override suspend fun sendRecoveryEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun deleteAccount() {
        auth.currentUser?.delete()?.await()
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun getUsername(): Flow<FetchResult<String>> = flow {
        try {
            auth.currentUser?.let {
                emit(FetchResult.Success(fireStoreServiceImpl.getUsername(it.uid)))
                return@flow
            }
            emit(FetchResult.Success(""))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(e.toString()))
        }
    }
}