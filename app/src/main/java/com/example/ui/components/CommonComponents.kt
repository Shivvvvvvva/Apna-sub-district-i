package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.platform.LocalContext
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.appBackgroundColor
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appSubtitleColor
import com.example.ui.theme.appSurfaceColor
import com.example.ui.theme.appSurfaceVariantColor
import com.example.ui.theme.appTextColor
import com.example.data.model.BusTimetable
import com.example.localization.AppLanguage
import com.example.localization.LanguageManager
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmSurface

@Composable
fun AppHeader(
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Surface(
        color = SaffronPrimary,
        contentColor = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top Row: Title + Language Switcher & Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = "Bus",
                            tint = SaffronPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = LanguageManager.getAppName(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.testTag("app_header_title")
                        )
                        Text(
                            text = "नांदेड जिल्हा (Nanded)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFFE0B2)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Dark Mode Toggle Icon Button
                    IconButton(
                        onClick = {
                            ThemeManager.toggleDarkMode(context)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .testTag("app_header_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (ThemeManager.isDarkThemeActive) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 3 Language Buttons
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = LanguageManager.currentLanguage == lang
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable {
                                        LanguageManager.currentLanguage = lang
                                        onLanguageChange(lang)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                                    .testTag("lang_button_${lang.code}")
                            ) {
                                Text(
                                    text = lang.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SaffronPrimary else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = LanguageManager.getAppSubtitle(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFFFF3E0)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Offline status badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x2B000000))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LanguageManager.getOfflineModeText(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFF9C4)
                )
            }
        }
    }
}

@Composable
fun NextBusCard(
    bus: BusTimetable?,
    tomorrowFirstBus: BusTimetable?,
    remainingMinutes: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var reminderSet by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("next_bus_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = EmeraldContainer,
            contentColor = OnEmeraldContainer
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Badge + Favorite & Reminder action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EmeraldGreen,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LanguageManager.getNextBusBadge(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (bus != null) {
                    Row {
                        IconButton(
                            onClick = { reminderSet = !reminderSet },
                            modifier = Modifier.size(36.dp).testTag("next_bus_reminder")
                        ) {
                            Icon(
                                imageVector = if (reminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                                contentDescription = "Reminder",
                                tint = if (reminderSet) EmeraldGreen else Color(0xFF2E7D32)
                            )
                        }
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier.size(36.dp).testTag("next_bus_fav")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) MaroonSecondary else Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (bus != null) {
                // Bus Destination + Departure Time (Very large for elderly)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.getDestinationLabel(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E4620)
                        )
                        Text(
                            text = "🚌 " + LanguageManager.getLocalizedDestination(bus),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F3911),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, EmeraldGreen)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = LanguageManager.getDepartureTimeLabel(),
                                fontSize = 10.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                text = bus.departureTime,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Starting Bus Stand & Route
                Text(
                    text = "${LanguageManager.getStartingStandLabel()} ${LanguageManager.getLocalizedDepot(bus)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A471C)
                )

                val route = LanguageManager.getLocalizedRoute(bus)
                Text(
                    text = "${LanguageManager.getRouteLabel()} $route",
                    fontSize = 13.sp,
                    color = Color(0xFF275229)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Estimated remaining time pill
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Time remaining",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LanguageManager.formatRemainingTime(remainingMinutes),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF134E17)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = LanguageManager.getNextBusVerifiedNote(),
                    fontSize = 11.sp,
                    color = Color(0xFF2D592F),
                    fontWeight = FontWeight.Medium
                )

                if (reminderSet) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = LanguageManager.getReminderSet(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F5132)
                    )
                }
            } else {
                // Today's last bus has departed
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚠️ " + LanguageManager.getLastBusDepartedNote(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaroonSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (tomorrowFirstBus != null) {
                        Text(
                            text = "उद्याची पहिली बस: ${LanguageManager.getLocalizedDestination(tomorrowFirstBus)} (${tomorrowFirstBus.departureTime})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusCard(
    bus: BusTimetable,
    remainingMinutes: Int,
    isNextBus: Boolean,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var reminderSet by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bus_card_${bus.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isNextBus) {
                if (ThemeManager.isDarkThemeActive) Color(0xFF00381C) else Color(0xFFF1F8E9)
            } else appSurfaceColor()
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isNextBus) BorderStroke(1.5.dp, EmeraldGreen) else BorderStroke(1.dp, appBorderColor())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Destination + Departure Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (isNextBus) {
                        Surface(
                            color = EmeraldGreen,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Text(
                                text = "🟢 " + LanguageManager.getNextBusBadge(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "🚌 " + LanguageManager.getLocalizedDestination(bus),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextColor(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Large readable departure time
                Surface(
                    color = if (isNextBus) {
                        if (ThemeManager.isDarkThemeActive) Color(0xFF004D27) else EmeraldContainer
                    } else {
                        if (ThemeManager.isDarkThemeActive) Color(0xFF5C1B00) else SaffronContainer
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = bus.departureTime,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isNextBus) EmeraldGreen else SaffronPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Starting Stand
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Stand",
                    tint = MaroonSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${LanguageManager.getStartingStandLabel()} ${LanguageManager.getLocalizedDepot(bus)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = appTextColor()
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Route & Village Stops
            val routeStr = LanguageManager.getLocalizedRoute(bus)
            val stops = routeStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            val hasAnjankhed = bus.busRouteMr.contains("अंजनखेड")
            val hasSarkhani = bus.busRouteMr.contains("सारखणी")

            if (hasAnjankhed || hasSarkhani) {
                Surface(
                    color = if (ThemeManager.isDarkThemeActive) Color(0xFF862700) else SaffronContainer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Text(
                        text = "📍 " + (if (hasAnjankhed && hasSarkhani) "अंजनखेड व सारखणी थांबा उपलब्ध" else if (hasAnjankhed) "अंजनखेड थांबा उपलब्ध" else "सारखणी थांबा उपलब्ध"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            // Route text
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = "Route",
                    tint = SaffronPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${LanguageManager.getRouteLabel()} $routeStr",
                    fontSize = 13.sp,
                    color = appSubtitleColor(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Village stops chips row
            if (stops.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "गावे:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = appSubtitleColor()
                    )
                    stops.forEach { stop ->
                        val isHighlightedVillage = stop.contains("अंजनखेड") || stop.contains("सारखणी")
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isHighlightedVillage) {
                                if (ThemeManager.isDarkThemeActive) Color(0xFF7A2400) else Color(0xFFFFE0B2)
                            } else appSurfaceVariantColor()
                        ) {
                            Text(
                                text = (if (isHighlightedVillage) "⭐ " else "") + stop,
                                fontSize = 11.sp,
                                fontWeight = if (isHighlightedVillage) FontWeight.Bold else FontWeight.Normal,
                                color = if (isHighlightedVillage) {
                                    if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else Color(0xFF8B2500)
                                } else appTextColor(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom bar: Remaining time indicator + Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (remainingMinutes in 1..720) {
                    Surface(
                        color = if (remainingMinutes < 30) Color(0xFFFFEBEE) else Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = LanguageManager.formatRemainingTime(remainingMinutes),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (remainingMinutes < 30) MaroonSecondary else Color(0xFF1F2937),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else if (remainingMinutes <= 0 && remainingMinutes >= -60) {
                    Text(
                        text = "निघून गेली (Departed)",
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF)
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row {
                    // Reminder toggle
                    IconButton(
                        onClick = { reminderSet = !reminderSet },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (reminderSet) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Reminder",
                            tint = if (reminderSet) SaffronPrimary else Color(0xFF9CA3AF)
                        )
                    }

                    // Favorite toggle
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaroonSecondary else Color(0xFF9CA3AF)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = reminderSet) {
                Text(
                    text = LanguageManager.getReminderSet(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BusStandSelectorDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) options
        else options.filter { it.contains(searchQuery.trim(), ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HighContrastText
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("शोधा (Search)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    items(filteredOptions) { option ->
                        val isSelected = option.equals(selectedOption, ignoreCase = true)
                        OutlinedCard(
                            onClick = {
                                onSelect(option)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) SaffronContainer else Color.White
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) SaffronPrimary else Color(0xFFE5E7EB)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) SaffronPrimary else HighContrastText
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करा (Close)")
            }
        }
    )
}
