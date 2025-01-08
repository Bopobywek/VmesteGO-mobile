package ru.vmestego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ru.vmestego.ui.theme.VmesteGOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VmesteGOTheme {
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



//@Preview(showBackground = true)
//@Composable
//fun VmesteGOPreview() {
//    val ticketsViewModel = TicketsViewModel()
//    ticketsViewModel.addTicket(Uri.EMPTY)
//    ticketsViewModel.addTicket(Uri.EMPTY)
//    ticketsViewModel.addTicket(Uri.EMPTY)
//    ticketsViewModel.addTicket(Uri.EMPTY)
//    VmesteGOTheme {
//        TicketsScreen(ticketsViewModel)
//    }
//}