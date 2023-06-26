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
import java.math.BigDecimal
import java.math.RoundingMode

class HomeScreenViewModel : ViewModel() {


    private val db = Firebase.firestore
    private val infoReference = db.collection("info").document("GyyBIwmt8fDfTBAipZ5h")
    private val dataReference = db.collection("data").document("6uf5lDeR8fpW0TOntk3V")


    val firstInputState = mutableStateOf(MInputState())

    val secondInputState = mutableStateOf(MInputState())

    val thirdInputState = mutableStateOf(MInputState())

    var indexOfActive = mutableStateOf<Int?>(null)

    val currenciesChangeMenuOpenedFrom = mutableStateOf(0)

    var listForChangeCurrency: List<List<String?>> = listOf()

    val currenciesInfo = mutableStateOf<CurrenciesInfo?>(null)

    val currenciesData = mutableStateOf<CurrenciesData?>(null)

    val dataLoaded = mutableStateOf(false)

    private val listOfInputs = arrayListOf(firstInputState, secondInputState, thirdInputState)

    //TODO reduce boilerplate code, add full name variable to inputState class blueprint, test

    // TODO add full name to input properties and make it change on currency change


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

    private fun calculateValue(
        calculateToUsdOfBaseInput: Double,
        valueOfActiveInput: String,
        calculateToUsdOfActiveInput: Double,
    ): String {

        val number = valueOfActiveInput.toDouble() * calculateToUsdOfActiveInput * calculateToUsdOfBaseInput

        return BigDecimal(number).setScale(4, RoundingMode.HALF_UP).toString()
    }

    fun focusInput(position: Int) {
        indexOfActive.value = position
        firstInputState.value = firstInputState.value.copy(active = position == 0)
        secondInputState.value = secondInputState.value.copy(active = position == 1)
        thirdInputState.value = thirdInputState.value.copy(active = position == 2)
    }

    fun changeInput(number: Char) {
        //TODO TEST
        val valueOfActiveInput = listOfInputs[indexOfActive.value!!].value.value + number

        val calculateToUsdOfActiveInput = listOfInputs[indexOfActive.value!!].value.calculateToUSD

        for ((index, inputState) in listOfInputs.withIndex()) {
            when (index) {
                0 -> firstInputState.value = firstInputState.value.copy(
                    value = if (inputState.value.active!!) {
                        addNumbers(inputState.value.value!!, number)
                    } else {
                        calculateValue(
                            inputState.value.calculateToUSD!!,
                            valueOfActiveInput,
                            calculateToUsdOfActiveInput!!
                        )
                    }
                )
                1 -> secondInputState.value = secondInputState.value.copy(
                    value = if (inputState.value.active!!) {
                        addNumbers(inputState.value.value!!, number)
                    } else {
                        calculateValue(
                            inputState.value.calculateToUSD!!,
                            valueOfActiveInput,
                            calculateToUsdOfActiveInput!!
                        )
                    }
                )
                2 -> thirdInputState.value = thirdInputState.value.copy(
                    value = if (inputState.value.active!!) {
                        addNumbers(inputState.value.value!!, number)
                    } else {
                        calculateValue(
                            inputState.value.calculateToUSD!!,
                            valueOfActiveInput,
                            calculateToUsdOfActiveInput!!
                        )
                    }
                )
            }
        }
    }


    fun changeCurrency(code: String) {

        val calculateToUsdValue = currenciesData.value?.data!![code]?.value

        when (currenciesChangeMenuOpenedFrom.value) {
            0 -> firstInputState.value = firstInputState.value.copy(
                currency = code, calculateToUSD = calculateToUsdValue,
            )
            1 -> secondInputState.value = secondInputState.value.copy(
                currency = code, calculateToUSD = calculateToUsdValue,
            )
            2 -> thirdInputState.value = thirdInputState.value.copy(
                currency = code, calculateToUSD = calculateToUsdValue,
            )
        }

        val valueOfActiveInput = listOfInputs[indexOfActive.value!!].value.value!!

        val calculateToUsdOfActiveInput = listOfInputs[indexOfActive.value!!].value.calculateToUSD!!

        for ((index, state) in listOfInputs.withIndex()) {

            if (indexOfActive.value != index) {
                when (index) {
                    0 -> firstInputState.value = firstInputState.value.copy(
                        value = calculateValue(
                            state.value.calculateToUSD!!,
                            valueOfActiveInput,
                            calculateToUsdOfActiveInput
                        )
                    )
                    1 -> secondInputState.value = secondInputState.value.copy(
                        value = calculateValue(
                            state.value.calculateToUSD!!,
                            valueOfActiveInput,
                            calculateToUsdOfActiveInput
                        )
                    )
                    2 -> thirdInputState.value = thirdInputState.value.copy(
                        value = calculateValue(
                            state.value.calculateToUSD!!,
                            valueOfActiveInput,
                            calculateToUsdOfActiveInput
                        )
                    )
                }
            }
        }
    }

    fun clearInputs() {
        firstInputState.value = firstInputState.value.copy(value = "0")
        secondInputState.value = secondInputState.value.copy(value = "0")
        thirdInputState.value = thirdInputState.value.copy(value = "0")
    }

    fun backSpace() {

        val valueOfActiveInput = deleteLast(listOfInputs[indexOfActive.value!!].value.value!!)

        val calculateToUsdOfActiveInput = listOfInputs[indexOfActive.value!!].value.calculateToUSD

        for ((index, inputState) in listOfInputs.withIndex()) {
            when (index) {
                0 -> firstInputState.value =
                    firstInputState.value.copy(
                        value = if (inputState.value.active!!) {
                            deleteLast(inputState.value.value!!)
                        } else {
                            calculateValue(
                                inputState.value.calculateToUSD!!,
                                valueOfActiveInput,
                                calculateToUsdOfActiveInput!!
                            )
                        }
                    )

                1 -> secondInputState.value =
                    secondInputState.value.copy(
                        value = if (inputState.value.active!!) {
                            deleteLast(inputState.value.value!!)
                        } else {
                            calculateValue(
                                inputState.value.calculateToUSD!!,
                                valueOfActiveInput,
                                calculateToUsdOfActiveInput!!
                            )
                        }
                    )
                2 -> thirdInputState.value =
                    thirdInputState.value.copy(
                        value = if (inputState.value.active!!) {
                            deleteLast(inputState.value.value!!)
                        } else {
                            calculateValue(
                                inputState.value.calculateToUSD!!,
                                valueOfActiveInput,
                                calculateToUsdOfActiveInput!!
                            )
                        }
                    )
            }

        }
    }

    fun loadData() {
        // TODO Check if user runs for the first time, if true, then start with base values of USD, GBP, EUR, if false then load from storage

        infoReference.get().addOnSuccessListener { infoData ->
            val j = infoData.toObject<CurrenciesInfo>()
            currenciesInfo.value = j
            dataReference.get().addOnSuccessListener { data ->
                val d = data.toObject<CurrenciesData>()
                currenciesData.value = d

                listForChangeCurrency = currenciesInfo.value?.data!!.map { (key, value) ->
                    listOf(key, value.name)

                }.sortedBy { it[0] }

                // TODO temporary
                val calc = currenciesData.value?.data?.get("USD")?.value
                indexOfActive.value = 0
                firstInputState.value = MInputState("USD", true, "1", calc)
                secondInputState.value = MInputState("USD", false, "1", calc)
                thirdInputState.value = MInputState("USD", false, "1", calc)


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

