/*
 * Copyright (©) 2024 Dikidjatar
 * All Rights Reserved.
 */

package com.djatar.ardath.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.djatar.ardath.core.presentation.common.animatedComposable
import com.djatar.ardath.core.presentation.components.utils.Screen
import com.djatar.ardath.feature.presentation.auth.AuthViewModel
import com.djatar.ardath.feature.presentation.auth.signin.SignInScreen
import com.djatar.ardath.feature.presentation.auth.signup.SignUpScreen
import com.djatar.ardath.feature.presentation.chatview.ChatViewScreen
import com.djatar.ardath.feature.presentation.common.ChatViewModel
import com.djatar.ardath.feature.presentation.common.ChatsScreen
import com.djatar.ardath.feature.presentation.profile.EditProfileScreen
import com.djatar.ardath.feature.presentation.profile.ProfileScreen
import com.djatar.ardath.feature.presentation.profile.UserViewModel
import com.djatar.ardath.feature.presentation.settings.SettingsScreen
import com.djatar.ardath.feature.presentation.settings.account.UsernameScreen
import com.djatar.ardath.feature.presentation.utils.CHAT_USER_ID
import com.djatar.ardath.feature.presentation.utils.IS_CHAT_ON
import com.djatar.ardath.feature.presentation.utils.NotificationUtil
import com.djatar.ardath.feature.presentation.utils.PreferenceUtil
import com.djatar.ardath.feature.presentation.utils.PreferenceUtil.updateString
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private const val TAG = "AppEntry"

