/*
 * Copyright (©) 2024 Dikidjatar
 * All Rights Reserved.
 */

package com.djatar.ardath.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.utils.NavigationItem
import com.djatar.ardath.core.presentation.components.utils.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun rememberNavigationItems(): List<NavigationItem> {
    val chatsTitle = stringResource(R.string.nav_chats)
    val profileTitle = stringResource(R.string.nav_profile)

    return remember {
        mutableListOf(
            NavigationItem(
                name = chatsTitle,
                route = Screen.ChatsScreen.route,
                icon = Icons.Outlined.ChatBubble
            ),
            NavigationItem(
                name = profileTitle,
                route = Screen.ProfileScreen.route,
                icon = Icons.Outlined.ManageAccounts
            )
        )
    }

}

@Composable
fun AppBarContainer(
    navController: NavController,
    bottomBarState: Boolean,
    isScrolling: Boolean,
    hasLogin: Boolean,
    content: @Composable () -> Unit
) {
    val navigationItems = rememberNavigationItems()
    val backStackEntry by navController.currentBackStackEntryAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        content()
        var showNavBar by remember(bottomBarState, isScrolling, hasLogin) {
            mutableStateOf(bottomBarState && !isScrolling && hasLogin)
        }
        LaunchedEffect(bottomBarState, isScrolling, hasLogin) {
            snapshotFlow {
                bottomBarState && !isScrolling && hasLogin
            }.distinctUntilChanged().collectLatest {
                showNavBar = it
            }
        }

        AnimatedVisibility(
            visible = showNavBar,
            enter = slideInVertically { it * 2 },
            exit = slideOutVertically { it * 2 },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomAppBar(
                navigationItems = navigationItems,
                backStackEntry = backStackEntry
            ) { navigate(navController, it) }
        }
    }
}

@Composable
fun BottomAppBar(
    navigationItems: List<NavigationItem>,
    backStackEntry: NavBackStackEntry?,
    onClick: (route: String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)) {
        navigationItems.forEach { item ->
            val selected = item.route == backStackEntry?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        onClick(item.route)
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(text = item.name, style = MaterialTheme.typography.bodyMedium) }
            )
        }
    }
}


private fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopAppBarDefaults.defaultAppBarColor() : TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors().copy(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )
}












