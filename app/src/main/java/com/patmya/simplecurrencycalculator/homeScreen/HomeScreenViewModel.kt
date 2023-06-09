package com.patmya.simplecurrencycalculator.homeScreen


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.patmya.simplecurrencycalculator.model.MInputState
import com.patmya.simplecurrencycalculator.model.CurrenciesInfo
import com.patmya.simplecurrencycalculator.model.CurrenciesData
import com.patmya.simplecurrencycalculator.room.InputD
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


    //val context = Context()

    private val dbFireStore = Firebase.firestore
    private val infoReference = dbFireStore.collection("info").document("GyyBIwmt8fDfTBAipZ5h")
    private val dataReference = dbFireStore.collection("data").document("6uf5lDeR8fpW0TOntk3V")

    //private val dbRoom = Room.databaseBuilder(context, AppDatabase::class.java, "my-db").build()


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

    fun changeInput(number: Char, onUpdate: (List<InputD>) -> Unit) {
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
        onUpdate(updateRoom())
    }


    fun changeCurrency(code: String, onUpdate: (List<InputD>) -> Unit) {

        val calculateToUsdValue = currenciesData.value?.data!![code]?.value
        val newCurrency = currenciesInfo.value!!.data?.get(code)!!

        when (currenciesChangeMenuOpenedFrom.value) {
            0 -> firstInputState.value = firstInputState.value.copy(
                currency = code, calculateToUSD = calculateToUsdValue, fullTitle = newCurrency.name
            )
            1 -> secondInputState.value = secondInputState.value.copy(
                currency = code, calculateToUSD = calculateToUsdValue, fullTitle = newCurrency.name
            )
            2 -> thirdInputState.value = thirdInputState.value.copy(
                currency = code, calculateToUSD = calculateToUsdValue, fullTitle = newCurrency.name
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

        onUpdate(updateRoom())

    }

    private fun updateRoom(): List<InputD>{
        val listToUpdate = listOfInputs.mapIndexed{ index, mutableState ->
        val state = mutableState.value
            when (index) {
                0 -> InputD(0, state.currency, true, state.value, state.calculateToUSD, state.fullTitle)
                1 -> InputD(1, state.currency, false, state.value, state.calculateToUSD, state.fullTitle)
                2 -> InputD(2, state.currency, false, state.value, state.calculateToUSD, state.fullTitle)
                else -> throw Exception("Unexpected input state")
            }
        }
        return listToUpdate
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

    fun loadData(inputData: List<InputD>){

        infoReference.get().addOnSuccessListener { infoData ->
            val j = infoData.toObject<CurrenciesInfo>()
            currenciesInfo.value = j
            dataReference.get().addOnSuccessListener { data ->
                val d = data.toObject<CurrenciesData>()
                currenciesData.value = d

                listForChangeCurrency = currenciesInfo.value?.data!!.map { (key, value) ->
                    listOf(key, value.name)

                }.sortedBy { it[0] }

                inputData.forEachIndexed { index, inputD ->
                    val inputValues = MInputState(currency = inputD.currency, active = inputD.active, value = inputD.value, calculateToUSD = inputD.calculateToUSD, fullTitle = inputD.fullTitle)
                    when (index){
                        0 -> firstInputState.value = inputValues
                        1 -> secondInputState.value = inputValues
                        2 -> thirdInputState.value = inputValues
                    }
                }

                dataLoaded.value = currenciesInfo.value != null && currenciesData.value != null && listOfInputs.isNotEmpty()

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

