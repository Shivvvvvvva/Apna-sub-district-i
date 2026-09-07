package com.example.localization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.model.BusTimetable

enum class AppLanguage(val displayName: String, val code: String) {
    MARATHI("मराठी", "mr"),
    HINDI("हिंदी", "hi"),
    ENGLISH("English", "en")
}

object LanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.MARATHI)

    fun getAppName(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "गाव बससेवा"
        AppLanguage.HINDI -> "गांव बससेवा"
        AppLanguage.ENGLISH -> "Gav Bus Seva"
    }

    fun getAppSubtitle(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "तुमच्या गावातून बस कुठे आणि किती वाजता?"
        AppLanguage.HINDI -> "आपके गांव से बस कहाँ और कितने बजे?"
        AppLanguage.ENGLISH -> "Where & when is the bus from your village?"
    }

    fun getDistrictHeader(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "नांदेड जिल्हा ग्रामीण बस वेळापत्रक"
        AppLanguage.HINDI -> "नांदेड़ जिला ग्रामीण बस समय सारणी"
        AppLanguage.ENGLISH -> "Nanded District Rural Bus Timetable"
    }

    fun getOfflineModeText(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "📴 ऑफलाइन मोड (इंटरनेटशिवाय चालू)"
        AppLanguage.HINDI -> "📴 ऑफलाइन मोड (इंटरनेट के बिना तैयार)"
        AppLanguage.ENGLISH -> "📴 Offline Mode (No Internet Needed)"
    }

    fun getFromLabel(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "पासून (बसस्थानक)"
        AppLanguage.HINDI -> "से (बस स्टैंड)"
        AppLanguage.ENGLISH -> "FROM (Bus Stand)"
    }

    fun getToLabel(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "पर्यंत (गंतव्य)"
        AppLanguage.HINDI -> "तक (गंतव्य)"
        AppLanguage.ENGLISH -> "TO (Destination)"
    }

    fun getSearchBusesButton(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "🔎 बस शोधा"
        AppLanguage.HINDI -> "🔎 बस खोजें"
        AppLanguage.ENGLISH -> "🔎 Find Buses"
    }

    fun getTodayBusesHeader(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "आजच्या बस"
        AppLanguage.HINDI -> "आज की बसें"
        AppLanguage.ENGLISH -> "Today's Buses"
    }

    fun getMorningBuses(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "🌅 सकाळच्या बस"
        AppLanguage.HINDI -> "🌅 सुबह की बसें"
        AppLanguage.ENGLISH -> "🌅 Morning Buses"
    }

    fun getAfternoonBuses(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "☀️ दुपारच्या बस"
        AppLanguage.HINDI -> "☀️ दोपहर की बसें"
        AppLanguage.ENGLISH -> "☀️ Afternoon Buses"
    }

    fun getEveningBuses(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "🌆 संध्याकाळच्या बस"
        AppLanguage.HINDI -> "🌆 शाम की बसें"
        AppLanguage.ENGLISH -> "🌆 Evening Buses"
    }

    fun getAllBuses(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "सर्व बस"
        AppLanguage.HINDI -> "सभी बसें"
        AppLanguage.ENGLISH -> "All Buses"
    }

    fun getDepotsQuick(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "📍 जवळचे बसस्थानक"
        AppLanguage.HINDI -> "📍 नजदीकी बस स्टैंड"
        AppLanguage.ENGLISH -> "📍 Bus Stands"
    }

    fun getFavoritesQuick(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "⭐ आवडते मार्ग"
        AppLanguage.HINDI -> "⭐ पसंदीदा मार्ग"
        AppLanguage.ENGLISH -> "⭐ Favorite Routes"
    }

    fun getAllTimetableQuick(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "🚌 सर्व वेळापत्रक"
        AppLanguage.HINDI -> "🚌 पूरी समय सारणी"
        AppLanguage.ENGLISH -> "🚌 Full Timetable"
    }

    fun getNextBusBadge(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "🟢 पुढील बस"
        AppLanguage.HINDI -> "🟢 अगली बस"
        AppLanguage.ENGLISH -> "🟢 Next Bus"
    }

    fun getNextBusVerifiedNote(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "ही बस आज उपलब्ध timetable मध्ये आहे."
        AppLanguage.HINDI -> "यह बस आज उपलब्ध timetable में है।"
        AppLanguage.ENGLISH -> "This bus is listed in today's timetable."
    }

    fun getLastBusDepartedNote(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "आजची शेवटची बस निघून गेली आहे."
        AppLanguage.HINDI -> "आज की आखिरी बस रवाना हो चुकी है।"
        AppLanguage.ENGLISH -> "Today's last bus has already departed."
    }

    fun getNoInfoAvailable(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "ही माहिती उपलब्ध नाही."
        AppLanguage.HINDI -> "यह जानकारी उपलब्ध नहीं है।"
        AppLanguage.ENGLISH -> "Information not available."
    }

    fun getRouteLabel(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "मार्ग:"
        AppLanguage.HINDI -> "मार्ग:"
        AppLanguage.ENGLISH -> "Route:"
    }

    fun getDepartureTimeLabel(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "बस सुटण्याची वेळ:"
        AppLanguage.HINDI -> "बस छूटने का समय:"
        AppLanguage.ENGLISH -> "Departure Time:"
    }

    fun getStartingStandLabel(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "सुरुवात बसस्थानक:"
        AppLanguage.HINDI -> "शुरुआती बस स्टैंड:"
        AppLanguage.ENGLISH -> "Starting Stand:"
    }

    fun getDestinationLabel(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "गंतव्य:"
        AppLanguage.HINDI -> "गंतव्य:"
        AppLanguage.ENGLISH -> "Destination:"
    }

    fun getSetReminder(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "🔔 बसची आठवण ठेवा"
        AppLanguage.HINDI -> "🔔 बस की याद दिलाएं"
        AppLanguage.ENGLISH -> "🔔 Set Bus Reminder"
    }

    fun getReminderSet(): String = when (currentLanguage) {
        AppLanguage.MARATHI -> "✅ आठवण सेट केली!"
        AppLanguage.HINDI -> "✅ रिमाइंडर सेट हो गया!"
        AppLanguage.ENGLISH -> "✅ Reminder Set!"
    }

    fun getLocalizedDepot(bus: BusTimetable): String = when (currentLanguage) {
        AppLanguage.MARATHI -> if (bus.depotOrBusStandMr.isNotBlank()) bus.depotOrBusStandMr else bus.depotOrBusStand
        AppLanguage.HINDI -> if (bus.depotOrBusStandHi.isNotBlank()) bus.depotOrBusStandHi else bus.depotOrBusStand
        AppLanguage.ENGLISH -> bus.depotOrBusStand
    }

    fun getLocalizedDestination(bus: BusTimetable): String = when (currentLanguage) {
        AppLanguage.MARATHI -> if (bus.destinationMr.isNotBlank()) bus.destinationMr else bus.destination
        AppLanguage.HINDI -> if (bus.destinationHi.isNotBlank()) bus.destinationHi else bus.destination
        AppLanguage.ENGLISH -> bus.destination
    }

    fun getLocalizedRoute(bus: BusTimetable): String = when (currentLanguage) {
        AppLanguage.MARATHI -> if (bus.busRouteMr.isNotBlank()) bus.busRouteMr else bus.busRoute
        AppLanguage.HINDI -> if (bus.busRouteHi.isNotBlank()) bus.busRouteHi else bus.busRoute
        AppLanguage.ENGLISH -> if (bus.busRoute.isNotBlank()) bus.busRoute else getNoInfoAvailable()
    }

    fun getLocalizedBusType(bus: BusTimetable): String = bus.busType

    fun formatRemainingTime(minutes: Int): String {
        return when (currentLanguage) {
            AppLanguage.MARATHI -> {
                when {
                    minutes <= 0 -> "बस सुटण्याची वेळ झाली आहे"
                    minutes < 60 -> "बस सुटायला अंदाजे $minutes मिनिटे बाकी"
                    else -> {
                        val hours = minutes / 60
                        val mins = minutes % 60
                        if (mins == 0) "बस सुटायला अंदाजे $hours तास बाकी"
                        else "बस सुटायला अंदाजे $hours तास $mins मिनिटे बाकी"
                    }
                }
            }
            AppLanguage.HINDI -> {
                when {
                    minutes <= 0 -> "बस छूटने का समय हो गया है"
                    minutes < 60 -> "बस छूटने में लगभग $minutes मिनट बाकी"
                    else -> {
                        val hours = minutes / 60
                        val mins = minutes % 60
                        if (mins == 0) "बस छूटने में लगभग $hours घंटे बाकी"
                        else "बस छूटने में लगभग $hours घंटे $mins मिनट बाकी"
                    }
                }
            }
            AppLanguage.ENGLISH -> {
                when {
                    minutes <= 0 -> "Bus departure time right now"
                    minutes < 60 -> "Approx $minutes mins remaining for departure"
                    else -> {
                        val hours = minutes / 60
                        val mins = minutes % 60
                        if (mins == 0) "Approx $hours hours remaining"
                        else "Approx $hours hrs $mins mins remaining"
                    }
                }
            }
        }
    }
}
