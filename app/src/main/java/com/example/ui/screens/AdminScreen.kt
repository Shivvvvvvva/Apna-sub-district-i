package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BusTimetable
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.HighContrastText
import com.example.ui.theme.MaroonSecondary
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.WarmParchment
import com.example.ui.theme.WarmSurface
import com.example.ui.theme.appBackgroundColor
import com.example.ui.viewmodel.BusTimetableViewModel
import kotlinx.coroutines.launch

@Composable
fun AdminScreen(
    viewModel: BusTimetableViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allTimetables by viewModel.allTimetables.collectAsStateWithLifecycle()

    var isAuthenticated by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    var showAddDialog by remember { mutableStateOf(false) }
    var busToEdit by remember { mutableStateOf<BusTimetable?>(null) }
    var busToDelete by remember { mutableStateOf<BusTimetable?>(null) }

    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }

    var showExportDialog by remember { mutableStateOf(false) }
    var exportedData by remember { mutableStateOf("") }

    var showResetConfirm by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        // Admin Authentication screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(appBackgroundColor())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = MaroonSecondary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "व्यवस्थापक कक्ष (Admin Access)",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = HighContrastText
            )
            Text(
                text = "कृपया पुढे जाण्यासाठी व्यवस्थापक पासवर्ड प्रविष्ट करा.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = pinInput,
                onValueChange = {
                    pinInput = it
                    pinError = false
                },
                label = { Text("Admin Password") },
                visualTransformation = PasswordVisualTransformation(),
                isError = pinError,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_pin_input")
            )

            if (pinError) {
                Text(
                    text = "चुकीचा पासवर्ड. कृपया पुन्हा प्रयत्न करा.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (pinInput == "Shivam@##") {
                        isAuthenticated = true
                    } else {
                        pinError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("admin_login_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaroonSecondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("लॉगिन करा (Enter)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        // Authenticated Admin Dashboard
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(appBackgroundColor())
        ) {
            // Header
            Surface(
                color = Color(0xFF1E293B),
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚙️ व्यवस्थापक नियंत्रण कक्ष",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "एकूण नोंदवलेल्या बस: ${allTimetables.size}",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    TextButton(
                        onClick = {
                            isAuthenticated = false
                            pinInput = ""
                        }
                    ) {
                        Text("बाहेर पडा", color = Color(0xFFF87171), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Action Buttons Row: Add Bus, Import CSV/JSON, Export, Reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.weight(1f).testTag("admin_add_bus_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("नवीन बस", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showImportDialog = true },
                    modifier = Modifier.weight(1f).testTag("admin_import_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = "Import", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Import CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            exportedData = viewModel.exportCsv()
                            showExportDialog = true
                        }
                    },
                    modifier = Modifier.weight(1f).testTag("admin_export_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Export", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Reset to default button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier.testTag("admin_reset_btn")
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = "Reset", tint = MaroonSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("मूळ डेटा लोड करा (Reset to Default)", color = MaroonSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Timetable entries list for editing / deleting
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allTimetables, key = { it.id }) { bus ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = WarmSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${bus.depotOrBusStandMr} ➔ ${bus.destinationMr}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HighContrastText
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = SaffronPrimary,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = bus.departureTime,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "मार्ग: ${bus.busRouteMr.ifBlank { bus.busRoute }}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }

                            Row {
                                IconButton(onClick = { busToEdit = bus }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB))
                                }
                                IconButton(onClick = { busToDelete = bus }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Bus Dialog
    if (showAddDialog) {
        BusFormDialog(
            title = "नवीन बस वेळापत्रक जोडा",
            initialBus = null,
            onDismiss = { showAddDialog = false },
            onSave = { bus ->
                viewModel.addBus(bus)
                showAddDialog = false
            }
        )
    }

    // Edit Bus Dialog
    if (busToEdit != null) {
        BusFormDialog(
            title = "बस वेळापत्रक संपादित करा",
            initialBus = busToEdit,
            onDismiss = { busToEdit = null },
            onSave = { updated ->
                viewModel.updateBus(updated)
                busToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (busToDelete != null) {
        AlertDialog(
            onDismissRequest = { busToDelete = null },
            title = { Text("नोंद डिलीट करायची?") },
            text = { Text("तुम्हाला ${busToDelete?.destinationMr} (${busToDelete?.departureTime}) ची नोंद कायमस्वरूपी डिलीट करायची आहे का?") },
            confirmButton = {
                Button(
                    onClick = {
                        busToDelete?.let { viewModel.deleteBus(it) }
                        busToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("डिलीट करा")
                }
            },
            dismissButton = {
                TextButton(onClick = { busToDelete = null }) {
                    Text("रद्द करा")
                }
            }
        )
    }

    // Reset Confirm Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("मूळ डेटा लोड करायचा?") },
            text = { Text("यामुळे माहूर (४२ फेऱ्या) आणि किनवट (२३ फेऱ्या) चा मूळ वेळापत्रक डेटा पूर्ववत लोड केला जाईल.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDefault()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaroonSecondary)
                ) {
                    Text("होय, रीसेट करा")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("रद्द करा")
                }
            }
        )
    }

    // Import CSV / JSON Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("CSV किंवा JSON डेटा Import करा") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "उदा. फॉरमॅट:\nमाहूर, नांदेड, 06:00, हदगाव मार्गे\nकिंवा JSON array",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        label = { Text("डेटा पेस्ट करा") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importText.trim().startsWith("[")) {
                            viewModel.importJson(importText) { showImportDialog = false }
                        } else {
                            viewModel.importCsv(importText) { showImportDialog = false }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Import करा")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("रद्द करा")
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("वेळापत्रक डेटा Export (CSV)") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("खालील डेटा तुम्ही कॉपी करून सेव्ह करू शकता:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = exportedData,
                        onValueChange = { },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Timetable CSV", exportedData)
                        clipboard.setPrimaryClip(clip)
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("क्लिपबोर्डवर कॉपी करा")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("बंद करा")
                }
            }
        )
    }
}

@Composable
fun BusFormDialog(
    title: String,
    initialBus: BusTimetable?,
    onDismiss: () -> Unit,
    onSave: (BusTimetable) -> Unit
) {
    var standMr by remember { mutableStateOf(initialBus?.depotOrBusStandMr ?: "माहूर") }
    var standEn by remember { mutableStateOf(initialBus?.depotOrBusStand ?: "Mahur") }
    var destMr by remember { mutableStateOf(initialBus?.destinationMr ?: "") }
    var destEn by remember { mutableStateOf(initialBus?.destination ?: "") }
    var time by remember { mutableStateOf(initialBus?.departureTime ?: "07:00") }
    var routeMr by remember { mutableStateOf(initialBus?.busRouteMr ?: "") }
    var routeEn by remember { mutableStateOf(initialBus?.busRoute ?: "") }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = standMr,
                    onValueChange = {
                        standMr = it
                        if (standEn.isBlank()) standEn = it
                    },
                    label = { Text("सुरुवात बसस्थानक (मराठी)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destMr,
                    onValueChange = {
                        destMr = it
                        if (destEn.isBlank()) destEn = it
                    },
                    label = { Text("गंतव्य (Destination मराठी)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destEn,
                    onValueChange = { destEn = it },
                    label = { Text("Destination (English)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("सुटण्याची वेळ (उदा. 07:30)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = routeMr,
                    onValueChange = {
                        routeMr = it
                        if (routeEn.isBlank()) routeEn = it
                    },
                    label = { Text("मार्ग (मराठी)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalStandMr = standMr.trim().ifBlank { "माहूर" }
                    val finalStandEn = standEn.trim().ifBlank { "Mahur" }
                    val finalDestMr = destMr.trim().ifBlank { "नांदेड" }
                    val finalDestEn = destEn.trim().ifBlank { finalDestMr }
                    val finalTime = time.trim().ifBlank { "06:00" }
                    val finalRouteMr = routeMr.trim().ifBlank { "थेट मार्ग" }
                    val finalRouteEn = routeEn.trim().ifBlank { finalRouteMr }

                    val toSave = initialBus?.copy(
                        depotOrBusStand = finalStandEn,
                        depotOrBusStandMr = finalStandMr,
                        depotOrBusStandHi = finalStandMr,
                        destination = finalDestEn,
                        destinationMr = finalDestMr,
                        destinationHi = finalDestMr,
                        departureTime = finalTime,
                        busRoute = finalRouteEn,
                        busRouteMr = finalRouteMr,
                        busRouteHi = finalRouteMr
                    ) ?: BusTimetable(
                        depotOrBusStand = finalStandEn,
                        depotOrBusStandMr = finalStandMr,
                        depotOrBusStandHi = finalStandMr,
                        destination = finalDestEn,
                        destinationMr = finalDestMr,
                        destinationHi = finalDestMr,
                        departureTime = finalTime,
                        busRoute = finalRouteEn,
                        busRouteMr = finalRouteMr,
                        busRouteHi = finalRouteMr,
                        sourceFile = "Admin_Manual_Entry"
                    )

                    onSave(toSave)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Text("सेव्ह करा")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करा")
            }
        }
    )
}
