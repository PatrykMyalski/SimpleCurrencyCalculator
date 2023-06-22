package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.patmya.simplecurrencycalculator.MInputState
import com.patmya.simplecurrencycalculator.model.CurrenciesInfo
import com.patmya.simplecurrencycalculator.model.CurrenciesData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class HomeScreenViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val infoReference = db.collection("info").document("GyyBIwmt8fDfTBAipZ5h")
    private val dataReference = db.collection("data").document("6uf5lDeR8fpW0TOntk3V")

    val firstInputState = mutableStateOf(MInputState("DKK", true, "69"))

    val secondInputState = mutableStateOf(MInputState("USD", false, "2137"))

    val thirdInputState = mutableStateOf(MInputState("PLN", false, "911"))

    val currenciesInfo = mutableStateOf<CurrenciesInfo?>(null)

    val currenciesData = mutableStateOf<CurrenciesData?>(null)

    val dataLoaded = mutableStateOf(false)

    private val listOfInputs = arrayListOf(firstInputState, secondInputState, thirdInputState)


    private fun addNumbers(currentNumber: String, adding: Char): String {
        //TODO change reaction on '.'
        return if (currentNumber == "0") {
            adding.toString()
        } else {
            "$currentNumber$adding"
        }
    }

    private fun deleteLast(currentNumber: String): String {
        return if (currentNumber == "0" || currentNumber.length == 1) return "0" else currentNumber.dropLast(
            1
        )
    }

    fun focusInput(position: Int) {
        firstInputState.value = firstInputState.value.copy(active = position == 1)
        secondInputState.value = secondInputState.value.copy(active = position == 2)
        thirdInputState.value = thirdInputState.value.copy(active = position == 3)
    }

    fun changeInput(number: Char) {

        for ((index, inputState) in listOfInputs.withIndex()) {
            if (inputState.value.active!!) {
                when (index) {
                    0 -> firstInputState.value = firstInputState.value.copy(
                        value = addNumbers(
                            inputState.value.value!!, number
                        )
                    )
                    1 -> secondInputState.value = secondInputState.value.copy(
                        value = addNumbers(
                            inputState.value.value!!, number
                        )
                    )
                    2 -> thirdInputState.value = thirdInputState.value.copy(
                        value = addNumbers(
                            inputState.value.value!!, number
                        )
                    )
                }
            }
            // TODO here make the rest if inputs calculate their value according to currency rate
        }
    }

    fun clearInputs() {
        firstInputState.value = firstInputState.value.copy(value = "0")
        secondInputState.value = secondInputState.value.copy(value = "0")
        thirdInputState.value = thirdInputState.value.copy(value = "0")
    }

    fun backSpace() {
        for ((index, inputState) in listOfInputs.withIndex()) {
            if (inputState.value.active!!) {
                when (index) {
                    0 -> firstInputState.value =
                        firstInputState.value.copy(value = deleteLast(inputState.value.value!!))
                    1 -> secondInputState.value =
                        secondInputState.value.copy(value = deleteLast(inputState.value.value!!))
                    2 -> thirdInputState.value =
                        thirdInputState.value.copy(value = deleteLast(inputState.value.value!!))
                }
            }
        }
    }

    fun loadData() {
        infoReference.get().addOnSuccessListener { infoData ->
            val i = infoData.toObject<CurrenciesInfo>()
            currenciesInfo.value = i
            dataReference.get().addOnSuccessListener { data ->
                val d = data.toObject<CurrenciesData>()
                currenciesData.value = d
                dataLoaded.value = currenciesInfo.value != null && currenciesData.value != null
                println(currenciesInfo.value)
                println(currenciesData.value)
            }.addOnFailureListener {
                println(it)
            }
        }.addOnFailureListener {
            println(it)
        }
    }

    fun transformData() {


        CoroutineScope(Dispatchers.Main).launch {
            println("start")
            withContext(Dispatchers.IO) {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url("https://api.currencyapi.com/v3/currencies?apikey=DfHgL2ZN6L0kDaxrBKOwgDhYQeHIlyjkQ7R6dNMl&currencies=")
                    .build()

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()

                    val gson = Gson()

                    val data = gson.fromJson(responseBody, CurrenciesInfo::class.java)


                    infoReference.set(data).addOnSuccessListener {
                        println("data successfully posted on firebase firestore")
                    }

                } else {
                    println("error occurs")
                }
            }
            println("done")
        }

    }
}

