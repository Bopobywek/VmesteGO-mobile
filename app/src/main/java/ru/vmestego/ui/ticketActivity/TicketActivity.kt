package ru.vmestego.ui.ticketActivity

import ru.vmestego.ui.mainActivity.eventForm.EventFormScreen
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.vmestego.data.EventDataDto
import ru.vmestego.ui.mainActivity.search.EventsList
import ru.vmestego.ui.mainActivity.MainActivity
import ru.vmestego.ui.mainActivity.search.SearchViewModel
import ru.vmestego.ui.ticketActivity.models.EventRouteDto
import ru.vmestego.ui.theme.VmesteGOTheme
import ru.vmestego.utils.IntentHelper
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar


class TicketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var uri: Uri? = null
        var params: TicketActivityParams? = null
        when {
            intent?.action == Intent.ACTION_SEND -> {
                if (intent.type == "application/pdf") {
                    uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                    } else {
                        intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri
                    }
                }
            }

            intent?.action == "TICKET_FOR_EVENT" -> {
                params = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, TicketActivityParams::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM) as? TicketActivityParams
                }

                uri = params?.uri
            }
        }

        if (uri == null ) {
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
                    TicketSettingsScreen(uri, params?.eventDto)
                }
            }
        }
    }
}

data class TicketActivityParams(
    val uri: Uri,
    val eventDto: EventRouteDto
) : Parcelable {
    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(uri, flags)
        val json = Json.encodeToString(eventDto)
        parcel.writeString(json)
    }

    companion object CREATOR : Parcelable.Creator<TicketActivityParams> {
        override fun createFromParcel(parcel: Parcel): TicketActivityParams {
            val uri = parcel.readParcelable<Uri>(Uri::class.java.classLoader)!!
            val json = parcel.readString()!!
            val eventDto = Json.decodeFromString<EventRouteDto>(json)
            return TicketActivityParams(uri, eventDto)
        }

        override fun newArray(size: Int): Array<TicketActivityParams?> =
            arrayOfNulls(size)
    }
}

@Serializable
object EventParameters

@Serializable
object TicketParameters

data class EventDto(
    val title: String,
    val location: String,
    val date: LocalDate,
    val time: LocalTime,
    val externalId: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// https://stackoverflow.com/a/67133534
fun EventParametersScreen(
    navigateToTicketParams: (EventRouteDto?) -> Unit,
    scope: CoroutineScope,
    viewModel: EventParametersViewModel = viewModel(),
    searchViewModel: SearchViewModel = viewModel()
) {
    Scaffold(
        // https://composables.com/material3/searchbar
        topBar = {
            val colors1 = SearchBarDefaults.colors()
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchViewModel.searchText,
                        onQueryChange = searchViewModel::onQueryChanged,
                        onSearch = searchViewModel::onSearch,
                        expanded = false,
                        onExpandedChange = {},
                        enabled = true,
                        placeholder = { Text("Поиск...") },
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
                    EventFormScreen {
                        showBottomSheet = false
                        scope.launch(Dispatchers.Main) {
                            val receivedId =
                                viewModel.addEvent(
                                    EventDataDto(
                                        it.title,
                                        it.location,
                                        LocalDateTime.of(it.date, it.time),
                                        it.externalId
                                    )
                                )
                            navigateToTicketParams(
                                EventRouteDto(
                                    receivedId,
                                    it.title,
                                    it.location,
                                    LocalDateTime.of(it.date, it.time)
                                )
                            )
                        }
                    }
                }
            }
            if (searchViewModel.searchText.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Не нашли мероприятие?")
                    Button(onClick = { showBottomSheet = true }) {
                        Text("Создайте его")
                    }
                }
            } else {
                Box(
                    Modifier
                        .padding(20.dp)
                        .fillMaxSize()
                ) {
                    val scope = rememberCoroutineScope()
                    EventsList(searchViewModel, {}, { e ->
                        scope.launch(Dispatchers.Main) {
                            val receivedId =
                                viewModel.addEvent(
                                    EventDataDto(
                                        e.eventName,
                                        e.locationName,
                                        e.dateTime,
                                        e.id
                                    )
                                )
                            navigateToTicketParams(
                                EventRouteDto(
                                    receivedId,
                                    e.eventName,
                                    e.locationName,
                                    e.dateTime
                                )
                            )
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun TicketParametersScreen(
    ticketUri: Uri,
    eventRouteDto: EventRouteDto?,
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
                    if (newUri != null && eventRouteDto != null) {
                        viewModel.addTicket(newUri, eventRouteDto.uid)
                    }
                    activity.finish()
                },
                enabled = eventRouteDto != null,
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
                    if (eventRouteDto == null) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        ) {

                            Text("Выберите мероприятие")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(eventRouteDto.name)
                        }

                    }
                }
            }
        }
    }
}


// https://stackoverflow.com/a/79047721
@Composable
fun TicketSettingsScreen(uri: Uri, eventUi: EventRouteDto?) {
    val navController = rememberNavController()
    val eventParamName = "event_dto"
    val scope = rememberCoroutineScope()
    NavHost(
        navController,
        startDestination = TicketParameters,
        // https://stackoverflow.com/a/78741718
    ) {
        composable<TicketParameters> { entry ->
            val serialized = entry.savedStateHandle.get<String>(eventParamName)
            val eventRouteDto =
                if (serialized == null && eventUi == null) {
                    null
                } else if (serialized == null && eventUi != null) {
                   eventUi
                }
                else {
                    Json.decodeFromString<EventRouteDto?>(serialized!!)
                }
            TicketParametersScreen(uri, eventRouteDto, {
                navController.navigate(
                    EventParameters
                )
            })
        }
        composable<EventParameters> {
            EventParametersScreen(
                {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(eventParamName, Json.encodeToString(it))
                    navController.popBackStack()
                },
                scope
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Page() {
    TicketSettingsScreen(Uri.parse("test"), null)
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
