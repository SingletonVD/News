package com.singletonv.news.presentation.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.singletonv.news.R
import com.singletonv.news.domain.entity.Interval
import com.singletonv.news.domain.entity.Language

@Composable
fun Language.toUiString(): String {
    return when (this) {
        Language.ENGLISH -> stringResource(R.string.english)
        Language.RUSSIAN -> stringResource(R.string.russian)
        Language.FRENCH -> stringResource(R.string.french)
        Language.GERMAN -> stringResource(R.string.german)
    }
}

@Composable
fun Interval.toUiString(): String {
    return if (this.minutes >= 60) {
        stringResource(R.string.settings_hours_label, this.minutes / 60)
    } else {
        stringResource(R.string.settings_minutes_label, this.minutes)
    }
}