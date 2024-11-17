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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.djatar.ardath.feature.domain.models.Message
import com.djatar.ardath.feature.domain.models.MessageStatus
import com.djatar.ardath.feature.presentation.utils.TIME_DATE_FORMAT
import com.djatar.ardath.feature.presentation.utils.getColor
import com.djatar.ardath.feature.presentation.utils.getDate
import com.djatar.ardath.ui.theme.ArdathTheme

@Composable
fun MessageItemContent(
    message: Message,
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
                if (!message.senderImage.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(message.senderName)
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
                            .background(
                                message.senderName
                                    .firstOrNull()
                                    .getColor()
                            )
                            .size(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message.senderName.firstOrEmpty().uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = if (isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceDim)
                    .padding(8.dp)
                    .widthIn(min = 50.dp, max = 300.dp),
            ) {
                Column {
                    Text(
                        text = message.text.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isMe) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Bottom),
                            text = message.timestamp.getDate(TIME_DATE_FORMAT),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                        )
                        if (isMe) {
                            val icon: ImageVector = when (message.status) {
                                MessageStatus.SENT.name -> Icons.Outlined.Done
                                MessageStatus.READ.name -> Icons.Outlined.DoneAll
                                else -> Icons.Outlined.AccessTime
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItemHeader(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

fun String?.firstOrEmpty(): String {
    return if (this.isNullOrEmpty()) "" else this.first().toString()
}

@Preview(showBackground = true)
@Composable
private fun MessageItemPreview() {
    ArdathTheme {
        MessageItemContent(message = Message(
            senderName = "John Doe",
            text = "Hello, my name is John Doe".repeat(3),
        ))
    }
}

@Preview(showBackground = true)
@Composable
private fun MessageItemPreview2() {
    ArdathTheme {
        MessageItemContent(message = Message(
            senderName = "John Doe",
            text = "Hello, my name is John Doe".repeat(1),
        ), isMe = true)
    }
}