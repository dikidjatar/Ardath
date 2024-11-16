package com.djatar.ardath.feature.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.defaultAppBarColor
import com.djatar.ardath.core.presentation.components.utils.Screen
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.presentation.utils.getColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingBar(
    user: User?,
    scrollBehavior: TopAppBarScrollBehavior,
    navigationIcon: @Composable () -> Unit = {},
    navController: NavController,
    onLogout: () -> Unit
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val iconSize = lerp(60.dp, 45.dp, collapsedFraction)
    val maxLines = if (collapsedFraction > 0.3f) 1 else 2

    LargeTopAppBar(
        colors = TopAppBarDefaults.defaultAppBarColor(),
        navigationIcon = navigationIcon,
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(iconSize)
                        .clip(CircleShape)
                        .background(
                            user?.name
                                ?.uppercase()
                                ?.firstOrNull()
                                .getColor()
                        ),
                    contentAlignment = Alignment.Center
                ) { Text(text = user?.name?.firstOrNull().toString()) }
                Column {
                    Text(
                        text = user?.name.orEmpty(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.W500
                        ),
                        maxLines = maxLines,
                        lineHeight = TextUnit(0.3f, TextUnitType.Em)
                    )
                    Text(
                        text  = user?.status.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = TextUnit(0.3f, TextUnitType.Em)
                    )
                }
            }
        },
        actions = {
            if (user != null) {
                ActionMore(
                    onEditInfo = { navController.navigate(Screen.EditProfileScreen()) },
                    onLogout = onLogout
                )
            }
        }
    )
}

@Composable
private fun ActionMore(onEditInfo: () -> Unit, onLogout: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.edit_info)) },
            leadingIcon = { Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null)
            },
            onClick = {
                expanded = false
                onEditInfo()
            },
        )
        // HorizontalDivider(thickness = 0.5.dp)
        DropdownMenuItem(
            text = { Text(text = stringResource(R.string.logout)) },
            colors = MenuDefaults.itemColors().copy(
                textColor = MaterialTheme.colorScheme.error
            ),
            leadingIcon = { Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error)
            },
            onClick = {
                expanded = false
                onLogout()
            }
        )
    }
}