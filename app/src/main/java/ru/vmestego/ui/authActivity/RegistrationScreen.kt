package ru.vmestego.ui.authActivity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistrationScreen(onGoBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TextButton(
                onClick = onGoBackClick,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text("Назад")
            }
        })
    { innerPadding ->
        RegistrationScreenContent(innerPadding) { onGoBackClick() }
    }
}

@Composable
fun RegistrationScreenContent(innerPadding: PaddingValues, viewModel: RegistrationViewModel = viewModel(), onGoBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Новый аккаунт",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 13.dp)
            )

            OutlinedTextField(
                value = viewModel.login,
                onValueChange = { viewModel.updateLogin(it) },
                label = { Text("Логин") },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.loginHasErrors,
                supportingText = {
                    if (viewModel.loginHasErrors) {
                        Text("Поле должно быть заполнено")
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.emailHasErrors,
                supportingText = {
                    if (viewModel.emailHasErrors) {
                        Text("Incorrect email format.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = viewModel.passwordHasErrors,
                supportingText = {
                    if (viewModel.passwordHasErrors) {
                        Text("Слабый пароль")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp),
                    color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = { viewModel.registerUser(onGoBackClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !viewModel.hasValidationErrors
                ) {
                    Text(text = "Зарегистрироваться", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

