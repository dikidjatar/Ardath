package com.djatar.ardath.feature.presentation.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.djatar.ardath.R
import com.djatar.ardath.core.presentation.components.BackButton
import com.djatar.ardath.feature.domain.models.User
import com.djatar.ardath.feature.domain.models.UserState
import com.djatar.ardath.feature.presentation.utils.FULL_DATE_FORMAT
import com.djatar.ardath.feature.presentation.utils.getColor
import com.djatar.ardath.feature.presentation.utils.getDate
import com.djatar.ardath.ui.theme.ArdathTheme

private const val TAG = "ProfilePage"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: UserState,
    user: User?,
    isCurrentUser: MutableState<Boolean>,
    onNavigateToEditProfile: () -> Unit = {},
    onBack: () -> Unit = {}
) {

    BackHandler(isCurrentUser.value) {
        onBack()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (!state.isLoading && !user?.name.isNullOrEmpty()) {
                        Text(
                            text = user?.name ?: "",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (!isCurrentUser.value) {
                        BackButton { onBack() }
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .size(20.dp),
                            strokeWidth = 5.dp
                        )
                    }
                }
                !state.error.isNullOrEmpty() -> item {
                    Text(
                        text = "Failed to load user data: ${state.error ?: ""}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                else -> item {
                    UserProfile(
                        user = user ?: User(name = "Unknown"),
                        isCurrentUser = isCurrentUser,
                        onNavigateToEditProfile = onNavigateToEditProfile
                    )
                }
            }
        }
    }
}

@Composable
private fun UserProfile(
    user: User,
    isCurrentUser: MutableState<Boolean>,
    onNavigateToEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
            if (user.photoUrl.isNullOrEmpty()) {
                DefaultProfileIcon(user.name)
            } else {
                ImageProfile(user.photoUrl)
            }
            if (isCurrentUser.value) {
                AddIcon()
            }
        }
        Text(
            text = user.username,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = stringResource(R.string.last_seen, user.lastSeen.getDate(
                format = FULL_DATE_FORMAT,
                stringToday = stringResource(R.string.today),
                stringYesterday = stringResource(R.string.yesterday)
            )),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isCurrentUser.value) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ActionProfileButton(stringResource(R.string.edit_profile), onNavigateToEditProfile)
                ActionProfileButton(stringResource(R.string.share_profile)) { }
            }
        } else {
            ElevatedButton(
                onClick = {},
                colors = ButtonDefaults.elevatedButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 30.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Outlined.Chat, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.send_message))
            }
        }
    }
}

@Composable
private fun ActionProfileButton(label: String, onClick: () -> Unit) {
    ElevatedAssistChip(
        label = { Text(text = label) },
        colors = AssistChipDefaults.assistChipColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceDim
        ),
        onClick = onClick
    )
}

@Composable
private fun DefaultProfileIcon(name: String, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .clickable { onClick() }
            .background(
                color = name
                    .first()
                    .getColor(),
                CircleShape
            )
            .border(3.dp, Color.Black, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.first().uppercase(),
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp
            )
        )
    }
}

@Composable
private fun ImageProfile(photoUrl: String) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape),
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(photoUrl)
            .build(),
        contentDescription = null,
    )
}

@Composable
private fun BoxScope.AddIcon(onClick: () -> Unit = {}) {
    OutlinedIconButton(
        modifier = Modifier.align(Alignment.BottomEnd),
        colors = IconButtonDefaults.outlinedIconButtonColors().copy(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = BorderStroke(2.dp, Color.Black),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UserProfilePreview() {
    ArdathTheme {
        val currentUser = remember { mutableStateOf(true) }
        UserProfile(User(name = "User Name", username = "UserName.zd2kl22"), currentUser) {}
    }
}

