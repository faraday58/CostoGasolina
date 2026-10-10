package com.mexiti.costogasolina.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mexiti.costogasolina.R
import com.mexiti.costogasolina.domain.calcularMonto
import com.mexiti.costogasolina.ui.components.AddOptionalData
import com.mexiti.costogasolina.ui.components.EditNumberField
import com.mexiti.costogasolina.ui.components.FloatButton
import com.mexiti.costogasolina.ui.components.MainTitle
import com.mexiti.costogasolina.ui.theme.CostoGasolinaTheme

@Composable
fun AddView(navController: NavController, modifier: Modifier = Modifier) {
    CostGasLayout(navController = navController, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostGasLayout(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    MainTitle(title = stringResource(id = R.string.calcular_monto))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatButton(onClick = {
                navController.popBackStack()
            })
        }
    ) { innerPadding ->
        CostGasContent(
            onAgregarClicked = {
                navController.popBackStack()
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CostGasContent(
    onAgregarClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var precioLitroEntrada by remember { mutableStateOf("") }
    var cantLitrosEntrada by remember { mutableStateOf("") }
    var propinaEntrada by remember { mutableStateOf("") }
    var darPropina by remember { mutableStateOf(false) }
    var guardarKilometros by remember { mutableStateOf(false) }
    var kilometrsRecorridos by remember { mutableStateOf("") }

    val precioLitro = precioLitroEntrada.toDoubleOrNull() ?: 0.0
    val cantLitros = cantLitrosEntrada.toDoubleOrNull() ?: 0.0
    val propina = if (darPropina) propinaEntrada.toDoubleOrNull() ?: 0.0 else 0.0
    val total = calcularMonto(precioLitro, cantLitros, darPropina = darPropina, propina = propina)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tarjeta principal con el formulario
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditNumberField(
                    label = R.string.ingresa_gasolina,
                    leadingIcon = R.drawable.money_gas,
                    keyboardsOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    value = precioLitroEntrada,
                    onValueChanged = { precioLitroEntrada = it }
                )

                EditNumberField(
                    label = R.string.litros,
                    leadingIcon = R.drawable.gasolina,
                    keyboardsOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = if (darPropina) ImeAction.Next else ImeAction.Done
                    ),
                    value = cantLitrosEntrada,
                    onValueChanged = { cantLitrosEntrada = it }
                )

                AddOptionalData(
                    flagData = darPropina,
                    onflagCheckedChange = { darPropina = it }
                )

                AnimatedVisibility(
                    visible = darPropina,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    EditNumberField(
                        label = R.string.propina,
                        leadingIcon = R.drawable.outline_18_up_rating_24,
                        keyboardsOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        value = propinaEntrada,
                        onValueChanged = { propinaEntrada = it }
                    )
                }
                AddOptionalData(
                    flagData = guardarKilometros,
                    onflagCheckedChange = { guardarKilometros = it },
                    question = stringResource(id = R.string.guardar_kilometraje)
                )

                AnimatedVisibility(
                    visible = guardarKilometros,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    EditNumberField(
                        label = R.string.kilometros,
                        leadingIcon = R.drawable.car__kilometer,
                        keyboardsOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        value = kilometrsRecorridos,
                        onValueChanged = { kilometrsRecorridos = it }
                    )
                }
            }
        }

        // Tarjeta destacada para el Total
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.monto_total, total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CostGasLayoutPreview() {
    CostoGasolinaTheme {
        CostGasContent(
            onAgregarClicked = {}
        )
    }
}