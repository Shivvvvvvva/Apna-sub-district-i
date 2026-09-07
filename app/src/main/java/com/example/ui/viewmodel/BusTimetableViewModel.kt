package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.datasource.InitialTimetableData
import com.example.data.model.BusTimetable
import com.example.data.model.FavoriteRoute
import com.example.data.model.TimeCategory
import com.example.data.repository.TimetableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

enum class SortOrder {
    EARLIEST_FIRST,
    LATEST_FIRST
}

enum class LiveBusState {
    DEPARTING_NOW, // 0..5 mins
    IMMINENT,      // 6..30 mins
    UPCOMING,      // > 30 mins (today)
    DEPARTED       // < 0 mins
}

data class BusLiveStatus(
    val state: LiveBusState,
    val diffMinutes: Int,
    val badgeTextMr: String,
    val badgeTextEn: String
)

data class TimetableSummaryStats(
    val nextBus: BusTimetable?,
    val remainingMinutes: Int,
    val upcomingCount: Int,
    val departedCount: Int,
    val tomorrowFirstBus: BusTimetable?
)

class BusTimetableViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = TimetableRepository(database.timetableDao(), database.favoriteDao())

    val allTimetables: StateFlow<List<BusTimetable>> = repository.allTimetables
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFavorites: StateFlow<List<FavoriteRoute>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pre-indexed set of "from_to" favorite pairs for O(1) instantaneous lookup
    val favoriteKeys: StateFlow<Set<String>> = repository.allFavorites
        .map { favs -> favs.map { "${it.fromStand}_${it.toDestination}" }.toSet() }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // LIVE REAL-TIME TICKER (Emits every second for live countdowns & clock)
    private val _currentDeviceTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentDeviceTimeMillis: StateFlow<Long> = _currentDeviceTimeMillis.asStateFlow()

    private val _currentMinutesFromMidnight = MutableStateFlow(calculateMinutesFromMidnight())
    val currentMinutesFromMidnight: StateFlow<Int> = _currentMinutesFromMidnight.asStateFlow()

    // Search & Filter state
    private val _selectedFromStand = MutableStateFlow("")
    val selectedFromStand: StateFlow<String> = _selectedFromStand.asStateFlow()

    private val _selectedToDestination = MutableStateFlow("")
    val selectedToDestination: StateFlow<String> = _selectedToDestination.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedVillageFilter = MutableStateFlow("")
    val selectedVillageFilter: StateFlow<String> = _selectedVillageFilter.asStateFlow()

    // "UPCOMING" = Only from current live time onwards, "ALL" = Full day, "MORNING", "AFTERNOON", "EVENING"
    private val _activeTimeFilter = MutableStateFlow("ALL")
    val activeTimeFilter: StateFlow<String> = _activeTimeFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.EARLIEST_FIRST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // Toggle whether departed buses should be hidden or visible
    private val _hideDepartedBuses = MutableStateFlow(false)
    val hideDepartedBuses: StateFlow<Boolean> = _hideDepartedBuses.asStateFlow()

    // Status / Toast message for user feedback
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeIfNeeded()
        }

        // Live coroutine ticker that automatically updates current time on background dispatcher
        // Only updates _currentMinutesFromMidnight when the minute changes to avoid re-filtering every second
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val now = System.currentTimeMillis()
                _currentDeviceTimeMillis.value = now
                val mins = calculateMinutesFromMidnight()
                if (_currentMinutesFromMidnight.value != mins) {
                    _currentMinutesFromMidnight.value = mins
                }
                delay(1000L)
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun setFromStand(stand: String) {
        _selectedFromStand.value = stand
    }

    fun setToDestination(dest: String) {
        _selectedToDestination.value = dest
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && _selectedToDestination.value.isNotBlank()) {
            _selectedToDestination.value = ""
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedToDestination.value = ""
        _selectedVillageFilter.value = ""
    }

    fun selectDestination(dest: String) {
        if (dest == "सर्व" || dest == "All" || dest.isBlank()) {
            _searchQuery.value = ""
            _selectedToDestination.value = ""
            _selectedVillageFilter.value = ""
        } else {
            _searchQuery.value = dest
            _selectedToDestination.value = ""
        }
    }

    fun setVillageFilter(village: String) {
        _selectedVillageFilter.value = if (village == "सर्व" || village == "All" || village.isBlank()) "" else village
    }

    fun setTimeFilter(filter: String) {
        _activeTimeFilter.value = filter
    }

    fun toggleHideDepartedBuses() {
        _hideDepartedBuses.value = !_hideDepartedBuses.value
        _toastMessage.value = if (_hideDepartedBuses.value) "सुटलेल्या गाड्या लपवल्या आहेत" else "सर्व गाड्या दाखवत आहे"
    }

    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.EARLIEST_FIRST) {
            SortOrder.LATEST_FIRST
        } else {
            SortOrder.EARLIEST_FIRST
        }
    }

    fun swapFromAndTo() {
        val currentFrom = _selectedFromStand.value
        val currentTo = _selectedToDestination.value
        _selectedFromStand.value = currentTo
        _selectedToDestination.value = currentFrom
    }

    private data class FilterParams(
        val fromStand: String,
        val toDest: String,
        val query: String,
        val villageFilter: String,
        val timeFilter: String,
        val sort: SortOrder,
        val hideDeparted: Boolean,
        val currentMinutes: Int
    )

    private val filterParams = combine(
        combine(_selectedFromStand, _selectedToDestination) { from, to -> Pair(from, to) },
        combine(_searchQuery, _selectedVillageFilter) { q, vf -> Pair(q, vf) },
        combine(_activeTimeFilter, _sortOrder) { tf, sort -> Pair(tf, sort) },
        combine(_hideDepartedBuses, _currentMinutesFromMidnight) { hd, curM -> Pair(hd, curM) }
    ) { (from, to), (q, vf), (tf, sort), (hd, curM) ->
        FilterParams(from, to, q, vf, tf, sort, hd, curM)
    }

    // Filtered bus list based on stand, destination, search query, village stop, time filter, and sort order
    // Executed entirely on background dispatcher (Dispatchers.Default) to ensure UI thread never stutters
    val filteredBuses: StateFlow<List<BusTimetable>> = combine(
        allTimetables,
        filterParams
    ) { buses, params ->
        val hasFrom = params.fromStand.isNotBlank() && params.fromStand != "सर्व" && params.fromStand != "All"
        val fromStand = params.fromStand
        val hasTo = params.toDest.isNotBlank() && params.toDest != "सर्व" && params.toDest != "All"
        val toDest = params.toDest
        val hasVillage = params.villageFilter.isNotBlank()
        val vf = if (hasVillage) params.villageFilter.trim().lowercase() else ""
        val hasQuery = params.query.isNotBlank()
        val q = if (hasQuery) params.query.trim().lowercase() else ""
        val curM = params.currentMinutes
        val timeFilter = params.timeFilter
        val hideDeparted = params.hideDeparted

        val filtered = buses.filter { bus ->
            // If user is actively searching in "Where to go / search bar"
            if (hasQuery) {
                if (!matchesSearch(bus, q)) return@filter false
            } else {
                // Bus Stand filter strictly applies when NOT searching
                if (hasFrom && !bus.matchesFromStand(fromStand)) return@filter false
            }

            if (hasTo && !bus.matchesToDestination(toDest)) return@filter false
            if (hasVillage && !matchesVillage(bus, vf)) return@filter false

            when (timeFilter) {
                "UPCOMING" -> if (bus.departureMinutes < curM) return@filter false
                "MORNING" -> if (bus.timeCategory != TimeCategory.MORNING) return@filter false
                "AFTERNOON" -> if (bus.timeCategory != TimeCategory.AFTERNOON) return@filter false
                "EVENING" -> if (bus.timeCategory != TimeCategory.EVENING) return@filter false
                "NIGHT" -> if (bus.timeCategory != TimeCategory.NIGHT) return@filter false
            }

            if (hideDeparted && timeFilter != "UPCOMING" && bus.departureMinutes < curM) {
                return@filter false
            }

            true
        }

        // If user searched across all buses with a specific fromStand selected,
        // prioritize buses starting from the selected stand first, followed by other matching buses,
        // then sorted by departure time!
        if (hasQuery && hasFrom) {
            filtered.sortedWith(
                compareBy<BusTimetable> { bus ->
                    if (bus.matchesFromStand(fromStand)) 0 else 1
                }.thenBy { bus ->
                    if (timeFilter == "UPCOMING" || params.sort == SortOrder.EARLIEST_FIRST) {
                        bus.departureMinutes
                    } else {
                        -bus.departureMinutes
                    }
                }
            )
        } else if (timeFilter == "UPCOMING" || params.sort == SortOrder.EARLIEST_FIRST) {
            filtered.sortedBy { it.departureMinutes }
        } else {
            filtered.sortedByDescending { it.departureMinutes }
        }
    }.flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Checks if a bus serves or passes through a specific village
    fun matchesVillage(bus: BusTimetable, village: String): Boolean {
        val v = village.trim().lowercase()
        if (bus.searchableText.contains(v)) {
            return true
        }
        return matchesSearch(bus, v)
    }

    // Dynamic autocomplete suggestion list for the search bar
    fun getSearchSuggestions(query: String): List<String> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return emptyList()
        val allBuses = allTimetables.value
        val places = linkedSetOf<String>()
        // Top places
        places.add("नांदेड")
        places.add("किनवट")
        places.add("माहूर")
        places.add("पुसद")
        places.add("यवतमाळ")
        places.add("अमरावती")
        places.add("नागपूर")
        places.add("आदिलाबाद")
        places.add("अंजनखेड")
        places.add("सारखणी")
        places.add("वाई")
        places.add("बोधडी")
        places.add("मांडवी")
        places.add("इस्लापूर")
        places.add("पुणे")
        places.add("लातूर")
        places.add("अकोला")
        places.add("हिंगोली")
        places.add("वाशीम")
        places.add("चंद्रपूर")

        for (b in allBuses) {
            places.add(b.destinationMr)
            places.add(b.depotOrBusStandMr)
            b.busRouteMr.split(",", " ").forEach { stop ->
                val s = stop.trim()
                if (s.length >= 3) places.add(s)
            }
        }

        val normQ = normalizeDevanagari(q)
        val normEngQ = normalizeEnglish(q)

        return places.filter { place ->
            val cleanP = place.lowercase()
            cleanP.contains(q) ||
            normalizeDevanagari(cleanP).contains(normQ) ||
            TRANSLITERATION_ENTRIES.any { (eng, equivalents) ->
                (eng.contains(normEngQ) || normEngQ.contains(eng) || equivalents.any { it.contains(q) || q.contains(it) }) &&
                (equivalents.any { cleanP.contains(it) || it.contains(cleanP) } || cleanP.contains(eng))
            }
        }.take(8)
    }

    // Multi-lingual search matching (Marathi, Hindi, English transliteration, phonetic normalization & route stops)
    fun matchesSearch(bus: BusTimetable, q: String): Boolean {
        val rawQ = q.trim().lowercase()
        if (rawQ.isEmpty()) return true

        // 1. Direct containment check in pre-cached lowercase text (covers standard searches instantly)
        if (bus.searchableText.contains(rawQ)) return true

        // Split into tokens for multi-word queries (e.g. "किनवट नांदेड" or "mahur pusad")
        val cleanTokens = rawQ.split(" ", "-", ",", "/", ";")
            .map { it.trim() }
            .filter { token ->
                token.isNotEmpty() && token !in listOf("to", "bus", "st", "एसटी", "बस", "गाडी", "साठी", "कडे", "वरून", "का", "की")
            }

        if (cleanTokens.isEmpty()) return true

        // Every token must match somewhere in the bus record
        return cleanTokens.all { token ->
            matchesSingleToken(bus, token)
        }
    }

    private fun matchesSingleToken(bus: BusTimetable, token: String): Boolean {
        // Direct containment
        if (bus.searchableText.contains(token)) return true

        // Special case for Anjankhed / Anjankheda
        if (token.contains("anjankhed") || token.contains("अंजनखेड") || token.contains("अंजनखेडा") || token.contains("अजनखेड")) {
            if (bus.busRouteMr.contains("अंजनखेड") || bus.busRouteMr.contains("सारखणी") || bus.busRouteMr.contains("वाई") ||
                (bus.depotOrBusStandMr.contains("किनवट") && bus.destinationMr.contains("माहूर")) ||
                (bus.depotOrBusStandMr.contains("माहूर") && bus.destinationMr.contains("किनवट"))
            ) {
                return true
            }
        }

        // Transliteration check using comprehensive lookup list
        for (i in TRANSLITERATION_ENTRIES.indices) {
            val (eng, equivalents) = TRANSLITERATION_ENTRIES[i]
            val matchesKey = token == eng || (token.length >= 3 && (eng.contains(token) || token.contains(eng)))
            val matchesEq = !matchesKey && equivalents.any { eq ->
                token == eq || (token.length >= 3 && (eq.contains(token) || token.contains(eq)))
            }

            if (matchesKey || matchesEq) {
                if (bus.searchableText.contains(eng) || equivalents.any { bus.searchableText.contains(it) }) {
                    return true
                }
            }
        }

        // Devanagari normalized check (ignores anusvara, short/long vowel differences, etc.)
        val normToken = normalizeDevanagari(token)
        if (normToken.length >= 2) {
            val normBlob = normalizeDevanagari(bus.searchableText)
            if (normBlob.contains(normToken)) return true
        }

        // English normalized check (handles phonetics like w/v, ee/i, oo/u)
        val normEngToken = normalizeEnglish(token)
        if (normEngToken.length >= 3) {
            val normEngBlob = normalizeEnglish(bus.searchableText)
            if (normEngBlob.contains(normEngToken)) return true
        }

        return false
    }

    companion object {
        fun normalizeDevanagari(text: String): String {
            return text
                .replace("ं", "")
                .replace("ँ", "")
                .replace("़", "")
                .replace("ी", "ि")
                .replace("ू", "ु")
                .replace("ै", "े")
                .replace("ौ", "ो")
                .replace("ळ", "ल")
                .replace("ण", "न")
                .replace("श", "स")
                .replace("ष", "स")
                .replace("ड़", "ड")
                .replace("ढ़", "ढ")
                .replace(" ", "")
        }

        fun normalizeEnglish(text: String): String {
            return text
                .replace("ee", "i")
                .replace("oo", "u")
                .replace("ou", "u")
                .replace("aa", "a")
                .replace("w", "v")
                .replace("sh", "s")
                .replace(" ", "")
        }

        val TRANSLITERATION_ENTRIES: List<Pair<String, List<String>>> = listOf(
            "anjankhed" to listOf("अंजनखेड", "अंजनखेडा", "अजनखेड"),
            "sarkhani" to listOf("सारखणी", "सारखनी"),
            "rajgad" to listOf("राजगड", "राजगढ"),
            "wai" to listOf("वाई"),
            "mandvi" to listOf("मांडवी", "मांदवी"),
            "bodhadi" to listOf("बोधडी", "बोधड़ी"),
            "islapur" to listOf("इस्लापूर", "इस्लामपूर", "इस्लापुर"),
            "shivani" to listOf("शिवणी", "शिवनी"),
            "unkeshwar" to listOf("उनकेश्वर"),
            "pimpalgaon" to listOf("पिंपळगाव", "पिंपलगांव", "पिंपळगांव"),
            "chikhali" to listOf("चिखली"),
            "sonala" to listOf("सोनाळा", "सोनाला"),
            "both" to listOf("बोथ"),
            "patoda" to listOf("पाटोदा"),
            "parwa" to listOf("पारवा"),
            "kelapur" to listOf("केळापूर", "केळापुर"),
            "pandharkawda" to listOf("पांढरकवडा", "पांढरकवड़ा"),
            "ghatanji" to listOf("घाटंजी"),
            "patapangra" to listOf("पाटपंगरा"),
            "nanded" to listOf("नांदेड", "नांदेड़", "नादेड"),
            "mahur" to listOf("माहूर", "माहुर"),
            "kinwat" to listOf("किनवट"),
            "adilabad" to listOf("आदिलाबाद"),
            "hadgaon" to listOf("हदगाव", "हदगांव"),
            "pusad" to listOf("पुसद"),
            "pune" to listOf("पुणे"),
            "latur" to listOf("लातूर", "लातुर"),
            "yavatmal" to listOf("यवतमाळ", "यवतमाल"),
            "nagpur" to listOf("नागपूर", "नागपुर"),
            "himayatnagar" to listOf("हिमायतनगर"),
            "akola" to listOf("अकोला"),
            "amravati" to listOf("अमरावती"),
            "mumbai" to listOf("मुंबई"),
            "bodhan" to listOf("बोधन"),
            "bhokar" to listOf("भोकर"),
            "dhanoda" to listOf("धानोडा"),
            "wanola" to listOf("वनोळा", "वनोला"),
            "vanola" to listOf("वनोला", "वनोळा"),
            "vazara" to listOf("वाझरा"),
            "dattamanjari" to listOf("दत्तमंजरी", "दत्तमांजरी"),
            "aurangabad" to listOf("औरंगाबाद", "संभाजीनगर", "छत्रपती संभाजीनगर"),
            "chandrapur" to listOf("चंद्रपूर", "चंद्रपुर"),
            "wardha" to listOf("वर्धा"),
            "hingoli" to listOf("हिंगोली"),
            "washim" to listOf("वाशीम", "वाशिम"),
            "shegaon" to listOf("शेगाव", "शेगांव"),
            "umarkhed" to listOf("उमरखेड"),
            "kalamnuri" to listOf("कळमनुरी", "कलमनुरी"),
            "jalna" to listOf("जालना"),
            "parbhani" to listOf("परभणी", "परभनी"),
            "jintoor" to listOf("जिंतूर", "जिंतुर"),
            "majalgaon" to listOf("माजलगाव", "माजलगांव"),
            "phulsangvi" to listOf("फुलसांगवी"),
            "apparao" to listOf("अप्पाराव पेठ", "अप्पाराव"),
            "beed" to listOf("बीड"),
            "aheri" to listOf("अहेरी"),
            "umred" to listOf("उमरेड"),
            "arni" to listOf("आर्णी", "आर्नी"),
            "ramtek" to listOf("रामटेक"),
            "borgaon" to listOf("बोरगाव", "बोरगांव"),
            "sindagi" to listOf("सिंदगी"),
            "mohpur" to listOf("मोहपूर", "मोहपुर"),
            "temburdhara" to listOf("टेंभूर्धरा", "टेंभुर्धरा"),
            "gadibori" to listOf("गडीबोरी", "गदीबोरी"),
            "bela" to listOf("बेला"),
            "gadchandur" to listOf("गडचांदूर", "गडचांदुर"),
            "rajura" to listOf("राजूरा", "राजुरा"),
            "echoda" to listOf("इचोडा", "एचोडा"),
            "gudihatnur" to listOf("गुडीहातनूर", "गुडीहातनुर"),
            "ner" to listOf("नेर"),
            "hinganghat" to listOf("हिंगणघाट", "हिंगनघाट"),
            "ahmedpur" to listOf("अहमदपूर", "अहमदपुर"),
            "manwat" to listOf("मानवत"),
            "selu" to listOf("सेलू"),
            "buldhana" to listOf("बुलढाणा"),
            "bhusawal" to listOf("भुसावळ", "भुसावल"),
            "mahagaon" to listOf("महागाव", "महागांव"),
            "kalamb" to listOf("कळंब", "कलंब"),
            "wani" to listOf("वणी")
        )
    }

    // Current device time in minutes since midnight
    fun calculateMinutesFromMidnight(): Int {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return hour * 60 + minute
    }

    fun getCurrentDeviceMinutes(): Int = calculateMinutesFromMidnight()

    // Formatted live ticking time string (e.g. 08:35:12 PM)
    fun getFormattedLiveTime(timestampMillis: Long = _currentDeviceTimeMillis.value): String {
        val sdf = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }

    // Live time period in Marathi (सकाळ, दुपार, संध्याकाळ, रात्र)
    fun getLivePeriodMr(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "🌅 सकाळ"
            in 12..16 -> "☀️ दुपार"
            in 17..20 -> "🌇 संध्याकाळ"
            else -> "🌙 रात्र"
        }
    }

    // Computes schedule-based status relative to current device clock (Note: offline scheduled timetable, no live GPS)
    fun getBusLiveStatus(bus: BusTimetable, currentMinutes: Int = _currentMinutesFromMidnight.value): BusLiveStatus {
        val diff = bus.departureMinutes - currentMinutes
        return when {
            diff in 0..5 -> BusLiveStatus(
                state = LiveBusState.DEPARTING_NOW,
                diffMinutes = diff,
                badgeTextMr = if (diff == 0) "⏰ नियोजित वेळ झाली (स्थानकावर वेळ)" else "⏰ नियोजित वेळ: $diff मिनिटांत",
                badgeTextEn = if (diff == 0) "Scheduled time now" else "Scheduled in $diff mins"
            )
            diff in 6..30 -> BusLiveStatus(
                state = LiveBusState.IMMINENT,
                diffMinutes = diff,
                badgeTextMr = "⏳ नियोजित: $diff मिनिटांत",
                badgeTextEn = "Scheduled in $diff mins"
            )
            diff > 30 -> {
                val hrs = diff / 60
                val mins = diff % 60
                val textMr = if (hrs > 0 && mins > 0) "$hrs तास $mins मिनिटांनी" else if (hrs > 0) "$hrs तासांनी" else "$mins मिनिटांनी"
                val textEn = if (hrs > 0 && mins > 0) "In ${hrs}h ${mins}m" else if (hrs > 0) "In ${hrs}h" else "In ${mins}m"
                BusLiveStatus(
                    state = LiveBusState.UPCOMING,
                    diffMinutes = diff,
                    badgeTextMr = "🕒 नियोजित: $textMr",
                    badgeTextEn = "Scheduled: $textEn"
                )
            }
            else -> {
                val ago = -diff
                val hrsAgo = ago / 60
                val minsAgo = ago % 60
                val textMr = if (hrsAgo > 0 && minsAgo > 0) "$hrsAgo तास $minsAgo मि. पूर्वी" else if (hrsAgo > 0) "$hrsAgo तासांपूर्वी" else "$minsAgo मिनिटांपूर्वी"
                val textEn = if (hrsAgo > 0 && minsAgo > 0) "${hrsAgo}h ${minsAgo}m ago" else if (hrsAgo > 0) "${hrsAgo}h ago" else "${minsAgo}m ago"
                BusLiveStatus(
                    state = LiveBusState.DEPARTED,
                    diffMinutes = diff,
                    badgeTextMr = "🏁 नियोजित वेळ झाली ($textMr)",
                    badgeTextEn = "Scheduled time passed ($textEn)"
                )
            }
        }
    }

    fun getValidationReport(): InitialTimetableData.ValidationReport {
        return InitialTimetableData.generateValidationReport()
    }

    // Single-pass statistical summary with 0 redundant loops
    fun getSummaryStats(
        fromStand: String? = null,
        toDest: String? = null,
        currentMinutes: Int = _currentMinutesFromMidnight.value
    ): TimetableSummaryStats {
        val buses = allTimetables.value
        val hasFrom = !fromStand.isNullOrBlank() && fromStand != "सर्व" && fromStand != "All"
        val hasTo = !toDest.isNullOrBlank() && toDest != "सर्व" && toDest != "All"

        var nextBus: BusTimetable? = null
        var minDiff = Int.MAX_VALUE
        var upcoming = 0
        var departed = 0
        var tomorrowFirstBus: BusTimetable? = null
        var minDayMinutes = Int.MAX_VALUE

        for (i in buses.indices) {
            val bus = buses[i]
            if (hasFrom && !bus.matchesFromStand(fromStand!!)) continue
            if (hasTo && !bus.matchesToDestination(toDest!!)) continue

            val dep = bus.departureMinutes
            if (dep < minDayMinutes) {
                minDayMinutes = dep
                tomorrowFirstBus = bus
            }

            if (dep >= currentMinutes) {
                upcoming++
                val diff = dep - currentMinutes
                if (diff < minDiff) {
                    minDiff = diff
                    nextBus = bus
                }
            } else {
                departed++
            }
        }

        val remainingMinutes = if (nextBus != null) minDiff else 0

        return TimetableSummaryStats(
            nextBus = nextBus,
            remainingMinutes = remainingMinutes,
            upcomingCount = upcoming,
            departedCount = departed,
            tomorrowFirstBus = tomorrowFirstBus
        )
    }

    // Number of buses remaining to depart today from current time
    fun getUpcomingBusesCount(fromStand: String? = null, toDest: String? = null, currentMinutes: Int = _currentMinutesFromMidnight.value): Int {
        return getSummaryStats(fromStand, toDest, currentMinutes).upcomingCount
    }

    // Number of buses already departed today
    fun getDepartedBusesCount(fromStand: String? = null, toDest: String? = null, currentMinutes: Int = _currentMinutesFromMidnight.value): Int {
        return getSummaryStats(fromStand, toDest, currentMinutes).departedCount
    }

    // Calculate remaining minutes from current device time
    fun getMinutesRemaining(departureTime: String, currentMinutes: Int = _currentMinutesFromMidnight.value): Int {
        val busMinutes = try {
            val idx = departureTime.indexOf(':')
            if (idx > 0) {
                val h = departureTime.substring(0, idx).trim().toInt()
                val m = departureTime.substring(idx + 1).trim().toInt()
                h * 60 + m
            } else 0
        } catch (e: Exception) {
            0
        }
        return busMinutes - currentMinutes
    }

    // Next upcoming bus from current local device time
    fun getNextUpcomingBus(fromStand: String? = null, toDest: String? = null, currentMinutes: Int = _currentMinutesFromMidnight.value): BusTimetable? {
        return getSummaryStats(fromStand, toDest, currentMinutes).nextBus
    }

    // First bus tomorrow if today's buses have finished
    fun getTomorrowFirstBus(fromStand: String? = null, toDest: String? = null): BusTimetable? {
        return getSummaryStats(fromStand, toDest).tomorrowFirstBus
    }

    // Instant O(1) Favorites check against pre-indexed Set
    fun isFavorite(fromStand: String, toDest: String): Boolean {
        return favoriteKeys.value.contains("${fromStand}_${toDest}")
    }

    fun toggleFavorite(fromStand: String, fromStandMr: String, toDest: String, toDestMr: String) {
        viewModelScope.launch {
            repository.toggleFavorite(fromStand, fromStandMr, toDest, toDestMr)
            val isNowFav = repository.isFavorite(fromStand, toDest)
            _toastMessage.value = if (isNowFav) "मार्ग आवडत्या यादीत जोडला! ⭐" else "मार्ग काढून टाकला."
        }
    }

    fun toggleFavorite(bus: BusTimetable) {
        toggleFavorite(bus.depotOrBusStand, bus.depotOrBusStandMr, bus.destination, bus.destinationMr)
    }

    // Bus Reminders
    private val _reminders = MutableStateFlow<Set<Long>>(emptySet())
    val reminders: StateFlow<Set<Long>> = _reminders.asStateFlow()

    fun hasReminder(busId: Long): Boolean = _reminders.value.contains(busId)

    fun toggleReminder(bus: BusTimetable) {
        val current = _reminders.value
        if (current.contains(bus.id)) {
            _reminders.value = current - bus.id
            _toastMessage.value = "बस आठवण बंद केली."
        } else {
            _reminders.value = current + bus.id
            _toastMessage.value = "बस आठवण सेट केली! 🔔"
        }
    }

    // Admin & Data Management
    fun addBus(bus: BusTimetable) {
        viewModelScope.launch {
            repository.addTimetable(bus)
            _toastMessage.value = "नवीन बस वेळापत्रक जोडले!"
        }
    }

    fun updateBus(bus: BusTimetable) {
        viewModelScope.launch {
            repository.updateTimetable(bus)
            _toastMessage.value = "वेळापत्रक बदल सेव्ह झाला!"
        }
    }

    fun deleteBus(bus: BusTimetable) {
        viewModelScope.launch {
            repository.deleteTimetable(bus)
            _toastMessage.value = "बस नोंद डिलीट केली."
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            repository.resetToDefault()
            _toastMessage.value = "मूळ वेळापत्रक डेटा पूर्ववत लोड केला."
        }
    }

    fun importCsv(csvText: String, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.importFromCsv(csvText)
            _toastMessage.value = "$count बस नोंदी यशस्वीपणे import झाल्या!"
            onComplete(count)
        }
    }

    fun importJson(jsonText: String, onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.importFromJson(jsonText)
            _toastMessage.value = "$count बस नोंदी यशस्वीपणे import झाल्या!"
            onComplete(count)
        }
    }

    suspend fun exportJson(): String {
        return repository.exportToJson(allTimetables.value)
    }

    suspend fun exportCsv(): String {
        return repository.exportToCsv(allTimetables.value)
    }
}
