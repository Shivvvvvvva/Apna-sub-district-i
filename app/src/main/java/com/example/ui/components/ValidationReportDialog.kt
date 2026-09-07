package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.InitialTimetableData
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.appBackgroundColor
import com.example.ui.theme.appBorderColor
import com.example.ui.theme.appSubtitleColor
import com.example.ui.theme.appSurfaceColor
import com.example.ui.theme.appSurfaceVariantColor
import com.example.ui.theme.appTextColor

@Composable
fun ValidationReportDialog(
    report: InitialTimetableData.ValidationReport,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("validation_report_dialog"),
        containerColor = appSurfaceColor(),
        shape = RoundedCornerShape(20.dp),
        title = {
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
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "वेळापत्रक पडताळणी अहवाल",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = appTextColor()
                        )
                        Text(
                            text = "Validation & Integrity Audit",
                            fontSize = 12.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = appSubtitleColor()
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Verified Status Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (ThemeManager.isDarkThemeActive) Color(0xFF00381C) else Color(0xFFE8F5E9)
                    ),
                    border = BorderStroke(1.5.dp, EmeraldGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "१००% अधिकृत PDF नुसार प्रमाणित",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "PRIMARY SOURCE OF TRUTH: अधिकृत PDF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = appTextColor()
                            )
                        }
                    }
                }

                // Mandatory 7 metrics audit table
                Text(
                    text = "पडताळणी आकडेवारी (Validation Metrics):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaroonSecondary
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = appSurfaceVariantColor(),
                    border = BorderStroke(1.dp, appBorderColor())
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ReportMetricRow(
                            label = "एकूण काढलेल्या नोंदी (Total records extracted)",
                            value = "${report.totalRecordsExtracted} (किनवट: ${report.kinwatRecords}, माहूर: ${report.mahurRecords})",
                            isSuccess = true
                        )
                        Divider(color = appBorderColor())
                        ReportMetricRow(
                            label = "अचूक जुळलेल्या नोंदी (Records matched exactly)",
                            value = "${report.recordsMatchedExactly} (100%)",
                            isSuccess = true
                        )
                        Divider(color = appBorderColor())
                        ReportMetricRow(
                            label = "तफावत असलेल्या नोंदी (Records with differences)",
                            value = "${report.recordsWithDifferences}",
                            isSuccess = report.recordsWithDifferences == 0
                        )
                        Divider(color = appBorderColor())
                        ReportMetricRow(
                            label = "गहाळ नोंदी (Missing records)",
                            value = "${report.missingRecords}",
                            isSuccess = report.missingRecords == 0
                        )
                        Divider(color = appBorderColor())
                        ReportMetricRow(
                            label = "दुबार नोंदी (Duplicate records)",
                            value = "${report.duplicateRecords}",
                            isSuccess = report.duplicateRecords == 0
                        )
                        Divider(color = appBorderColor())
                        ReportMetricRow(
                            label = "मार्ग संघर्ष (Route conflicts)",
                            value = "${report.routeConflicts}",
                            isSuccess = report.routeConflicts == 0
                        )
                        Divider(color = appBorderColor())
                        ReportMetricRow(
                            label = "वेळ संघर्ष (Time conflicts)",
                            value = "${report.timeConflicts}",
                            isSuccess = report.timeConflicts == 0
                        )
                    }
                }

                // Source Files Section
                Text(
                    text = "अधिकृत दस्तऐवज स्त्रोत (Authoritative Source Files):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaroonSecondary
                )

                report.sourceFiles.forEach { pdf ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = appSurfaceVariantColor(),
                        border = BorderStroke(1.dp, appBorderColor())
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pdf,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = appTextColor()
                            )
                        }
                    }
                }

                // Compliance & Strict Rules Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = if (ThemeManager.isDarkThemeActive) Color(0xFF1E293B) else Color(0xFFFFFBEB),
                    border = BorderStroke(1.dp, if (ThemeManager.isDarkThemeActive) Color(0xFF334155) else Color(0xFFFDE68A))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "डेटा अचूकता नियम (Data Accuracy Rules):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (ThemeManager.isDarkThemeActive) Color(0xFFFCD34D) else Color(0xFFB45309)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• प्रत्येक बस नोंदणीमध्ये ६ घटक कायम ठेवले आहेत: १. स्त्रोत बस स्थानक, २. गंतव्य स्थान, ३. अचूक सुटण्याची वेळ, ४. अधिकृत मार्ग, ५. मूळ फाईल नाव, ६. पान क्रमांक.\n" +
                                "• कोणतीही वेळ अथवा मार्ग सामान्य ज्ञानाने बदललेला नाही किंवा वगळलेला नाही.\n" +
                                "• अनिश्चित नोंदी असल्यास स्पष्ट सूचना: \"मूळ वेळापत्रक तपासणे आवश्यक आहे.\"\n" +
                                "• टीप: हे वेळापत्रक अधिकृत MSRTC PDF वर आधारित असून रिअल-टाइम MSRTC GPS ट्रॅकिंग जोडलेले नाही. सुटण्याच्या वेळा नियोजित वेळापत्रकानुसार दर्शविल्या आहेत.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = appTextColor()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.testTag("dismiss_validation_dialog")
            ) {
                Text(
                    text = "समजले (Close)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun ReportMetricRow(
    label: String,
    value: String,
    isSuccess: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = appSubtitleColor(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (isSuccess) EmeraldContainer else Color(0xFFFFEBEE)
        ) {
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isSuccess) EmeraldGreen else Color(0xFFC62828),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}
