package com.example.cardadvisor.domain

import com.example.cardadvisor.data.db.Card
import com.example.cardadvisor.data.db.RewardRate

data class Recommendation(
    val card: Card,
    val rate: RewardRate?,
    val effectiveCashbackPct: Double,
    val reason: String
)

object RecommendationEngine {

    /**
     * Returns cards ranked by effective cashback % for the given category.
     * Points are converted to effective cashback using the card's centsPerPoint value.
     */
    fun rank(
        category: Category,
        cards: List<Card>,
        ratesByCard: Map<Long, List<RewardRate>>
    ): List<Recommendation> {
        return cards.map { card ->
            val rates = ratesByCard[card.id] ?: emptyList()
            val matchedRate = rates.firstOrNull { it.category == category }
                ?: rates.firstOrNull { it.category == Category.OTHER }

            val effectivePct = if (matchedRate != null) {
                when (matchedRate.rewardType) {
                    RewardType.CASHBACK -> matchedRate.rate
                    RewardType.POINTS -> matchedRate.rate * (card.centsPerPoint / 100.0)
                }
            } else {
                0.0
            }

            val reason = buildReason(matchedRate, effectivePct, category)
            Recommendation(card, matchedRate, effectivePct, reason)
        }.sortedByDescending { it.effectiveCashbackPct }
    }

    private fun buildReason(
        rate: RewardRate?,
        effectivePct: Double,
        category: Category
    ): String {
        if (rate == null) return "No specific reward for ${category.displayName}"
        return when (rate.rewardType) {
            RewardType.CASHBACK -> "%.1f%% cashback on ${category.displayName}".format(rate.rate)
            RewardType.POINTS -> "%.1fx points → ~%.1f%% value on ${category.displayName}"
                .format(rate.rate, effectivePct)
        }
    }
}
