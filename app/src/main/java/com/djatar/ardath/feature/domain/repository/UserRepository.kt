package com.djatar.ardath.feature.domain.repository

import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUsers() : Flow<Resource<List<User>>>

    fun getUserById(userId: String) : Flow<Resource<User?>>
}