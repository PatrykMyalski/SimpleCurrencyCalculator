package com.patmya.simplecurrencycalculator.homeScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patmya.simplecurrencycalculator.MInputState


// On choosing currency use this animation doc: https://developer.android.com/jetpack/compose/animation/composables-modifiers#animatedvisibility

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = viewModel()) {

    val scaffoldState = rememberScaffoldState()

    if (!viewModel.dataLoaded.value) {
        viewModel.loadData()
    }


    if (!viewModel.dataLoaded.value) Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ProgressIndicator()
    }
    else {


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
                HomeScreenMainView(viewModel = viewModel)
            }
        }
    }
}


@Composable
fun HomeScreenMainView(viewModel: HomeScreenViewModel) {

    // TODO only for development
    //viewModel.transformData()
    val showCurrencyChange = remember {
        mutableStateOf(false)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 20.dp)
            ) {
                CurrencyInput(0.33f, state = viewModel.firstInputState, onClick = {
                    viewModel.focusInput(1)
                }, onChangeCurrency = { showCurrencyChange.value = true })
                CurrencyInput(0.5f, state = viewModel.secondInputState, onClick = {
                    viewModel.focusInput(2)
                }, onChangeCurrency = { showCurrencyChange.value = true })
                CurrencyInput(state = viewModel.thirdInputState, onClick = {
                    viewModel.focusInput(3)
                }, onChangeCurrency = { showCurrencyChange.value = true })
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NumbersNest(onNumberInput = { viewModel.changeInput(it) },
                    onClear = { viewModel.clearInputs() },
                    onBackSpace = { viewModel.backSpace() })
            }
        }
        if (showCurrencyChange.value) {
            CurrencyChange(viewModel) {
                showCurrencyChange.value = false
            }
        }
    }
}

@Composable
fun CurrencyChange(viewModel: HomeScreenViewModel, onExit: () -> Unit) {


    val interactionSource = MutableInteractionSource()

    val animationState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    LaunchedEffect(key1 = animationState.currentState) {
        if (!animationState.currentState && !animationState.targetState) {
            onExit()
        }
    }

    AnimatedVisibility(
        visibleState = animationState, enter = slideInVertically(), exit = shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, indication = null) {
                    animationState.targetState = false
                },
            backgroundColor = Color(0, 0, 0, 20),
            elevation = 0.dp,
            shape = RoundedCornerShape(0.dp)

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.9f)
                        .background(Color.Black)
                ) {
                    CurrenciesColumn(viewModel)
                }
                Card(modifier = Modifier
                    .padding(top = 15.dp)
                    .clickable { animationState.targetState = false },
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = MaterialTheme.colors.primary) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        text = "Cancel",
                        fontSize = 24.sp,
                        color = MaterialTheme.colors.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CurrenciesColumn(viewModel: HomeScreenViewModel) {

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        for (i in viewModel.listForChangeCurrency) {
            CurrencyChangeLabel(code = i[0]!!, title = i[1]!!, onClick = { TODO() })
        }
    }
}

@Composable
fun CurrencyInput(
    height: Float = 1f,
    state: MutableState<MInputState>,
    onClick: () -> Unit,
    onChangeCurrency: () -> Unit,
) {

    val interactionSource = MutableInteractionSource()

    Row(
        modifier = Modifier
            .fillMaxHeight(height)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.clickable(
                interactionSource = interactionSource, indication = null
            ) { onChangeCurrency() }) {
                Text(text = "DKK", fontSize = 22.sp) // TODO temporary
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "arrow down"
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
            ) //TODO temporary
            Text(text = "Danish krone", fontSize = 12.sp)
        }
    }
}


