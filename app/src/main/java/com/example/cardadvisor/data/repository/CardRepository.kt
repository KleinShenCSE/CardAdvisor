package com.example.cardadvisor.data.repository

import com.example.cardadvisor.data.db.Card
import com.example.cardadvisor.data.db.CardDao
import com.example.cardadvisor.data.db.RewardRate
import kotlinx.coroutines.flow.Flow

class CardRepository(private val dao: CardDao) {

    val allCards: Flow<List<Card>> = dao.getAllCards()

    suspend fun getCard(id: Long): Card? = dao.getCardById(id)

    suspend fun saveCard(card: Card, rates: List<RewardRate>): Long {
        val cardId = dao.insertCard(card)
        dao.deleteRatesForCard(cardId)
        dao.insertRates(rates.map { it.copy(cardId = cardId) })
        return cardId
    }

    suspend fun updateCard(card: Card, rates: List<RewardRate>) {
        dao.updateCard(card)
        dao.deleteRatesForCard(card.id)
        dao.insertRates(rates.map { it.copy(cardId = card.id) })
    }

    suspend fun deleteCard(card: Card) = dao.deleteCard(card)

    suspend fun getRatesForCard(cardId: Long): List<RewardRate> =
        dao.getRatesForCard(cardId)

    suspend fun getAllRatesMapped(): Map<Long, List<RewardRate>> =
        dao.getAllRates().groupBy { it.cardId }
}
