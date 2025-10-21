package com.singletonv.news.domain.entity

data class Settings(
    val language: Language,
    val interval: Interval,
    val notificationsEnabled: Boolean,
    val wifiOnly: Boolean
) {
    companion object {

        val DEFAULT_LANGUAGE = Language.ENGLISH
        val DEFAULT_INTERVAL = Interval.MIN_15
        const val DEFAULT_NOTIFICATIONS_ENABLED = false
        const val DEFAULT_WIFI_ONLY = true
    }
}

enum class Language {

    ENGLISH, RUSSIAN, FRENCH, GERMAN
}

enum class Interval(val minutes: Int) {

    MIN_15(15),
    MIN_30(30),
    HOUR_1(60),
    HOUR_2(2 * 60),
    HOUR_4(4 * 60),
    HOUR_8(8 * 60),
    HOUR_24(24 * 60)
}