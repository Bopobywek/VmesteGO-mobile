package ru.vmestego

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen() {
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
                        placeholder = { Text("Search...") },
                        leadingIcon = null,
                        trailingIcon = null,
                        interactionSource = null,
                    )
                },
                expanded = false,
                onExpandedChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 5.dp),
                shape = RoundedCornerShape(15.dp),
                colors = colors1,
                tonalElevation = SearchBarDefaults.TonalElevation,
                shadowElevation = SearchBarDefaults.ShadowElevation,
                windowInsets = SearchBarDefaults.windowInsets
            ) {}
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
            Row(Modifier.padding()) {
                Button(
                    colors = ButtonDefaults.buttonColors(Color.LightGray, contentColor = Color.Black),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {}
                ) {
                    Text("Дата", modifier = Modifier.clickable { isUserSelectDate.value = true })
                }
                Spacer(Modifier.size(10.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(Color.LightGray, contentColor = Color.Black),
                    shape = RoundedCornerShape(15.dp),
                    onClick = {}
                ) {
                    Text("Тип")
                }
            }
            Spacer(Modifier.size(16.dp))
            EventsList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsList() {
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
                    EventCard()
                }
            }
        }
    }
}

@Composable
fun EventCard() {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 150.dp)
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
