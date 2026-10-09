package com.mexiti.costogasolina.domain

import androidx.annotation.VisibleForTesting
import java.text.NumberFormat

@VisibleForTesting
internal fun calcularMonto(precio: Double, cantLitros: Double, darPropina: Boolean, propina:Double ): String{
    var monto = precio * cantLitros
    if ( darPropina){
        monto +=  propina
    }
    return NumberFormat.getCurrencyInstance().format(monto)

}
