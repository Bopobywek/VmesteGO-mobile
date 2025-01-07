package ru.vmestego

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun TicketCard(ticket: Ticket) {
    val formatter = DateTimeFormatter.ofPattern("EE, dd MMM. yyyy", Locale("ru")) // Russian locale
    val formattedDate = ticket.date.format(formatter)
    Column(
        modifier = Modifier
            .clickable { Log.i("Main", "hello") }
            .fillMaxWidth()
            .heightIn(70.dp)
            .padding(20.dp)
            .background(
                Color.LightGray,
                shape = RoundedCornerShape(20)
            )
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row {
            Column {
                Text(text = "Последнее испытание", Modifier.fillMaxWidth(0.5f))
            }
            Column {
                Text(text = formattedDate)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Icon(Icons.Filled.Place, "Add")
            Text(text = "КЗ Измайлово")
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
        Divider(color = color)
    }
}

fun isCurrentMonth(date: LocalDate): Boolean =
    date.month == LocalDate.now().month && date.year == LocalDate.now().year

fun getCurrentMonthHeaderIndex(grouped: Map<LocalDate, List<Ticket>>): Int {
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
fun TicketList(tickets: List<Ticket>) {
    val grouped = tickets.groupBy { it.date.withDayOfMonth(1) }
    val ordered = grouped.toSortedMap()

    // https://stackoverflow.com/a/74227507
    val initialIndex = getCurrentMonthHeaderIndex(ordered)
    Log.i("Tickets", "current index $initialIndex")
    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    LazyColumn(state = listState) {
        ordered.forEach { (date, dateTickets) ->
            // Render header composable using [date]
            item {
                DateHeader(date)
            }

            val sortedTickets = dateTickets.sortedBy { t -> t.date }
            // Render list of [people] who have the same [date]
            items(sortedTickets) { ticket ->
                TicketCard(ticket)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TicketsScreen(ticketsViewModel: TicketsViewModel = viewModel()) {
    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(
                shape = CircleShape,
                onClick = { ticketsViewModel.addTicket() }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )
    {
        TicketList(ticketsViewModel.tickets)
    }
}