package com.mexiti.costogasolina.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gasoline_transactions")
data class GasolineTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pricePerLiter: Double, //Precio por litro de Gasolina
    val liters: Double, //Litros de Gasolina
    val tip: Double, // Propina aplicada
    val date: String, //Fecha de la transacción
    val kilometers: Double // Kilometros recorridos totales por el automóvil
    )
