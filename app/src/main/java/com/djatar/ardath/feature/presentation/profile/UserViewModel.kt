package com.djatar.ardath.feature.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.domain.models.UserState
import com.djatar.ardath.feature.domain.models.UsernameState
import com.djatar.ardath.feature.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    private val _usernameState = MutableStateFlow(UsernameState())
    val usernameState = _usernameState.asStateFlow()

    private var usernameJob: Job? = null

    fun getUsers() {

        _userState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getUsers().collect { resource ->
                if (resource is Resource.Error) {
                    Log.e(TAG, resource.message.toString())
                    _userState.update { it.copy(isLoading = false, error = resource.message ?: "Unknown error") }
                    return@collect
                }
                _userState.update {
                    it.copy(users = resource.data ?: emptyList(), isLoading = false, error = null)
                }
            }
        }
    }

    fun getUserById(userId: String) {

        _userState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getUserById(userId).collectLatest { resource ->
                _userState.update {
                    when (resource) {
                        is Resource.Error ->
                            it.copy(
                                user = resource.data,
                                isLoading = false,
                                error = resource.message ?: "Unknown error"
                            )
                        is Resource.Success -> {
                            it.copy(user = resource.data, isLoading = false, error = null)
                        }
                    }
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            repository.getCurrentUser().collectLatest { res ->
                _userState.update {
                    it.copy(currentUser = res.data, isLoading = false, isUpdating = false)
                }
            }
        }
    }

    fun updateProfile(user: User) {
        _userState.update { it.copy(isUpdating = true) }
        viewModelScope.launch {
            try { repository.updateProfile(user).also { getCurrentUser() } }
            catch (e: Exception) {
                Log.e(TAG, "Update user failure", e)
                _userState.update { it.copy(isUpdating = false) }
            }
        }
    }

    fun updateUsername(username: String) {
        _usernameState.update { UsernameState(isLoading = true) }
        viewModelScope.launch {
            _usernameState.update {
                try {
                    repository.updateUsername(username)
                    getCurrentUser()
                    it.copy(isLoading = false)
                } catch (e: Exception) {
                    Log.e(TAG, "updateUsernameError", e)
                    it.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun checkingUsername(username: String) {
        if (username.isEmpty() || username.trim() == _userState.value.currentUser?.username) {
            return
        }

        _usernameState.update { UsernameState(isChecking = true) }

        usernameJob?.cancel()

        usernameJob = viewModelScope.launch {
            try {
                delay(100)
                repository.isUsernameTaken(username).also { isUsernameTaken ->
                    _usernameState.update {
                        it.copy(isUsernameTaken = isUsernameTaken, isChecking = false)
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _usernameState.update {
                        it.copy(isUsernameTaken = false, isChecking = false, error = e.message)
                    }
                }
            }
        }
    }

    fun clearUsernameState() = _usernameState.update { UsernameState() }

    fun clear() {
        _userState.update { UserState() }
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}