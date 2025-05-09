package ru.vmestego.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YesNoDialog(
    isDialogOpen: MutableState<Boolean>,
    title: String,
    text: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { isDialogOpen.value = false },
        title = { Text(text = title) },
        text = { Text(text) },
        confirmButton = {
            Button({
                isDialogOpen.value = false
                onConfirm()
            }, border = BorderStroke(1.dp, Color.LightGray)) {
                Text(confirmButtonText, fontSize = 18.sp)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    isDialogOpen.value = false
                    onDismiss()
                },
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(dismissButtonText, fontSize = 18.sp)
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
}