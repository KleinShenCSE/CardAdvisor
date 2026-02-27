package com.example.cardadvisor.ui.cards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardadvisor.data.db.Card
import com.example.cardadvisor.data.db.CardDatabase
import com.example.cardadvisor.data.db.RewardRate
import com.example.cardadvisor.data.repository.CardRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = CardRepository(
        CardDatabase.getInstance(app).cardDao()
    )

    val cards: StateFlow<List<Card>> = repository.allCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteCard(card: Card) {
        viewModelScope.launch { repository.deleteCard(card) }
    }
}
