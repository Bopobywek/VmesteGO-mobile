package ru.vmestego

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchScreen() {
    val isUserSearching = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            SearchBar(
                query = searchText.value,//text showed on SearchBar
                onQueryChange = { newQuery -> searchText.value = newQuery}, //update the value of searchText
                onSearch = { query -> ""}, //the callback to be invoked when the input service triggers the ImeAction.Search action
                active = true, //whether the user is searching or not
                onActiveChange = { isUserSearching.value = !isUserSearching.value }, //the callback to be invoked when this search bar's active state is changed
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search...") }
            ) {
            }
        }
    ) {

    }
}