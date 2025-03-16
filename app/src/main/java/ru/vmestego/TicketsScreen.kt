package ru.vmestego

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun TicketCard(ticket: TicketUi) {
    val formatter = DateTimeFormatter.ofPattern("EE, dd MMM. yyyy", Locale("ru")) // Russian locale
    val formattedDate = ticket.date.format(formatter)
    val context = LocalContext.current

    Box(
        Modifier.padding(20.dp)
    ) {
    ElevatedCard(
        modifier = Modifier
            .heightIn(min = 100.dp)
            .fillMaxWidth()
            .clickable {
                Log.i("Main", "hello")
                // https://stackoverflow.com/a/48950071
                val intent = IntentHelper.createOpenPdfIntent(ticket.ticketUri)
                context.startActivity(intent)
            }) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)) {
                Row {
                    Column {
                        Text(text = ticket.eventName, Modifier.fillMaxWidth(0.5f))
                    }
                    Column {
                        Text(text = formattedDate)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(Icons.Filled.Place, "Add")
                    Text(text = ticket.locationName)
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: LocalDate) {
    val color = if (isCurrentMonth(date)) Color.Red else Color.Black
    val monthsTranslations = stringArrayResource(id = R.array.months_ru)
    val monthName = monthsTranslations[date.month.value - 1]
    val year = date.year
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        Text(
            text = "$monthName, $year",
            color = color
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = color)
    }
}

fun isCurrentMonth(date: LocalDate): Boolean =
    date.month == LocalDate.now().month && date.year == LocalDate.now().year

fun <T> getCurrentMonthHeaderIndex(grouped: Map<LocalDate, List<T>>): Int {
    var counter = 0
    for (pair in grouped) {
        if (isCurrentMonth(pair.key)) {
            return counter
        }
        counter += pair.value.size + 1
    }

    return 0
}

// https://stackoverflow.com/questions/71195961/item-headers-not-displaying-correctly-in-lazy-column
// TODO: a lot of sorting and O(n) algorithms, rewrite it late
@Composable
fun TicketList(tickets: List<TicketUi>) {
    val grouped = tickets.groupBy { it.date.withDayOfMonth(1) }
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
        ordered.forEach { (date, dateTickets) ->
            item {
                DateHeader(date)
            }

            val sortedTickets = dateTickets.sortedBy { t -> t.date }
            items(sortedTickets) { ticket ->
                TicketCard(ticket)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TicketsScreen(ticketsViewModel: TicketsViewModel = viewModel()) {

    // https://commonsware.com/blog/2020/08/08/uri-access-lifetime-still-shorter-than-you-might-think.html
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            run {
                Log.i("TicketsScreen", uri?.encodedPath.toString())
                if (uri != null) {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                    val intent = Intent(context, TicketActivity::class.java)
                    intent.setAction(Intent.ACTION_SEND)
                    intent.setType("application/pdf")
                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    context.startActivity(intent)
                }
            }
        }

    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(
                shape = CircleShape,
                onClick = {
                    launcher.launch(arrayOf("application/pdf"))
                }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        TicketList(ticketsViewModel.tickets)
    }
}