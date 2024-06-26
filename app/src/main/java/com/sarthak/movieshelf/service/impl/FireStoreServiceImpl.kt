package com.sarthak.movieshelf.service.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.sarthak.movieshelf.domain.model.MinimalMovieItem
import com.sarthak.movieshelf.domain.model.Review
import com.sarthak.movieshelf.domain.model.User
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

    override suspend fun getRatingAverageAndCount(movieId: Int): Flow<FetchResult<Pair<Float, Int>>> = flow {
        try {
            emit(FetchResult.Loading())
            val ratingQuery = firestore.collection("ratings")
                .whereEqualTo("movieId", movieId)

            val querySnapshot = ratingQuery.get().await()
            if (!querySnapshot.isEmpty) {
                val totalRating = querySnapshot.documents.mapNotNull { it.getDouble("rating") }.sum()
                val averageRating = totalRating / querySnapshot.size()
                emit(FetchResult.Success(Pair(averageRating.toFloat(), querySnapshot.size())))
            } else {
                emit(FetchResult.Success(Pair(-1.0F, -1)))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(e.toString()))
        }
    }

    override suspend fun getReviews(movieId: Int): Flow<FetchResult<List<Pair<Review, String>>>> = flow {
        try {
            emit(FetchResult.Loading())
            val ratingQuery = firestore.collection("reviews")
                .whereEqualTo("movieId", movieId)

            val querySnapshot = ratingQuery.get().await()
            if (!querySnapshot.isEmpty) {
                val reviewsList = querySnapshot.documents.mapNotNull {
                    val review = it.toObject(Review::class.java)
                    val username = getUsername(review!!.userId)
                    Pair(review, username)
                }
                emit(FetchResult.Success(reviewsList))
            } else {
                emit(FetchResult.Success(emptyList()))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(e.toString()))
        }
    }

    override suspend fun isMovieInWatchlist(userId: String, movieId: Int): Flow<FetchResult<Boolean>> = flow {
        try {
            emit(FetchResult.Loading())
            val userRef = firestore.collection("users").document(userId)
            val userSnapshot = userRef.get().await()
            val watchlist = userSnapshot.get("watchlist") as? List<Int>
            var isInWatchList = false
            if (watchlist != null) {
                for(item in watchlist) {
                    if(item == movieId) {
                        isInWatchList = true
                    }
                }
            }
            emit(FetchResult.Success(isInWatchList))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(e.toString()))
        }
    }
    override suspend fun updateWatchlist(
        userId: String,
        movieId: Int,
        posterPath: Any,
        title: Any,
        releaseDate: Any
    ): Flow<FetchResult<Boolean>> = flow {
        try {
            emit(FetchResult.Loading())
            val userRef = firestore.collection("users").document(userId)
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
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentWatchlist = snapshot.get("watchlist") as? List<Int> ?: emptyList()
                var isInWatchList = false
                for(item in currentWatchlist) {
                    if(item == movieId) {
                        isInWatchList = true
                    }
                }
                var updatedWatchlist: MutableList<Int> = ArrayList()
                if(isInWatchList) {
                    for(item in currentWatchlist) {
                        if(item == movieId) {
                            continue
                        }
                        updatedWatchlist.add(item)
                    }
                } else {
                    for(item in currentWatchlist) {
                        updatedWatchlist.add(item)
                    }
                    updatedWatchlist.add(movieId)
                }
                transaction.update(userRef, "watchlist", updatedWatchlist)
            }.await()
            emit(FetchResult.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(e.toString()))
        }
    }

    override suspend fun getMoviesInWatchlist(userId: String): Flow<FetchResult<List<MinimalMovieItem>>> = flow {
        try {
            emit(FetchResult.Loading())
            val userRef = firestore.collection("users").document(userId)
            val userSnapshot = userRef.get().await()
            val watchlist = userSnapshot.get("watchlist") as? List<Int> ?: emptyList()

            if (watchlist.isEmpty()) {
                emit(FetchResult.Success(emptyList()))
                return@flow
            }

            val movieDocumentSnapshots = watchlist.map { movieId ->
                firestore.collection("movies").document(movieId.toString()).get().await()
            }

            val movies = movieDocumentSnapshots.mapNotNull { docSnapshot ->
                MinimalMovieItem(
                    id = docSnapshot.getLong("movieId")?.toInt() ?: -1,
                    posterPath = docSnapshot.getString("posterPath") ?: "",
                    releaseDate = docSnapshot.getString("releaseDate") ?: "",
                    title = docSnapshot.getString("title") ?: ""
                )
            }

            emit(FetchResult.Success(movies))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(FetchResult.Error(e.toString()))
        }
    }
}