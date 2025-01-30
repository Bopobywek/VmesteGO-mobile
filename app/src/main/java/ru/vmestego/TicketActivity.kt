package ru.vmestego

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ru.vmestego.ui.TicketParametersViewModel
import ru.vmestego.ui.theme.VmesteGOTheme
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar


class TicketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var uri: Uri? = null
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if (intent.type == "application/pdf") {
                    uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                    }
                    // TODO: https://stackoverflow.com/a/69699089
                    // I need to save file to another place and take persistable permission
//                    if (uri != null) {
//                        contentResolver.takePersistableUriPermission(
//                            uri,
//                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
//                        )
//                    }
                }
            }

//            // TODO: вынести в константы
//            intent?.action == "createTicket" -> {
//
//            }
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
fun EventParametersScreen(navigateToTicketParams: (Int?) -> Unit) {
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
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 5.dp),
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
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            var showBottomSheet by remember { mutableStateOf(false) }

            // TODO: при рефаче важно учесть, что для каждого мероприятия будет создан свой bottomSheet,
            // поэтому нельзя навесить общий viewModel
            if (showBottomSheet) {
                // https://www.youtube.com/watch?v=VxgWUdOKgtI
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    // TODO: autocomplete https://stackoverflow.com/a/72586090
                    var title by remember { mutableStateOf("") }
                    var location by remember { mutableStateOf("") }
                    var date by remember { mutableStateOf(LocalDate.now()) }
                    var time by remember { mutableStateOf(LocalTime.now()) }
                    var showDateInput by remember { mutableStateOf(false) }
                    var showTimeInput by remember { mutableStateOf(false) }

                    if (showDateInput) {
                        DatePickerModalInput(
                            onDismiss = { showDateInput = false },
                            onDateSelected = {
                                date = Instant.ofEpochMilli(it!!).atZone(
                                    ZoneId.systemDefault()
                                ).toLocalDate()
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

                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxHeight()
                    ) {
                        OutlinedTextField(
                            value = title,
                            placeholder = { Text("Название") },
                            onValueChange = { title = it },
                            label = { Text("Название") },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Место проведения") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = date.toString(),
                            onValueChange = {
                            },
                            readOnly = true,
                            label = { Text("Дата проведения") },
                            modifier = Modifier
                                .fillMaxWidth(),
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) {
                                                showDateInput = true
                                            }
                                        }
                                    }
                                }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = time.toString(),
                            onValueChange = {
                            },
                            readOnly = true,
                            label = { Text("Время проведения") },
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = {
                            showBottomSheet = false
                            navigateToTicketParams(12)
                        }) {
                            Text("Сохранить")
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Не нашли мероприятие?")
                Button(onClick = { showBottomSheet = true }) {
                    Text("Создайте его")
                }
            }
        }
    }
}

@Composable
fun TicketParametersScreen(
    ticketUri: Uri,
    eventId: Int?,
    navigateToEventParams: () -> Unit,
    viewModel: TicketParametersViewModel = viewModel()
) {
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
        },
        bottomBar = {
            Button(
                onClick = {
                    val newUri = copyFileToInternalStorage(
                        activity,
                        ticketUri
                    )
                    if (newUri != null) {
                        viewModel.addTicket(newUri)
                    }
                },
                enabled = eventId != null,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text("Сохранить")
            }
        }
    )
    { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(padding)
                .padding(start = 20.dp, top = 5.dp, end = 20.dp)
        ) {
            Column {
                Text("Билет")
                Button(
                    onClick = {
                        val intent = IntentHelper.createOpenPdfIntent(ticketUri)
                        activity.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(queryName(activity.contentResolver, ticketUri))
                }
            }
            Column {
                Text("Мероприятие")
                ElevatedCard(
                    onClick = navigateToEventParams,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(5.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        if (eventId == null) {
                            Text("Выберите мероприятие")
                        } else {
                            Text(eventId.toString())
                        }
                    }
                }
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
        composable<TicketParameters> { entry ->
            val eventId = entry.savedStateHandle.get<Int?>("event_id")
            TicketParametersScreen(uri, eventId, {
                navController.navigate(
                    EventParameters
                )
            })
        }
        composable<EventParameters> {
            EventParametersScreen {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("event_id", it)
                navController.popBackStack()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page() {
    TicketSettingsScreen(Uri.parse("test"))
}

fun queryName(resolver: ContentResolver, uri: Uri): String {
    val returnCursor = checkNotNull(
        resolver.query(uri, null, null, null, null)
    )
    val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    return name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    ) {
        DatePicker(state = datePickerState, showModeToggle = false)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModalInput(
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute)) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}


fun calculatePdfHash(inputStream: InputStream?, algorithm: String = "SHA-256"): String? {
    if (inputStream == null) {
        return null
    }

    return try {
        val digest = MessageDigest.getInstance(algorithm)
        val buffer = ByteArray(1024)
        var bytesRead: Int

        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }

        inputStream.close()
        digest.digest().joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun copyFileToInternalStorage(context: Context, uri: Uri): Uri? {
    val contentResolver = context.contentResolver

    val fileHash = calculatePdfHash(contentResolver.openInputStream(uri))
    val file = File(context.filesDir, "${fileHash}.pdf")

    try {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        // https://stackoverflow.com/a/38768148
        // https://stackoverflow.com/a/38858040
        // https://stackoverflow.com/a/42516202
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
