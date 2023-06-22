package com.patmya.simplecurrencycalculator.model

data class CurrencyInfo(
    var symbol: String? = null,
    var name: String? = null,
    var symbol_native: String? = null,
    val decimal_digits: Int? = null,
    var rounding: Int? = null,
    var code: String? = null,
    var name_plural: String? = null
)
