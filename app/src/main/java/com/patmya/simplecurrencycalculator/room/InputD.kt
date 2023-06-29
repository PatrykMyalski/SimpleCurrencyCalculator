package com.patmya.simplecurrencycalculator.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InputD(
    @PrimaryKey val id: Int = 0,
    var currency: String? = null,
    var active: Boolean? = null,
    var value: String? = null,
    var calculateToUSD: Double? = null,
    var fullTitle: String? = null,
)
