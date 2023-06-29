package com.patmya.simplecurrencycalculator.room

import androidx.room.*

@Dao
interface InputDDao {
    @Query("SELECT * FROM inputd")
    fun getAll(): List<InputD>

    @Update
    fun updateAll(inputs: List<InputD>)

    @Insert
    fun insertAll(inputs: List<InputD>)

}