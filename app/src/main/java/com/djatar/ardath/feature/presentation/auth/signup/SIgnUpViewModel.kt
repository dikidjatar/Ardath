package com.djatar.ardath.feature.presentation.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.djatar.ardath.feature.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.util.nextAlphanumericString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SIgnUpViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<SignUpState>(SignUpState.Nothing)
    val state = _state.asStateFlow()

    fun signUp(name: String, email: String, password: String) {
        _state.value = SignUpState.Loading

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.user?.let { user ->
                        user.updateProfile(
                            UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                        ).addOnCompleteListener {
                            addNewUserToDatabase(
                                user.uid ,
                                user.displayName ?: name,
                                user.email ?: email
                            )
                        }
                        return@addOnCompleteListener
                    }
                    _state.value = SignUpState.Error()
                } else {
                    _state.value = SignUpState.Error(task.exception)
                }
            }
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
                _state.value = SignUpState.Success
            } else {
                Log.e(TAG, "failed save data user", task.exception)
                _state.value = SignUpState.Error(
                    Exception("Failed to save user data", task.exception?.cause)
                )
            }
        }
    }

    fun clearState() {
        _state.value = SignUpState.Nothing
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}

sealed class SignUpState {
    data object Nothing : SignUpState()
    data object Loading : SignUpState()
    data object Success : SignUpState()
    data class Error(val error: Throwable? = null) : SignUpState()
}