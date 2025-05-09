package ru.vmestego.ui.mainActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.vmestego.bll.services.shared.models.CategoryResponse
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.utils.LocalDateTimeFormatters
import ru.vmestego.utils.rememberCachedImageLoader
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    goToEvent: (EventUi) -> Unit,
    createEvent: () -> Unit
) {
    val isUserSelectDate = remember { mutableStateOf(false) }
    var showCategoryDialog = remember { mutableStateOf(false) }
    Scaffold(
        // https://composables.com/material3/searchbar
        topBar = {
            val colors1 = SearchBarDefaults.colors()
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = viewModel.searchText,
                            onQueryChange = viewModel::onQueryChanged,
                            onSearch = viewModel::onSearch,
                            expanded = false,
                            onExpandedChange = {},
                            enabled = true,
                            placeholder = { Text("Поиск...") },
                            leadingIcon = null,
                            trailingIcon = null,
                            interactionSource = null
                        )
                    },
                    expanded = false,
                    onExpandedChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(15.dp),
                    colors = colors1,
                    tonalElevation = SearchBarDefaults.TonalElevation,
                    shadowElevation = SearchBarDefaults.ShadowElevation,
                    windowInsets = SearchBarDefaults.windowInsets
                ) {}

                Spacer(modifier = Modifier.width(5.dp))
                IconButton(
                    onClick = { createEvent() },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 7.dp)
                        .background(Color.Transparent, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        rememberVectorPainter(image = Icons.Outlined.Add),
                        contentDescription = "Localized description",
                        tint = { Color.Gray })
                }
            }
        }
    ) { padding ->
        if (showCategoryDialog.value) {
            MultiSelectDialog(
                title = "Выберите пункты",
                options = viewModel.categories.collectAsState().value,
                optionLabel = { it.name },
                initiallySelected = viewModel.categoriesApplied,
                onDismiss = { showCategoryDialog.value = false },
                onDone = { newSelection ->
                    viewModel.applyCategoriesFilter(newSelection)
                }
            )
        }

        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(padding)
        ) {
            if (isUserSelectDate.value) {
                DateRangePickerModal({
                    viewModel.applyDateFilter(it.first!!, it.second)
                }, { isUserSelectDate.value = false })
            }
            val scrollState = rememberScrollState()
            Row(
                Modifier
                    .padding()
                    .horizontalScroll(scrollState)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = { isUserSelectDate.value = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        when {
                            viewModel.startDateFilter == null && viewModel.endDateFilter == null -> {
                                Text("Дата")
                            }

                            viewModel.endDateFilter == null -> {
                                Text("от ${viewModel.startDateFilter!!.toSimpleDateString()}")
                            }

                            else -> {
                                Text("с ${viewModel.startDateFilter!!.toSimpleDateString()} по ${viewModel.endDateFilter!!.toSimpleDateString()}")
                            }
                        }

                        if (viewModel.startDateFilter != null || viewModel.endDateFilter != null) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Очистить фильтр",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        viewModel.resetDateFilters()
                                    }
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.size(10.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = { showCategoryDialog.value = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Тип")

                        if (viewModel.categoriesApplied.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Очистить фильтр",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable {
                                        viewModel.resetCategoriesFilter()
                                    }
                                    .padding(start = 4.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.size(10.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {}
                ) {
                    Text("Москва")
                }
            }
            Spacer(Modifier.size(16.dp))
            EventsList(viewModel, goToEvent, {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsList(
    viewModel: SearchViewModel,
    goToEvent: (EventUi) -> Unit,
    onEventClick: (EventUi) -> Unit
) {
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing = remember { mutableStateOf(false) }
    PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = {
            coroutineScope.launch {
                isRefreshing.value = true
                viewModel.update()
                isRefreshing.value = false
                state.animateToHidden()
            }
        },
        state = state
    ) {
        val events by viewModel.events.collectAsState()
        if (events.isEmpty() && !viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Не удалось найти мероприятия", color = Color.LightGray)
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 10.dp)
        ) {
            items(events) {
                EventCard(it, goToEvent, onEventClick)
            }
        }
    }
}

@Composable
fun EventCard(eventUi: EventUi, goToEvent: (EventUi) -> Unit, onEventClick: (EventUi) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
            .clickable {
                onEventClick(eventUi)
                goToEvent(eventUi)
            }
    ) {
        val imageLoader = rememberCachedImageLoader()
        AsyncImage(
            model = eventUi.imageUrl,
            imageLoader = imageLoader,
            contentDescription = "",
            placeholder = ColorPainter(Color.LightGray),
            error = ColorPainter(Color.LightGray),
            // https://developer.android.com/develop/ui/compose/graphics/images/customize
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Box(Modifier.padding(10.dp)) {
            Column {
                Text(eventUi.eventName, fontSize = 20.sp)
                Text(eventUi.locationName, fontSize = 16.sp)
                Text(LocalDateTimeFormatters.formatByDefault(eventUi.dateTime), fontSize = 16.sp)
            }
        }
    }
}

// https://developer.android.com/develop/ui/compose/components/datepickers#range
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<LocalDateTime?, LocalDateTime?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    var startDate: LocalDateTime? = null
                    if (dateRangePickerState.selectedStartDateMillis != null) {
                        startDate = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(dateRangePickerState.selectedStartDateMillis!!),
                            ZoneId.systemDefault()
                        )
                    }
                    var endDate: LocalDateTime? = null
                    if (dateRangePickerState.selectedEndDateMillis != null) {
                        endDate = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(dateRangePickerState.selectedEndDateMillis!!),
                            ZoneId.systemDefault()
                        )
                    }
                    onDateRangeSelected(
                        Pair(
                            startDate,
                            endDate
                        )
                    )
                    onDismiss()
                },
                enabled = dateRangePickerState.selectedStartDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(top = 10.dp)
        )
    }
}

fun LocalDateTime.toSimpleDateString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
    return this.format(formatter)
}


data class CategoryUi(
    val id: Long,
    val name: String
)

fun CategoryResponse.toCategoryUi(): CategoryUi {
    return CategoryUi(id, name)
}


@Composable
fun <T> MultiSelectDialog(
    title: String = "Выбор",
    options: List<T>,
    initiallySelected: List<T> = emptyList(),
    optionLabel: (T) -> String = { it.toString() },
    onDismiss: () -> Unit,
    onDone: (List<T>) -> Unit
) {
    val selectedItems = remember { mutableStateListOf<T>().apply { addAll(initiallySelected) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDone(selectedItems)
                onDismiss()
            }) {
                Text("Готово")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Column {
                options.forEach { item ->
                    val isSelected = item in selectedItems
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .toggleable(
                                value = isSelected,
                                onValueChange = {
                                    if (isSelected) selectedItems.remove(item)
                                    else selectedItems.add(item)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(optionLabel(item))
                    }
                }
            }
        }
    )
}