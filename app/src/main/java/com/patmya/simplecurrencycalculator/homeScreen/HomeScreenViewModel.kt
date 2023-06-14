package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.patmya.simplecurrencycalculator.MInputState

class HomeScreenViewModel : ViewModel() {


    val firstInputState = mutableStateOf(MInputState("DKK", true, "69"))

    val secondInputState = mutableStateOf(MInputState("USD", false, "2137"))

    val thirdInputState = mutableStateOf(MInputState("PLN", false, "911"))

    private val listOfInputs =
        arrayListOf(firstInputState, secondInputState, thirdInputState)

    fun focusInput(position: Int) {
        firstInputState.value = firstInputState.value.copy(active = position == 1)
        secondInputState.value = secondInputState.value.copy(active = position == 2)
        thirdInputState.value = thirdInputState.value.copy(active = position == 3)
    }

    private fun addNumbers(currentNumber: String, adding: Char): String{
        //TODO change reaction on '.'
        return if (currentNumber == "0"){
            adding.toString()
        } else {
            "$currentNumber$adding"
        }
    }

    private fun deleteLast(currentNumber: String): String {
        return if (currentNumber == "0" || currentNumber.length == 1) return "0" else currentNumber.dropLast(1)
    }
    fun changeInput(number: Char) {

        for ((index, inputState) in listOfInputs.withIndex()) {
            if (inputState.value.active!!) {
                when (index) {
                    0 -> firstInputState.value = firstInputState.value.copy(value = addNumbers(inputState.value.value!!, number))
                    1 -> secondInputState.value = secondInputState.value.copy(value = addNumbers(inputState.value.value!!, number))
                    2 -> thirdInputState.value = thirdInputState.value.copy(value = addNumbers(inputState.value.value!!, number))
                }
            }
            // TODO here make the rest if inputs calculate their value according to currency rate
        }
    }

    fun clearInputs(){
        firstInputState.value = firstInputState.value.copy(value = "0")
        secondInputState.value = secondInputState.value.copy(value = "0")
        thirdInputState.value = thirdInputState.value.copy(value = "0")
    }

    fun backSpace(){
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
}

