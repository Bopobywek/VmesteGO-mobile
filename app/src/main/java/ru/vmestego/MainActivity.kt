package ru.vmestego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.Room
import dagger.hilt.android.AndroidEntryPoint
import ru.vmestego.data.AppDatabase
import ru.vmestego.ui.TicketsScreen
import ru.vmestego.ui.TicketsViewModel
import ru.vmestego.ui.theme.VmesteGOTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val ticketsViewModel: TicketsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VmesteGOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketsScreen(ticketsViewModel)
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