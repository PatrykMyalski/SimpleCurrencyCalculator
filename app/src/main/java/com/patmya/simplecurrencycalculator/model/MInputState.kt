package com.patmya.simplecurrencycalculator.model

data class MInputState(
    var currency: String? = null,
    var active: Boolean? = null,
    var recentlyChanged: Boolean? = null,
    var value: String? = null,
    var calculateToUSD: Double? = null,
    var fullTitle: String? = null,
)
