package com.patmya.simplecurrencycalculator.homeScreen


import androidx.compose.runtime.mutableStateListOf
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

    // References from firebase
    private val dbFireStore = Firebase.firestore
    private val infoReference = dbFireStore.collection("info").document("GyyBIwmt8fDfTBAipZ5h")
    private val dataReference = dbFireStore.collection("data").document("6uf5lDeR8fpW0TOntk3V")

    // States of inputs, later will be added to list, needs to be separated for UI
    val firstInputState = mutableStateOf(MInputState())
    val secondInputState = mutableStateOf(MInputState())
    val thirdInputState = mutableStateOf(MInputState())
    private val listOfInputs = arrayListOf(firstInputState, secondInputState, thirdInputState)

    // index of input state user is currently typing
    private var indexOfActive = mutableStateOf<Int?>(null)

    // in which input user wants yo change currency
    val currenciesChangeMenuOpenedFrom = mutableStateOf(0)

    // list of all currencies, it will be override with loadData()
    var listForChangeCurrency: MutableList<List<String?>> = mutableStateListOf()

    // object that holds all needed info about currencies, it will be override with loadData()
    private val currenciesInfo = mutableStateOf<CurrenciesInfo?>(null)

    // object that hold exchange rate of all currencies, it will be override with loadData()
    private val currenciesData = mutableStateOf<CurrenciesData?>(null)

    // provides info to UI that data is loaded or not
    val dataLoaded = mutableStateOf(false)

    // responsible for adding currentNumber and number user want to be added in active input field
    private fun addNumbers(currentNumber: String, adding: Char): String {

        return if (currentNumber == "0" && adding != '.') {
            adding.toString()
        } else {
            "$currentNumber$adding"
        }
    }

    // function for backspace button
    private fun deleteLast(currentNumber: String): String {

        return if (currentNumber == "0" || currentNumber.length == 1 || currentNumber == "< 0.005") return "0" else currentNumber.dropLast(
            1
        )
    }

    // preventing user from inputting two dots in number
    private fun checkIfNumberFormatException(number: String, adding: Char): Boolean {
        return '.' in number && adding == '.'
    }

    // calculating inputted value, rounding it and returning it as string
    private fun calculateValue(
        calculateToUsdOfBaseInput: Double,
        valueOfActiveInput: String,
        calculateToUsdOfActiveInput: Double,
    ): String {

        val number =
            valueOfActiveInput.toDouble() * calculateToUsdOfBaseInput / calculateToUsdOfActiveInput

        val rounded = BigDecimal(number).setScale(2, RoundingMode.HALF_UP).toString()

        // checking if value is bigger than 0.005 if not then info about that is provided to user
        return if (rounded.take(4) == "0.00") "< 0.005" else rounded
    }

    // on every key stroke data about inputs value and currencies chosen is saved in ROOM
    private fun updateRoom(): List<InputD> {
        val listToUpdate = listOfInputs.mapIndexed { index, mutableState ->
            val state = mutableState.value
            when (index) {
                0 -> InputD(
                    0, state.currency, true, state.value, state.calculateToUSD, state.fullTitle
                )
                1 -> InputD(
                    1, state.currency, false, state.value, state.calculateToUSD, state.fullTitle
                )
                2 -> InputD(
                    2, state.currency, false, state.value, state.calculateToUSD, state.fullTitle
                )
                else -> throw Exception("Unexpected input state")
            }
        }
        return listToUpdate
    }

    // changing current active input that user will put numbers in
    fun focusInput(position: Int) {
        indexOfActive.value = position
        firstInputState.value = firstInputState.value.copy(active = position == 0)
        secondInputState.value = secondInputState.value.copy(active = position == 1)
        thirdInputState.value = thirdInputState.value.copy(active = position == 2)
    }

    // main function responsible for changing values in input fields
    fun changeInput(number: Char, onUpdate: (List<InputD>) -> Unit) {

        if (checkIfNumberFormatException(
                listOfInputs[indexOfActive.value!!].value.value!!, number
            )
        ) return

        var valueOfInput = listOfInputs[indexOfActive.value!!].value.value!!

        // preventing user from inputting more than two digits after dot
        if ('.' in valueOfInput) {
            if (valueOfInput.length - valueOfInput.indexOf('.') == 3) return
        }

        // when user want to input number in field that previously showed "< 0.005" info for calculation purpose
        if (valueOfInput == "< 0.005") valueOfInput = "0"

        val calculateToUsdOfActiveInput = listOfInputs[indexOfActive.value!!].value.calculateToUSD

        // iterating through listOfInputs and changing state for each
        for ((index, inputState) in listOfInputs.withIndex()) {
            when (index) {
                0 -> firstInputState.value = firstInputState.value.copy(
                    // for each input checking if input field is active, if it is then we only use addNumbers function
                    value = if (inputState.value.active!!) {
                        addNumbers(valueOfInput, number)
                    } else {
                        // in other case we are calculating input value
                        calculateValue(
                            inputState.value.calculateToUSD!!,
                            addNumbers(valueOfInput, number),
                            calculateToUsdOfActiveInput!!
                        )
                    }
                )
                1 -> secondInputState.value = secondInputState.value.copy(
                    value = if (inputState.value.active!!) {
                        addNumbers(valueOfInput, number)
                    } else {
                        calculateValue(
                            inputState.value.calculateToUSD!!,
                            addNumbers(valueOfInput, number),
                            calculateToUsdOfActiveInput!!
                        )
                    }
                )
                2 -> thirdInputState.value = thirdInputState.value.copy(
                    value = if (inputState.value.active!!) {
                        addNumbers(valueOfInput, number)
                    } else {
                        calculateValue(
                            inputState.value.calculateToUSD!!,
                            addNumbers(valueOfInput, number),
                            calculateToUsdOfActiveInput!!
                        )
                    }
                )
            }
        }
        // updating room
        onUpdate(updateRoom())
    }

    // responsible for searching currency on changing currency menu
    fun searchCurrency(input: String, onDone: (MutableList<List<String?>>) -> Unit) {

        val info = currenciesInfo.value?.data!!

        // empty list that in future will hold that search query found
        val listForSearch = mutableListOf<List<String?>>()

        when (input) {
            // here are cases of currency codes which length is different than 3
            "avax" -> {
                val i = info["AVAX"]!!
                // returning list consisting only of code and full currency name
                onDone(mutableListOf(listOf(i.code, i.name)))

            }
            "matic" -> {
                val i = info["MATIC"]!!
                onDone(mutableListOf(listOf(i.code, i.name)))
            }
            else -> {
                // iterating through list that is made from list of currency code [0] and full currency title [1]
                listForChangeCurrency.forEach {

                    // getting code and title in case of finding it
                    val code = it[0]!!
                    val title = it[1]!!

                    // transforming for future comparison with user input that also is lowercase in composable function
                    val codeLowercase = code.lowercase()
                    val titleLowercase = title.lowercase()

                    // list of code, title pair if it will match and will be later added to listForSearch
                    val ifCorrectToUpdate = listOf(code, title)

                    // if input length is 3, only checking currency codes
                    if (input.length == 3) {
                        if (codeLowercase == input) {
                            listForSearch += ifCorrectToUpdate
                        }
                    // if its length is different, checking title matching with input
                    } else {
                        if (input in titleLowercase) {
                            listForSearch += ifCorrectToUpdate
                        }
                    }

                }
                // if there is nothing found, returning list consisting of element that will inform of unsuccessful search
                if (listForSearch.isEmpty()) {
                    onDone(mutableListOf(listOf("", "Not Found")))
                } else onDone(listForSearch)
            }
        }
    }


    // function that is handling currency change
    fun changeCurrency(code: String, onUpdate: (List<InputD>) -> Unit) {

        // code is passed from composable and it is pointing at currency code
        // getting there exchange rates of new currency
        val calculateToUsdValue = currenciesData.value?.data!![code]?.value
        // getting data of new currency
        val newCurrency = currenciesInfo.value!!.data?.get(code)!!

        // iterating and changing currency of field that changing menu was opened
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


        // calculating all values
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
        // updating ROOM
        onUpdate(updateRoom())
    }


    // handling AC click
    fun clearInputs() {
        firstInputState.value = firstInputState.value.copy(value = "0")
        secondInputState.value = secondInputState.value.copy(value = "0")
        thirdInputState.value = thirdInputState.value.copy(value = "0")
    }

    //handling backSpace click
    fun backSpace() {

        val valueOfActiveInput = deleteLast(listOfInputs[indexOfActive.value!!].value.value!!)

        val calculateToUsdOfActiveInput = listOfInputs[indexOfActive.value!!].value.calculateToUSD

        // calculating values of all fields according to active input filed change
        for ((index, inputState) in listOfInputs.withIndex()) {
            when (index) {
                0 -> firstInputState.value = firstInputState.value.copy(
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

                1 -> secondInputState.value = secondInputState.value.copy(
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
                2 -> thirdInputState.value = thirdInputState.value.copy(
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


    // loading data on app launch
    fun loadData(inputData: List<InputD>) {
        // input data is data that is passed from main activity out of ROOM

        // getting reference from firebase,
        // transforming infoData and exchange rates to usable objects
        infoReference.get().addOnSuccessListener { infoData ->
            val j = infoData.toObject<CurrenciesInfo>()
            currenciesInfo.value = j
            dataReference.get().addOnSuccessListener { data ->
                val d = data.toObject<CurrenciesData>()
                currenciesData.value = d

                // overriding listForChangeCurrency with lists of currency code and full currency title pairs
                listForChangeCurrency = currenciesInfo.value?.data!!.map { (key, value) ->
                    listOf(key, value.name)
                // sorting it alphabetically
                }.sortedBy { it[0] }.toMutableList()

                // iterating through data of inputs, transforming to usable objects and overriding state
                inputData.forEachIndexed { index, inputD ->
                    if (inputD.active == true) indexOfActive.value = index
                    val inputValues = MInputState(
                        currency = inputD.currency,
                        active = inputD.active,
                        value = inputD.value,
                        calculateToUSD = inputD.calculateToUSD,
                        fullTitle = inputD.fullTitle
                    )
                    when (index) {
                        0 -> firstInputState.value = inputValues
                        1 -> secondInputState.value = inputValues
                        2 -> thirdInputState.value = inputValues
                    }
                }
                // when everything went well then dataLoaded value can be changed to true and UI will be shown to user
                dataLoaded.value =
                    currenciesInfo.value != null && currenciesData.value != null && listOfInputs.isNotEmpty()

            }.addOnFailureListener {
                println(it)
            }
        }.addOnFailureListener {
            println(it)
        }
    }

    // legacy function that was responsible for api request and passing it to firebase as a method to walk around api limitations
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

