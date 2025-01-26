package ru.vmestego

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ru.vmestego.ui.theme.VmesteGOTheme

class TicketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var uri : Uri? = null
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if (intent.type == "application/pdf") {
                    uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                    }
                    // TODO: https://stackoverflow.com/a/69699089
//                    if (uri != null) {
//                        contentResolver.takePersistableUriPermission(
//                            uri,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                        )
//                    }
                }
            }
        }

        if (uri == null) {
            Log.w("TicketsActivity", "No uri provided for ticket, can't open activity")
            finish()
            return
        }

        setContent {
            VmesteGOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    TicketSettingsScreen(uri)

                }
            }
        }
    }
}

@Serializable
object EventParameters

@Serializable
object TicketParameters


@OptIn(ExperimentalMaterial3Api::class)
@Composable
// https://stackoverflow.com/a/67133534
fun EventParametersScreen(navigateToTicketParams: () -> Unit) {
    val isUserSearching = remember { mutableStateOf(false) }
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
                    .padding(start = 16.dp, end = 16.dp, bottom = 5.dp),
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
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Не нашли мероприятие?")
                Button(onClick = {}) {
                    Text("Создайте его")
                }
            }
        }
    }
}

@Composable
fun TicketParametersScreen(navigateToEventParams: () -> Unit) {
    val activity = LocalContext.current as Activity
    Scaffold(
        topBar = {
            TextButton(
                onClick = {
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text("Отмена")
            }
        }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        ) {
            Column {
                Text("Билет")
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) { }
            }
            Column {
                Text("Мероприятие")
                Button(
                    onClick = navigateToEventParams,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) { }
            }
        }
    }
}

// https://stackoverflow.com/a/79047721
@Composable
fun TicketSettingsScreen(uri: Uri) {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = TicketParameters,
        // https://stackoverflow.com/a/78741718
    ) {
        composable<TicketParameters> {
            TicketParametersScreen {
                navController.navigate(
                    EventParameters
                )
            }
        }
        composable<EventParameters> {
            EventParametersScreen {
                navController.popBackStack(
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page() {
    TicketSettingsScreen(Uri.parse("test"))
}