package com.patmya.simplecurrencycalculator.homeScreen


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patmya.simplecurrencycalculator.room.InputD


@Composable
fun HomeScreen(
    inputData: List<InputD>,
    viewModel: HomeScreenViewModel = viewModel(),
    onUpdate: (List<InputD>) -> Unit,
) {

    val scaffoldState = rememberScaffoldState()

    if (!viewModel.dataLoaded.value) {
        viewModel.loadData(inputData)
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
                HomeScreenMainView(viewModel = viewModel) { list ->
                    onUpdate(list)
                }
            }
        }
    }
}

@Composable
fun HomeScreenMainView(viewModel: HomeScreenViewModel, onUpdate: (List<InputD>) -> Unit) {

    val showCurrencyChange = remember {
        mutableStateOf(false)
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                CurrencyInput(state = viewModel.firstInputState, onClick = {
                    viewModel.focusInput(0)
                }, onChangeCurrency = {
                    showCurrencyChange.value = true
                    viewModel.currenciesChangeMenuOpenedFrom.value = 0
                })
                CurrencyInput(state = viewModel.secondInputState, onClick = {
                    viewModel.focusInput(1)
                }, onChangeCurrency = {
                    showCurrencyChange.value = true
                    viewModel.currenciesChangeMenuOpenedFrom.value = 1
                })
                CurrencyInput(state = viewModel.thirdInputState, onClick = {
                    viewModel.focusInput(2)
                }, onChangeCurrency = {
                    showCurrencyChange.value = true
                    viewModel.currenciesChangeMenuOpenedFrom.value = 2
                })
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NumbersNest(onNumberInput = {
                    viewModel.changeInput(it) { list ->
                        onUpdate(list)
                    }
                },
                    onClear = { viewModel.clearInputs() },
                    onBackSpace = { viewModel.backSpace() })
            }
        }
        if (showCurrencyChange.value) {
            CurrencyChange(viewModel, onExit = {
                showCurrencyChange.value = false
                viewModel.currenciesChangeMenuOpenedFrom.value = 0
            }, onUpdate = { list ->
                onUpdate(list)
            })
        }
    }
}

@SuppressLint("UnrememberedMutableState", "MutableCollectionMutableState")
@Composable
fun CurrencyChange(
    viewModel: HomeScreenViewModel,
    onExit: () -> Unit,
    onUpdate: (List<InputD>) -> Unit,
) {

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

    val listOfCurrencies = mutableStateOf(viewModel.listForChangeCurrency)
    BackHandler {
        animationState.targetState = false
    }

    AnimatedVisibility(
        visibleState = animationState, enter = slideInVertically(), exit = shrinkVertically()
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 30.dp)
                .clickable(interactionSource = interactionSource, indication = null) {
                    animationState.targetState = false
                },
            backgroundColor = Color(0, 0, 0, 40),
            elevation = 0.dp,
            shape = RoundedCornerShape(0.dp)

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.88f)
                        .background(MaterialTheme.colors.background)
                ) {
                    SearchBar(onSearch = {
                        viewModel.searchCurrency(it) { listToUpdate ->
                            listOfCurrencies.value = listToUpdate
                        }
                    }, onClear = {
                        listOfCurrencies.value = viewModel.listForChangeCurrency
                    })
                    CurrenciesColumn(viewModel,
                        list = listOfCurrencies,
                        onClose = { animationState.targetState = false }) { list ->
                        onUpdate(list)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit, onClear: () -> Unit) {

    val context = LocalContext.current
    val text = "Provide at least three characters for currency code"
    val duration = Toast.LENGTH_SHORT
    val toast = Toast.makeText(context, text, duration)
    val primaryVariant = MaterialTheme.colors.primaryVariant
    val onPrimary = MaterialTheme.colors.onPrimary
    val backgroundColor = MaterialTheme.colors.background

    var textState by remember { mutableStateOf("") }
    OutlinedTextField(value = textState,
        onValueChange = {
            textState = it
            if (it.trim().length >= 3) {
                onSearch(it.trim().lowercase())
            }

        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        shape = RoundedCornerShape(5.dp),
        placeholder = { Text(text = "Currency code or name") },
        trailingIcon = {
            Icon(imageVector = Icons.Default.Close,
                contentDescription = "clear input",
                modifier = Modifier.clickable {
                    textState = ""
                    onClear()
                })
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            val trimmedInput = textState.trim()
            if (trimmedInput.length >= 3) onSearch(trimmedInput.lowercase()) else toast.show()
        }

        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = onPrimary,
            backgroundColor = backgroundColor,
            cursorColor = onPrimary,
            focusedBorderColor = onPrimary,
            unfocusedBorderColor = primaryVariant,
            placeholderColor = primaryVariant
        ))
}

@Composable
fun CurrenciesColumn(
    viewModel: HomeScreenViewModel,
    list: MutableState<MutableList<List<String?>>>,
    onClose: () -> Unit,
    onUpdate: (List<InputD>) -> Unit,
) {

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        for (i in list.value) {
            CurrencyChangeLabel(code = i[0]!!, title = i[1]!!, onClick = {
                viewModel.changeCurrency(i[0]!!) { list ->
                    onUpdate(list)
                }
                onClose()
            })
        }
    }
}


