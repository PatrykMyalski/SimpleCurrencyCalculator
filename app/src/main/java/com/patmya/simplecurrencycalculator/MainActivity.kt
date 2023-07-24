package com.patmya.simplecurrencycalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.room.Room
import com.patmya.simplecurrencycalculator.homeScreen.HomeScreen
import com.patmya.simplecurrencycalculator.homeScreen.ProgressIndicator
import com.patmya.simplecurrencycalculator.room.AppDatabase
import com.patmya.simplecurrencycalculator.room.InputD
import com.patmya.simplecurrencycalculator.ui.theme.SimpleCurrencyCalculatorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // used to allow transparent status bar
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )

        // predefining inputsState
        val inputsState = mutableStateOf<List<InputD>>(emptyList())

        // building ROOM database
        val db = Room.databaseBuilder(
            applicationContext, AppDatabase::class.java, "inputs-database"
        ).allowMainThreadQueries().build()

        // invoking dao
        val inputsDao = db.inputDao()

        // inserting emptyList into ROOM
        inputsDao.insertAll(listOf())

        //getting data on app launch
        inputsDao.getAll().run {
            // creating list that will be used when user launch app for the first time
            val defaultList = listOf(
                InputD(0, "USD", true, "0", 1.0, "US Dollar"),
                InputD(1, "USD", false, "0", 1.0, "US Dollar"),
                InputD(2, "USD", false, "0", 1.0, "US Dollar")
            )
            // if list that was returned is empty then defaultList is inserted there and will be send as inputsState
            if (this.isEmpty()) {
                inputsDao.insertAll(defaultList)
                inputsState.value = defaultList
            } else inputsState.value = this // if there was data saved, then it will be returned as state
        }

        // function that is responsible for updating ROOM
        fun onUpdate(list: List<InputD>){
            inputsDao.updateAll(list)
        }

        super.onCreate(savedInstanceState)
        setContent {
            SimpleCurrencyCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    if (inputsState.value.isEmpty()) {
                        ProgressIndicator()
                    } else {
                        HomeScreen(inputsState.value){list ->
                            // invoked by viewModel in case of ROOM updating
                            onUpdate(list)
                        }
                    }
                }
            }
        }
    }
}
