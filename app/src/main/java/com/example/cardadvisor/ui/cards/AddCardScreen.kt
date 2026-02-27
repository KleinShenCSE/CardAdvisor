package com.example.cardadvisor.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardadvisor.domain.Category
import com.example.cardadvisor.domain.RewardType
import com.example.cardadvisor.ui.components.CardPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreen(
    onBack: () -> Unit,
    cardId: Long? = null,
    viewModel: AddCardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(cardId) {
        if (cardId != null) viewModel.loadCard(cardId)
    }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    val title = if (state.editingCardId != null) "Edit Card" else "Add Card"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card info
            item {
                Text("Card Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Card Name") },
                    placeholder = { Text("e.g. Chase Sapphire Preferred") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.lastFour,
                        onValueChange = viewModel::onLastFourChange,
                        label = { Text("Last 4 Digits") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    // Network dropdown
                    NetworkDropdown(
                        selected = state.network,
                        onSelect = viewModel::onNetworkChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Card color picker
            item {
                Spacer(Modifier.height(4.dp))
                Text("Card Color", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            item {
                CardColorPicker(
                    selectedColor = state.color,
                    onColorSelected = viewModel::onColorChange
                )
            }
            // Card preview
            item {
                CardPreview(
                    name = state.name.ifBlank { "Card Name" },
                    lastFour = state.lastFour.ifBlank { "0000" },
                    network = state.network,
                    color = state.color
                )
            }

            // Reward type toggle
            item {
                Spacer(Modifier.height(4.dp))
                Text("Default Reward Type", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RewardType.entries.forEach { type ->
                        FilterChip(
                            selected = state.rewardType == type,
                            onClick = { viewModel.onRewardTypeChange(type) },
                            label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
            }

            // Points value (only shown if POINTS type selected)
            if (state.rewardType == RewardType.POINTS) {
                item {
                    OutlinedTextField(
                        value = state.centsPerPoint,
                        onValueChange = viewModel::onCentsPerPointChange,
                        label = { Text("Cents per Point") },
                        placeholder = { Text("e.g. 2.0 for Chase UR") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                }
            }

            // Reward rates per category
            item {
                Spacer(Modifier.height(4.dp))
                Text(
                    if (state.rewardType == RewardType.CASHBACK) "Cashback % by Category"
                    else "Points Multiplier by Category",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "Leave blank if not applicable",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(state.rates) { entry ->
                CategoryRateRow(
                    entry = entry,
                    rewardType = state.rewardType,
                    onRateChange = { viewModel.onRateChange(entry.category, it) }
                )
            }

            // Save button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.name.isNotBlank() && !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    } else {
                        Text("Save Card")
                    }
                }
            }
        }
    }
}

private val cardColorPalette = listOf(
    0xFF1A73E8L, // Google Blue
    0xFF0F9D58L, // Google Green
    0xFFDB4437L, // Google Red
    0xFFF4B400L, // Google Yellow
    0xFF673AB7L, // Deep Purple
    0xFF00BCD4L, // Cyan
    0xFFFF5722L, // Deep Orange
    0xFF37474FL, // Blue Grey
    0xFF000000L, // Black (Amex style)
    0xFF8D6E63L, // Brown (Rose Gold)
    0xFF546E7AL, // Slate
    0xFF1B5E20L, // Dark Green
)

@Composable
private fun CardColorPicker(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(cardColorPalette) { colorLong ->
            val color = Color(colorLong)
            val isSelected = selectedColor == colorLong
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        else Modifier.border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                    )
                    .clickable { onColorSelected(colorLong) },
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = if (color.luminance() > 0.4f) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun CategoryRateRow(
    entry: RateEntry,
    rewardType: RewardType,
    onRateChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "${entry.category.emoji} ${entry.category.displayName}",
            modifier = Modifier.weight(1f),
            fontSize = 14.sp
        )
        OutlinedTextField(
            value = entry.rate,
            onValueChange = onRateChange,
            modifier = Modifier.width(100.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            suffix = { Text(if (rewardType == RewardType.CASHBACK) "%" else "x") },
            placeholder = { Text("0") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkDropdown(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("Visa", "Mastercard", "Amex", "Discover")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Network") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = { onSelect(opt); expanded = false }
                )
            }
        }
    }
}
