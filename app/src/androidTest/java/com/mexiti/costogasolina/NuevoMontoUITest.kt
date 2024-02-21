package com.mexiti.costogasolina

import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import java.text.NumberFormat

class NuevoMontoUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun CalcularMonto_21l_22_15(){
        composeTestRule.setContent {
            Surface {
                CostGasLayout()
            }
        }
        composeTestRule.onNodeWithText("Ingresa precio por litro de Gasolina")
            .performTextInput("24")
        composeTestRule.onNodeWithText("Litros")
            .performTextInput("10")

        val montoEsperado = NumberFormat.getCurrencyInstance().format(440)

        composeTestRule.onNodeWithText("Monto Total: $montoEsperado").
        assertExists("No se encontro ningún nodo con este texto")

    }
}