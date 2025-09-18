package com.habitflow.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitflow.feature.onboarding.TimeUtils
import com.habitflow.feature.onboarding.ui.OnboardingPrimaryAction
import com.habitflow.feature.onboarding.ui.OnboardingScreenScaffold
import com.habitflow.feature.onboarding.ui.OnboardingSecondaryAction
import com.habitflow.feature.onboarding.ui.OnboardingTheme

private const val TOTAL_STEPS = 9

@Composable
fun WelcomeScreen(
    onNext: () -> Unit,
    onLogin: () -> Unit
) {
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 1,
            totalSteps = TOTAL_STEPS,
            title = "Hoş geldin",
            subtitle = "Küçük alışkanlıklar büyük değişimlere yol açar. Gel, gününü birlikte kurgulayalım.",
            primaryAction = OnboardingPrimaryAction("Planımı Oluştur", onNext),
            secondaryAction = OnboardingSecondaryAction("Zaten hesabım var", onLogin)
        ) {
            HeroIllustration()
            Text(
                text = "Sana özel rutinlerle bugün başlayalım.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoalsScreen(
    selected: List<String>,
    other: String,
    onToggle: (String) -> Unit,
    onOtherChanged: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val chips = remember { listOf("Sağlıklı yaşam", "Daha iyi odak", "Kilo kontrolü") }
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 2,
            totalSteps = TOTAL_STEPS,
            title = "Hedeflerini seç",
            subtitle = "HabitFlow’dan ne bekliyorsun? Birden fazla seçim yapabilirsin.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                chips.forEach { goal ->
                    val selectedState = selected.contains(goal)
                    val leadingIcon: (@Composable () -> Unit)? = if (selectedState) {
                        { Icon(imageVector = Icons.Filled.Check, contentDescription = null) }
                    } else null
                    FilterChip(
                        selected = selectedState,
                        onClick = { onToggle(goal) },
                        label = { Text(goal) },
                        leadingIcon = leadingIcon,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )
                    )
                }
            }
            TextField(
                value = other,
                onValueChange = onOtherChanged,
                label = { Text("Diğer hedefin?") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun SleepScreen(
    state: OnboardingState,
    onWakeChange: (String) -> Unit,
    onSleepChange: (String) -> Unit,
    onNightShiftToggle: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val wakeHour = remember(state.wake) { parseHour(state.wake) }
    val sleepHour = remember(state.sleep) { parseHour(state.sleep) }
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 3,
            totalSteps = TOTAL_STEPS,
            title = "Uyku düzeni",
            subtitle = "Uyanış ve uyku saatlerini paylaş, gün planını ona göre kuralım.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            TimeSlider(
                title = "Genelde kaçta uyanırsın?",
                valueText = state.wake,
                sliderValue = wakeHour,
                onValueChange = { onWakeChange(formatHour(it.toInt())) }
            )
            TimeSlider(
                title = "Genelde kaçta uyursun?",
                valueText = state.sleep,
                sliderValue = sleepHour,
                onValueChange = { onSleepChange(formatHour(it.toInt())) }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Gece modu", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (state.night) "Gece vardiyası için plan yapıyoruz" else "Varsayılan gündüz programı",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.night,
                    onCheckedChange = onNightShiftToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun RoutineBlocksScreen(
    blocks: List<com.habitflow.domain.model.TimeBlock>,
    onAdd: (com.habitflow.domain.model.TimeBlock) -> Unit,
    onAddAuto: () -> Unit,
    onRemove: (Int) -> Unit,
    onStartMinus: (Int) -> Unit,
    onStartPlus: (Int) -> Unit,
    onEndMinus: (Int) -> Unit,
    onEndPlus: (Int) -> Unit,
    onLabelChange: (Int, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 4,
            totalSteps = TOTAL_STEPS,
            title = "Gün içi bloklar",
            subtitle = "İş/okul veya odak bloklarını ekle. Saatleri gerektiğinde ayarlayabilirsin.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                blocks.forEachIndexed { index, block ->
                    BlockCard(
                        block = block,
                        onLabelChange = { onLabelChange(index, it) },
                        onStartMinus = { onStartMinus(index) },
                        onStartPlus = { onStartPlus(index) },
                        onEndMinus = { onEndMinus(index) },
                        onEndPlus = { onEndPlus(index) },
                        onRemove = { onRemove(index) }
                    )
                }
                AssistChip(
                    onClick = { onAddAuto() },
                    label = { Text("Akıllı blok ekle") },
                    leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                )
            }
        }
    }
}

