package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patmya.simplecurrencycalculator.R


// On choosing currency use this animation doc: https://developer.android.com/jetpack/compose/animation/composables-modifiers#animatedvisibility

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {

    val scaffoldState = rememberScaffoldState()

    val chosenCurrencyState = remember {
        mutableStateListOf("PLN", "DKK", "USD")
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar()
        },
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
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(horizontal = 20.dp)
        ) {
            CurrencyInput(0.33f)
            CurrencyInput(0.5f)
            CurrencyInput()


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
    Text(text = "Exchange rates are provided by tralalalalalla")
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
            Card(modifier = Modifier.fillMaxHeight(0.5f).fillMaxWidth(0.8f), shape = CircleShape, elevation = 1.dp) {
                Row(modifier = Modifier.fillMaxSize().clickable { /*TODO*/ }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text(text = "AC", fontSize = 24.sp)
                }
            }
            Card(modifier = Modifier.fillMaxSize(0.8f), shape = CircleShape, elevation = 1.dp) {
                Icon(modifier = Modifier.fillMaxSize().clickable { /*TODO*/ },
                    painter = painterResource(id = R.drawable.backspace),
                    contentDescription = "arrow back"
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

            Card(modifier = Modifier.size(70.dp), shape = CircleShape, elevation = 0.dp) {
                    Text(text = i.toString(), modifier = modifier, fontSize = 38.sp, textAlign = TextAlign.Center)
            }
        }

    }
}


@Composable
fun CurrencyInput(height: Float = 1f) {
    Row(
        modifier = Modifier
            .fillMaxHeight(height)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.5f), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "DKK", fontSize = 22.sp) // TODO temporary
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "arrow down")
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text(text = "0", fontSize = 27.sp, textAlign = TextAlign.End) //TODO temporary
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
