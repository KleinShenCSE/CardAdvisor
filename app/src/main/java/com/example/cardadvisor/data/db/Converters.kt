package com.example.cardadvisor.data.db

import androidx.room.TypeConverter
import com.example.cardadvisor.domain.Category
import com.example.cardadvisor.domain.RewardType

class Converters {
    @TypeConverter fun fromCategory(v: Category) = v.name
    @TypeConverter fun toCategory(v: String) = Category.valueOf(v)

    @TypeConverter fun fromRewardType(v: RewardType) = v.name
    @TypeConverter fun toRewardType(v: String) = RewardType.valueOf(v)
}
