package ru.vmestego.ui.mainActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.vmestego.ui.authActivity.AuthActivity
import ru.vmestego.data.SecureStorage
import ru.vmestego.ui.theme.VmesteGOTheme

class MainActivity : ComponentActivity() {
    private lateinit var secureStorage: SecureStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secureStorage = SecureStorage.getStorageInstance(this)

        if (secureStorage.getToken().isNullOrEmpty()) {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        setContent {
            VmesteGOTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen()
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun VmesteGOPreview() {
    VmesteGOTheme {
        AppScreen()
    }
}