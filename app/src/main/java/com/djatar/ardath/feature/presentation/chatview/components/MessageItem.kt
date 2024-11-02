package com.djatar.ardath.feature.presentation.chatview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.djatar.ardath.ui.theme.ArdathTheme
import com.djatar.ardath.utils.getColor

@Composable
fun MessageItem(
    displayName: String = "John Doe",
    userAvatar: String? = null,
    message: String = "Hello, my name is $displayName",
    isMe: Boolean = false,
    onProfileClick: () -> Unit = {}
) {

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
        ) {
            if (!isMe) {
                if (userAvatar != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(userAvatar)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(50.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .clickable { onProfileClick() }
                            .background(displayName.first().getColor())
                            .size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.first().uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceDim)
                    .widthIn(min = 60.dp, max = 250.dp),
            ) {
                if (!isMe) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        text = displayName,
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    modifier = Modifier.padding(
                        top = if (isMe) 8.dp else 0.dp , start = 8.dp,
                        end = 8.dp, bottom = 8.dp
                    ),
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageItemPreview() {
    ArdathTheme {
        MessageItem()
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageItemPreview2() {
    ArdathTheme {
        MessageItem(isMe = true)
    }
}