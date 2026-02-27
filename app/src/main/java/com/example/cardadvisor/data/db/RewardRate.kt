package com.example.cardadvisor.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.cardadvisor.domain.Category
import com.example.cardadvisor.domain.RewardType

@Entity(
    tableName = "reward_rates",
    foreignKeys = [ForeignKey(
        entity = Card::class,
        parentColumns = ["id"],
        childColumns = ["cardId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("cardId")]
)
data class RewardRate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val category: Category,
    val rewardType: RewardType,
    val rate: Double   // cashback % or points multiplier (e.g. 3.0 = 3x points or 3%)
)
