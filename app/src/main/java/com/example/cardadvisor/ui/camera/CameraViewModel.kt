package com.example.cardadvisor.ui.camera

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardadvisor.api.GeminiService
import com.example.cardadvisor.data.db.CardDatabase
import com.example.cardadvisor.data.repository.CardRepository
import com.example.cardadvisor.domain.Category
import com.example.cardadvisor.domain.RecommendationEngine
import com.example.cardadvisor.domain.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

sealed class CameraUiState {
    object Idle : CameraUiState()
    object Analyzing : CameraUiState()
    data class Success(
        val category: Category,
        val description: String,
        val recommendations: List<Recommendation>
    ) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

class CameraViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = CardRepository(
        CardDatabase.getInstance(app).cardDao()
    )
    private val geminiService = GeminiService()

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState

    fun analyzePhoto(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = CameraUiState.Analyzing

            geminiService.identifyPurchase(imageFile).fold(
                onSuccess = { geminiResult ->
                    val cards = repository.allCards.first()
                    val ratesMap = repository.getAllRatesMapped()
                    val ranked = RecommendationEngine.rank(geminiResult.category, cards, ratesMap)
                    _uiState.value = CameraUiState.Success(
                        category = geminiResult.category,
                        description = geminiResult.description,
                        recommendations = ranked
                    )
                },
                onFailure = { e ->
                    _uiState.value = CameraUiState.Error(e.message ?: "Unknown error")
                }
            )
        }
    }

    fun reset() {
        _uiState.value = CameraUiState.Idle
    }
}
