package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.R
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.datasource.InitialTimetableData
import com.example.data.datasource.OfficialTimetableEntry
import com.example.data.model.BusTimetable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.FactCheck
import com.example.ui.components.ValidationReportDialog
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.appBackgroundColor
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appSubtitleColor
import com.example.ui.theme.appSurfaceColor
import com.example.ui.theme.appSurfaceVariantColor
import com.example.ui.theme.appTextColor
import com.example.localization.AppLanguage
import com.example.localization.LanguageManager
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.WarmSurface
import com.example.ui.viewmodel.BusTimetableViewModel
import com.example.ui.viewmodel.LiveBusState
import com.example.ui.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BusTimetableViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allTimetables by viewModel.allTimetables.collectAsStateWithLifecycle()
    val filteredBuses by viewModel.filteredBuses.collectAsStateWithLifecycle()
    val fromStand by viewModel.selectedFromStand.collectAsStateWithLifecycle()
    val toDest by viewModel.selectedToDestination.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeTimeFilter by viewModel.activeTimeFilter.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val selectedVillageFilter by viewModel.selectedVillageFilter.collectAsStateWithLifecycle()

    var isOfficialTableMode by remember { mutableStateOf(false) }
    var showValidationReportDialog by remember { mutableStateOf(false) }

    // Default to Mahur on initial load if not set
    LaunchedEffect(Unit) {
        if (fromStand.isBlank()) {
            viewModel.setFromStand("माहूर")
        }
    }

    // Real-time dynamic time updates & filters
    val currentDeviceTimeMillis by viewModel.currentDeviceTimeMillis.collectAsStateWithLifecycle()
    val currentMinutesFromMidnight by viewModel.currentMinutesFromMidnight.collectAsStateWithLifecycle()
    val hideDepartedBuses by viewModel.hideDepartedBuses.collectAsStateWithLifecycle()
    val favoriteKeys by viewModel.favoriteKeys.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()

    // Calculate next upcoming bus dynamically with single-pass memoized summary stats
    val summaryStats = remember(allTimetables, fromStand, toDest, currentMinutesFromMidnight) {
        viewModel.getSummaryStats(fromStand, toDest, currentMinutesFromMidnight)
    }
    val nextBus = summaryStats.nextBus
    val remainingMinutes = summaryStats.remainingMinutes
    val upcomingCount = summaryStats.upcomingCount
    val departedCount = summaryStats.departedCount
    val tomorrowFirstBus = summaryStats.tomorrowFirstBus

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        // TOP HEADER: Warm Saffron with direct emergency call and language switch
        Surface(
            color = SaffronPrimary,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_majha_gaon_logo),
                            contentDescription = "माझं गाव Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "माझं गाव",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFF1B5E20),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "बससेवा",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "माझी ओळख, माझा अभिमान",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFFE0B2)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Quick language switcher
                        Surface(
                            onClick = {
                                val nextLang = when (LanguageManager.currentLanguage) {
                                    AppLanguage.MARATHI -> AppLanguage.ENGLISH
                                    AppLanguage.ENGLISH -> AppLanguage.HINDI
                                    AppLanguage.HINDI -> AppLanguage.MARATHI
                                }
                                LanguageManager.currentLanguage = nextLang
                            },
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Language, contentDescription = "Language", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (LanguageManager.currentLanguage) {
                                        AppLanguage.MARATHI -> "मराठी"
                                        AppLanguage.HINDI -> "हिंदी"
                                        AppLanguage.ENGLISH -> "ENG"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // STEP 0: LIVE REAL-TIME TICKER & TIME-BASED UPDATE CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_time_status_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ThemeManager.isDarkThemeActive) Color(0xFF1B263B) else Color(0xFFF0FDF4)
                    ),
                    border = BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Live Clock Header Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "स्थानिक वेळ (Device Time)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (ThemeManager.isDarkThemeActive) Color(0xFF862700) else SaffronContainer
                                ) {
                                    Text(
                                        text = viewModel.getLivePeriodMr(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Real-time digital clock with seconds
                            Text(
                                text = viewModel.getFormattedLiveTime(currentDeviceTimeMillis),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = appTextColor(),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Immediate Next Bus summary
                        if (nextBus != null) {
                            val liveStatus = viewModel.getBusLiveStatus(nextBus, currentMinutesFromMidnight)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (ThemeManager.isDarkThemeActive) Color(0xFF0F172A) else Color.White,
                                border = BorderStroke(1.dp, if (liveStatus.state == LiveBusState.DEPARTING_NOW) Color(0xFFE53935) else EmeraldGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (liveStatus.state == LiveBusState.DEPARTING_NOW) Color(0xFFFFEBEE) else EmeraldContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsBus,
                                            contentDescription = "Next Bus",
                                            tint = if (liveStatus.state == LiveBusState.DEPARTING_NOW) Color(0xFFC62828) else EmeraldGreen,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "पुढील गाडी: ",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = appSubtitleColor()
                                                )
                                                Text(
                                                    text = LanguageManager.getLocalizedDestination(nextBus),
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = appTextColor()
                                                )
                                            }
                                            Text(
                                                text = nextBus.departureTime,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (liveStatus.state == LiveBusState.DEPARTING_NOW) Color(0xFFC62828) else EmeraldGreen
                                            )
                                        }
                                        Text(
                                            text = "${LanguageManager.getLocalizedDepot(nextBus)}हून  •  ${liveStatus.badgeTextMr}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (liveStatus.state == LiveBusState.DEPARTING_NOW) Color(0xFFE53935) else EmeraldGreen
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (ThemeManager.isDarkThemeActive) Color(0xFF0F172A) else Color.White,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaroonSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "आजच्या उर्वरित वेळेतील सर्व बसेस सुटल्या आहेत.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = appTextColor()
                                        )
                                        if (tomorrowFirstBus != null) {
                                            Text(
                                                text = "उद्या सकाळची पहिली बस: ${tomorrowFirstBus.departureTime} (${LanguageManager.getLocalizedDestination(tomorrowFirstBus)})",
                                                fontSize = 11.sp,
                                                color = EmeraldGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Actions: "🔴 चालू वेळेनुसार पुढील गाड्या" & "सुटलेल्या गाड्या लपवा"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isUpcomingActive = activeTimeFilter == "UPCOMING"
                            Button(
                                onClick = {
                                    if (isUpcomingActive) {
                                        viewModel.setTimeFilter("ALL")
                                    } else {
                                        viewModel.setTimeFilter("UPCOMING")
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("filter_upcoming_from_now"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isUpcomingActive) SaffronPrimary else EmeraldGreen
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isUpcomingActive) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUpcomingActive) "चालू वेळेनुसार (दाखवत आहे ✓)" else "चालू वेळेनुसार बसेस ($upcomingCount)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            // Toggle hide departed
                            Surface(
                                onClick = { viewModel.toggleHideDepartedBuses() },
                                shape = RoundedCornerShape(8.dp),
                                color = if (hideDepartedBuses) MaroonSecondary else appSurfaceVariantColor(),
                                border = BorderStroke(1.dp, if (hideDepartedBuses) MaroonSecondary else appBorderColor()),
                                modifier = Modifier.testTag("toggle_hide_departed")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (hideDepartedBuses) "सुटलेल्या लपवल्या ✓" else "सुटलेल्या लपवा ($departedCount)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (hideDepartedBuses) Color.White else appTextColor()
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Offline Official PDF Disclosure & Audit Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (ThemeManager.isDarkThemeActive) Color(0xFF101924) else Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBus,
                                        contentDescription = null,
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "अधिकृत PDF वेळापत्रक • रिअल-टाइम GPS ट्रॅकिंग नाही",
                                        fontSize = 11.sp,
                                        color = appSubtitleColor(),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                TextButton(
                                    onClick = { showValidationReportDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = EmeraldGreen)
                                ) {
                                    Text(
                                        text = "अहवाल पहा >",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 1: CHOOSE BUS STAND (Mahur vs Kinwat vs All)
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor()),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "१. बस कुठून हवी आहे? (Select Bus Stand)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaroonSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Mahur Stand Button
                            val isMahur = fromStand.contains("माहूर") || fromStand.contains("Mahur")
                            Surface(
                                onClick = { viewModel.setFromStand("माहूर") },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("stand_mahur"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isMahur) SaffronPrimary else appSurfaceVariantColor(),
                                border = BorderStroke(1.5.dp, if (isMahur) SaffronPrimary else appBorderColor()),
                                shadowElevation = if (isMahur) 3.dp else 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isMahur) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = "🚩 माहूर",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMahur) Color.White else appTextColor()
                                        )
                                    }
                                    Text(
                                        text = "४२ मार्ग (१११ फेऱ्या)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isMahur) Color(0xFFFFE0B2) else appSubtitleColor()
                                    )
                                }
                            }

                            // Kinwat Stand Button
                            val isKinwat = fromStand.contains("किनवट") || fromStand.contains("Kinwat")
                            Surface(
                                onClick = { viewModel.setFromStand("किनवट") },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("stand_kinwat"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isKinwat) SaffronPrimary else appSurfaceVariantColor(),
                                border = BorderStroke(1.5.dp, if (isKinwat) SaffronPrimary else appBorderColor()),
                                shadowElevation = if (isKinwat) 3.dp else 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isKinwat) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }
                                        Text(
                                            text = "🚩 किनवट",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isKinwat) Color.White else appTextColor()
                                        )
                                    }
                                    Text(
                                        text = "२३ मार्ग (५९ फेऱ्या)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isKinwat) Color(0xFFFFE0B2) else appSubtitleColor()
                                    )
                                }
                            }

                            // All Stands Button
                            val isAll = fromStand.isBlank() || fromStand == "सर्व" || fromStand == "सर्व (All)"
                            Surface(
                                onClick = { viewModel.setFromStand("सर्व") },
                                modifier = Modifier
                                    .weight(0.9f)
                                    .testTag("stand_all"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isAll) SaffronPrimary else appSurfaceVariantColor(),
                                border = BorderStroke(1.5.dp, if (isAll) SaffronPrimary else appBorderColor()),
                                shadowElevation = if (isAll) 3.dp else 0.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "सर्व",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAll) Color.White else appTextColor()
                                    )
                                    Text(
                                        text = "१७० फेऱ्या",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isAll) Color(0xFFFFE0B2) else appSubtitleColor()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 2: WHERE DO YOU WANT TO GO? (Quick Search & Top Destination Chips & Village Stops)
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor()),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "२. कुठे जायचे आहे? (Where to go?)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaroonSecondary
                            )
                            if (searchQuery.isNotEmpty() || toDest.isNotEmpty()) {
                                TextButton(
                                    onClick = { viewModel.clearSearch() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaroonSecondary)
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("सर्व दाखवा (Reset)", fontSize = 12.sp, color = MaroonSecondary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search box with instant clear and live indicator
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_input_box"),
                            placeholder = {
                                Text(
                                    text = "गावाचे नाव किंवा थांबा शोधा (उदा. अंजनखेड, सारखणी, नांदेड...)",
                                    fontSize = 13.sp,
                                    color = appSubtitleColor()
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search", tint = SaffronPrimary)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.clearSearch() }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.Gray)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = appBorderColor(),
                                focusedContainerColor = appSurfaceVariantColor(),
                                unfocusedContainerColor = appSurfaceVariantColor(),
                                focusedTextColor = appTextColor(),
                                unfocusedTextColor = appTextColor()
                            )
                        )

                        // If user is typing, show dynamic live suggestions
                        val suggestions = remember(searchQuery) {
                            if (searchQuery.isNotBlank()) viewModel.getSearchSuggestions(searchQuery) else emptyList()
                        }
                        if (suggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "सुचवलेली ठिकाणे (टॅप करा):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = appSubtitleColor()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(suggestions) { suggestion ->
                                    SuggestionChip(
                                        onClick = { viewModel.setSearchQuery(suggestion) },
                                        label = { Text("📍 $suggestion", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = if (ThemeManager.isDarkThemeActive) Color(0xFF374151) else Color(0xFFFFF3E0),
                                            labelColor = SaffronPrimary
                                        ),
                                        border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f))
                                    )
                                }
                            }
                        }

                        // Search result feedback badge
                        if (searchQuery.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "‘$searchQuery’ साठी ${filteredBuses.size} बसेस उपलब्ध",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (filteredBuses.isNotEmpty()) EmeraldGreen else Color(0xFFC62828)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // POPULAR DESTINATIONS & STOPS HORIZONTAL SCROLL CHIPS
                        Text(
                            text = "लोकप्रिय ठिकाणे व थांबे (Quick Select):",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = appSubtitleColor()
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val popularDestinations = remember {
                            listOf(
                                "सर्व" to "सर्व गाड्या",
                                "नांदेड" to "नांदेड (४० फेऱ्या)",
                                "किनवट" to "किनवट",
                                "माहूर" to "माहूर",
                                "पुसद" to "पुसद",
                                "यवतमाळ" to "यवतमाळ",
                                "अमरावती" to "अमरावती",
                                "नागपूर" to "नागपूर",
                                "आदिलाबाद" to "आदिलाबाद",
                                "अंजनखेड" to "📍 अंजनखेड थांबा",
                                "सारखणी" to "📍 सारखणी थांबा",
                                "वाई" to "📍 वाई थांबा",
                                "बोधडी" to "📍 बोधडी",
                                "मांडवी" to "📍 मांडवी",
                                "इस्लापूर" to "📍 इस्लामपूर",
                                "लातूर" to "लातूर",
                                "पुणे" to "पुणे",
                                "अकोला" to "अकोला",
                                "हिंगोली" to "हिंगोली",
                                "वाशीम" to "वाशीम",
                                "चंद्रपूर" to "चंद्रपूर"
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(popularDestinations) { (destKey, destLabel) ->
                                val isSelected = (destKey == "सर्व" && searchQuery.isEmpty() && toDest.isEmpty()) ||
                                        (destKey != "सर्व" && (searchQuery.equals(destKey, ignoreCase = true) || toDest.equals(destKey, ignoreCase = true)))

                                Surface(
                                    onClick = {
                                        viewModel.selectDestination(destKey)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) SaffronPrimary else appSurfaceVariantColor(),
                                    border = BorderStroke(1.dp, if (isSelected) SaffronPrimary else appBorderColor()),
                                    modifier = Modifier.testTag("chip_dest_$destKey")
                                ) {
                                    Text(
                                        text = destLabel,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else appTextColor(),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // STEP 3: TIME OF DAY FILTERS (Upcoming live time, Morning, Afternoon, Evening)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val timeFilters = listOf(
                        "UPCOMING" to "🔴 चालू वेळेनुसार",
                        "ALL" to "सर्व वेळा",
                        "MORNING" to "🌅 सकाळ",
                        "AFTERNOON" to "☀️ दुपार",
                        "EVENING" to "🌙 संध्याकाळ"
                    )

                    timeFilters.forEach { (filterKey, filterLabel) ->
                        val isSelected = activeTimeFilter == filterKey
                        Surface(
                            onClick = { viewModel.setTimeFilter(filterKey) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) SaffronPrimary else appSurfaceVariantColor(),
                            border = BorderStroke(1.dp, if (isSelected) SaffronPrimary else appBorderColor())
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = filterLabel,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else appTextColor()
                                )
                            }
                        }
                    }
                }
            }

            // LIVE NEXT BUS HIGHLIGHT BANNER
            if (nextBus != null && remainingMinutes >= 0 && remainingMinutes <= 120) {
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("next_bus_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = EmeraldContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "Next Bus",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "पुढील बस: ",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = LanguageManager.getLocalizedDestination(nextBus),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1B5E20)
                                    )
                                }
                                Text(
                                    text = "वेळ: ${nextBus.departureTime}  •  अंदाजे $remainingMinutes मिनिटांत सुटेल",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }

            // SECTION TITLE & VIEW MODE TOGGLE (Trip Cards vs Official Chart Table)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = { isOfficialTableMode = false },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_trips"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isOfficialTableMode) MaroonSecondary else Color.White,
                        border = BorderStroke(1.5.dp, if (!isOfficialTableMode) MaroonSecondary else Color(0xFFD1D5DB)),
                        shadowElevation = if (!isOfficialTableMode) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DirectionsBus,
                                contentDescription = null,
                                tint = if (!isOfficialTableMode) Color.White else MaroonSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🎴 फेऱ्यांची यादी (${filteredBuses.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isOfficialTableMode) Color.White else HighContrastText
                            )
                        }
                    }

                    Surface(
                        onClick = { isOfficialTableMode = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_official_table"),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isOfficialTableMode) MaroonSecondary else Color.White,
                        border = BorderStroke(1.5.dp, if (isOfficialTableMode) MaroonSecondary else Color(0xFFD1D5DB)),
                        shadowElevation = if (isOfficialTableMode) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (isOfficialTableMode) Color.White else MaroonSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📋 मूळ अधिकृत तक्ता",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOfficialTableMode) Color.White else HighContrastText
                            )
                        }
                    }
                }
            }

            if (isOfficialTableMode) {
                // OFFICIAL TIMETABLE TABLE MODE (1:1 with PDF)
                val officialRows = when {
                    fromStand.contains("किनवट") || fromStand.contains("Kinwat") -> InitialTimetableData.kinwatOfficialRows
                    fromStand.contains("माहूर") || fromStand.contains("Mahur") -> InitialTimetableData.mahurOfficialRows
                    else -> InitialTimetableData.mahurOfficialRows + InitialTimetableData.kinwatOfficialRows
                }.filter { row ->
                    if (searchQuery.isBlank()) true
                    else {
                        val q = searchQuery.trim().lowercase()
                        row.destination.lowercase().contains(q) ||
                        row.destinationMr.lowercase().contains(q) ||
                        row.busRoute.lowercase().contains(q) ||
                        row.busRouteMr.lowercase().contains(q)
                    }
                }

                item {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaroonSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "अधिकृत एसटी वेळापत्रक फलक (एकूण ${officialRows.size} मार्ग)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaroonSecondary
                            )
                        }
                    }
                }

                items(officialRows, key = { "${it.depotOrBusStand}_${it.no}_${it.destination}" }) { row ->
                    OfficialRowCard(
                        entry = row,
                        onViewTrips = {
                            viewModel.setToDestination(row.destinationMr)
                            isOfficialTableMode = false
                        }
                    )
                }
            } else {
                // TRIP CARDS LIST MODE (Live Next Bus, Countdown, Filters)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "उपलब्ध गाड्या (${filteredBuses.size} फेऱ्या)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighContrastText
                        )

                        // Sort Order Button
                        TextButton(
                            onClick = { viewModel.toggleSortOrder() },
                            colors = ButtonDefaults.textButtonColors(contentColor = SaffronPrimary)
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (sortOrder == SortOrder.EARLIEST_FIRST) "पहिली आधी" else "शेवटची आधी",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // EMPTY STATE (If no buses match)
                if (filteredBuses.isEmpty()) {
                    item {
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = WarmSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "No buses",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (activeTimeFilter == "UPCOMING") "आजच्या चालू वेळेनंतरच्या सर्व बसेस सुटल्या आहेत." else "या मार्गावर सध्या कोणतीही बस सापडली नाही.",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighContrastText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (activeTimeFilter == "UPCOMING" && tomorrowFirstBus != null) {
                                    Text(
                                        text = "उद्या सकाळची पहिली बस: ${tomorrowFirstBus.departureTime} (${LanguageManager.getLocalizedDestination(tomorrowFirstBus)})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = "कृपया सर्व वेळापत्रक पाहण्यासाठी खालील बटण दाबा.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        viewModel.clearSearch()
                                        viewModel.setTimeFilter("ALL")
                                        if (hideDepartedBuses) viewModel.toggleHideDepartedBuses()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("सर्व बसेस पहा (Show All Timetable)")
                                }
                            }
                        }
                    }
                }

                // BUS CARDS LIST
                items(filteredBuses, key = { it.id }) { bus ->
                    SimpleBusCard(
                        bus = bus,
                        viewModel = viewModel,
                        isNextBus = (bus.id == nextBus?.id),
                        isFavorite = favoriteKeys.contains("${bus.depotOrBusStand}_${bus.destination}"),
                        hasReminder = reminders.contains(bus.id),
                        currentMinutes = currentMinutesFromMidnight,
                        selectedVillageFilter = selectedVillageFilter
                    )
                }
            }
        }

        if (showValidationReportDialog) {
            ValidationReportDialog(
                report = viewModel.getValidationReport(),
                onDismiss = { showValidationReportDialog = false }
            )
        }
    }
}

