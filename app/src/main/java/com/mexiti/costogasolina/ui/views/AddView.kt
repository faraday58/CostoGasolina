package com.mexiti.costogasolina.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mexiti.costogasolina.R
import com.mexiti.costogasolina.domain.calcularMonto
import com.mexiti.costogasolina.ui.components.ActionElevatedButton
import com.mexiti.costogasolina.ui.components.EditNumberField
import com.mexiti.costogasolina.ui.theme.CostoGasolinaTheme
import com.mexiti.costogasolina.ui.components.AddTip
import com.mexiti.costogasolina.ui.components.FloatButton
import com.mexiti.costogasolina.ui.components.MainTitle


@Composable
fun AddView(navController: NavController, modifier: Modifier = Modifier){
    CostGasLayout(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostGasLayout(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    MainTitle(title = stringResource(id = R.string.calcular_monto))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatButton(onClick = {
                navController.popBackStack()
            })
        }
    ) {
        CostGasContent(
            onAgregarClicked = {
                navController.popBackStack()
            },
            modifier = Modifier.padding(it)
        )
    }

}



@Composable
fun CostGasContent(
    onAgregarClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var precioLitroEntrada by remember {
        mutableStateOf("")
    }
    var cantLitrosEntrada by remember {
        mutableStateOf("")
    }
    var propinaEntrada by remember {
        mutableStateOf("")
    }
    var darPropina by remember {
        mutableStateOf(false)
    }

    val precioLitro = precioLitroEntrada.toDoubleOrNull() ?: 0.0
    val cantLitros = cantLitrosEntrada.toDoubleOrNull() ?: 0.0
    val propina = propinaEntrada.toDoubleOrNull() ?: 0.0
    val total = calcularMonto(precioLitro,cantLitros, darPropina = darPropina, propina = propina)

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(15.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(15.dp)),
        verticalArrangement = Arrangement.Top
    ) {

        EditNumberField(
            label = R.string.ingresa_gasolina,
            leadingIcon = R.drawable.money_gas ,
            keyboardsOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            value = precioLitroEntrada,
            onValueChanged = {precioLitroEntrada = it}
        )
        EditNumberField(
            label = R.string.litros,
            leadingIcon = R.drawable.gasolina ,
            keyboardsOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ) ,
            value = cantLitrosEntrada,
            onValueChanged = {cantLitrosEntrada = it}
        )



        AddTip(darPropina = darPropina
            , onTipCheckedChange = {darPropina = it}

        )
        EditNumberField(
            label = R.string.propina,
            leadingIcon = R.drawable.outline_18_up_rating_24,
            keyboardsOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ) ,
            value = propinaEntrada,
            onValueChanged = {propinaEntrada = it}
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.monto_total,total),
            fontWeight = FontWeight.Black,
            fontSize = 30.sp
        )

    }

}







@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CostGasLayoutPreview() {
    CostoGasolinaTheme {
        CostGasContent (
            onAgregarClicked = {}
        )
    }
}