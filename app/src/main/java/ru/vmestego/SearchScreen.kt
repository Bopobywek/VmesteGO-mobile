package ru.vmestego

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(goToEvent: () -> Unit) {
    val isUserSearching = remember { mutableStateOf(false) }
    val isUserSelectDate = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }
    Scaffold(
        // https://composables.com/material3/searchbar
        topBar = {
            val onActiveChange = {
                isUserSearching.value = !isUserSearching.value
            } //the callback to be invoked when this search bar's active state is changed
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
                            query = searchText.value,//text showed on SearchBar
                            onQueryChange = { newQuery ->
                                searchText.value = newQuery
                            }, //update the value of searchText
                            onSearch = { query -> "" }, //the callback to be invoked when the input service triggers the ImeAction.Search action
                            expanded = false, // whether the user is searching or not
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
                    onClick = {},
                    modifier = Modifier
                        .fillMaxHeight()
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
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(padding)
        ) {
            if (isUserSelectDate.value) {
                DateRangePickerModal({}, { isUserSelectDate.value = false })
            }
            val scrollState = rememberScrollState()
            Row(Modifier
                .padding()
                .horizontalScroll(scrollState)) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = { isUserSelectDate.value = true }
                ) {
                    Text("Дата")
                }
                Spacer(Modifier.size(10.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {}
                ) {
                    Text("Тип")
                }
                Spacer(Modifier.size(10.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        Color.LightGray,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {}
                ) {
                    Text("Москва")
                }
            }
            Spacer(Modifier.size(16.dp))
            EventsList(goToEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsList(goToEvent: () -> Unit) {
    val state = rememberPullToRefreshState()
    val isRefreshing = remember { mutableStateOf(false) }
    PullToRefreshBox(
        isRefreshing = isRefreshing.value,
        onRefresh = { isRefreshing.value = false },
        state = state
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            for (i in 1..10) {
                item {
                    EventCard(goToEvent)
                }
            }
        }
    }
}

@Composable
fun EventCard(goToEvent: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
            .clickable {
                goToEvent()
            }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "",
            colorFilter = ColorFilter.tint(generateWarmSoftColor()),
            // https://developer.android.com/develop/ui/compose/graphics/images/customize
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Box(Modifier.padding(10.dp)) {
            Column {
                Text("Икар", fontSize = 20.sp)
                Text("КЗ Измайлово", fontSize = 16.sp)
                Text("19:00", fontSize = 16.sp)
            }
        }
    }
}

// https://developer.android.com/develop/ui/compose/components/datepickers#range
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}
