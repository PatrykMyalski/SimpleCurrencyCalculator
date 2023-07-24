package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patmya.simplecurrencycalculator.R
import com.patmya.simplecurrencycalculator.model.MInputState


@Composable
fun TopBar() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.primary
    ){}
}

@Composable
fun CurrencyChangeLabel(code: String, title: String, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier.clickable { onClick(code) }, shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 20.sp)
            Text(text = code, fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
        text = "Exchange rates are provided by currencyapi.com",
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

@Composable
fun CurrencyInput(
    state: MutableState<MInputState>,
    onClick: () -> Unit,
    onChangeCurrency: () -> Unit,
) {

    val interactionSource = MutableInteractionSource()

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.2f), verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.clickable(
                interactionSource = interactionSource, indication = null
            ) { onChangeCurrency() }) {
                Text(text = state.value.currency!!, fontSize = 22.sp)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown, contentDescription = "arrow down"
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(interactionSource = interactionSource, indication = null) {
                    onClick()
                }, horizontalAlignment = Alignment.End
        ) {
            Text(
                text = state.value.value.toString(),
                fontSize = 27.sp,
                textAlign = TextAlign.End,
                color = if (state.value.active!!) MaterialTheme.colors.secondary else MaterialTheme.colors.onPrimary
            )
            Text(text = state.value.fullTitle!!, fontSize = 12.sp)
        }
    }
}
