package com.djatar.ardath.feature.presentation.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.djatar.ardath.R
import com.djatar.ardath.core.Position
import com.djatar.ardath.core.SettingsEntity
import com.djatar.ardath.core.presentation.components.BackButton
import com.djatar.ardath.core.presentation.components.utils.Screen
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.presentation.profile.UserViewModel
import com.djatar.ardath.feature.presentation.settings.components.SettingBar
import com.djatar.ardath.feature.presentation.settings.components.SettingsItem

private const val TAG = "SettingsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    userViewModel: UserViewModel,
    onLogout: () -> Unit,
    onBackPressed: () -> Unit
) {

    val userState by userViewModel.userState.collectAsStateWithLifecycle()
    val currentUser = userState.currentUser
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val settingList = rememberSettingsList(currentUser) {
        navController.navigate(it)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingBar(
                user = currentUser,
                scrollBehavior = scrollBehavior,
                navigationIcon = { BackButton(onBackPressed) },
                navController = navController,
                onLogout = onLogout
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(
                items = settingList,
                key = { it.title + it.type.toString() }
            ) { SettingsItem(it) }
        }
    }
}

@Composable
fun rememberSettingsList(
    user: User?,
    navigate: (String) -> Unit
): SnapshotStateList<SettingsEntity> {
    val context = LocalContext.current

    val emailPref = remember(user?.email) {
        SettingsEntity.Preference(
            title = user?.email ?: "",
            summary = context.getString(R.string.setting_email_desc),
            screenPosition = Position.Top
        )
    }
    val usernamePref = remember(user?.email) {
        SettingsEntity.Preference(
            title = '@'.plus(user?.username ?: ""),
            summary = context.getString(R.string.setting_username_desc),
            screenPosition = Position.Bottom,
            onClick = { navigate(Screen.UsernameScreen()) }
        )
    }
    val bgColor = MaterialTheme.colorScheme.error
    val titleColor = MaterialTheme.colorScheme.onError
    val deleteAccountPref = remember {
        SettingsEntity.Preference(
            title = context.getString(R.string.setting_delete_account),
            containerColor = bgColor,
            titleContentColor = titleColor,
            onClick = {},
            screenPosition = Position.Alone
        )
    }

    val chatPref = remember {
        SettingsEntity.Preference(
            title = context.getString(R.string.setting_chats),
            screenPosition = Position.Top
        )
    }
    val privacyPref = remember {
        SettingsEntity.Preference(
            title = context.getString(R.string.setting_privacy),
            screenPosition = Position.Middle
        )
    }
    val languagePref = remember {
        SettingsEntity.Preference(
            title = context.getString(R.string.setting_language),
            screenPosition = Position.Bottom
        )
    }

    return remember(user) {
        mutableStateListOf<SettingsEntity>().apply {
            /* ACCOUNT */
            if (user != null) {
                add(SettingsEntity.Header(context.getString(R.string.setting_account)))
                add(emailPref)
                add(usernamePref)
            }

            /* SETTINGS */
            add(SettingsEntity.Header(context.getString(R.string.settings)))
            add(chatPref)
            add(privacyPref)
            add(languagePref)

            if (user != null) {
                add(deleteAccountPref)
            }
        }
    }
}