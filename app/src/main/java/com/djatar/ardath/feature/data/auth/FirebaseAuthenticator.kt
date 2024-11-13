package com.djatar.ardath.feature.data.auth

import com.djatar.ardath.feature.domain.auth.BaseAuthenticator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticator : BaseAuthenticator {
    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser? {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        return Firebase.auth.currentUser
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser? {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
        return Firebase.auth.currentUser
    }

    override fun getUser(): FirebaseUser? {
        return Firebase.auth.currentUser
    }

    override fun signOut(): FirebaseUser? {
        Firebase.auth.signOut()
        return Firebase.auth.currentUser
    }
}