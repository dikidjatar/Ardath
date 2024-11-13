package com.djatar.ardath.feature.domain.auth

import com.google.firebase.auth.FirebaseUser

interface BaseAuthenticator {
    suspend fun signUpWithEmailAndPassword(email: String, password: String): FirebaseUser?

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser?

    fun getUser(): FirebaseUser?

    fun signOut(): FirebaseUser?
}