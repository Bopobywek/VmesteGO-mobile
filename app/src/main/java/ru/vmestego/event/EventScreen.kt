package ru.vmestego.event

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.vmestego.R
import ru.vmestego.core.EventStatus
import ru.vmestego.ui.mainActivity.UserUi
import ru.vmestego.ui.mainActivity.generateWarmSoftColor
import ru.vmestego.utils.LocalDateFormatters
import ru.vmestego.utils.LocalDateTimeFormatters
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun EventScreenWrapper(viewModel: EventViewModel, goBackToSearch: () -> Unit) {
    val showBottomSheet = remember { mutableStateOf(false) }
    Scaffold(bottomBar = {
        Button(
            onClick = {
                showBottomSheet.value = true
            },
            shape = RoundedCornerShape(10.dp),

            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 15.dp)
        )
        {
            Text("Обсуждение", fontSize = 16.sp)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        // fff5ee
                        .background(Color(1f, 0.9607843137254902f, 0.9333f, 1f))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = viewModel.comments.collectAsState().value.size.toString(),
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }
    }) { innerPadding ->
        val event by viewModel.event.collectAsState()
        if (event == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            EventScreen(event!!, innerPadding, goBackToSearch, showBottomSheet, viewModel)
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    eventUi: EventUi,
    innerPadding: PaddingValues,
    goBackToSearch: () -> Unit,
    showBottomSheet: MutableState<Boolean>,
    viewModel: EventViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = "",
                colorFilter = ColorFilter.tint(generateWarmSoftColor()),
                // https://developer.android.com/develop/ui/compose/graphics/images/customize
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, bottom = 5.dp),
                contentAlignment = Alignment.BottomStart
            ) {

                Text(
                    eventUi.eventName,
                    Modifier

                        .fillMaxWidth(),
                    color = Color.White,
                    fontSize = 36.sp,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
            }

            Icon(
                rememberVectorPainter(image = Icons.Filled.Close),
                contentDescription = "Localized description",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(32.dp)
                    .clickable {
                        goBackToSearch()
                    },
                tint = { Color.White })
        }

        Column(
            Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 10.dp)
        ) {
            Text(LocalDateTimeFormatters.formatByDefault(eventUi.dateTime), fontSize = 16.sp)

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(eventUi.locationName, fontSize = 24.sp)
                Icon(rememberVectorPainter(image = Icons.Filled.LocationOn),
                    contentDescription = "Localized description",
                    tint = { Color.Gray })
            }

            Spacer(Modifier.height(20.dp))

            SingleChoiceSegmentedButton(
                Modifier.fillMaxWidth(),
                eventUi.eventStatus,
                viewModel::changeEventStatus
            )


            val showFriendsWithoutStatusesModal = remember { mutableStateOf(false) }
            if (showFriendsWithoutStatusesModal.value) {
                FriendsModal(
                    showFriendsWithoutStatusesModal,
                    viewModel.friendsWithStatus.collectAsState().value,
                    viewModel::inviteFriend
                )
            }

            Spacer(Modifier.height(10.dp))

            Button(
                { showFriendsWithoutStatusesModal.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Пригласите своих друзей", fontSize = 16.sp)
            }

            Spacer(Modifier.height(10.dp))


            Column(Modifier.verticalScroll(rememberScrollState())) {
                InEventSection("О мероприятии") {
                    Text(eventUi.description, lineHeight = 18.sp)
                }

                Spacer(Modifier.height(5.dp))
            }
        }
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            val comments by viewModel.comments.collectAsState()
            var inputText by remember { mutableStateOf("") }

            Scaffold(
                bottomBar = {
                    MessageInputField(inputText, { inputText = it }) {
                        if (inputText.isNotBlank()) {
                            viewModel.addComment(inputText)
                            inputText = ""
                        }
                    }
                }
            ) {
                LazyColumn(Modifier.padding(it)) {
                    items(comments) {
                        CommentItem(it, viewModel::removeComment)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsModal(
    showModal: MutableState<Boolean>,
    friends: List<UserWithStatusUi>,
    sendInvite: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = {
            showModal.value = false
        },
        sheetState = sheetState
    ) {
        if (friends.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text("Ваш список друзей пуст", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
                    Text("Скорее добавьте кого-нибудь", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            for (friend in friends) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                ) {
                    AsyncImage(
                        model = friend.user.imageUrl,
                        error = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = friend.user.name,
                        fontWeight = FontWeight.W400,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    if (friend.status != null) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            friend.status.toText(),
                            color = Color.DarkGray,
                            fontWeight = FontWeight.W300,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }

                    if (friend.status == null) {
                        Spacer(Modifier.weight(1f))
                        Button(
                            onClick = { sendInvite(friend.user.id) },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Text("Пригласить")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun InEventSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit)
) {
    Column(modifier = modifier) {
        Text(title, fontSize = 20.sp)
        Spacer(Modifier.height(2.dp))
        HorizontalDivider(thickness = 2.dp)
        Spacer(Modifier.height(5.dp))
        content()
    }
}

@Composable
fun MessageInputField(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(start = 12.dp, end = 24.dp)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Комментарий...") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(16.dp)
        )

        IconButton(
            onClick = onSendClick,
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}


@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    status: EventStatus?,
    changeStatus: (EventStatus) -> Unit
) {
    val options = listOf("Хочу пойти", "Иду", "Не иду")

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = {
                    changeStatus(index.toEventStatus())
                },
                selected = index == status?.ordinal,
                label = { Text(label) }
            )
        }
    }
}

fun Int.toEventStatus(): EventStatus {
    if (this == 0) {
        return EventStatus.WantToGo
    }
    if (this == 1) {
        return EventStatus.Going
    }
    return EventStatus.NotGoing
}

fun EventStatus?.toText(): String {
    if (this == EventStatus.WantToGo) {
        return "Хочет пойти"
    }
    if (this == EventStatus.Going) {
        return "Пойдет"
    }
    if (this == EventStatus.NotGoing) {
        return "Не пойдет"
    }

    return ""
}


@Composable
fun CommentItem(
    commentUi: CommentUi,
    remove: (CommentUi) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = commentUi.username,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = commentUi.createdAt.formatPrettyTime(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = commentUi.text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(
            onClick = {
                remove(commentUi)
                      },
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Comment",
                tint = Color.Red
            )
        }
    }
}

fun LocalDateTime.formatPrettyTime(): String {
    val now = LocalDateTime.now()
    val duration = Duration.between(this, now)

    return when {
        duration.toMinutes() < 1 -> "Только что"
        duration.toMinutes() < 60 -> "${duration.toMinutes()} мин. назад"
        duration.toHours() < 24 -> "${duration.toHours()} ч. назад"
        duration.toDays() == 1L -> "Вчера"
        duration.toDays() < 7 -> "${duration.toDays()} д. назад"
        else -> this.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
}
