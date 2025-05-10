@file:OptIn(ExperimentalMaterial3Api::class)

package ru.vmestego.ui.mainActivity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.imageLoader
import ru.vmestego.R
import ru.vmestego.ui.authActivity.AuthActivity
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.utils.LocalDateTimeFormatters
import kotlin.math.min


@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    goToEvent: (EventUi) -> Unit) {
    val activity = LocalContext.current as Activity
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    if (showBottomSheet) {
        NotificationsModalBottomSheet(
            viewModel, sheetState
        ) { showBottomSheet = false }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { showBottomSheet = true }) {
                BadgedBox(
                    badge = {
                        if (viewModel.hasUnreadNotifications.collectAsState().value) {
                            Badge()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
            IconButton(onClick = {
                viewModel.logout()
                activity.startActivity(Intent(activity, AuthActivity::class.java))
                activity.finish()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        val context = LocalContext.current
        val photoUri =
            remember { mutableStateOf<String>("https://storage.yandexcloud.net/vmestego/photo_1_2025-04-08_01-10-21.jpg") }
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                run {
                    if (uri != null) {
                        photoUri.value = uri.encodedPath!!
                        Log.d("PhotoPicker", "Selected URI: $uri")
                        val bytes = context.contentResolver.openInputStream(uri)!!.readBytes()
                        viewModel.updateImage(bytes)
                        context.imageLoader.diskCache?.clear()
                        context.imageLoader.memoryCache?.clear()
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
            }

        val userInfo by viewModel.userInfo.collectAsState()
        if (userInfo == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(36.dp))
        } else {
            key(viewModel.imageState.collectAsState().value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = userInfo!!.imageUrl,
                        error = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(2.dp, Color.DarkGray), CircleShape)
                            .clickable {
                                launcher.launch(arrayOf("image/*"))
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userInfo!!.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val wantToGoEvents by viewModel.wantToGoEvents.collectAsState()
        EventSection("Хочу пойти", wantToGoEvents, goToEvent)

        Spacer(modifier = Modifier.height(8.dp))

        val goingEvents by viewModel.goingToEvents.collectAsState()
        EventSection("Иду", goingEvents, goToEvent)

        Spacer(modifier = Modifier.height(8.dp))

        val notGoingEvents by viewModel.notGoingToEvents.collectAsState()
        EventSection("Не пойду", notGoingEvents, goToEvent)

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun EventSection(
    title: String,
    events: List<EventUi>,
    goToEvent: (EventUi) -> Unit
) {
    var showBottomSheet = remember { mutableStateOf(false) }
    val maxSize = 3

    if (showBottomSheet.value) {
        EventsListModal(showBottomSheet, events, goToEvent)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp
        )
        if (events.size > maxSize) {
            TextButton(onClick = {
                showBottomSheet.value = true
            }) {
                Text(text = "Показать все (${events.size})")
            }
        }
    }

    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 2.dp)
    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (events.isEmpty()) {
            Text(
                "Таких мероприятий пока нет",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }
        for (index in 0..min(events.size, maxSize) - 1) {
            val it = events[index]
            Box(
                Modifier.padding(horizontal = 20.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .heightIn(min = 100.dp)
                        .fillMaxWidth()
                        .clickable {
                            goToEvent(it)
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Row {
                            Column {
                                Text(text = it.eventName, Modifier.fillMaxWidth(0.5f))
                            }
                            Column {
                                Text(text = LocalDateTimeFormatters.formatByDefault(it.dateTime))
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            Icon(Icons.Filled.Place, "Add")
                            Text(text = it.locationName)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsModalBottomSheet(
    viewModel: ProfileViewModel,
    sheetState: SheetState,
    closeSheet: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            closeSheet()
        },
        sheetState = sheetState
    ) {
        Box(Modifier.fillMaxSize()) {
            val listState = rememberLazyListState()
            val notifications by viewModel.notifications.collectAsState()

            if (notifications.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет новых уведомлений", color = Color.LightGray)
                }
            }

            val visibleItemIds by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val visibleItemsInfo = layoutInfo.visibleItemsInfo
                    if (visibleItemsInfo.isEmpty()) {
                        emptyList()
                    } else {
                        visibleItemsInfo.map { it.index }
                    }
                }
            }
            LaunchedEffect(visibleItemIds) {
                viewModel.markNotificationsAsRead(visibleItemIds)
            }

            LazyColumn(
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp),
                state = listState
            ) {
                itemsIndexed(notifications) { index, notification ->
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(LocalDateTimeFormatters.formatByDefault(notification.createdAt))
                            if (!notification.isRead) {
                                Spacer(Modifier.weight(1f))
                                Badge()
                            }
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = notification.text,
                                fontWeight = FontWeight.W400,
                                fontSize = 20.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        thickness = 2.dp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsListModal(
    showModal: MutableState<Boolean>,
    events: List<EventUi>,
    goToEvent: (EventUi) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            showModal.value = false
        },
        sheetState = sheetState
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(events) {
                ElevatedCard(
                    modifier = Modifier
                        .heightIn(min = 100.dp)
                        .padding(horizontal = 20.dp)
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .clickable {
                            goToEvent(it)
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp)
                    ) {
                        Column {
                            Text(
                                text = it.eventName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = LocalDateTimeFormatters.formatByDefault(it.dateTime),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Place, contentDescription = "Location")
                            Text(
                                text = it.locationName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}