@Composable
fun OfficialRowCard(
    entry: OfficialTimetableEntry,
    onViewTrips: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasAnjankhed = entry.busRouteMr.contains("अंजनखेड")
    val hasSarkhani = entry.busRouteMr.contains("सारखणी")

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("official_row_${entry.no}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor()),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top row: NO badge + Destination + Depot badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Serial Number Badge
                Surface(
                    color = MaroonSecondary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${entry.no}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${entry.destinationMr} (${entry.destination})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextColor()
                    )
                    Text(
                        text = "स्थानक: ${entry.depotOrBusStandMr}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SaffronPrimary
                    )
                }

                // Quick view button
                TextButton(
                    onClick = onViewTrips,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = EmeraldGreen)
                ) {
                    Text("फेऱ्या पहा >", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (hasAnjankhed || hasSarkhani) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = if (ThemeManager.isDarkThemeActive) Color(0xFF862700) else SaffronContainer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "📍 " + (if (hasAnjankhed && hasSarkhani) "अंजनखेड व सारखणी थांबा" else if (hasAnjankhed) "अंजनखेड थांबा" else "सारखणी थांबा"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Departure Times
            Surface(
                color = appSurfaceVariantColor(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = "Departure times",
                            tint = MaroonSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "सुटण्याच्या वेळा (Departure Times):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaroonSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // List of times rendered cleanly
                    val timesList = entry.departureTimes.split(",").map { it.trim() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        timesList.forEach { time ->
                            Surface(
                                color = appSurfaceColor(),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "🕒 $time",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appTextColor(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bus Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "मार्ग (Route): ",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = appTextColor()
                )
                Text(
                    text = if (entry.busRouteMr == entry.busRoute) entry.busRouteMr else "${entry.busRouteMr} (${entry.busRoute})",
                    fontSize = 12.sp,
                    color = appSubtitleColor(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SimpleBusCard(
    bus: BusTimetable,
    viewModel: BusTimetableViewModel,
    isNextBus: Boolean = false,
    isFavorite: Boolean = false,
    hasReminder: Boolean = false,
    currentMinutes: Int = 0,
    selectedVillageFilter: String = "",
    modifier: Modifier = Modifier
) {
    val liveStatus = remember(bus.departureMinutes, currentMinutes) {
        viewModel.getBusLiveStatus(bus, currentMinutes)
    }

    val hasAnjankhed = remember(bus.busRouteMr) { bus.busRouteMr.contains("अंजनखेड") }
    val hasSarkhani = remember(bus.busRouteMr) { bus.busRouteMr.contains("सारखणी") }
    val passesSelectedVillage = remember(selectedVillageFilter, bus.busRouteMr) {
        selectedVillageFilter.isNotBlank() && bus.busRouteMr.contains(selectedVillageFilter)
    }

    val isDeparted = liveStatus.state == LiveBusState.DEPARTED
    val isDepartingNow = liveStatus.state == LiveBusState.DEPARTING_NOW

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bus_card_${bus.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isNextBus -> if (ThemeManager.isDarkThemeActive) Color(0xFF00381C) else Color(0xFFF1F8E9)
                isDeparted -> if (ThemeManager.isDarkThemeActive) Color(0xFF18202A) else Color(0xFFF9FAFB)
                else -> appSurfaceColor()
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDeparted) 1.dp else 2.dp),
        border = BorderStroke(
            width = if (isNextBus || isDepartingNow) 2.dp else 1.dp,
            color = when {
                isNextBus -> EmeraldGreen
                isDepartingNow -> Color(0xFFE53935)
                isDeparted -> if (ThemeManager.isDarkThemeActive) Color(0xFF374151) else Color(0xFFE5E7EB)
                else -> appBorderColor()
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Next Bus Indicator if applicable
            if (isNextBus) {
                Surface(
                    color = EmeraldGreen,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "🟢 पुढील सुटणारी गाडी (NEXT BUS)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // Top Row: Time Badge, Destination, and Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Large Time Badge
                Surface(
                    color = when {
                        isDepartingNow -> Color(0xFFE53935)
                        isDeparted -> Color(0xFF757575)
                        else -> EmeraldGreen
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = "Time", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = bus.departureTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Destination
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = LanguageManager.getLocalizedDestination(bus),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeparted) appSubtitleColor() else appTextColor()
                    )
                    Text(
                        text = "${LanguageManager.getLocalizedDepot(bus)}हून सुटणारी",
                        fontSize = 12.sp,
                        color = MaroonSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Action Icons (Save & Reminder)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.toggleFavorite(bus) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Favorite",
                            tint = if (isFavorite) SaffronPrimary else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleReminder(bus) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (hasReminder) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Set Reminder",
                            tint = if (hasReminder) EmeraldGreen else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Live Time Status Pill
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                color = when (liveStatus.state) {
                    LiveBusState.DEPARTING_NOW -> Color(0xFFFFEBEE)
                    LiveBusState.IMMINENT -> Color(0xFFFFF3E0)
                    LiveBusState.UPCOMING -> Color(0xFFE8F5E9)
                    LiveBusState.DEPARTED -> if (ThemeManager.isDarkThemeActive) Color(0xFF262626) else Color(0xFFF3F4F6)
                },
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(
                    1.dp,
                    when (liveStatus.state) {
                        LiveBusState.DEPARTING_NOW -> Color(0xFFE53935)
                        LiveBusState.IMMINENT -> Color(0xFFFF9800)
                        LiveBusState.UPCOMING -> EmeraldGreen
                        LiveBusState.DEPARTED -> Color(0xFFD1D5DB)
                    }
                )
            ) {
                Text(
                    text = liveStatus.badgeTextMr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (liveStatus.state) {
                        LiveBusState.DEPARTING_NOW -> Color(0xFFC62828)
                        LiveBusState.IMMINENT -> Color(0xFFE65100)
                        LiveBusState.UPCOMING -> Color(0xFF2E7D32)
                        LiveBusState.DEPARTED -> Color(0xFF6B7280)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            // Village highlight badge (Specifically for Anjankhed, Sarkhani, or active filter)
            if (hasAnjankhed || hasSarkhani || passesSelectedVillage) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = if (ThemeManager.isDarkThemeActive) Color(0xFF862700) else SaffronContainer,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val badgeText = when {
                            passesSelectedVillage && selectedVillageFilter !in listOf("अंजनखेड", "सारखणी") ->
                                "$selectedVillageFilter थांबा उपलब्ध (मार्गावर आहे)"
                            hasAnjankhed && hasSarkhani ->
                                "अंजनखेड व सारखणी थांबा उपलब्ध (मार्गावर)"
                            hasAnjankhed ->
                                "अंजनखेड थांबा उपलब्ध (मार्गावर)"
                            hasSarkhani ->
                                "सारखणी थांबा उपलब्ध (मार्गावर)"
                            else -> ""
                        }
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bus Route Details
            Surface(
                color = appSurfaceVariantColor(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "मार्गे: ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextColor()
                    )
                    Text(
                        text = LanguageManager.getLocalizedRoute(bus),
                        fontSize = 12.sp,
                        color = appSubtitleColor(),
                        lineHeight = 16.sp
                    )
                }
            }

            // Intermediate village stops chips row
            val routeStr = LanguageManager.getLocalizedRoute(bus)
            val stops = routeStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            if (stops.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Route,
                        contentDescription = null,
                        tint = SaffronPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "गावे:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = appSubtitleColor()
                    )
                    stops.forEach { stop ->
                        val isHighlighted = stop.contains("अंजनखेड") || stop.contains("सारखणी") ||
                            (selectedVillageFilter.isNotBlank() && stop.contains(selectedVillageFilter))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isHighlighted) {
                                if (ThemeManager.isDarkThemeActive) Color(0xFF7A2400) else Color(0xFFFFE0B2)
                            } else appSurfaceVariantColor(),
                            border = if (isHighlighted) BorderStroke(1.dp, SaffronPrimary) else null
                        ) {
                            Text(
                                text = (if (isHighlighted) "⭐ " else "") + stop,
                                fontSize = 11.sp,
                                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                color = if (isHighlighted) {
                                    if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else Color(0xFF8B2500)
                                } else appTextColor(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bus type & frequency footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "गाडी प्रकार: ${LanguageManager.getLocalizedBusType(bus)}",
                    fontSize = 11.sp,
                    color = appSubtitleColor()
                )
                Text(
                    text = "दररोज सेवा (Daily)",
                    fontSize = 11.sp,
                    color = EmeraldGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Official PDF Source Verification Metadata Pill
            Surface(
                color = appSurfaceVariantColor(),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📄 ${bus.sourceFile} • पान ${bus.sourcePage}",
                        fontSize = 10.sp,
                        color = appSubtitleColor(),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "मूळ वेळ: ${if (bus.exactDepartureTime.isNotEmpty()) bus.exactDepartureTime else bus.departureTime}",
                        fontSize = 10.sp,
                        color = MaroonSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Conflict Warning if present
            if (bus.isDataConflict) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFE53935)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ DATA CONFLICT: ${bus.conflictDetails}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC62828),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Uncertainty Note if present
            if (bus.uncertaintyNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFA000)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠️ ${bus.uncertaintyNote}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
