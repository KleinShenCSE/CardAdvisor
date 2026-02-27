package com.example.cardadvisor.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    // Cards
    @Query("SELECT * FROM cards ORDER BY name ASC")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Long): Card?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: Card): Long

    @Update
    suspend fun updateCard(card: Card)

    @Delete
    suspend fun deleteCard(card: Card)

    // Reward rates
    @Query("SELECT * FROM reward_rates WHERE cardId = :cardId")
    suspend fun getRatesForCard(cardId: Long): List<RewardRate>

    @Query("SELECT * FROM reward_rates")
    suspend fun getAllRates(): List<RewardRate>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRate(rate: RewardRate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<RewardRate>)

    @Query("DELETE FROM reward_rates WHERE cardId = :cardId")
    suspend fun deleteRatesForCard(cardId: Long)
}