@Composable
fun AppEntry(
    navController: NavHostController,
    paddingValues: PaddingValues,
    bottomBarState: MutableState<Boolean>,
    isScrolling: MutableState<Boolean>,
    hasLogin: MutableState<Boolean>
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val bottomNavItems = rememberNavigationItems()

    val startDest by rememberSaveable(hasLogin.value) {
        val route = if (hasLogin.value) Screen.ChatsScreen() else Screen.SignInScreen()
        mutableStateOf(route)
    }
    val currentDest = remember(navController.currentDestination) {
        navController.currentDestination?.route
    }

    var lastShouldDisplay by rememberSaveable {
        mutableStateOf(bottomNavItems.find { item -> item.route == currentDest } != null)
    }

    val chatViewModel = hiltViewModel<ChatViewModel>()
    val authViewModel = hiltViewModel<AuthViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>().also {
        LaunchedEffect(hasLogin.value) { it.getCurrentUser() }
    }

    LaunchedEffect(backStackEntry) {
        backStackEntry?.destination?.route?.let {
            val shouldDisplayBottomBar =
                bottomNavItems.find { item -> item.route == it } != null
            if (lastShouldDisplay != shouldDisplayBottomBar) {
                bottomBarState.value = shouldDisplayBottomBar
                lastShouldDisplay = shouldDisplayBottomBar
            }
        }
    }

    LaunchedEffect(Unit) {
        Firebase.auth.addAuthStateListener { auth ->
            hasLogin.value = auth.currentUser != null
        }
    }

    val onBackPressed: () -> Unit = {
        with(navController) {
            if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                popBackStack()
            }
        }
    }

    fun signOut() {
        chatViewModel.clear()
        userViewModel.clear()
        authViewModel.signOut()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = startDest
        ) {
            animatedComposable(Screen.SignInScreen()) {
                SignInScreen(
                    viewModel = authViewModel,
                    paddingValues = paddingValues,
                    onNavigateToRegisterScreen = { navController.navigate(Screen.SignUpScreen()) },
                )
            }
            animatedComposable(Screen.SignUpScreen()) {
                SignUpScreen(
                    viewModel = authViewModel,
                    paddingValues = paddingValues,
                    onNavigateToLoginScreen = onBackPressed
                )
            }
            animatedComposable(Screen.ChatsScreen()) {
                val viewModel = hiltViewModel<ChatViewModel>().also {
                    it.loadChats()
                }

                ChatsScreen(
                    chatViewModel = viewModel,
                    chatStateFlow = viewModel.chatState,
                    paddingValues = paddingValues,
                    isScrolling = isScrolling,
                    selectionState = viewModel.multiSelectState,
                    selectedChatState = viewModel.selectedChatState,
                    toggleSelection = viewModel::toggleSelection,
                    onLoadMore = { batchSize -> viewModel.loadChats(batchSize) },
                    onNavigateToChatView = { navController.navigate(it) },
                    onNavigateToProfile = { navController.navigate(it) },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings()) {
                            launchSingleTop = true
                        }
                    }
                )
            }
            animatedComposable(
                route = Screen.ChatViewScreen.idAndTitle(),
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    },
                    navArgument("chatId") {
                        type = NavType.StringType
                    },
                    navArgument("title") {
                        type = NavType.StringType
                    }
                )
            ) { navBackStackEntry ->
                val otherUserId: String = remember {
                    navBackStackEntry.arguments?.getString("userId") ?: ""
                }
                val chatId: String = remember {
                    navBackStackEntry.arguments?.getString("chatId") ?: ""
                }
                val title: String = remember {
                    navBackStackEntry.arguments?.getString("title") ?: ""
                }

                val viewModel = chatViewModel.also {
                     LaunchedEffect(Unit) { it.listenForMessages(chatId) }
                }

                DisposableEffect(otherUserId) {
                    NotificationUtil.cancelNotification(otherUserId.hashCode())

                    PreferenceUtil.encodeString(CHAT_USER_ID, otherUserId)
                    PreferenceUtil.updateValue(IS_CHAT_ON, true)

                    onDispose {
                        CHAT_USER_ID.updateString("")
                        PreferenceUtil.updateValue(IS_CHAT_ON, false)
                    }
                }

                ChatViewScreen(
                    chatViewModel = viewModel,
                    messageState = viewModel.messageState,
                    title = title,
                    paddingValues = paddingValues,
                    onSendMessage = { chatTitle, messageText ->
                        viewModel.sendMessage(otherUserId, chatId, chatTitle, messageText)
                    },
                    onNavigateToProfile = { route -> navController.navigate(route) },
                    onNavigateBack = onBackPressed
                )
            }

            animatedComposable(Screen.ProfileScreen()) { backStackEntry ->

                val state by userViewModel.userState.collectAsStateWithLifecycle()

                ProfileScreen(
                    state = state,
                    user = state.currentUser,
                    isCurrentUser = remember { mutableStateOf(true) },
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfileScreen())
                    }
                ) {
                    navController.navigate(Screen.ChatsScreen()) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            }

            animatedComposable(
                route = Screen.ProfileScreen.withId(),
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val userId = remember(backStackEntry) {
                    backStackEntry.arguments?.getString("userId") ?: ""
                }

                LaunchedEffect(userId) { userViewModel.getUserById(userId) }
                val state by userViewModel.userState.collectAsStateWithLifecycle()

                ProfileScreen(
                    state = state,
                    user = state.user,
                    isCurrentUser = remember { mutableStateOf(false) },
                ) { onBackPressed() }
            }

            settingsGraph(
                navController = navController,
                paddingValues = paddingValues,
                userViewModel = userViewModel,
                onLogout = { signOut() },
                onBackPressed = onBackPressed
            )
        }
    }
}

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    userViewModel: UserViewModel,
    onLogout: () -> Unit,
    onBackPressed: () -> Unit
) {
    navigation(startDestination = Screen.SettingsScreen(), route = Screen.Settings()) {
        animatedComposable(Screen.SettingsScreen()) {
            SettingsScreen(
                navController = navController,
                paddingValues = paddingValues,
                userViewModel = userViewModel,
                onLogout = onLogout,
                onBackPressed = onBackPressed
            )
        }

        animatedComposable(Screen.EditProfileScreen()) {
            EditProfileScreen(
                userViewModel = userViewModel,
                paddingValues = paddingValues,
                onBackPressed = onBackPressed
            )
        }

        animatedComposable(Screen.UsernameScreen()) {
            UsernameScreen(
                userViewModel = userViewModel,
                onBackPressed = onBackPressed
            )
        }
    }
}