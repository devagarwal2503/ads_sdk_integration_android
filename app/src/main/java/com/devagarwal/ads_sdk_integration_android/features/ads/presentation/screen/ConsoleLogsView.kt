package com.devagarwal.ads_sdk_integration_android.features.ads.presentation.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devagarwal.ads_sdk_integration_android.core.logger.AppLogger
import kotlinx.coroutines.launch

/**
 * Filter levels available in our developer log console.
 */
enum class LogFilterLevel { ALL, INFO, DEBUG, WARNING, ERROR }

/**
 * ConsoleLogsView displays the real-time stream of application and SDK events.
 *
 * It is fully interactive:
 * - Listens directly to a Kotlin SharedFlow log stream from the [AppLogger].
 * - Allows searching/filtering logs on the fly by level or keyword.
 * - Supports copying all logs to clipboard or clearing the current memory log buffer.
 * - Controls whether the viewer should automatically snap-scroll down when new logs arrive.
 */
@Composable
fun ConsoleLogsView(
    logger: AppLogger,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Holds our in-memory cache of log messages for display on this screen.
    val allLogs = remember { mutableStateListOf<String>() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf(LogFilterLevel.ALL) }
    var autoScroll by remember { mutableStateOf(true) }

    // Collect log stream reactively from AppLogger SharedFlow when this screen enters composition.
    LaunchedEffect(key1 = true) {
        logger.logStream.collect { log ->
            allLogs.add(log)
            if (autoScroll) {
                coroutineScope.launch {
                    val lastIndex = allLogs.size - 1
                    if (lastIndex >= 0) {
                        listState.animateScrollToItem(lastIndex)
                    }
                }
            }
        }
    }

    // Filter logs whenever the raw log size, search query, or selected filter level changes.
    val filteredLogs = remember(allLogs.size, searchQuery, selectedLevel) {
        allLogs.filter { log ->
            // Level Filter: match exact square brackets prefixed by AppLogger.
            val matchesLevel = when (selectedLevel) {
                LogFilterLevel.INFO -> log.startsWith("[INFO]")
                LogFilterLevel.DEBUG -> log.startsWith("[DEBUG]")
                LogFilterLevel.WARNING -> log.startsWith("[WARNING]")
                LogFilterLevel.ERROR -> log.startsWith("[ERROR]")
                LogFilterLevel.ALL -> true
            }

            // Search query Filter (case-insensitive substring match).
            val matchesQuery = if (searchQuery.isNotEmpty()) {
                log.contains(searchQuery, ignoreCase = true)
            } else true

            matchesLevel && matchesQuery
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Controls Section: Search input field & Choice Chips
        PaddingValues(horizontal = 16.dp, vertical = 8.dp).let { padding ->
            Column(modifier = Modifier.padding(padding)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search logs...", color = Color.Gray, fontSize = 13.sp) },
                    prefix = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF161626),
                        unfocusedContainerColor = Color(0xFF161626),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.LightGray,
                        unfocusedTextColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Log Level filter selection chips (INFO, DEBUG, WARNING, ERROR, ALL)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    LogFilterLevel.values().forEach { level ->
                        val isSelected = selectedLevel == level
                        val label = level.name
                        val selectedColor = when (level) {
                            LogFilterLevel.ERROR -> Color.Red
                            LogFilterLevel.WARNING -> Color(0xFFFFB300) // Amber
                            LogFilterLevel.DEBUG -> Color.Blue
                            else -> Color(0xFF0097A7)
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedLevel = level },
                            label = { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = selectedColor.copy(alpha = 0.2f),
                                selectedLabelColor = Color.White,
                                labelColor = Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = selectedColor,
                                borderColor = Color.White.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        }

        // Action Toolbar: Auto-scroll toggle, Copy-all button, Clear-all button
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = autoScroll,
                    onCheckedChange = { autoScroll = it },
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF0097A7))
                )
                Text(
                    text = "Auto-scroll",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Row {
                IconButton(
                    onClick = {
                        if (allLogs.isNotEmpty()) {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("OsmosLogs", allLogs.joinToString("\n"))
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copy all logs",
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = {
                        allLogs.clear()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Clear logs",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // The console viewport displaying all log strings in a scrollable view with level-specific text coloring.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF07070F))
                .border(1.dp, Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
        ) {
            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No matching logs found.",
                        color = Color.White.copy(alpha = 0.24f),
                        fontSize = 12.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredLogs) { log ->
                        val color = when {
                            log.startsWith("[ERROR]") -> Color.Red
                            log.startsWith("[INFO]") -> Color.Green
                            log.startsWith("[WARNING]") -> Color(0xFFFFB300) // Amber
                            log.startsWith("[DEBUG]") -> Color.Cyan
                            else -> Color.LightGray
                        }

                        Text(
                            text = log,
                            color = color,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
