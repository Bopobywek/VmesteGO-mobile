@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package ru.vmestego.ui.mainActivity.eventForm

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.vmestego.ui.mainActivity.toSimpleDateString
import ru.vmestego.ui.ticketActivity.DatePickerModalInput
import ru.vmestego.ui.ticketActivity.EventDto
import ru.vmestego.ui.ticketActivity.TimePickerModalInput
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventFormScreen(
    viewModel: EventFormViewModel = viewModel(),
    existingEventId: Long? = null,
    onSubmit: (EventDto) -> Unit
) {
    val context = LocalContext.current
    var date by viewModel.date
    var time by viewModel.time
    var title by viewModel.title
    var location by viewModel.location
    var description by viewModel.description
    var ageRestriction by viewModel.ageRestriction
    var price by viewModel.price
    var selectedCategories by viewModel.selectedCategories
    var isPrivate by viewModel.isPrivate

    var showDateInput by remember { mutableStateOf(false) }
    var showTimeInput by remember { mutableStateOf(false) }

    var scope = rememberCoroutineScope()

    if (existingEventId != null) {
        LaunchedEffect(existingEventId) {
            viewModel.loadExistingForEdit(existingEventId)
        }
    }

    Scaffold(
        topBar = {
            if (viewModel.isAdmin()) {
                val options = listOf("Публичное", "Приватное")

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = {
                                isPrivate = (index > 0)
                            },
                            selected = index == if (isPrivate) 1 else 0,
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    scope.launch(Dispatchers.Main) {
                        val eventResult = viewModel.createEvent()
                        if (eventResult.isError()) {
                            when (eventResult.error) {
                                ErrorType.FORM_VALIDATION -> Toast.makeText(
                                    context,
                                    "Проверьте заполнение формы",
                                    Toast.LENGTH_SHORT
                                ).show()

                                else -> Toast.makeText(
                                    context,
                                    "Попробуйте ещё раз",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val event = eventResult.getResultIfNotNull()
                            onSubmit(
                                EventDto(
                                    event.eventName,
                                    event.locationName,
                                    event.dateTime.toLocalDate(),
                                    LocalTime.of(event.dateTime.hour, event.dateTime.minute),
                                    event.id
                                )
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text("Сохранить")
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Название*") },
                supportingText = {
                    if (viewModel.titleError.value != null) {
                        Text(viewModel.titleError.value!!)
                    }
                },
                isError = viewModel.titleError.value != null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { viewModel.updateLocation(it) },
                label = { Text("Локация*") },
                supportingText = {
                    if (viewModel.locationError.value != null) {
                        Text(viewModel.locationError.value!!)
                    }
                },
                isError = viewModel.locationError.value != null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Описание*") },
                supportingText = {
                    if (viewModel.descriptionError.value != null) {
                        Text(viewModel.descriptionError.value!!)
                    }
                },
                isError = viewModel.descriptionError.value != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (viewModel.isAdmin()) {
                OutlinedTextField(
                    value = ageRestriction,
                    onValueChange = { ageRestriction = it.filter { it.isDigit() } },
                    label = { Text("Возрастное ограничение") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it.filter { it.isDigit() || it == '.' } },
                    label = { Text("Цена") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = date.atStartOfDay().toSimpleDateString(),
                onValueChange = {},
                label = { Text("Дата*") },
                trailingIcon = {
                    IconButton(onClick = { showDateInput = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                    }
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            val timeFormatter =
                DateTimeFormatter.ofPattern("HH:mm", Locale("ru"))
            OutlinedTextField(
                value = time.format(timeFormatter),
                onValueChange = {
                },
                readOnly = true,
                label = { Text("Время*") },
                modifier = Modifier
                    .fillMaxWidth(),
                interactionSource = remember { MutableInteractionSource() }
                    .also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is PressInteraction.Release) {
                                    showTimeInput = true
                                }
                            }
                        }
                    }
            )

            Text("Категории", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.categories.collectAsState().value.forEach { category ->
                    FilterChip(
                        selected = category in selectedCategories,
                        onClick = {
                            selectedCategories = if (category in selectedCategories)
                                selectedCategories - category
                            else
                                selectedCategories + category
                        },
                        label = { Text(category.name) }
                    )
                }
            }
        }

        if (showDateInput) {
            DatePickerModalInput(
                onDismiss = { showDateInput = false },
                onDateSelected = {
                    if (it != null) {
                        date = Instant.ofEpochMilli(it).atZone(
                            ZoneId.systemDefault()
                        ).toLocalDate()
                    }
                    showDateInput = false
                })
        }

        if (showTimeInput) {
            TimePickerModalInput(
                onDismiss = { showTimeInput = false },
                onConfirm = {
                    time = it
                    showTimeInput = false
                })
        }
    }
}
