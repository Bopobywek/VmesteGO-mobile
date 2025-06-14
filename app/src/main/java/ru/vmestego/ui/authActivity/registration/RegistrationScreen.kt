package ru.vmestego.ui.authActivity.registration

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
import androidx.compose.ui.focus.onFocusChanged
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
                isError = viewModel.loginError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            viewModel.loginFocusedOnce = true
                            viewModel.validateLogin()
                        }
                    }
            )
            if (viewModel.loginError != null) {
                Text(
                    text = viewModel.loginError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Email") },
                isError = viewModel.emailError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            viewModel.emailFocusedOnce = true
                            viewModel.validateEmail()
                        }
                    }
            )
            if (viewModel.emailError != null) {
                Text(
                    text = viewModel.emailError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                isError = viewModel.passwordError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            viewModel.passwordFocusedOnce = true
                            viewModel.validatePassword()
                        }
                    }
            )
            if (viewModel.passwordError != null) {
                Text(
                    text = viewModel.passwordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(2.dp),
                    color = MaterialTheme.colorScheme.primary
                )
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

