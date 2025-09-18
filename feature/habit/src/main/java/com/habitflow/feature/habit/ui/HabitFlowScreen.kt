package com.habitflow.feature.habit.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.habitflow.core.designsystem.DesignTokens as HabitFlowDesign
import com.habitflow.core.ui.DateUtils
import com.habitflow.domain.model.DayCount
import com.habitflow.domain.model.Habit
import com.habitflow.domain.model.ReminderType
import com.habitflow.domain.model.WeekProgress
import com.habitflow.feature.habit.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HabitFlowScreen(
    habits: List<Habit>,
    todayKey: String,
    remindersEnabled: Boolean,
    dailyCounts: List<DayCount>,
    weeklyProgress: List<WeekProgress>,
    onToggleHabit: (String, Boolean) -> Unit,
    onDeleteHabit: (String) -> Unit,
    onToggleReminders: (Boolean) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr")) }
    val dayFormatter = remember { DateTimeFormatter.ofPattern("EEE", Locale("tr")) }
    val chipDays = remember(todayKey) { DateUtils.lastDays(5) }
    val completedToday = habits.count { it.history[todayKey] == true }
    val completionRate = if (habits.isEmpty()) 0f else completedToday / habits.size.toFloat()
    val weeklyAverage = weeklyProgress.lastOrNull()?.percentage ?: 0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HabitFlowDesign.backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = HabitFlowDesign.screenPadding, vertical = HabitFlowDesign.screenPadding),
            verticalArrangement = Arrangement.spacedBy(HabitFlowDesign.sectionSpacing)
        ) {
            HeroSection(
                dateText = dateFormatter.format(today),
                completed = completedToday,
                total = habits.size,
                chipDays = chipDays,
                today = today,
                dayFormatter = dayFormatter,
                onProfileClick = onProfileClick
            )

            ReminderToggleCard(enabled = remindersEnabled, onToggle = onToggleReminders)

            QuickStatsRow(
                completionRate = completionRate,
                weeklyPercentage = weeklyAverage
            )

            HabitListSection(
                habits = habits,
                todayKey = todayKey,
                onToggleHabit = onToggleHabit,
                onDeleteHabit = onDeleteHabit
            )

            ChartSection(
                dailyData = dailyCounts,
                weeklyData = weeklyProgress
            )
        }
    }
}

@Composable
private fun HeroSection(
    dateText: String,
    completed: Int,
    total: Int,
    chipDays: List<LocalDate>,
    today: LocalDate,
    dayFormatter: DateTimeFormatter,
    onProfileClick: () -> Unit
) {
    Card(
        shape = HabitFlowDesign.largeCardShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(HabitFlowDesign.heroGradient)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(id = R.string.hero_greeting),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(id = R.string.hero_today_label, dateText),
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.85f))
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(id = R.string.profile_action_label),
                            tint = Color.White
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    chipDays.forEach { date ->
                        val isToday = date == today
                        val backgroundColor = if (isToday) Color.White else Color.White.copy(alpha = 0.2f)
                        val textColor = if (isToday) HabitFlowDesign.badgeText else Color.White
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(HabitFlowDesign.capsuleShape)
                                .background(backgroundColor)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = dayFormatter.format(date).uppercase(Locale("tr")),
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = if (total == 0) {
                        "Bugün henüz alışkanlık eklemedin."
                    } else {
                        "$completed / $total alışkanlık tamamlandı"
                    },
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun ReminderToggleCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        shape = HabitFlowDesign.cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(HabitFlowDesign.progressFill.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = HabitFlowDesign.progressFill
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (enabled) "Hatırlatmalar açık" else "Hatırlatmalar kapalı",
                        style = MaterialTheme.typography.titleMedium,
                        color = HabitFlowDesign.textPrimary
                    )
                    Text(
                        text = "Günlük hatırlatma bildirimlerini yönet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HabitFlowDesign.textSecondary
                    )
                }
            }
            Switch(checked = enabled, onCheckedChange = onToggle)
        }
    }
}

