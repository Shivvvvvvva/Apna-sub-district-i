package com.example.data.repository

import com.example.data.database.FavoriteDao
import com.example.data.database.TimetableDao
import com.example.data.datasource.InitialTimetableData
import com.example.data.model.BusTimetable
import com.example.data.model.FavoriteRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class TimetableRepository(
    private val timetableDao: TimetableDao,
    private val favoriteDao: FavoriteDao
) {
    val allTimetables: Flow<List<BusTimetable>> = timetableDao.getAllTimetables()
    val allFavorites: Flow<List<FavoriteRoute>> = favoriteDao.getAllFavorites()

    suspend fun initializeIfNeeded() = withContext(Dispatchers.IO) {
        val count = timetableDao.getCount()
        val hasAnjankhed = timetableDao.hasAnjankhedRoute()
        if (count == 167 && hasAnjankhed) {
            val sample = timetableDao.getSampleExactDepartureTime()
            if (!sample.isNullOrEmpty()) {
                // Database is already pre-populated and ready; fast zero-cost return
                return@withContext
            }
        }
        timetableDao.clearAll()
        timetableDao.insertAll(InitialTimetableData.getAllDefaultEntries())
    }

    suspend fun resetToDefault() = withContext(Dispatchers.IO) {
        timetableDao.clearAll()
        timetableDao.insertAll(InitialTimetableData.getAllDefaultEntries())
    }

    suspend fun addTimetable(entry: BusTimetable) = withContext(Dispatchers.IO) {
        timetableDao.insert(entry)
    }

    suspend fun updateTimetable(entry: BusTimetable) = withContext(Dispatchers.IO) {
        timetableDao.update(entry)
    }

    suspend fun deleteTimetable(entry: BusTimetable) = withContext(Dispatchers.IO) {
        timetableDao.delete(entry)
    }

    suspend fun toggleFavorite(fromStand: String, fromStandMr: String, toDest: String, toDestMr: String) = withContext(Dispatchers.IO) {
        val exists = favoriteDao.isFavorite(fromStand, toDest)
        if (exists) {
            favoriteDao.deleteByRoute(fromStand, toDest)
        } else {
            favoriteDao.insertFavorite(
                FavoriteRoute(
                    fromStand = fromStand,
                    fromStandMr = fromStandMr,
                    toDestination = toDest,
                    toDestinationMr = toDestMr
                )
            )
        }
    }

    suspend fun isFavorite(fromStand: String, toDest: String): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.isFavorite(fromStand, toDest)
    }

    // CSV Import: lines format: depot, destination, departure_time, route
    suspend fun importFromCsv(csvText: String): Int = withContext(Dispatchers.IO) {
        val lines = csvText.lines()
        val imported = mutableListOf<BusTimetable>()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("depot", ignoreCase = true)) {
                continue
            }
            val parts = trimmed.split(",").map { it.trim().removeSurrounding("\"") }
            if (parts.size >= 3) {
                val depot = parts[0]
                val dest = parts[1]
                val time = parts[2]
                val route = if (parts.size >= 4) parts.subList(3, parts.size).joinToString(", ") else "थेट मार्ग"
                
                imported.add(
                    BusTimetable(
                        depotOrBusStand = depot,
                        depotOrBusStandMr = depot,
                        depotOrBusStandHi = depot,
                        destination = dest,
                        destinationMr = dest,
                        destinationHi = dest,
                        departureTime = time,
                        busRoute = route,
                        busRouteMr = route,
                        busRouteHi = route,
                        sourceFile = "User_CSV_Import"
                    )
                )
            }
        }
        if (imported.isNotEmpty()) {
            timetableDao.insertAll(imported)
        }
        imported.size
    }

    // JSON Import
    suspend fun importFromJson(jsonText: String): Int = withContext(Dispatchers.IO) {
        val list = mutableListOf<BusTimetable>()
        try {
            val array = JSONArray(jsonText.trim())
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val depot = obj.optString("depot_or_bus_stand", obj.optString("location", "Mahur"))
                val dest = obj.optString("destination", "")
                val route = obj.optString("bus_route", obj.optString("route", "थेट मार्ग"))
                val source = obj.optString("source_file", "Kinwat_Bus_Timetable.pdf")
                val page = obj.optInt("source_page", 1)
                val rowNo = obj.optInt("pdf_row_no", 0)

                if (obj.has("departure_time")) {
                    val rawTime = obj.getString("departure_time")
                    val normalized = InitialTimetableData.normalizeTime(rawTime)
                    
                    // Check against authoritative PDF data
                    val authoritativeMatch = InitialTimetableData.getAllDefaultEntries().firstOrNull {
                        it.depotOrBusStand.equals(depot, ignoreCase = true) &&
                        it.destination.equals(dest, ignoreCase = true) &&
                        it.departureTime == normalized
                    }
                    
                    val isConflict = authoritativeMatch != null && authoritativeMatch.busRoute != route
                    val conflictDetails = if (isConflict) "DATA CONFLICT: PDF route is '${authoritativeMatch?.busRoute}' but imported route is '$route'" else ""

                    list.add(
                        BusTimetable(
                            depotOrBusStand = if (isConflict) authoritativeMatch!!.depotOrBusStand else depot,
                            depotOrBusStandMr = depot,
                            depotOrBusStandHi = depot,
                            destination = if (isConflict) authoritativeMatch!!.destination else dest,
                            destinationMr = dest,
                            destinationHi = dest,
                            departureTime = normalized,
                            exactDepartureTime = rawTime,
                            busRoute = if (isConflict) authoritativeMatch!!.busRoute else route,
                            busRouteMr = route,
                            busRouteHi = route,
                            sourceFile = if (isConflict) authoritativeMatch!!.sourceFile else source,
                            sourcePage = if (isConflict) authoritativeMatch!!.sourcePage else page,
                            pdfRowNo = if (isConflict) authoritativeMatch!!.pdfRowNo else rowNo,
                            isDataConflict = isConflict,
                            conflictDetails = conflictDetails,
                            uncertaintyNote = if (isConflict) "मूळ वेळापत्रक तपासणे आवश्यक आहे." else ""
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (list.isNotEmpty()) {
            timetableDao.insertAll(list)
        }
        list.size
    }

    suspend fun exportToJson(timetables: List<BusTimetable>): String = withContext(Dispatchers.Default) {
        val array = JSONArray()
        for (item in timetables) {
            val obj = JSONObject()
            obj.put("source_bus_stand", item.depotOrBusStand)
            obj.put("destination", item.destination)
            obj.put("exact_departure_time", item.exactDepartureTime)
            obj.put("departure_time_24h", item.departureTime)
            obj.put("exact_bus_route", item.busRoute)
            obj.put("source_file", item.sourceFile)
            obj.put("source_page", item.sourcePage)
            obj.put("pdf_row_no", item.pdfRowNo)
            obj.put("is_data_conflict", item.isDataConflict)
            obj.put("uncertainty_note", item.uncertaintyNote)
            array.put(obj)
        }
        array.toString(2)
    }

    suspend fun exportToCsv(timetables: List<BusTimetable>): String = withContext(Dispatchers.Default) {
        val sb = StringBuilder()
        sb.append("Source Bus Stand,Destination,Exact Departure Time,Departure Time 24h,Exact Bus Route,Source File,Source Page,PDF Row No,Conflict Status,Uncertainty Note\n")
        for (item in timetables) {
            sb.append("\"${item.depotOrBusStand}\",\"${item.destination}\",\"${item.exactDepartureTime}\",\"${item.departureTime}\",\"${item.busRoute}\",\"${item.sourceFile}\",${item.sourcePage},${item.pdfRowNo},\"${if (item.isDataConflict) "DATA CONFLICT" else "VERIFIED"}\",\"${item.uncertaintyNote}\"\n")
        }
        sb.toString()
    }
}
