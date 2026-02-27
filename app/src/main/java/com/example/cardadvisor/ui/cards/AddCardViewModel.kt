package com.example.cardadvisor.ui.cards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardadvisor.data.db.Card
import com.example.cardadvisor.data.db.CardDatabase
import com.example.cardadvisor.data.db.RewardRate
import com.example.cardadvisor.data.repository.CardRepository
import com.example.cardadvisor.domain.Category
import com.example.cardadvisor.domain.RewardType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RateEntry(
    val category: Category,
    val rewardType: RewardType = RewardType.CASHBACK,
    val rate: String = ""  // user input string, parsed on save
)

data class AddCardUiState(
    val editingCardId: Long? = null,   // null = create, non-null = edit
    val name: String = "",
    val lastFour: String = "",
    val network: String = "Visa",
    val color: Long = 0xFF1A73E8,
    val centsPerPoint: String = "1.0",
    val rewardType: RewardType = RewardType.CASHBACK,
    val rates: List<RateEntry> = Category.entries.map { RateEntry(it) },
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

class AddCardViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = CardRepository(
        CardDatabase.getInstance(app).cardDao()
    )

    private val _state = MutableStateFlow(AddCardUiState())
    val state: StateFlow<AddCardUiState> = _state

    fun loadCard(cardId: Long) {
        viewModelScope.launch {
            val card = repository.getCard(cardId) ?: return@launch
            val existingRates = repository.getRatesForCard(cardId)
            val rateMap = existingRates.associateBy { it.category }
            // Detect dominant reward type from existing rates
            val dominantType = existingRates.groupingBy { it.rewardType }
                .eachCount().maxByOrNull { it.value }?.key ?: RewardType.CASHBACK

            update {
                copy(
                    editingCardId = cardId,
                    name = card.name,
                    lastFour = card.lastFour,
                    network = card.network,
                    color = card.color,
                    centsPerPoint = card.centsPerPoint.toString(),
                    rewardType = dominantType,
                    rates = Category.entries.map { cat ->
                        val r = rateMap[cat]
                        RateEntry(
                            category = cat,
                            rewardType = r?.rewardType ?: dominantType,
                            rate = r?.rate?.toString() ?: ""
                        )
                    }
                )
            }
        }
    }

    fun onNameChange(v: String) = update { copy(name = v) }
    fun onLastFourChange(v: String) = update { copy(lastFour = v.take(4)) }
    fun onNetworkChange(v: String) = update { copy(network = v) }
    fun onColorChange(v: Long) = update { copy(color = v) }
    fun onCentsPerPointChange(v: String) = update { copy(centsPerPoint = v) }
    fun onRewardTypeChange(v: RewardType) = update { copy(rewardType = v) }

    fun onRateChange(category: Category, rate: String) = update {
        copy(rates = rates.map {
            if (it.category == category) it.copy(rate = rate, rewardType = rewardType) else it
        })
    }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) return
        update { copy(isSaving = true) }

        viewModelScope.launch {
            val rates = s.rates.mapNotNull { entry ->
                val rate = entry.rate.toDoubleOrNull() ?: return@mapNotNull null
                if (rate <= 0.0) return@mapNotNull null
                RewardRate(
                    cardId = 0,
                    category = entry.category,
                    rewardType = entry.rewardType,
                    rate = rate
                )
            }

            if (s.editingCardId != null) {
                val card = Card(
                    id = s.editingCardId,
                    name = s.name.trim(),
                    lastFour = s.lastFour,
                    network = s.network,
                    color = s.color,
                    centsPerPoint = s.centsPerPoint.toDoubleOrNull() ?: 1.0
                )
                repository.updateCard(card, rates)
            } else {
                val card = Card(
                    name = s.name.trim(),
                    lastFour = s.lastFour,
                    network = s.network,
                    color = s.color,
                    centsPerPoint = s.centsPerPoint.toDoubleOrNull() ?: 1.0
                )
                repository.saveCard(card, rates)
            }
            update { copy(isSaving = false, saved = true) }
        }
    }

    private fun update(block: AddCardUiState.() -> AddCardUiState) {
        _state.value = _state.value.block()
    }
}
