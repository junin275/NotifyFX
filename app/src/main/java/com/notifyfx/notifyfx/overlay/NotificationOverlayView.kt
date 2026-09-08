package com.notifyfx.notifyfx.overlay

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notifyfx.notifyfx.model.NotificationAction
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.ui.OverlayViewModel

@Composable
fun NotificationOverlayView(
    viewModel: OverlayViewModel,
    onNotificationClick: (NotificationModel) -> Unit,
    onActionClick: (NotificationModel, Int) -> Unit,
    onDismiss: (NotificationModel) -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val style by remember { mutableStateOf(NotificationStyle.getDefault()) }
    val expandedIndex = remember { mutableStateOf<Int?>(null) }

    // Only show the most recent notification for now
    val currentNotification = notifications.firstOrNull()
    if (currentNotification == null) return

    val isExpanded = expandedIndex.value == 0

    RenderNotification(
        notification = currentNotification,
        style = style,
        isExpanded = isExpanded,
        onClick = { onNotificationClick(currentNotification) },
        onActionClick = { index -> onActionClick(currentNotification, index) },
        onDismiss = { onDismiss(currentNotification) },
        onExpandToggle = { expandedIndex.value = if (isExpanded) null else 0 }
    )
}

@Composable
fun RenderNotification(
    notification: NotificationModel,
    style: NotificationStyle,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onActionClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    onExpandToggle: () -> Unit
) {
    val cornerRadius = RoundedCornerShape(style.cornerRadius.dp)
    val titleFontFamily = resolveFontFamily(style.titleFontFamily)
    val textFontFamily = resolveFontFamily(style.textFontFamily)
    val actionFontFamily = resolveFontFamily(style.actionFontFamily)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = style.horizontalMargin.dp,
                vertical = style.verticalMargin.dp
            )
            .shadow(
                elevation = style.shadow.elevation.dp,
                shape = cornerRadius,
                ambientColor = Color(style.shadow.color),
                spotColor = Color(style.shadow.color)
            )
            .clip(cornerRadius)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onExpandToggle
            ),
        shape = cornerRadius,
        colors = CardDefaults.cardColors(
            containerColor = Color(style.backgroundColor),
            contentColor = Color(style.titleColor)
        ),
        border = if (style.borderWidth > 0f) {
            BorderStroke(style.borderWidth.dp, Color(style.borderColor))
        } else null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(style.itemSpacing.dp)
        ) {
            // Header: Icon + Title + App Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (style.showIcon) {
                    val bitmap = notification.icon ?: notification.largeIcon
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = notification.appName,
                            modifier = Modifier
                                .size(style.iconSize.dp)
                                .clip(RoundedCornerShape(style.iconCornerRadius.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Notification,
                            contentDescription = notification.appName,
                            modifier = Modifier.size(style.iconSize.dp),
                            tint = Color(style.titleColor)
                        )
                    }
                }

                // Title and App Name
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (style.showAppName) {
                        Text(
                            text = notification.appName,
                            fontSize = (style.titleFontSize - 2).sp,
                            fontFamily = titleFontFamily,
                            fontWeight = FontWeight.Medium,
                            color = Color(style.titleColor).copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = notification.displayTitle,
                        fontSize = style.titleFontSize.sp,
                        fontFamily = titleFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color(style.titleColor),
                        maxLines = style.titleMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Dismiss button
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color(style.titleColor).copy(alpha = 0.6f)
                    )
                }
            }

            // Notification text
            if (notification.displayText.isNotBlank()) {
                Text(
                    text = notification.displayText,
                    fontSize = style.textFontSize.sp,
                    fontFamily = textFontFamily,
                    color = Color(style.textColor),
                    maxLines = if (isExpanded) Int.MAX_VALUE else style.textMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Sub-text / timestamp
            if (style.showTimestamp && notification.subText != null) {
                Text(
                    text = notification.subText!!,
                    fontSize = style.subTextFontSize.sp,
                    fontFamily = textFontFamily,
                    color = Color(style.subTextColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            // Actions
            if (notification.hasActions && (isExpanded || notification.actions.size <= 2)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(style.actionSpacing.dp)
                ) {
                    notification.actions.forEachIndexed { index, action ->
                        ActionButton(
                            action = action,
                            style = style,
                            fontFamily = actionFontFamily,
                            onClick = { onActionClick(index) }
                        )
                    }
                }
            } else if (notification.hasActions && !isExpanded) {
                Text(
                    text = "Tap to expand • ${notification.actions.size} actions",
                    fontSize = 12.sp,
                    color = Color(style.subTextColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    action: NotificationAction,
    style: NotificationStyle,
    fontFamily: FontFamily,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(style.actionButtonHeight.dp)
            .padding(horizontal = style.actionButtonPadding.dp),
        shape = RoundedCornerShape(style.actionButtonCornerRadius.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(style.actionBackgroundColor),
            contentColor = Color(style.actionTextColor)
        )
    ) {
        Text(
            text = action.title,
            fontSize = style.actionFontSize.sp,
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun resolveFontFamily(name: String): FontFamily {
    return when (name) {
        "sans-serif" -> FontFamily.SansSerif
        "sans-serif-medium" -> FontFamily.SansSerif
        "sans-serif-bold" -> FontFamily.SansSerif
        "monospace" -> FontFamily.Monospace
        "serif" -> FontFamily.Serif
        "cursive" -> FontFamily.Cursive
        else -> FontFamily.Default
    }
}