package com.mexiti.costogasolina.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mexiti.costogasolina.model.GasolineTransaction

@Database(entities = [GasolineTransaction::class], version = 1, exportSchema = false)
abstract class GasolineDatabase: RoomDatabase() {
    abstract fun gasolineDao(): GasolineDao

    companion object {
        @Volatile
        private var INSTANCE: GasolineDatabase? = null

        fun getDatabase(context: Context): GasolineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GasolineDatabase::class.java,
                    "gasoline_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}