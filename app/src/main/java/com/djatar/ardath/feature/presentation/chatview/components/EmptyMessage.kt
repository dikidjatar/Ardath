package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.djatar.ardath.R
import com.djatar.ardath.ui.theme.ArdathTheme

@Composable
fun EmptyMessage(onClick: (String) -> Unit = {}) {

    val interactionSource = remember { MutableInteractionSource() }
    var isClick by remember { mutableStateOf(false) }

    val animateGreeting by animateIntAsState(
        targetValue = if (isClick) 50 else 57,
        label = "animateGreeting"
    )

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            isClick = when (interaction) {
                is PressInteraction.Press -> true
                else -> false
            }
        }
    }

    MaterialTheme.typography.displayLarge
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .imePadding()
                .size(200.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.empty_message),
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.empty_message_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.clickable(
                        onClick = { onClick("\uD83D\uDC4B") },
                        interactionSource = interactionSource,
                        indication = null
                    ),
                    text = "\uD83D\uDC4B",
                    fontSize = animateGreeting.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyMessagePreview() {
    ArdathTheme {
        EmptyMessage()
    }
}