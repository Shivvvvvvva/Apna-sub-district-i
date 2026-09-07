package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonContainer
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.appBackgroundColor
import com.example.ui.viewmodel.BusTimetableViewModel

data class StandInfo(
    val nameMr: String,
    val nameEn: String,
    val taluka: String,
    val phone: String,
    val isAuthoritative: Boolean,
    val description: String
)

@Composable
fun BusStandsScreen(
    viewModel: BusTimetableViewModel,
    onViewBusesFromStand: (standName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val stands = listOf(
        StandInfo(
            nameMr = "श्री क्षेत्र माहूर बसस्थानक",
            nameEn = "Mahur Bus Stand",
            taluka = "माहूर तालुका",
            phone = "02460-268222",
            isAuthoritative = true,
            description = "रेणुका माता देवस्थान, ४२ अधिकृत थेट बस फेऱ्या उपलब्ध"
        ),
        StandInfo(
            nameMr = "किनवट बसस्थानक",
            nameEn = "Kinwat Bus Stand",
            taluka = "किनवट तालुका",
            phone = "02469-222222",
            isAuthoritative = true,
            description = "आदिवासी बहुल क्षेत्र, २३ अधिकृत थेट बस फेऱ्या उपलब्ध"
        ),
        StandInfo(
            nameMr = "नांदेड मुख्य मध्यवर्ती बसस्थानक",
            nameEn = "Nanded Central Bus Stand (CBS)",
            taluka = "नांदेड मुख्यालय",
            phone = "02462-234222",
            isAuthoritative = false,
            description = "जिल्हा मुख्य नियंत्रण कक्ष व सर्व प्रमुख शहरांकडे जाणाऱ्या बस"
        ),
        StandInfo(
            nameMr = "हदगाव बसस्थानक",
            nameEn = "Hadgaon Bus Stand",
            taluka = "हदगाव तालुका",
            phone = "02468-222333",
            isAuthoritative = false,
            description = "माहूर-नांदेड महामार्गावरील मुख्य जंक्शन"
        ),
        StandInfo(
            nameMr = "हिमायतनगर बसस्थानक",
            nameEn = "Himayatnagar Bus Stand",
            taluka = "हिमायतनगर तालुका",
            phone = "02468-242222",
            isAuthoritative = false,
            description = "परमेश्वर मंदिर, किनवट-नांदेड जोड रस्ता"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        // Top Banner
        Surface(
            color = MaroonSecondary,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "📍 बसस्थानक व संपर्क माहिती",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "नांदेड जिल्ह्यातील मुख्य एसटी बसस्थानके व चौकशी क्रमांक",
                    fontSize = 12.sp,
                    color = Color(0xFFFFD1D9)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // MSRTC Official Helpline Banner
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Helpline",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "एसटी महामंडळ टोल-फ्री हेल्पलाइन",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "1800 22 1250 (२४ तास विनामूल्य)",
                                    fontSize = 13.sp,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1800221250"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("कॉल करा", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            items(stands) { stand ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("stand_item_${stand.nameEn}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = WarmSurface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stand.nameMr,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HighContrastText
                                )
                                Text(
                                    text = "${stand.nameEn} • ${stand.taluka}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            if (stand.isAuthoritative) {
                                Surface(
                                    color = MaroonContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "वेळापत्रक उपलब्ध",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaroonSecondary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stand.description,
                            fontSize = 13.sp,
                            color = Color(0xFF4B5563)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Call enquiry button
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${stand.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaroonSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stand.phone, fontSize = 11.sp, color = MaroonSecondary, fontWeight = FontWeight.Bold)
                            }

                            // View buses button
                            Button(
                                onClick = {
                                    val searchKey = if (stand.nameEn.contains("Mahur")) "माहूर"
                                    else if (stand.nameEn.contains("Kinwat")) "किनवट"
                                    else stand.nameMr
                                    viewModel.setFromStand(searchKey)
                                    onViewBusesFromStand(searchKey)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "Bus",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("बस पहा", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
