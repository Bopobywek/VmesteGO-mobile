package ru.vmestego

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.vmestego.ui.theme.VmesteGOTheme
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VmesteGOTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketsScreen()
                }
            }
        }
    }
}

data class Ticket(val name: String, val date: LocalDate = LocalDate.now())

@Composable
fun TicketRow(ticket: Ticket) {
    Column(
        modifier = Modifier
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
                Text(text = "Сб, 19 окт. 2024 ")
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ) {
        Text(text = date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = Color.Black)
    }
}


// https://stackoverflow.com/questions/71195961/item-headers-not-displaying-correctly-in-lazy-column
@Composable
fun TicketList(tickets: List<Ticket>) {
    val grouped = tickets.groupBy { it.date }
    // https://stackoverflow.com/a/74227507
    // TODO: нужен оффсет именно хедера
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = tickets.indexOfFirst { t -> t.date.month == LocalDate.now().month })
    LazyColumn(state = listState) {
        grouped.forEach { (date, dateTickets) ->
            // Render header composable using [date]
            item {
                DateHeader(date)
            }

            // Render list of [people] who have the same [date]
            items(dateTickets) { ticket ->
                TicketRow(ticket)
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TicketsScreen() {
    Scaffold(
        floatingActionButton = {
            LargeFloatingActionButton(
                shape = CircleShape,
                onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Add, "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    )
    {
        TicketList(
            listOf(
                Ticket("1", LocalDate.of(2024, 12, 1)),
                Ticket("1", LocalDate.of(2024, 2, 1)),
                Ticket("1", LocalDate.of(2024, 1, 1)),
                Ticket("1", LocalDate.of(2024, 3, 1)),
                Ticket("1", LocalDate.of(2024, 4, 1)),
                Ticket("1", LocalDate.of(2024, 5, 1)),
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun VmesteGOPreview() {
    VmesteGOTheme {
        TicketsScreen()
    }
}