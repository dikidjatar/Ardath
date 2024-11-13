package com.djatar.ardath.feature.domain.repository

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface BaseAuthRepository {
    suspend fun signUpWithEmailAndPassword(email: String, password: String): FirebaseUser?

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser?

    fun getCurrentUser(): FirebaseUser?

    fun signOut(): FirebaseUser?
}