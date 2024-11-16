package com.djatar.ardath.feature.presentation.settings.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.djatar.ardath.core.Position
import com.djatar.ardath.core.SettingsEntity
import com.djatar.ardath.core.SettingsType
import com.djatar.ardath.ui.theme.ArdathTheme

@Composable
fun SettingsItem(item: SettingsEntity) {
    var checked by remember(item.isChecked) { mutableStateOf(item.isChecked ?: false) }

    val icon: @Composable () -> Unit = {
        require(item.icon != null) { "Icon at this stage cannot be null" }
        Icon(
            imageVector = item.icon!!,
            contentDescription = null
        )
    }

    val summary: @Composable () -> Unit = {
        require(!item.summary.isNullOrEmpty()) { "Summary at this stage cannot be null or empty" }
        Text(text = item.summary!!)
    }
    val switch: @Composable () -> Unit = {
        Switch(checked = checked, onCheckedChange = null)
    }
    val label: @Composable () -> Unit = {
        require(item.label != null) { "Label at this stage cannot be null" }
        Text(text = item.label!!)
    }

    val shape = remember(item.screenPosition) {
        when (item.screenPosition) {
            Position.Alone -> RoundedCornerShape(24.dp)
            Position.Bottom -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            )

            Position.Middle -> RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )

            Position.Top -> RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = 4.dp,
                bottomEnd = 4.dp
            )
        }
    }
    val paddingModifier =
        when (item.screenPosition) {
            Position.Alone -> Modifier.padding(bottom = 16.dp)
            Position.Bottom -> Modifier.padding(top = 1.dp, bottom = 16.dp)
            Position.Middle -> Modifier.padding(vertical = 1.dp)
            Position.Top -> Modifier.padding(bottom = 1.dp)
        }

    val supportingContent: (@Composable () -> Unit)? =
        when (item.type) {
            SettingsType.Default,
            SettingsType.Switch,
            SettingsType.TextField ->
                if (!item.summary.isNullOrEmpty()) summary else null
            SettingsType.Header -> null
        }

    val trailingContent: (@Composable () -> Unit)? =
        when (item.type) {
            SettingsType.Switch -> switch
            else -> null
        }

    val clickableModifier = if (!item.isHeader)
        Modifier.clickable(enabled = item.enabled) {
            if (item.type == SettingsType.Switch) {
                item.onChecked?.let {
                    checked = !checked
                    it(checked)
                }
            } else item.onClick?.invoke()
        }
    else Modifier

    when (item.type) {
        SettingsType.Header -> {
            Text(
                text = item.title,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp, vertical = 8.dp)
                    .padding(bottom = 8.dp)
            )
        }
        SettingsType.TextField -> {
            TextField(
                value = item.value,
                onValueChange = item.onValueChange,
                label = if (item.label != null) label else null,
                maxLines = item.maxLines,
                enabled = item.enabled,
                supportingText = supportingContent,
                modifier = Modifier
                    .then(paddingModifier)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = shape,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    errorTextColor = MaterialTheme.colorScheme.error
                )
            )
        }
        else -> {
            val alpha by animateFloatAsState(
                targetValue = if (item.enabled) 1f else 0.5f,
                label = "alpha"
            )
            val containerColor = if (item.containerColor != null) item.containerColor
            else MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
            val titleContentColor = if (item.titleContentColor != null) item.titleContentColor
            else ListItemDefaults.colors().headlineColor
            ListItem(
                headlineContent = { Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                ) },
                supportingContent = supportingContent,
                trailingContent = trailingContent,
                leadingContent = if (item.icon != null) icon else null,
                modifier = Modifier
                    .then(paddingModifier)
                    .padding(horizontal = 16.dp)
                    .clip(shape)
                    .background(color = containerColor!!)
                    .then(clickableModifier)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .alpha(alpha),
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    headlineColor = titleContentColor!!
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsItemsPreview() {
    ArdathTheme {
        Column(modifier = Modifier.wrapContentHeight()) {
            SettingsItem(
                item = SettingsEntity.Preference(
                    title = "Preview alone title",
                    summary = "Preview summary"
                )
            )
            SettingsItem(
                item = SettingsEntity.Header(
                    title = "Preview header"
                )
            )
            SettingsItem(
                item = SettingsEntity.Preference(
                    icon = Icons.Outlined.Settings,
                    title = "Preview Top Title",
                    summary = "Preview Summary",
                    screenPosition = Position.Top
                )
            )
            SettingsItem(
                item = SettingsEntity.SwitchPreference(
                    title = "Preview Middle Title",
                    summary = "Preview Summary\nSecond Line\nThird Line",
                    screenPosition = Position.Middle
                )
            )
            SettingsItem(
                item = SettingsEntity.Preference(
                    title = "Preview Bottom Title",
                    summary = "Preview Summary",
                    screenPosition = Position.Middle
                )
            )
            var value by remember { mutableStateOf("") }
            SettingsItem(
                item = SettingsEntity.TextField(
                    value = value,
                    onValueChange = { str -> value = str},
                    label = "Your Name",
                    maxLines = 1,
                    screenPosition = Position.Bottom
                )
            )
        }
    }
}