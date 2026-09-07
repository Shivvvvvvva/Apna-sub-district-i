package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BusTimetable
import com.example.localization.LanguageManager
import com.example.ui.components.BusCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.appBackgroundColor
import com.example.ui.viewmodel.BusTimetableViewModel

@Composable
fun TimetableScreen(
    viewModel: BusTimetableViewModel,
    modifier: Modifier = Modifier
) {
    val allTimetables by viewModel.allTimetables.collectAsStateWithLifecycle()
    val activeTimeFilter by viewModel.activeTimeFilter.collectAsStateWithLifecycle()
    val favoriteKeys by viewModel.favoriteKeys.collectAsStateWithLifecycle()
    val currentMinutes by viewModel.currentMinutesFromMidnight.collectAsStateWithLifecycle()
    var selectedDepotFilter by remember { mutableStateOf("सर्व") }
    var searchQuery by remember { mutableStateOf("") }
    var isTableView by remember { mutableStateOf(false) }

    // Filter buses using cached searchableText and departureMinutes
    val displayedBuses = remember(allTimetables, activeTimeFilter, selectedDepotFilter, searchQuery) {
        var list = allTimetables

        // Depot filter
        if (selectedDepotFilter != "सर्व" && selectedDepotFilter != "All") {
            list = list.filter {
                it.depotOrBusStand.equals(selectedDepotFilter, ignoreCase = true) ||
                it.depotOrBusStandMr.equals(selectedDepotFilter, ignoreCase = true)
            }
        }

        // Time filter
        list = when (activeTimeFilter) {
            "MORNING" -> list.filter { it.timeCategory == com.example.data.model.TimeCategory.MORNING }
            "AFTERNOON" -> list.filter { it.timeCategory == com.example.data.model.TimeCategory.AFTERNOON }
            "EVENING" -> list.filter { it.timeCategory == com.example.data.model.TimeCategory.EVENING }
            else -> list
        }

        // Fast O(1) Search query using precomputed searchableText
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase()
            list = list.filter { it.searchableText.contains(q) }
        }

        list.sortedBy { it.departureMinutes }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        // Top Header
        Surface(
            color = MaroonSecondary,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📋 संपूर्ण बस वेळापत्रक",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "माहूर व किनवट बसस्थानक (एकूण ${displayedBuses.size} बस)",
                        fontSize = 12.sp,
                        color = Color(0xFFFFD1D9)
                    )
                }

                // Table vs Card view toggle
                IconButton(
                    onClick = { isTableView = !isTableView },
                    modifier = Modifier.testTag("toggle_table_view")
                ) {
                    Icon(
                        imageVector = if (isTableView) Icons.Default.ViewAgenda else Icons.Default.TableChart,
                        contentDescription = "Toggle View",
                        tint = Color.White
                    )
                }
            }
        }

        // Search Bar
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("गंतव्य किंवा मार्ग शोधा (Search)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("timetable_search_field")
            )
        }

        // Depot Filters (माहूर, किनवट, सर्व)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val depots = listOf("सर्व", "माहूर", "किनवट")
            items(depots) { depot ->
                val isSelected = selectedDepotFilter == depot
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedDepotFilter = depot },
                    label = { Text(depot, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaroonSecondary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Time Category Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val timeFilters = listOf(
                "ALL" to "सर्व वेळा",
                "MORNING" to "🌅 सकाळ (04-12)",
                "AFTERNOON" to "☀️ दुपार (12-17)",
                "EVENING" to "🌆 संध्याकाळ (17-24)"
            )
            items(timeFilters) { (key, label) ->
                val isSelected = activeTimeFilter == key
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setTimeFilter(key) },
                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Timetable Content: Either Table or Card View
        if (isTableView) {
            // CLASSIC TABLE VIEW: | क्र. | गंतव्य | सुटण्याची वेळ | मार्ग |
            val tableScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                // Table Header
                Surface(
                    color = MaroonSecondary,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(tableScroll)
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "क्र.", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(text = "स्थानक", modifier = Modifier.width(80.dp), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(text = "गंतव्य", modifier = Modifier.width(110.dp), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(text = "वेळ", modifier = Modifier.width(60.dp), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(text = "मार्ग", modifier = Modifier.width(180.dp), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                    }
                }

                // Table Rows
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    itemsIndexed(displayedBuses) { index, bus ->
                        val isEven = index % 2 == 0
                        Surface(
                            color = if (isEven) Color(0xFFFAF7F2) else Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(tableScroll)
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${index + 1}", modifier = Modifier.width(40.dp), fontSize = 12.sp, color = HighContrastText)
                                Text(text = LanguageManager.getLocalizedDepot(bus), modifier = Modifier.width(80.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaroonSecondary)
                                Text(text = LanguageManager.getLocalizedDestination(bus), modifier = Modifier.width(110.dp), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = HighContrastText)
                                Text(text = bus.departureTime, modifier = Modifier.width(60.dp), fontSize = 13.sp, fontWeight = FontWeight.Black, color = SaffronPrimary)
                                Text(text = LanguageManager.getLocalizedRoute(bus), modifier = Modifier.width(180.dp), fontSize = 12.sp, color = Color(0xFF4B5563))
                            }
                        }
                    }
                }
            }
        } else {
            // MOBILE CARD VIEW
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedBuses, key = { it.id }) { bus ->
                    val isFav = favoriteKeys.contains("${bus.depotOrBusStand}_${bus.destination}")
                    val diff = bus.departureMinutes - currentMinutes
                    val remaining = if (diff >= 0) diff else (diff + 1440)
                    BusCard(
                        bus = bus,
                        remainingMinutes = remaining,
                        isNextBus = false,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            viewModel.toggleFavorite(
                                bus.depotOrBusStand,
                                bus.depotOrBusStandMr,
                                bus.destination,
                                bus.destinationMr
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
