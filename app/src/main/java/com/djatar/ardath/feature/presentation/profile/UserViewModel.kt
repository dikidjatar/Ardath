package com.djatar.ardath.feature.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.djatar.ardath.core.Resource
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.domain.models.UserState
import com.djatar.ardath.feature.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _state = MutableStateFlow(UserState())
    val state = _state.asStateFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun getUsers() {

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            repository.getUsers().collect { resource ->
                if (resource is Resource.Error) {
                    Log.e(TAG, resource.message.toString())
                    _state.update { it.copy(isLoading = false, error = resource.message ?: "Unknown error") }
                    return@collect
                }
                _state.update {
                    it.copy(users = resource.data ?: emptyList(), isLoading = false, error = null)
                }
            }
        }
    }

    fun getUserById(userId: String) {
        if (_user.value != null && _user.value?.id == userId) {
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            repository.getUserById(userId).collectLatest { resource ->
                _state.update {
                    when (resource) {
                        is Resource.Error -> it.copy(isLoading = false, error = resource.message ?: "Unknown error")
                        is Resource.Success -> {
                            _user.value = resource.data
                            it.copy(isLoading = false, error = null)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "UserViewModel"
    }
}