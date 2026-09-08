package com.notifyfx.notifyfx.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.spacedBy
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notifyfx.notifyfx.overlay.RenderNotification
import com.notifyfx.notifyfx.model.NotificationStyle
import com.notifyfx.notifyfx.model.PreviewNotification
import com.notifyfx.notifyfx.ui.DesignerViewModel
import kotlinx.coroutines.launch

@Composable
fun DesignerScreen(
    viewModel: DesignerViewModel,
    onSaveStyle: (NotificationStyle) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentStyle by viewModel.currentStyle.observeAsState(NotificationStyle.getDefault())
    val presets by viewModel.presets.observeAsState(emptyList())
    val customStyles by viewModel.customStyles.observeAsState(emptyList())

    val previewNotification = remember { PreviewNotification() }
    val isEditing = remember { mutableStateOf(false) }
    val editStyle = remember { mutableStateOf(currentStyle.copy()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Notification Designer") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            navigationIcon = {
                IconButton(onClick = { /* navigate back */ }) {
                    Icon(imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                if (isEditing.value) {
                    OutlinedButton(
                        onClick = {
                            isEditing.value = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val newStyle = editStyle.value.copy(
                                id = "custom_${System.currentTimeMillis()}",
                                name = "Custom ${customStyles.size + 1}",
                                isPreset = false
                            )
                            onSaveStyle(newStyle)
                            viewModel.saveCurrentStyle()
                            isEditing.value = false
                            scope.launch {
                                snackbarHostState.showSnackbar("Style saved!")
                            }
                        }
                    ) {
                        Text("Save")
                    }
                } else {
                    Button(onClick = { isEditing.value = true }) {
                        Text("Customize")
                    }
                }
            }
        )

        // Main Content
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Panel - Preview
            Box(
                modifier = Modifier
                    .weight(1f, fill = true)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Live Preview", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 16.dp))

                    // Preview notification card
                    RenderNotification(
                        notification = previewNotification.toNotificationModel(),
                        style = editStyle.value,
                        isExpanded = true,
                        onClick = {},
                        onActionClick = {},
                        onDismiss = {},
                        onExpandToggle = {}
                    )
                }
            }

            // Right Panel - Controls
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Presets
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Presets", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 12.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            presets.forEach { preset ->
                                PresetButton(
                                    preset = preset,
                                    isSelected = editStyle.value.id == preset.id,
                                    onClick = { editStyle.value = preset }
                                )
                            }
                        }
                    }
                }

                // Custom Styles
                if (customStyles.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Custom Styles", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 12.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                customStyles.forEach { custom ->
                                    PresetButton(
                                        preset = custom,
                                        isSelected = editStyle.value.id == custom.id,
                                        onClick = { editStyle.value = custom }
                                    )
                                }
                            }
                        }
                    }
                }

                // Editor (when editing)
                if (isEditing.value) {
                    StyleEditorPanel(
                        style = editStyle.value,
                        onStyleChange = { editStyle.value = it }
                    )
                }
            }
        }

        SnackbarHost(snackbarHostState)
    }
}

@Composable
fun PresetButton(
    preset: NotificationStyle,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = preset.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            if (isSelected) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StyleEditorPanel(
    style: NotificationStyle,
    onStyleChange: (NotificationStyle) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Style Editor", fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 16.dp))

            // Container settings
            EditorSection("Container") {
                SliderRow("Width", style.width, 200f, 500f) { v ->
                    onStyleChange(style.copy(width = v))
                }
                SliderRow("Corner Radius", style.cornerRadius, 0f, 40f) { v ->
                    onStyleChange(style.copy(cornerRadius = v))
                }
                SliderRow("Padding", style.padding, 0f, 32f) { v ->
                    onStyleChange(style.copy(padding = v))
                }
            }

            // Background
            EditorSection("Background") {
                SliderRow("Transparency", style.transparency, 0f, 100f) { v ->
                    onStyleChange(style.copy(transparency = v))
                }
                SliderRow("Blur", style.backgroundBlur, 0f, 100f) { v ->
                    onStyleChange(style.copy(backgroundBlur = v))
                }
            }

            // Shadow
            EditorSection("Shadow") {
                SliderRow("Elevation", style.shadow.elevation, 0f, 24f) { v ->
                    onStyleChange(style.copy(shadow = style.shadow.copy(elevation = v)))
                }
                SliderRow("Blur", style.shadow.blur, 0f, 40f) { v ->
                    onStyleChange(style.copy(shadow = style.shadow.copy(blur = v)))
                }
            }

            // Typography
            EditorSection("Typography") {
                SliderRow("Title Size", style.titleFontSize, 10f, 28f) { v ->
                    onStyleChange(style.copy(titleFontSize = v))
                }
                SliderRow("Text Size", style.textFontSize, 10f, 24f) { v ->
                    onStyleChange(style.copy(textFontSize = v))
                }
            }

            // Actions
            EditorSection("Actions") {
                SliderRow("Button Height", style.actionButtonHeight, 28f, 56f) { v ->
                    onStyleChange(style.copy(actionButtonHeight = v))
                }
                SliderRow("Button Radius", style.actionButtonCornerRadius, 0f, 28f) { v ->
                    onStyleChange(style.copy(actionButtonCornerRadius = v))
                }
            }

            // Animation
            EditorSection("Animation") {
                SliderRow("Duration (ms)", style.animationDuration.toFloat(), 0f, 1000f) { v ->
                    onStyleChange(style.copy(animationDuration = v.toLong()))
                }
            }
        }
    }
}

@Composable
fun EditorSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 8.dp))
        content()
    }
}

@Composable
fun SliderRow(label: String, value: Float, min: Float, max: Float, onChange: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, modifier = Modifier.width(100.dp))
        androidx.compose.material3.Slider(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onChange,
            valueRange = min..max
        )
        Text(text = "%.1f".format(value), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(40.dp))
    }
}