@Composable
fun MealsScreen(
    meals: List<com.habitflow.domain.model.MealWindow>,
    onSet: (List<com.habitflow.domain.model.MealWindow>) -> Unit,
    onUpdate: (Int, String) -> Unit,
    onAddSnack: () -> Unit,
    onRemoveAt: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 5,
            totalSteps = TOTAL_STEPS,
            title = "Öğün zamanları",
            subtitle = "Ana öğünleri ve ara öğünleri planla. Zamanları 30 dk adımlarla ayarlayabilirsin.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                meals.forEachIndexed { index, meal ->
                    MealCard(
                        meal = meal,
                        onMinus = { onUpdate(index, TimeUtils.plusMinutes(meal.time, -30)) },
                        onPlus = { onUpdate(index, TimeUtils.plusMinutes(meal.time, +30)) },
                        onRemove = { onRemoveAt(index) }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = {
                            onSet(
                                listOf(
                                    com.habitflow.domain.model.MealWindow("Kahvaltı", "08:00"),
                                    com.habitflow.domain.model.MealWindow("Öğle", "12:30"),
                                    com.habitflow.domain.model.MealWindow("Akşam", "19:30")
                                )
                            )
                        },
                        label = { Text("Varsayılanları uygula") }
                    )
                    AssistChip(
                        onClick = onAddSnack,
                        label = { Text("Ara öğün ekle") },
                        leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null) }
                    )
                }
                if (meals.size < 2) {
                    Text(
                        text = "En az iki öğün zamanı belirlemelisin.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ExerciseScreen(
    slots: List<com.habitflow.domain.model.ExerciseSlot>,
    onToggle: (com.habitflow.domain.model.ExerciseSlot) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val options = remember {
        listOf(
            com.habitflow.domain.model.ExerciseSlot("Sabah", "07:00", "09:00"),
            com.habitflow.domain.model.ExerciseSlot("Öğle", "12:00", "13:00"),
            com.habitflow.domain.model.ExerciseSlot("Akşam", "19:00", "21:00")
        )
    }
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 6,
            totalSteps = TOTAL_STEPS,
            title = "Egzersiz tercihleri",
            subtitle = "Hangi saat aralıklarında egzersiz yapmaya uygunsun? Birden fazla seçenek olabilir.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                options.forEach { option ->
                    val selected = slots.any { it.label == option.label }
                    FilterChip(
                        selected = selected,
                        onClick = { onToggle(option) },
                        label = { Text(option.label) },
                        leadingIcon = {
                            Icon(Icons.Filled.FitnessCenter, contentDescription = null)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun HydrationScreen(
    value: Int,
    onChange: (Int) -> Unit,
    heightText: String,
    weightText: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    suggestion: Int?,
    onApplySuggestion: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 7,
            totalSteps = TOTAL_STEPS,
            title = "Su hedefi",
            subtitle = "Günlük hedefini belirle. Boy/kilo girerek kişiselleştirilmiş öneri alabilirsin.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            HydrationCard(
                value = value,
                onChange = onChange,
                heightText = heightText,
                weightText = weightText,
                onHeightChange = onHeightChange,
                onWeightChange = onWeightChange,
                suggestion = suggestion,
                onApplySuggestion = onApplySuggestion
            )
        }
    }
}

@Composable
fun QuietHoursScreen(
    start: String,
    end: String,
    onStartChange: (String) -> Unit,
    onEndChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val startValue = remember(start) { parseHour(start) }
    val endValue = remember(end) { parseHour(end) }
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 8,
            totalSteps = TOTAL_STEPS,
            title = "Sessiz saatler",
            subtitle = "Bildirimleri rahatsız etmediğimiz saat aralığını belirleyelim.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Devam", onNext),
            secondaryAction = OnboardingSecondaryAction(label = "Geri", onClick = onBack)
        ) {
            TimeSlider(
                title = "Başlangıç",
                valueText = start,
                sliderValue = startValue,
                onValueChange = { onStartChange(formatHour(it.toInt())) }
            )
            TimeSlider(
                title = "Bitiş",
                valueText = end,
                sliderValue = endValue,
                onValueChange = { onEndChange(formatHour(it.toInt())) }
            )
        }
    }
}

