package com.habitflow.feature.habit.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitflow.core.designsystem.DesignTokens as HabitFlowDesign
import com.habitflow.domain.model.ReminderType
import com.habitflow.feature.habit.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onSave: (name: String, reminderType: ReminderType, reminderTime: String?, weeklyDay: Int?, customDateTime: String?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val name = remember { mutableStateOf("") }
    val nameError = remember { mutableStateOf(false) }
    val reminderType = remember { mutableStateOf(ReminderType.DAILY) }
    val dailyTime = remember { mutableStateOf(LocalTime.of(9, 0)) }
    val weeklyDay = remember { mutableStateOf(DayOfWeek.MONDAY) }
    val weeklyTime = remember { mutableStateOf(LocalTime.of(9, 0)) }
    val customDate = remember { mutableStateOf(LocalDate.now()) }
    val customTime = remember { mutableStateOf(LocalTime.of(9, 0)) }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val customFormatter = remember { DateTimeFormatter.ofPattern("d MMM yyyy HH:mm", Locale("tr")) }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.add_habit_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = stringResource(android.R.string.cancel))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ElevatedCard(
                shape = HabitFlowDesign.cardShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(text = stringResource(id = R.string.habit_name_label), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = HabitFlowDesign.textPrimary)
                    OutlinedTextField(
                        value = name.value,
                        onValueChange = { name.value = it; if (nameError.value && it.isNotBlank()) nameError.value = false },
                        placeholder = { Text(text = stringResource(id = R.string.habit_input_hint)) },
                        isError = nameError.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (nameError.value) {
                        Text(text = stringResource(id = R.string.habit_name_error), color = Color(0xFFB91C1C), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            ReminderTypeSection(reminderType.value) { reminderType.value = it }

            when (reminderType.value) {
                ReminderType.DAILY -> DailySelectionCard(time = dailyTime.value, onSelectTime = {
                    TimePickerDialog(context, { _, hour, minute -> dailyTime.value = LocalTime.of(hour, minute) }, dailyTime.value.hour, dailyTime.value.minute, true).show()
                }, formatted = timeFormatter.format(dailyTime.value))
                ReminderType.WEEKLY -> WeeklySelectionCard(day = weeklyDay.value, time = weeklyTime.value, onSelectDay = { weeklyDay.value = it }, onSelectTime = {
                    TimePickerDialog(context, { _, hour, minute -> weeklyTime.value = LocalTime.of(hour, minute) }, weeklyTime.value.hour, weeklyTime.value.minute, true).show()
                })
                ReminderType.CUSTOM -> CustomSelectionCard(date = customDate.value, time = customTime.value, onSelectDate = {
                    val localDate = customDate.value
                    DatePickerDialog(context, { _, year, month, dayOfMonth -> customDate.value = LocalDate.of(year, month + 1, dayOfMonth) }, localDate.year, localDate.monthValue - 1, localDate.dayOfMonth).show()
                }, onSelectTime = {
                    TimePickerDialog(context, { _, hour, minute -> customTime.value = LocalTime.of(hour, minute) }, customTime.value.hour, customTime.value.minute, true).show()
                }, formatted = customFormatter.format(LocalDateTime.of(customDate.value, customTime.value)))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(text = stringResource(android.R.string.cancel)) }
                val isValid = name.value.isNotBlank()
                FilledTonalButton(
                    onClick = {
                        val trimmed = name.value.trim()
                        if (trimmed.isEmpty()) { nameError.value = true; return@FilledTonalButton }
                        val (reminderTimeValue, weeklyDayValue, customDateTimeValue) = when (reminderType.value) {
                            ReminderType.DAILY -> Triple(dailyTime.value.format(DateTimeFormatter.ISO_LOCAL_TIME), null, null)
                            ReminderType.WEEKLY -> Triple(weeklyTime.value.format(DateTimeFormatter.ISO_LOCAL_TIME), weeklyDay.value.value, null)
                            ReminderType.CUSTOM -> Triple(null, null, LocalDateTime.of(customDate.value, customTime.value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        }
                        onSave(trimmed, reminderType.value, reminderTimeValue, weeklyDayValue, customDateTimeValue)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isValid,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = HabitFlowDesign.progressFill, contentColor = Color.White),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) { Text(text = stringResource(id = R.string.save), fontSize = 16.sp) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReminderTypeSection(reminderType: ReminderType, onTypeSelected: (ReminderType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = stringResource(id = R.string.reminder_type_label), style = MaterialTheme.typography.titleMedium, color = HabitFlowDesign.textPrimary)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(selected = reminderType == ReminderType.DAILY, onClick = { onTypeSelected(ReminderType.DAILY) }, label = { Text(stringResource(id = R.string.reminder_type_daily)) })
            FilterChip(selected = reminderType == ReminderType.WEEKLY, onClick = { onTypeSelected(ReminderType.WEEKLY) }, label = { Text(stringResource(id = R.string.reminder_type_weekly)) })
            FilterChip(selected = reminderType == ReminderType.CUSTOM, onClick = { onTypeSelected(ReminderType.CUSTOM) }, label = { Text(stringResource(id = R.string.reminder_type_custom)) })
        }
    }
}

@Composable
private fun DailySelectionCard(time: LocalTime, onSelectTime: () -> Unit, formatted: String) {
    ElevatedCard(shape = HabitFlowDesign.cardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(id = R.string.daily_time_title), style = MaterialTheme.typography.titleMedium, color = HabitFlowDesign.textPrimary)
            Text(text = stringResource(id = R.string.daily_time_description), style = MaterialTheme.typography.bodyMedium, color = HabitFlowDesign.textSecondary)
            OutlinedButton(onClick = onSelectTime, shape = RoundedCornerShape(10.dp)) { Text(text = stringResource(id = R.string.select_time_value, formatted)) }
        }
    }
}

@Composable
private fun WeeklySelectionCard(day: DayOfWeek, time: LocalTime, onSelectDay: (DayOfWeek) -> Unit, onSelectTime: () -> Unit) {
    ElevatedCard(shape = HabitFlowDesign.cardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(id = R.string.weekly_section_title), style = MaterialTheme.typography.titleMedium, color = HabitFlowDesign.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DayOfWeek.values().forEach { d ->
                    FilterChip(selected = d == day, onClick = { onSelectDay(d) }, label = { Text(d.getDisplayName(TextStyle.SHORT, Locale("tr"))) })
                }
            }
            OutlinedButton(onClick = onSelectTime, shape = RoundedCornerShape(10.dp)) { Text(text = stringResource(id = R.string.select_time)) }
        }
    }
}

@Composable
private fun CustomSelectionCard(date: LocalDate, time: LocalTime, onSelectDate: () -> Unit, onSelectTime: () -> Unit, formatted: String) {
    ElevatedCard(shape = HabitFlowDesign.cardShape, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = stringResource(id = R.string.custom_section_title), style = MaterialTheme.typography.titleMedium, color = HabitFlowDesign.textPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onSelectDate, shape = RoundedCornerShape(10.dp)) { Text(text = stringResource(id = R.string.select_date)) }
                OutlinedButton(onClick = onSelectTime, shape = RoundedCornerShape(10.dp)) { Text(text = stringResource(id = R.string.select_time)) }
            }
            Text(text = formatted, style = MaterialTheme.typography.bodyMedium, color = HabitFlowDesign.textSecondary)
        }
    }
}
