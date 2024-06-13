package com.sarthak.movieshelf.service.impl

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.sarthak.movieshelf.domain.model.Review
import com.sarthak.movieshelf.domain.model.User
import com.sarthak.movieshelf.service.AuthService
import com.sarthak.movieshelf.service.FireStoreService
import com.sarthak.movieshelf.utils.FetchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireStoreServiceImpl @Inject constructor(private val firestore: FirebaseFirestore): FireStoreService {
    override suspend fun addUser(userId: String, user: User) {
        try {
            firestore.collection("users").document(userId).set(user).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun isUsernameExists(username: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun  getUsername(userId: String): String {
        return try {
            val querySnapshot = firestore.collection("users").document(userId).get().await()
            val username = querySnapshot.getString("username")
            username!!
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun addReview(
        userId: String,
        movieId: Int,
        title: String,
        posterPath: String,
        releaseDate: String,
        reviewText: String,
        ratingValue: Float,
        viewingDate: Long?
    ): Flow<FetchResult<String>> = flow {
        try {
            emit(FetchResult.Loading())
            val moviesRef  = firestore.collection("movies").document(movieId.toString())
            val movieDoc = moviesRef.get().await()
            if(!movieDoc.exists()) {
                val movieData = hashMapOf(
                    "movieId" to movieId,
                    "title" to title,
                    "posterPath" to posterPath,
                    "releaseDate" to releaseDate
                )
                moviesRef.set(movieData)
            }

            val reviewData = hashMapOf(
                "userId" to userId,
                "movieId" to movieId,
                "reviewText" to reviewText,
                "rating" to ratingValue
            )
            viewingDate?.let {
                reviewData["viewingDateInMillis"] = it
            }
            firestore.collection("reviews").add(reviewData).await()

            val ratingQuery = firestore.collection("ratings")
                .whereEqualTo("userId", userId)
                .whereEqualTo("movieId", movieId)

            val ratingQuerySnapshot = ratingQuery.get().await()
            if(ratingQuerySnapshot.isEmpty) {
                val ratingData = hashMapOf(
                    "userId" to userId,
                    "movieId" to movieId,
                    "rating" to ratingValue
                )
                firestore.collection("ratings").add(ratingData).await()
            } else {
                for (document in ratingQuerySnapshot.documents) {
                    document.reference.update("rating", ratingValue).await()
                }
            }

            emit(FetchResult.Success(data = "Review Submitted Successfully"))
        } catch (e: Exception) {
            emit(FetchResult.Error(message = e.toString()))
        }
    }

    override suspend fun getRatingAverageAndCount(movieId: Int): Pair<Float, Int> {
        var result = Pair<Float, Int>(-1.0F, -1)
        return try {
            val ratingQuery = firestore.collection("ratings")
                .whereEqualTo("movieId", movieId)

            val querySnapshot = ratingQuery.get().await()
            if (!querySnapshot.isEmpty) {
                val totalRating = querySnapshot.documents.mapNotNull { it.getDouble("rating") }.sum()
                val averageRating = totalRating / querySnapshot.size()
                result = result.copy(first = averageRating.toFloat(), second = querySnapshot.size())
                result
            } else {
                result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result
        }
    }

    override suspend fun getReviews(movieId: Int): List<Pair<Review, String>> {
        return try {
            val ratingQuery = firestore.collection("reviews")
                .whereEqualTo("movieId", movieId)

            val querySnapshot = ratingQuery.get().await()
            if (!querySnapshot.isEmpty) {
                val reviewsList = querySnapshot.documents.mapNotNull {
                    val review = it.toObject(Review::class.java)
                    val username = getUsername(review!!.userId)
                    Pair<Review, String>(review, username)
                }
                reviewsList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}