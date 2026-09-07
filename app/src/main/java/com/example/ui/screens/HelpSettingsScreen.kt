package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.ThemeMode
import com.example.ui.theme.appBackgroundColor
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appSubtitleColor
import com.example.ui.theme.appSurfaceColor
import com.example.ui.theme.appSurfaceVariantColor
import com.example.ui.theme.appTextColor
import com.example.localization.AppLanguage
import com.example.localization.LanguageManager
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronContainer
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.WarmSurface

@Composable
fun HelpSettingsScreen(
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var tapCount by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(appBackgroundColor())
            .verticalScroll(scrollState)
    ) {
        // Header
        Surface(
            color = SaffronPrimary,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "मदत व संपर्क (Help & Contact)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "गाव बससेवा – नांदेड जिल्हा ग्रामीण वेळापत्रक",
                    fontSize = 12.sp,
                    color = Color(0xFFFFE0B2)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dark Mode / Theme Selection Card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_selection_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (ThemeManager.isDarkThemeActive) Color(0xFF374151) else Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (ThemeManager.isDarkThemeActive) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = "App Theme",
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "डार्क मोड (Dark Mode)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appTextColor()
                                )
                                Text(
                                    text = if (ThemeManager.isDarkThemeActive) "सध्या डार्क मोड चालू आहे" else "सध्या लाइट मोड चालू आहे",
                                    fontSize = 12.sp,
                                    color = appSubtitleColor()
                                )
                            }
                        }

                        Switch(
                            checked = ThemeManager.isDarkThemeActive,
                            onCheckedChange = { ThemeManager.toggleDarkMode(context) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SaffronPrimary
                            ),
                            modifier = Modifier.testTag("help_dark_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "थीम पर्याय (Theme Preference):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appSubtitleColor()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            val isSelected = ThemeManager.currentThemeMode == mode
                            OutlinedCard(
                                onClick = { ThemeManager.setThemeMode(context, mode) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_mode_${mode.name.lowercase()}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = if (isSelected) {
                                        if (ThemeManager.isDarkThemeActive) Color(0xFF862700) else SaffronContainer
                                    } else appSurfaceVariantColor()
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SaffronPrimary else appBorderColor()
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (mode) {
                                            ThemeMode.SYSTEM -> "📱 सिस्टम"
                                            ThemeMode.LIGHT -> "☀️ प्रकाश"
                                            ThemeMode.DARK -> "🌙 डार्क"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary
                                        } else appTextColor()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Language Selection Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = "Language", tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "भाषा निवडा (Select Language)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = appTextColor()
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            val isSelected = LanguageManager.currentLanguage == lang
                            OutlinedCard(
                                onClick = { LanguageManager.currentLanguage = lang },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = if (isSelected) {
                                        if (ThemeManager.isDarkThemeActive) Color(0xFF862700) else SaffronContainer
                                    } else appSurfaceVariantColor()
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) SaffronPrimary else appBorderColor()
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = lang.displayName,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (ThemeManager.isDarkThemeActive) Color(0xFFFFDBCF) else SaffronPrimary
                                        } else appTextColor()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Offline Support Info Card
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "१००% ऑफलाइन कार्यक्षम",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "या ॲपमधील सर्व ४२ माहूर व २३ किनवट बस फेऱ्या थेट तुमच्या फोनमध्ये सेव्ह आहेत. इंटरनेट नसतानाही अखंड चालते.",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // How to use Guide
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ॲप कसे वापरावे? (How to Use)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextColor()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val steps = listOf(
                        "१. मुख्य स्क्रीनवर 'पासून' (बसस्थानक) आणि 'पर्यंत' (गंतव्य ठिकाण) निवडा.",
                        "२. मार्गावरील गावे (अंजनखेड, सारखणी, वाई, मांडवी इ.) फिल्टर करण्यासाठी गावाच्या बटनावर टॅप करा.",
                        "३. सर्च बारमध्ये कोणतेही गाव किंवा थांबा टाईप करून थेट बस शोधा.",
                        "४. 'सकाळ', 'दुपार', 'संध्याकाळ' बटणावर क्लिक करून वेळानुसार बस पहा.",
                        "५. बस सुटायला किती वेळ बाकी आहे ते हिरव्या पट्टीमध्ये दिसते.",
                        "६. रात्रीच्या प्रवासासाठी 'मदत व संपर्क' मधील 'डार्क मोड' (Dark Mode) पर्याय सुरू करा."
                    )
                    steps.forEach { step ->
                        Text(
                            text = step,
                            fontSize = 13.sp,
                            color = appTextColor(),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }

            // Helpline & Bus Stand Contacts Support
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = appSurfaceColor())
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "बसस्थानक व चौकशी संपर्क क्रमांक",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appTextColor()
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val contacts = listOf(
                        Triple("महाराष्ट्र एसटी टोल फ्री", "1800 22 1250", "1800221250"),
                        Triple("माहूर बसस्थानक चौकशी", "02460-268244", "02460268244"),
                        Triple("किनवट बसस्थानक चौकशी", "02469-222030", "02469222030"),
                        Triple("नांदेड मध्यवर्ती बसस्थानक (CBS)", "02462-234289", "02462234289"),
                        Triple("हदगाव बसस्थानक चौकशी", "02468-222045", "02468222045"),
                        Triple("हिमायतनगर बसस्थानक चौकशी", "02468-242220", "02468242220")
                    )

                    contacts.forEachIndexed { index, (name, displayPhone, dialUri) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HighContrastText)
                                Text(displayPhone, color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$dialUri"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("कॉल", fontSize = 12.sp)
                            }
                        }
                        if (index < contacts.size - 1) {
                            androidx.compose.material3.HorizontalDivider(color = Color(0xFFF3F4F6))
                        }
                    }
                }
            }

            // Discreet / Hidden Admin Section Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier.testTag("admin_access_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("व्यवस्थापक लॉगिन (Admin Access)", fontSize = 12.sp)
                }
            }

            // Version info & secret multi-tap trigger
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable {
                        tapCount++
                        if (tapCount >= 3) {
                            tapCount = 0
                            onNavigateToAdmin()
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "गाव बससेवा आवृत्ती १.०.० (Nanded Rural)",
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF)
                )
                Text(
                    text = "शेतकरी, विद्यार्थी व ग्रामस्थांच्या सेवेसाठी",
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}
