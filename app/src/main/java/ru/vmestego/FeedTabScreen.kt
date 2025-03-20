package ru.vmestego

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min
import kotlin.random.Random

@Composable
fun FeedTabScreen() {
    FeedEventsList(
        listOf(
            FeedEventUi("test", "test", LocalDate.parse("2024-05-01"), listOf()),
            FeedEventUi("test", "test", LocalDate.now(), listOf(
                EventFriendsUi("Golubex", generateWarmSoftColor()),
                EventFriendsUi("Битман", generateWarmSoftColor()),
                EventFriendsUi("Четкий", generateWarmSoftColor()),
            )),
            FeedEventUi("test", "test", LocalDate.now(), listOf(
                EventFriendsUi("Golubex", generateWarmSoftColor()),
                EventFriendsUi("Битман", generateWarmSoftColor()),
                EventFriendsUi("Битман", generateWarmSoftColor()),
                EventFriendsUi("Битман", generateWarmSoftColor()),
                EventFriendsUi("Битман", generateWarmSoftColor()),
                EventFriendsUi("Битман", generateWarmSoftColor()),
                EventFriendsUi("Четкий", generateWarmSoftColor()),
            )),
            FeedEventUi("test", "test", LocalDate.now(), listOf()),
            FeedEventUi("test", "test", LocalDate.now(), listOf()),
            FeedEventUi("test", "test", LocalDate.now(), listOf()),
        )
    )
}

@Composable
fun FeedEventsList(events: List<FeedEventUi>) {
    val grouped = events.groupBy { it.date.withDayOfMonth(1) }
    val ordered = grouped.toSortedMap()

    // https://stackoverflow.com/a/74227507
    val initialIndex = getCurrentMonthHeaderIndex(ordered)
    Log.i("Tickets", "current index $initialIndex")
    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp),
        state = listState
    ) {
        ordered.forEach { (date, dateEvents) ->
            item {
                DateHeader(date)
            }

            val sortedEvents = dateEvents.sortedBy { t -> t.date }
            items(sortedEvents) { event ->
                FeedEventCard(event)
            }
        }
    }
}

// https://stackoverflow.com/a/78475807
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedEventCard(event: FeedEventUi) {
    val profileImageSize = 24
    val boxContentPadding = 15

    // TODO: вынести bottomSheet в отдельную функцию
    val sheetState = rememberModalBottomSheetState()
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
            // Sheet content
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                for (friend in event.friends) {
                    // content
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_background),
                            contentDescription = "",
                            colorFilter = ColorFilter.tint(friend.color),
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = friend.name,
                            fontWeight = FontWeight.W400,
                            fontSize = 20.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    val formatter = DateTimeFormatter.ofPattern("EE, dd MMM yyyy", Locale("ru")) // Russian locale
    val formattedDate = LocalDate.now().format(formatter)
    // https://stackoverflow.com/a/69688759
    Box(
        Modifier.padding(15.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .heightIn(min = 100.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(boxContentPadding.dp)
            ) {
                Row {
                    Column {
                        Text(text = "Последнее испытание", Modifier.fillMaxWidth(0.5f))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = formattedDate, modifier = Modifier.align(Alignment.End))
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(Icons.Filled.Place, "Add")
                    Text(text = "КЗ Измайлово")
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        val maxImages = 5
        Row(
            modifier = Modifier
                .offset(x = boxContentPadding.dp, y = (profileImageSize / 2).dp)
                .align(Alignment.BottomStart)
                // https://stackoverflow.com/questions/66820206/ripple-with-rounded-corners-jetpack-compose
                // TODO: fix ripple width
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    showBottomSheet = true
                }
        ) {
            for (i in 0..min(event.friends.size - 1, maxImages - 1)) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(event.friends[i].color),
                    modifier = Modifier
                        .offset(x = (-i * profileImageSize / 2).dp)
                        .size(profileImageSize.dp)
                        .clip(CircleShape)
                )
            }

            if (event.friends.size >= maxImages) {
                val remain = event.friends.size - maxImages
                Box(
                    modifier = Modifier
                        .offset(x = (-maxImages * profileImageSize / 2).dp)
                        .size(profileImageSize.dp)
                        .clip(CircleShape)
                        // fff5ee
                        .background(Color(1f, 0.9607843137254902f, 0.9333f, 1f))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "+$remain",
                        fontSize = 14.sp,
                        // 696969
                        color = Color(0.4117647058823529f, 0.4117647058823529f, 0.4117647058823529f)
                    )
                }
            }
        }
    }
}

fun generateWarmSoftColor(): Color {
    val red = Random.nextFloat() * 0.3f + 0.7f  // 70% - 100% Red
    val green = Random.nextFloat() * 0.3f + 0.5f // 50% - 80% Green
    val blue = Random.nextFloat() * 0.3f + 0.4f  // 40% - 70% Blue
    return Color(red, green, blue, 1f) // Full opacity
}

data class FeedEventUi(
    val eventName: String,
    val locationName: String,
    val date: LocalDate = LocalDate.now(),
    val friends: List<EventFriendsUi>
)

data class EventFriendsUi(val name: String, val color: Color)