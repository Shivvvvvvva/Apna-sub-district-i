package com.example.data.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "timetable")
data class BusTimetable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    // 1. Source Bus Stand
    val depotOrBusStand: String,        // e.g. "Mahur", "Kinwat"
    val depotOrBusStandMr: String,      // e.g. "माहूर", "किनवट"
    val depotOrBusStandHi: String,      // e.g. "माहुर", "किनवट"
    // 2. Destination (Exact from PDF)
    val destination: String,            // e.g. "Nanded", "Adilabad"
    val destinationMr: String,          // e.g. "नांदेड", "आदिलाबाद"
    val destinationHi: String,          // e.g. "नांदेड़", "आदिलाबाद"
    // 3. Exact Departure Time
    val departureTime: String,          // "HH:mm" 24h format (e.g. "06:30", "18:00")
    val exactDepartureTime: String = "",// Exact text as in PDF (e.g. "6:30", "7:15")
    // 4. Exact Bus Route
    val busRoute: String,               // Exact English route text from PDF
    val busRouteMr: String,             // Marathi route text
    val busRouteHi: String,             // Hindi route text
    // 5. Source File
    val sourceFile: String = "Kinwat_Bus_Timetable.pdf",
    // 6. Source Page
    val sourcePage: Int = 1,
    val pdfRowNo: Int = 0,
    val busType: String = "साधी बस (Ordinary)",
    val platformNo: String = "",
    val isFavorite: Boolean = false,
    val isDataConflict: Boolean = false,
    val conflictDetails: String = "",
    val uncertaintyNote: String = ""    // "मूळ वेळापत्रक तपासणे आवश्यक आहे."
) {
    @Ignore
    private var _cachedMinutes: Int? = null

    // Converts "HH:mm" into minutes since midnight (Cached for instant comparisons)
    val departureMinutes: Int
        get() {
            val cached = _cachedMinutes
            if (cached != null) return cached
            val computed = try {
                val idx = departureTime.indexOf(':')
                if (idx > 0) {
                    val h = departureTime.substring(0, idx).trim().toInt()
                    val m = departureTime.substring(idx + 1).trim().toInt()
                    h * 60 + m
                } else 0
            } catch (e: Exception) {
                0
            }
            _cachedMinutes = computed
            return computed
        }

    @Ignore
    private var _searchableBlob: String? = null

    // Single pre-cached lowercase string blob for zero-allocation instant search
    val searchableText: String
        get() {
            val cached = _searchableBlob
            if (cached != null) return cached
            val extraSynonyms = buildString {
                if (busRouteMr.contains("सारखणी") || busRouteMr.contains("वाई") || busRouteMr.contains("अंजनखेड") ||
                    (depotOrBusStandMr.contains("किनवट") && destinationMr.contains("माहूर")) ||
                    (depotOrBusStandMr.contains("माहूर") && destinationMr.contains("किनवट"))
                ) {
                    append(" anjankhed अंजनखेड अंजनखेडा")
                }
                if (destinationMr.contains("औरंगाबाद") || destination.contains("Aurangabad", ignoreCase = true)) {
                    append(" sambhajinagar संभाजीनगर chhatrapati")
                }
            }
            val blob = "$depotOrBusStand $depotOrBusStandMr $depotOrBusStandHi $destination $destinationMr $destinationHi $departureTime $busRoute $busRouteMr $busRouteHi$extraSynonyms".lowercase()
            _searchableBlob = blob
            return blob
        }

    fun matchesFromStand(stand: String): Boolean {
        return depotOrBusStand.equals(stand, ignoreCase = true) ||
                depotOrBusStandMr.equals(stand, ignoreCase = true) ||
                depotOrBusStandHi.equals(stand, ignoreCase = true)
    }

    fun matchesToDestination(dest: String): Boolean {
        return destination.equals(dest, ignoreCase = true) ||
                destinationMr.equals(dest, ignoreCase = true) ||
                destinationHi.equals(dest, ignoreCase = true)
    }

    // Category: Morning (04:00-11:59), Afternoon (12:00-16:59), Evening (17:00-23:59), Night (00:00-03:59)
    val timeCategory: TimeCategory
        get() {
            val minutes = departureMinutes
            return when {
                minutes in 240..719 -> TimeCategory.MORNING
                minutes in 720..1019 -> TimeCategory.AFTERNOON
                minutes in 1020..1439 -> TimeCategory.EVENING
                else -> TimeCategory.NIGHT
            }
        }
}

enum class TimeCategory {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT
}

