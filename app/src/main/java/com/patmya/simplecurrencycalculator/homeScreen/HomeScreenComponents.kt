package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patmya.simplecurrencycalculator.R


@Composable
fun TopBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
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

@Composable
fun CurrencyChangeLabel(code: String, title: String, onClick: (String) -> Unit) {
    Card(modifier = Modifier
        .clickable { onClick(code) },
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = code, fontSize = 20.sp)
            Text(text = title, fontSize = 20.sp)
        }
    }
}

@Composable
fun ProgressIndicator(size: Int = 40, strokeWidth: Int = 1) {
    CircularProgressIndicator(
        modifier = Modifier.size(size.dp),
        strokeWidth = strokeWidth.dp,
        strokeCap = StrokeCap.Round,
        color = MaterialTheme.colors.secondary
    )
}

@Composable
fun NumbersNest(
    onNumberInput: (number: Char) -> Unit,
    onClear: () -> Unit,
    onBackSpace: () -> Unit,
) {

    val cardBGColor = MaterialTheme.colors.primaryVariant

    Text(
        // TODO
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
            KeyboardRow(numbers = "123") { onNumberInput(it) }
            KeyboardRow(numbers = "456") { onNumberInput(it) }
            KeyboardRow(numbers = "789") { onNumberInput(it) }
            KeyboardRow(numbers = " 0.") { onNumberInput(it) }
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
                        .clickable { onClear() },
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
                        .clickable { onBackSpace() },
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

            Card(
                modifier = Modifier.size(70.dp),
                shape = CircleShape,
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.primary
            ) {
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
