package com.djatar.ardath.feature.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.djatar.ardath.core.presentation.components.BackButton
import com.djatar.ardath.core.presentation.components.defaultAppBarColor
import com.djatar.ardath.feature.presentation.utils.getColor
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private const val TAG = "SettingsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    onBackPressed: () -> Unit
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.defaultAppBarColor(),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val collapsedFraction = scrollBehavior.state.collapsedFraction
                        val iconSize = lerp(60.dp, 45.dp, collapsedFraction)
                        val name = Firebase.auth.currentUser?.displayName.toString()
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(iconSize)
                                .clip(CircleShape)
                                .background(name.uppercase().first().getColor()),
                            contentAlignment = Alignment.Center
                        ) { Text(text = name.first().toString()) }
                        Column {
                            val maxLines = if (collapsedFraction > 0.3f) 1 else 2
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W500
                                ),
                                maxLines = maxLines,
                                lineHeight = TextUnit(0.3f, TextUnitType.Em)
                            )
                            Text(
                                text  = "online",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = TextUnit(0.3f, TextUnitType.Em)
                            )
                        }
                    }
                },
                navigationIcon = { BackButton(onBackPressed) },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {  }
    }
}