package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patmya.simplecurrencycalculator.MInputState
import com.patmya.simplecurrencycalculator.R


// On choosing currency use this animation doc: https://developer.android.com/jetpack/compose/animation/composables-modifiers#animatedvisibility

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {

    val scaffoldState = rememberScaffoldState()

    val chosenCurrencyState = remember {
        mutableStateListOf("PLN", "DKK", "USD")
    }

    Scaffold(
        scaffoldState = scaffoldState, topBar = {
            TopBar()
        }, backgroundColor = MaterialTheme.colors.primary
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HomeScreenMainView()
        }

    }
}


@Composable
fun ProgressIndicator(size: Int = 40, strokeWidth: Int = 1) {
    CircularProgressIndicator(
        modifier = Modifier.size(size.dp), strokeWidth = strokeWidth.dp, strokeCap = StrokeCap.Round
    )
}


@Composable
fun HomeScreenMainView() {

    val firstInputState = remember {
        mutableStateOf(MInputState("DKK", true, "69"))
    }
    val secondInputState = remember {
        mutableStateOf(MInputState("USD", false, "2137"))
    }
    val thirdInputState = remember {
        mutableStateOf(MInputState("PLN", false, "911"))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(horizontal = 20.dp)
        ) {
            CurrencyInput(0.33f, state = firstInputState, onClick = {
                firstInputState.value = firstInputState.value.copy(active = true)
                secondInputState.value = secondInputState.value.copy(active = false)
                thirdInputState.value =  thirdInputState.value.copy(active = false)
            }, onChange = {})
            CurrencyInput(0.5f, state = secondInputState, onClick = {
                firstInputState.value = firstInputState.value.copy(active = false)
                secondInputState.value = secondInputState.value.copy(active = true)
                thirdInputState.value =  thirdInputState.value.copy(active = false)
            }, onChange = {})
            CurrencyInput(state = thirdInputState, onClick = {
                firstInputState.value = firstInputState.value.copy(active = false)
                secondInputState.value = secondInputState.value.copy(active = false)
                thirdInputState.value =  thirdInputState.value.copy(active = true)
            }, onChange = {})


        }
        Column(
            modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NumbersNest()
        }
    }

}

@Composable
fun NumbersNest() {
    val keyboardValues = "123456789#0."

    val cardBGColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.4f)

    Text(
        text = "Exchange rates are provided by tralalalalalla",
        color = MaterialTheme.colors.primaryVariant
    )
    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.SpaceBetween,

            ) {
            KeyboardRow(numbers = "123", onClick = {/*TODO*/ })
            KeyboardRow(numbers = "456", onClick = {/*TODO*/ })
            KeyboardRow(numbers = "789", onClick = {/*TODO*/ })
            KeyboardRow(numbers = " 0.", onClick = {/*TODO*/ })
        }
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(0.8f),
                shape = CircleShape,
                elevation = 1.dp,
                backgroundColor = cardBGColor
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { /*TODO*/ },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "AC",
                        fontSize = 24.sp,
                        fontWeight = FontWeight(600),
                        color = MaterialTheme.colors.secondary
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxSize(0.8f),
                shape = CircleShape,
                elevation = 1.dp,
                backgroundColor = cardBGColor
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { /*TODO*/ },
                    painter = painterResource(id = R.drawable.backspace),
                    contentDescription = "arrow back",
                    tint = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
fun KeyboardRow(numbers: String, onClick: (number: Char) -> Unit) {


    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        for (i in numbers) {

            val modifier = if (i.toString() != " ") Modifier.clickable { onClick(i) } else Modifier

            Card(modifier = Modifier.size(70.dp), shape = CircleShape, elevation = 0.dp, backgroundColor = MaterialTheme.colors.primary) {
                Text(
                    text = i.toString(),
                    modifier = modifier,
                    fontSize = 38.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}


@Composable
fun CurrencyInput(height: Float = 1f, state: MutableState<MInputState>, onClick: () -> Unit, onChange: () -> Unit) {
    // TODO change responsible to state

    LaunchedEffect(key1 = state.value.active) {
        println("State active value: ${state.value.active}")
    }

    Row(
        modifier = Modifier
            .fillMaxHeight(height)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.2f), verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.clickable { onChange() }) {
                Text(text = "DKK", fontSize = 22.sp) // TODO temporary
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "arrow down")
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick() },
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "0",
                fontSize = 27.sp,
                textAlign = TextAlign.End,
                color = if (state.value.active!!) MaterialTheme.colors.secondary else MaterialTheme.colors.onPrimary
            ) //TODO temporary
            Text(text = "Danish krone", fontSize = 12.sp)
        }
    }
}

@Composable
fun TopBar() {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 0.dp) {
        // TODO maybe change to column and add ad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Currency calculator", fontSize = 20.sp)
        }
    }
}
