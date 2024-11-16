package com.djatar.ardath.feature.data.repository

import android.net.Uri
import android.util.Log
import com.djatar.ardath.core.Resource
import com.djatar.ardath.core.presentation.listeners.UserListener
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : UserRepository {

    override fun getUsers(): Flow<Resource<List<User>>> = callbackFlow {
        val query = database.reference.child("users").limitToFirst(20)
        val listener = UserListener(auth.currentUser?.uid, this)

        query.addListenerForSingleValueEvent(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    override fun getUserById(userId: String): Flow<Resource<User?>> = callbackFlow {
        val ref = database.reference.child("users").child(userId)
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

        ref.addListenerForSingleValueEvent(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun getCurrentUser(): Flow<Resource<User?>> {
        val uid = auth.currentUser?.uid ?: return flowOf(Resource.Success(null))
        return getUserById(uid)
    }

    override suspend fun updateProfile(user: User) {
        val currentUser = auth.currentUser!!
        val profileUpdates = UserProfileChangeRequest.Builder()
            .apply {
                if (!user.photoUrl.isNullOrEmpty()) photoUri = Uri.parse(user.photoUrl)
                if (user.name.isNotEmpty()) displayName = user.name
            }.build()
        currentUser.updateProfile(profileUpdates).await()
        database.reference.child("users").child(currentUser.uid)
            .setValue(user).await()
    }

    override suspend fun updateUsername(username: String) {
        val userId = auth.currentUser?.uid!!
        database.reference.child("users").child(userId)
            .child("username").setValue(username).await()
    }

    override suspend fun getNameById(userId: String): String? {
        val snapshot = database.reference.child("users").child(userId).get().await()
        return snapshot.child("name").getValue(String::class.java)
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        Log.d(TAG, "checkingUsername($username)")
        val snapshot = database.reference
            .child("users")
            .orderByChild("username").equalTo(username).get().await()
        return snapshot.exists()
    }

    companion object {
        private const val TAG = "UserRepository"
    }
}