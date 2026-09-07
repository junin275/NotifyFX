package com.notifyfx.notifyfx.overlay

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BitmapPainter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Painter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.notifyfx.notifyfx.model.NotificationAction
import com.notifyfx.notifyfx.model.NotificationModel
import com.notifyfx.notifyfx.model.NotificationStyle

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

    // Use the notification's app-specific style if available, otherwise default
    val effectiveStyle = currentNotification.packageName.let { pkg ->
        // TODO: Get app-specific style from viewModel
        style
    }

    RenderNotification(
        notification = currentNotification,
        style = effectiveStyle,
        isExpanded = isExpanded,
        onClick = { onNotificationClick(currentNotification) },
        onActionClick = { index -> onActionClick(currentNotification, index) },
        onDismiss = { onDismiss(currentNotification) },
        onExpandToggle = { expandedIndex.value = if (isExpanded) null else 0 }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val density = LocalDensity.current
    val cornerRadius = style.cornerRadius.dp
    val cardShape = RoundedCornerShape(cornerRadius)
    val shadow = Shadow(
        elevation = style.shadow.elevation.dp,
        shape = cardShape,
        ambientColor = Color(style.shadow.color),
        spotColor = Color(style.shadow.color),
        blurSize = style.shadow.blur.dp
    )

    val titleFontFamily = FontFamily(style.titleFontFamily)
    val textFontFamily = FontFamily(style.textFontFamily)
    val actionFontFamily = FontFamily(style.actionFontFamily)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(style.horizontalMargin.dp, style.verticalMargin.dp)
            .padding(style.padding.dp)
            .shadow(style.shadow.elevation.dp, cardShape, style.shadow.color)
            .clip(cardShape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { /* TODO: Show more options */ }
            ),
        shape = cardShape,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(style.backgroundColor),
            contentColor = Color(style.titleColor)
        ),
        border = if (style.borderWidth > 0f)
            androidx.compose.ui.graphics.drawscope.Stroke(width = style.borderWidth.dp, color = Color(style.borderColor))
        else
            null
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(style.itemSpacing.dp)
        ) {
            // Header: Icon + Title + App Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // App Icon
                if (style.showIcon) {
                    notification.icon?.let { bitmap ->
                        val painter = BitmapPainter(bitmap)
                        androidx.compose.foundation.Image(
                            painter = painter,
                            contentDescription = notification.appName,
                            modifier = Modifier
                                .size(style.iconSize.dp)
                                .clip(RoundedCornerShape(style.iconCornerRadius.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } ?: androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.default.Notification,
                        contentDescription = notification.appName,
                        modifier = Modifier.size(style.iconSize.dp),
                        tint = Color(style.titleColor)
                    )
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
                        imageVector = androidx.compose.material.icons.default.Close,
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
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }

            // Actions
            if (notification.hasActions && (isExpanded || notification.actions.size <= 2)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(style.actionSpacing.dp)
                ) {
                    notification.actions.forEachIndexed { index, action ->
                        ActionButton(
                            action = action,
                            style = style,
                            onClick = { onActionClick(index) }
                        )
                    }
                }
            } else if (notification.hasActions && !isExpanded) {
                // Show expand indicator
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
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(style.actionButtonHeight.dp)
            .padding(horizontal = style.actionButtonPadding.dp),
        shape = RoundedCornerShape(style.actionButtonCornerRadius.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color(style.actionBackgroundColor),
            contentColor = Color(style.actionTextColor)
        )
    ) {
        Text(
            text = action.title,
            fontSize = style.actionFontSize.sp,
            fontFamily = FontFamily(style.actionFontFamily),
            fontWeight = FontWeight.Medium
        )
    }
}