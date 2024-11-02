package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DriveFolderUpload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.djatar.ardath.R

@Composable
fun MessageInputContainer(
    modifier: Modifier = Modifier,
    messageText: MutableState<String>,
    onValueChange: (String) -> Unit,
    onSendMessage: (String) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }

    var showFeature by remember { mutableStateOf(true) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is FocusInteraction.Focus -> {
                    showFeature = false
                }
                is FocusInteraction.Unfocus -> {
                    showFeature = false
                }
                is PressInteraction.Press -> {
                    if (showFeature) {
                        showFeature = false
                    }
                }
            }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        AddFeatureButton(!showFeature) { showFeature = true }
        FeatureComponent(showFeature)

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 8.dp)
                .focusRequester(focusRequester),
            value = messageText.value,
            onValueChange = {
                onValueChange(it)
                showFeature = false
            },
            maxLines = 5,
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors().copy(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceDim,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
            ),
            placeholder = { Text(text = stringResource(R.string.message)) },
            interactionSource = interactionSource
        )

        IconButton(
            modifier = Modifier.padding(end = 8.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            onClick = { onSendMessage(messageText.value) }
        ) {
            Icon(imageVector = Icons.Filled.ArrowUpward, contentDescription = null)
        }
    }
}

@Composable
private fun AddFeatureButton(visible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(visible = visible) {
        IconButton(
            modifier = Modifier.padding(horizontal = 8.dp),
            colors = IconButtonDefaults.outlinedIconButtonColors().copy(
                containerColor = MaterialTheme.colorScheme.surfaceDim,
            ),
            onClick = onClick
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
        }
    }
}

@Composable
fun FeatureComponent(visible: Boolean) {
    AnimatedVisibility(visible = visible) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy((-12).dp)
        ) {
            FeatureButton(Icons.Outlined.PhotoCamera) { }
            FeatureButton(Icons.Outlined.Image) { }
            FeatureButton(Icons.Outlined.DriveFolderUpload) { }
        }
    }
}

@Composable
private fun FeatureButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }
}