@Composable
private fun QuickStatsRow(completionRate: Float, weeklyPercentage: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        StatCard(
            title = stringResource(id = R.string.daily_progress_title),
            value = "${(completionRate * 100).roundToInt()}%",
            gradient = HabitFlowDesign.metricBlueGradient,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = stringResource(id = R.string.weekly_progress_title),
            value = "$weeklyPercentage%",
            gradient = HabitFlowDesign.metricPinkGradient,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    gradient: androidx.compose.ui.graphics.Brush,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = title, color = Color.White)
                Text(text = value, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HabitListSection(
    habits: List<Habit>,
    todayKey: String,
    onToggleHabit: (String, Boolean) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(id = R.string.habits_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = HabitFlowDesign.textPrimary
        )
        habits.forEach { habit -> HabitCard(habit, todayKey, onToggleHabit, onDeleteHabit) }
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    todayKey: String,
    onToggleHabit: (String, Boolean) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    val todayDone = habit.history[todayKey] == true
    Card(
        shape = HabitFlowDesign.cardShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = habit.name, style = MaterialTheme.typography.titleMedium, color = HabitFlowDesign.textPrimary)
                    if (todayDone) {
                        Text(text = stringResource(id = R.string.habit_completed_badge), style = MaterialTheme.typography.bodySmall, color = HabitFlowDesign.textSecondary)
                    }
                }
                when (habit.reminderType) {
                    ReminderType.DAILY -> Text(text = habit.reminderTime ?: "", color = HabitFlowDesign.textSecondary)
                    ReminderType.WEEKLY -> Text(text = habit.reminderTime ?: "", color = HabitFlowDesign.textSecondary)
                    ReminderType.CUSTOM -> Text(text = runCatching { LocalDateTime.parse(habit.customDateTime ?: "").format(DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale("tr"))) }.getOrDefault(""), color = HabitFlowDesign.textSecondary)
                }
            }

            HabitActions(habitId = habit.id, todayDone = todayDone, onToggleHabit = onToggleHabit, onDeleteHabit = onDeleteHabit)
        }
    }
}

@Composable
private fun HabitActions(
    habitId: String,
    todayDone: Boolean,
    onToggleHabit: (String, Boolean) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        FilledTonalButton(
            onClick = { onToggleHabit(habitId, true) },
            enabled = !todayDone,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = HabitFlowDesign.badgeText,
                contentColor = Color.White,
                disabledContainerColor = HabitFlowDesign.badgeBackground,
                disabledContentColor = HabitFlowDesign.textSecondary
            )
        ) { Text(text = if (todayDone) "Tamamlandı" else stringResource(id = R.string.habit_complete)) }
        OutlinedButton(
            onClick = { onToggleHabit(habitId, false) },
            enabled = todayDone,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = HabitFlowDesign.badgeText)
        ) { Text(text = stringResource(id = R.string.habit_reset)) }
        TextButton(onClick = { onDeleteHabit(habitId) }) { Text(text = stringResource(id = R.string.habit_delete), color = Color(0xFFEF4444)) }
    }
}

@Composable
private fun ChartSection(dailyData: List<DayCount>, weeklyData: List<WeekProgress>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = stringResource(id = R.string.daily_progress_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = HabitFlowDesign.textPrimary)
        GradientChartCard { if (dailyData.all { it.count == 0 }) Text(text = stringResource(id = R.string.progress_empty_state), color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) else DailyProgressChart(data = dailyData) }
        Text(text = stringResource(id = R.string.weekly_progress_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = HabitFlowDesign.textPrimary)
        GradientChartCard { if (weeklyData.all { it.percentage == 0 }) Text(text = stringResource(id = R.string.progress_empty_state), color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) else WeeklyProgressChart(data = weeklyData) }
    }
}

@Composable
private fun GradientChartCard(content: @Composable () -> Unit) {
    Card(shape = HabitFlowDesign.largeCardShape, colors = CardDefaults.cardColors(containerColor = Color.Transparent), elevation = CardDefaults.cardElevation(4.dp)) {
        Box(modifier = Modifier.background(HabitFlowDesign.chartGradient).fillMaxWidth().padding(20.dp)) { content() }
    }
}

// Stubs for charts (implementation omitted here)
@Composable private fun DailyProgressChart(data: List<DayCount>) {}
@Composable private fun WeeklyProgressChart(data: List<WeekProgress>) {}
