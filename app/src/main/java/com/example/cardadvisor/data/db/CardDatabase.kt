package com.example.cardadvisor.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Card::class, RewardRate::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class CardDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao

    companion object {
        @Volatile private var INSTANCE: CardDatabase? = null

        fun getInstance(context: Context): CardDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CardDatabase::class.java,
                    "card_advisor.db"
                ).build().also { INSTANCE = it }
            }
    }
}
