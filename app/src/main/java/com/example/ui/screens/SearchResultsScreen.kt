package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.LanguageManager
import com.example.ui.components.BusCard
import com.example.ui.components.NextBusCard
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.appBackgroundColor
import com.example.ui.viewmodel.BusTimetableViewModel
import com.example.ui.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    viewModel: BusTimetableViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allTimetables by viewModel.allTimetables.collectAsStateWithLifecycle()
    val filteredBuses by viewModel.filteredBuses.collectAsStateWithLifecycle()
    val fromStand by viewModel.selectedFromStand.collectAsStateWithLifecycle()
    val toDest by viewModel.selectedToDestination.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeTimeFilter by viewModel.activeTimeFilter.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val currentMinutes by viewModel.currentMinutesFromMidnight.collectAsStateWithLifecycle()
    val favoriteKeys by viewModel.favoriteKeys.collectAsStateWithLifecycle()

    // Next bus in this filtered subset memoized with single-pass summary stats
    val summaryStats = remember(allTimetables, fromStand, toDest, currentMinutes) {
        viewModel.getSummaryStats(fromStand, toDest, currentMinutes)
    }
    val nextBus = summaryStats.nextBus
    val tomorrowFirstBus = if (nextBus == null) summaryStats.tomorrowFirstBus else null
    val remainingMinutes = summaryStats.remainingMinutes

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (toDest.isNotBlank()) "गंतव्य: $toDest" else "बस शोधा (Bus Search)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (fromStand.isNotBlank()) {
                            Text(
                                text = "सुरुवात: $fromStand",
                                fontSize = 12.sp,
                                color = Color(0xFFFFE0B2)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("search_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SaffronPrimary
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(appBackgroundColor())
        ) {
            // Search Input Field
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("नांदेड, पुसद, किनवट, आदिलाबाद (Search)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field")
                )
            }

            // Filters & Sort Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val filters = listOf(
                        "ALL" to "सर्व (All)",
                        "MORNING" to "🌅 सकाळ",
                        "AFTERNOON" to "☀️ दुपार",
                        "EVENING" to "🌆 संध्याकाळ"
                    )
                    items(filters) { (key, label) ->
                        val isSelected = activeTimeFilter == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setTimeFilter(key) },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Sort toggle button
                OutlinedButton(
                    onClick = { viewModel.toggleSortOrder() },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("sort_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (sortOrder == SortOrder.EARLIEST_FIRST) "पहिली" else "शेवटची",
                        fontSize = 11.sp
                    )
                }
            }

            // Result Count Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "एकूण उपलब्ध बस: ${filteredBuses.size}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4B5563)
                )
            }

            // Bus List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Next Bus Highlight
                if (nextBus != null && filteredBuses.contains(nextBus)) {
                    item {
                        Text(
                            text = "🟢 पुढील उपलब्ध बस (Next Bus)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        NextBusCard(
                            bus = nextBus,
                            tomorrowFirstBus = tomorrowFirstBus,
                            remainingMinutes = remainingMinutes,
                            isFavorite = favoriteKeys.contains("${nextBus.depotOrBusStand}_${nextBus.destination}"),
                            onToggleFavorite = {
                                viewModel.toggleFavorite(
                                    nextBus.depotOrBusStand,
                                    nextBus.depotOrBusStandMr,
                                    nextBus.destination,
                                    nextBus.destinationMr
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "सर्व वेळापत्रक यादी",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = HighContrastText
                        )
                    }
                }

                if (filteredBuses.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = "No bus",
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "या निकषांनुसार बस उपलब्ध नाही",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = HighContrastText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "कृपया दुसरा वेळ किंवा स्थानक निवडा.",
                                fontSize = 13.sp,
                                color = Color(0xFF6B7280)
                            )
                        }
                    }
                } else {
                    items(filteredBuses, key = { it.id }) { bus ->
                        val isFav = favoriteKeys.contains("${bus.depotOrBusStand}_${bus.destination}")
                        val diff = bus.departureMinutes - currentMinutes
                        val busRemaining = if (diff >= 0) diff else (diff + 1440)
                        BusCard(
                            bus = bus,
                            remainingMinutes = busRemaining,
                            isNextBus = (bus.id == nextBus?.id),
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
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
