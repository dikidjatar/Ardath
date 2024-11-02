package com.djatar.ardath.feature.data.repository

import android.util.Log
import com.djatar.ardath.core.Resource
import com.djatar.ardath.core.presentation.listeners.UserListener
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : UserRepository {

    override fun getUsers(): Flow<Resource<List<User>>> {
        return callbackFlow {
            val currentUser = auth.currentUser
            val query = database.reference.child("users")
                .limitToFirst(20)
            val listener = UserListener(currentUser?.uid, this)
            query.addListenerForSingleValueEvent(listener)
            awaitClose { query.removeEventListener(listener) }
        }
    }

    override fun getUserById(userId: String): Flow<Resource<User?>> {
        return callbackFlow {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.getValue(User::class.java)
                    trySendBlocking(Resource.Success(result))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching user", error.toException())
                    trySendBlocking(Resource.Error("Error fetching user"))
                }
            }
            val ref = database.reference.child("users").child(userId)
            ref.addListenerForSingleValueEvent(listener)

            awaitClose { ref.removeEventListener(listener) }
        }
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}