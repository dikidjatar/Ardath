package com.djatar.ardath.feature.domain.repository

import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers() : Flow<Resource<List<User>>>

    fun getUserById(userId: String) : Flow<Resource<User?>>

    fun getCurrentUser() : Flow<Resource<User?>>

    suspend fun updateProfile(user: User)

    suspend fun updateUsername(username: String)

    suspend fun getNameById(userId: String): String?

    suspend fun isUsernameTaken(username: String): Boolean
}