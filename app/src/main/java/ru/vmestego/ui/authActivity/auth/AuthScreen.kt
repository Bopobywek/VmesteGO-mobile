package ru.vmestego.ui.authActivity.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.vmestego.ui.mainActivity.MainActivity

@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModel(), onRegistrationClick: () -> Unit) {
    val activity = LocalContext.current as Activity

    LaunchedEffect(viewModel.authorizeError) {
        if (viewModel.authorizeError.isNotEmpty()) {
            Toast
                .makeText(activity, viewModel.authorizeError, Toast.LENGTH_SHORT)
                .show()

            viewModel.clearErrors()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вход",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 13.dp)
            )

            OutlinedTextField(
                value = viewModel.login,
                onValueChange = { viewModel.updateLogin(it) },
                label = { Text("Логин") },
                supportingText = {
                    if (viewModel.loginError != null) {
                        Text(viewModel.loginError!!)
                    }
                },
                isError = viewModel.loginError != null,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Пароль") },
                supportingText = {
                    if (viewModel.passwordError != null) {
                        Text(viewModel.passwordError!!)
                    }
                },
                isError = viewModel.passwordError != null,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!viewModel.isLoading) {
                Button(
                    onClick = {
                        viewModel.authorizeUser {
                            activity.startActivity(Intent(activity, MainActivity::class.java))
                            activity.finish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Войти", fontWeight = FontWeight.Bold)
                }
            } else {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onRegistrationClick) {
                Text(
                    text = "Ещё нет аккаунта? Зарегистрируйтесь",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}