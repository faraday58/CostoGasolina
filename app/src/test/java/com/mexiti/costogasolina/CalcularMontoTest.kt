package com.mexiti.costogasolina

import org.junit.Test
import java.text.NumberFormat
import org.junit.Assert.assertEquals

class CalcularMontoTest {
    @Test
    fun calcularMonto20l_23_94(){

        val precio = 23.94
        val cantLitros = 20.0
        val darPropina = false
        val propina = 0.0

        val montoEsperado = NumberFormat.getCurrencyInstance().format(478.8)
        val montoActual = calcularMonto(precio,cantLitros, darPropina =darPropina , propina = propina )

        assertEquals(montoEsperado,montoActual)
    }
    @Test
    fun calcularMonto20l_23_94_Nopropina(){

        val precio = 23.94
        val cantLitros = 20.0
        val darPropina = false
        val propina = 10.0

        val montoEsperado = NumberFormat.getCurrencyInstance().format(478.8)
        val montoActual = calcularMonto(precio,cantLitros, darPropina =darPropina , propina = propina )

        assertEquals(montoEsperado,montoActual)
    }

    @Test
    fun calcularMonto20l_23_94_propina(){

        val precio = 23.94
        val cantLitros = 20.0
        val darPropina = true
        val propina = 10.0

        val montoEsperado = NumberFormat.getCurrencyInstance().format(488.8)
        val montoActual = calcularMonto(precio,cantLitros, darPropina =darPropina , propina = propina )

        assertEquals(montoEsperado,montoActual)
    }
}