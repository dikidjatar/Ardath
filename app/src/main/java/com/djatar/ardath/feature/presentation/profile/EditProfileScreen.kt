package com.djatar.ardath.feature.presentation.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.R
import com.djatar.ardath.core.Position
import com.djatar.ardath.core.SettingsEntity
import com.djatar.ardath.core.presentation.components.BackButton
import com.djatar.ardath.core.presentation.components.defaultAppBarColor
import com.djatar.ardath.feature.domain.models.UpdateProfileRequest
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.presentation.settings.components.SettingsItem

private const val TAG = "EditProfileScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel,
    paddingValues: PaddingValues,
    onBackPressed: () -> Unit
) {
    val state by userViewModel.userState.collectAsStateWithLifecycle()
    val user = state.currentUser

    var name by remember(user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var bio by remember(user?.bio) { mutableStateOf(user?.bio.orEmpty()) }

    val updatedProfile = remember(name, bio) {
        UpdateProfileRequest(user ?: User())
            .setName(name)
            .setBio(bio)
            .build()
    }
    val showSaveButton by remember(user, updatedProfile, state.isUpdating) {
        mutableStateOf(
            user != null
                    && updatedProfile != user
                    && !state.isUpdating
        )
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(state.isUpdating) {
        if (state.isUpdating) keyboardController?.hide()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.defaultAppBarColor(),
                title = { Text(text = stringResource(R.string.edit_profile)) },
                navigationIcon = { BackButton(onBackPressed) },
                actions = {
                    if (showSaveButton) {
                        IconButton(onClick = { userViewModel.updateProfile(updatedProfile) }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                        }
                    }
                    if (state.isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .size(25.dp),
                            strokeWidth = 3.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) {
        if (user != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                item {
                    SettingsItem(SettingsEntity.TextField(
                        value = name,
                        onValueChange = { str -> name = str },
                        maxLines = 1,
                        enabled = !state.isUpdating,
                        label = stringResource(R.string.label_name),
                        screenPosition = Position.Top
                    ))
                }
                item {
                    SettingsItem(SettingsEntity.TextField(
                        value = bio,
                        onValueChange = { str -> bio = str },
                        maxLines = 5,
                        enabled = !state.isUpdating,
                        label = stringResource(R.string.label_bio),
                        screenPosition = Position.Bottom
                    ))
                }
            }
        }
    }
}