@Composable
fun PreviewScreen(
    state: OnboardingState,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    val timeline = remember(state) {
        buildList {
            add(TimelineItem(state.wake, "Uyanış", TimelineType.WAKE))
            addAll(state.mealWindows.map { TimelineItem(it.time, it.mealType, TimelineType.MEAL) })
            addAll(state.exerciseSlots.map { TimelineItem(it.start, "Egzersiz (${it.label})", TimelineType.EXERCISE) })
        }.sortedBy { TimeUtils.parseHm(it.time) }
    }
    OnboardingTheme {
        OnboardingScreenScaffold(
            step = 9,
            totalSteps = TOTAL_STEPS,
            title = "Günün hazır",
            subtitle = "Planını gözden geçir ve dilediğin zaman geri dönüp düzenle.",
            onBack = onBack,
            primaryAction = OnboardingPrimaryAction("Onayla ve Devam Et", onConfirm),
            secondaryAction = OnboardingSecondaryAction("Geri") { onBack() }
        ) {
            Text(
                text = "Su hedefi: ${state.hydrationGoal} ml",
                style = MaterialTheme.typography.titleMedium
            )
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                timeline.forEach { item ->
                    TimelineRow(item)
                }
            }
        }
    }
}

// region Reusable blocks

@Composable
private fun HeroIllustration() {
    val colors = listOf(Color(0xFFB7E0FF), Color(0xFFC6F6E7))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color(0xFF1C9E92),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kişisel rutinini oluştur",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TimeSlider(
    title: String,
    valueText: String,
    sliderValue: Int,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(valueText, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { onValueChange(it) },
            valueRange = 0f..23f,
            steps = 23
        )
    }
}

@Composable
private fun BlockCard(
    block: com.habitflow.domain.model.TimeBlock,
    onLabelChange: (String) -> Unit,
    onStartMinus: () -> Unit,
    onStartPlus: () -> Unit,
    onEndMinus: () -> Unit,
    onEndPlus: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TextField(
                value = block.label,
                onValueChange = onLabelChange,
                label = { Text("Blok adı") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                TimeStepper("Başlangıç", block.start, onStartMinus, onStartPlus)
                TimeStepper("Bitiş", block.end, onEndMinus, onEndPlus)
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Bloku sil",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun MealCard(
    meal: com.habitflow.domain.model.MealWindow,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(meal.mealType, style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(meal.time, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onMinus) { Icon(Icons.Filled.Remove, contentDescription = "30 dk geri al") }
                    IconButton(onClick = onPlus) { Icon(Icons.Filled.Add, contentDescription = "30 dk ileri al") }
                    IconButton(onClick = onRemove) { Icon(Icons.Filled.Remove, contentDescription = "Öğünü sil", tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
private fun TimeStepper(
    label: String,
    time: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            IconButton(onClick = onMinus) { Icon(Icons.Filled.Remove, contentDescription = "Zamanı azalt") }
            Text(time, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onPlus) { Icon(Icons.Filled.Add, contentDescription = "Zamanı arttır") }
        }
    }
}

@Composable
private fun HydrationCard(
    value: Int,
    onChange: (Int) -> Unit,
    heightText: String,
    weightText: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    suggestion: Int?,
    onApplySuggestion: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Günlük hedef: ${value} ml", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = value.toFloat(),
                onValueChange = { onChange(it.toInt()) },
                valueRange = 1000f..5000f,
                steps = ((5000 - 1000) / 250) - 1
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = heightText,
                    onValueChange = onHeightChange,
                    label = { Text("Boy (cm)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                TextField(
                    value = weightText,
                    onValueChange = onWeightChange,
                    label = { Text("Kilo (kg)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            suggestion?.let {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Önerilen: ${it} ml", style = MaterialTheme.typography.bodyMedium)
                    AssistChip(onClick = onApplySuggestion, label = { Text("Uygula") })
                }
            }
        }
    }
}

private data class TimelineItem(val time: String, val label: String, val type: TimelineType)

private enum class TimelineType { WAKE, MEAL, EXERCISE }

@Composable
private fun TimelineRow(item: TimelineItem) {
    val (icon, tint) = when (item.type) {
        TimelineType.WAKE -> Icons.Default.AccessTime to Color(0xFF3F51B5)
        TimelineType.MEAL -> Icons.Default.Restaurant to Color(0xFF4CAF50)
        TimelineType.EXERCISE -> Icons.Default.FitnessCenter to Color(0xFFE91E63)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Column {
            Text(item.time, style = MaterialTheme.typography.titleMedium, color = tint)
            Text(item.label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun parseHour(value: String): Int = value.substringBefore(':').toIntOrNull() ?: 0

private fun formatHour(hour: Int): String = "%02d:00".format(hour.coerceIn(0, 23))

// endregion
