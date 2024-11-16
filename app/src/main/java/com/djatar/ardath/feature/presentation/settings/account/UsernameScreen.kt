package com.djatar.ardath.feature.presentation.settings.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.djatar.ardath.R
import com.djatar.ardath.core.SettingsEntity
import com.djatar.ardath.core.presentation.components.BackButton
import com.djatar.ardath.feature.presentation.profile.UserViewModel
import com.djatar.ardath.feature.presentation.settings.components.SettingsItem

private const val TAG = "UsernameScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameScreen(
    userViewModel: UserViewModel,
    onBackPressed: () -> Unit
) {
    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val usernameState by userViewModel.usernameState.collectAsStateWithLifecycle()

    var username by remember(userState.currentUser?.username) {
        mutableStateOf(userState.currentUser?.username.orEmpty())
    }
    val showSaveButton by remember(usernameState) {
        mutableStateOf(
            !usernameState.isLoading
                    && !usernameState.isChecking
                    && usernameState.isUsernameTaken == false
                    && usernameState.error.isNullOrEmpty()
        )
    }

    DisposableEffect(username) {
        userViewModel.checkingUsername(username)
        onDispose { userViewModel.clearUsernameState() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.setting_username)) },
                navigationIcon = { BackButton(onBackPressed) },
                actions = {
                    if (showSaveButton) {
                        IconButton(onClick = { userViewModel.updateUsername(username) }) {
                            Icon(imageVector = Icons.Outlined.Check, contentDescription = null)
                        }
                    }
                    if (usernameState.isLoading) {
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
        Column(
            modifier = Modifier.padding(it)
        ) {
            SettingsItem(SettingsEntity.Header(stringResource(R.string.set_username)))
            SettingsItem(
                SettingsEntity.TextField(
                    value = username,
                    onValueChange = { str -> username = str }
                )
            )
            val text = when{
                usernameState.isUsernameTaken == true -> stringResource(R.string.username_not_ok)
                usernameState.isUsernameTaken == false -> stringResource(R.string.username_ok, username)
                usernameState.isChecking -> stringResource(R.string.checking_username)
                else -> ""
            }
            val color = when {
                usernameState.isUsernameTaken == true -> MaterialTheme.colorScheme.error
                usernameState.isUsernameTaken == false -> Color(0xFF1E822B)
                usernameState.isChecking -> Color.Unspecified
                else -> Color.Unspecified
            }
            Text(
                modifier = Modifier
                    .padding(horizontal = 18.dp),
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )
        }
    }
}