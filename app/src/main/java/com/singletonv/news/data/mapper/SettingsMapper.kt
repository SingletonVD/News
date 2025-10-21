package com.singletonv.news.data.mapper

import com.singletonv.news.domain.entity.RefreshConfig
import com.singletonv.news.domain.entity.Settings

fun Settings.toRefreshConfig(): RefreshConfig =
    RefreshConfig(
        language = language,
        interval = interval,
        wifiOnly = wifiOnly
    )