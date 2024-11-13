package com.djatar.ardath.feature.presentation.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djatar.ardath.ArdathApp.Companion.context
import com.djatar.ardath.R
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.domain.repository.BaseAuthRepository
import com.djatar.ardath.feature.presentation.auth.components.AuthError
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.util.nextAlphanumericString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: BaseAuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Nothing)
    val state = _state.asStateFlow()

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        when {
            email.isEmpty() ->
                _state.value = AuthState.ErrorCode(AuthError.EMPTY_EMAIL)
            password.isEmpty() ->
                _state.value = AuthState.ErrorCode(AuthError.EMPTY_PASSWORD)
            else -> try {
                _state.value = AuthState.Loading
                repository.signInWithEmailAndPassword(email, password)?.let { clearState() }
                    ?: _state.update { AuthState.Error(context.getString(R.string.sign_in_failure)) }
            } catch (e: Exception) {
                _state.value = AuthState.Error(getError(e))
            }
        }
    }

    fun signUpUser(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ) = viewModelScope.launch {
        when {
            name.isEmpty() ->
                _state.value = AuthState.ErrorCode(AuthError.EMPTY_NAME)
            email.isEmpty() ->
                _state.value = AuthState.ErrorCode(AuthError.EMPTY_EMAIL)
            password.isEmpty() ->
                _state.value = AuthState.ErrorCode(AuthError.EMPTY_PASSWORD)
            password != confirmPassword ->
                _state.value = AuthState.ErrorCode(AuthError.CONFIRM_PASSWORD)
            else -> try {
                _state.value = AuthState.Loading
                repository.signUpWithEmailAndPassword(email, password)?.let { user ->
                    user.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                    ).addOnCompleteListener {
                        addNewUserToDatabase(
                            user.uid,
                            user.displayName ?: name,
                            user.email ?: email
                        )
                        clearState()
                    }
                } ?: _state.update { AuthState.Error(context.getString(R.string.sign_up_failure)) }
            } catch (e: Exception) {
                _state.value = AuthState.Error(getError(e))
            }
        }
    }

    fun signOut() = viewModelScope.launch {
        try {
            repository.signOut()
        } catch (e: Exception) {
            _state.value = AuthState.Error(getError(e))
        }
    }

    private fun getError(e: Exception): String =
        e.toString().split(":").toTypedArray()[1]

    fun clearState() {
        _state.value = AuthState.Nothing
    }

    private fun addNewUserToDatabase(userId: String, name: String, email: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users")
        val username =
            "${name.replace(" ", "")}.${Random.nextAlphanumericString(6)}"
        val newUser = User(
            id = userId,
            name = name,
            username = username,
            email = email,
            status = "online",
            lastSeen = System.currentTimeMillis()
        )

        dbRef.child(userId).setValue(newUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "success to save data user")
            } else {
                Log.e(TAG, "failed save data user", task.exception)
            }
        }
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}

sealed interface AuthState {
    data object Nothing : AuthState
    data object Loading : AuthState
    data class ErrorCode(val code: Int) : AuthState
    data class Error(val error: String) : AuthState
}