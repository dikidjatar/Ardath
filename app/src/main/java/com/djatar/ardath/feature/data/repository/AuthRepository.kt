package com.djatar.ardath.feature.data.repository

import com.djatar.ardath.feature.domain.auth.BaseAuthenticator
import com.djatar.ardath.feature.domain.repository.BaseAuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authenticator: BaseAuthenticator
) : BaseAuthRepository {
    override suspend fun signUpWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser? {
        return authenticator.signUpWithEmailAndPassword(email, password)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): FirebaseUser? {
        return authenticator.signInWithEmailAndPassword(email, password)
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authenticator.getUser()
    }

    override fun signOut(): FirebaseUser? {
        return authenticator.signOut()
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}