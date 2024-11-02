package com.djatar.ardath.core.presentation.listeners

import android.util.Log
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.trySendBlocking

class UserListener(
    private val currentUserId: String?,
    private val channel: SendChannel<Resource<List<User>>>
) : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        val users = mutableListOf<User>()
        snapshot.children.forEach { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            user?.takeIf { user.id != currentUserId }?.let {
                users.add(it)
            }
        }
        channel.trySendBlocking(Resource.Success(users))
    }

    override fun onCancelled(error: DatabaseError) {
        Log.e(TAG, "Error get users", error.toException())
        channel.trySendBlocking(Resource.Error("Error get users"))
    }

    companion object {
        private const val TAG = "UserListener"
    }
}