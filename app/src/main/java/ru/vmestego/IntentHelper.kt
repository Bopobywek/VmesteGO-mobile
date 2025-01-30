package ru.vmestego

import android.content.Intent
import android.net.Uri

class IntentHelper {
    companion object {
        fun createOpenPdfIntent(uri: Uri) : Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }
    }
}