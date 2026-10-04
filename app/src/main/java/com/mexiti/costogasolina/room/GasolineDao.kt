package com.mexiti.costogasolina.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mexiti.costogasolina.model.GasolineTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface GasolineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: GasolineTransaction)

    @Query("SELECT * FROM gasoline_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<GasolineTransaction>>

}