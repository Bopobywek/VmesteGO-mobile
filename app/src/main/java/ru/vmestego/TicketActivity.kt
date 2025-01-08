package ru.vmestego

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import ru.vmestego.ui.theme.VmesteGOTheme

class TicketActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VmesteGOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    TicketSettingsScreen("url")

                }
            }
        }

        when {
            intent?.action == Intent.ACTION_SEND -> {
                val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri
                Log.i("TicketActivity", uri.toString())
                Log.i("TicketActivity", Environment.getExternalStoragePublicDirectory("").path)
                //contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val stream = contentResolver.openInputStream(uri)
                Log.i("TicketActivity", stream?.read().toString())
                stream?.close()
            }
        }
    }
}

@Composable
fun TicketSettingsScreen(url: String) {
    val activity = (LocalContext.current as Activity)
    Button(onClick = {
        activity.startActivity(Intent(activity, MainActivity::class.java))
        activity.finish()
    }) {
        Text(text = "Exit")
    }
}

@Preview(showBackground = true)
@Composable
fun Page() {

}