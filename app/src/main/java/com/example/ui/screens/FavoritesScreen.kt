package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.East
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.GoldenYellow
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.appBackgroundColor
import com.example.ui.viewmodel.BusTimetableViewModel

@Composable
fun FavoritesScreen(
    viewModel: BusTimetableViewModel,
    onRouteSelected: (from: String, to: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.allFavorites.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        // Top Header
        Surface(
            color = GoldenYellow,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "⭐ आवडते बस मार्ग (Saved Routes)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "तुमचे नेहमीचे प्रवास मार्ग एका क्लिकवर तपासा",
                    fontSize = 12.sp,
                    color = Color(0xFFFFF9C4)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (favorites.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "No favorites",
                    tint = Color(0xFFD1D5DB),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "अद्याप कोणताही आवडता मार्ग जोडलेला नाही",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HighContrastText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "बस कार्डवरील ⭐ चिन्हावर टॅप करून तुमचे नियमित मार्ग येथे सेव्ह करू शकता.",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(favorites, key = { it.id }) { fav ->
                    ElevatedCard(
                        onClick = {
                            viewModel.setFromStand(fav.fromStand)
                            viewModel.setToDestination(fav.toDestination)
                            onRouteSelected(fav.fromStand, fav.toDestination)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fav_item_${fav.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = WarmSurface),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = fav.fromStandMr.ifBlank { fav.fromStand },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaroonSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.East,
                                        contentDescription = "To",
                                        tint = SaffronPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = fav.toDestinationMr.ifBlank { fav.toDestination },
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "वेळापत्रक पाहण्यासाठी टॅप करा ➔",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            IconButton(
                                onClick = {
                                    viewModel.toggleFavorite(
                                        fav.fromStand,
                                        fav.fromStandMr,
                                        fav.toDestination,
                                        fav.toDestinationMr
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
