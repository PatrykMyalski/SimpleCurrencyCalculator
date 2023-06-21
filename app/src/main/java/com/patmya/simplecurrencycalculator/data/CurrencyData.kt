package com.patmya.simplecurrencycalculator.data

data class MCurrency(val data: Map<String, String>)

data class CurrencyData(val data: Map<String, MCurrency>)
