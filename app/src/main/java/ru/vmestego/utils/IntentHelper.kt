package ru.vmestego.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import ru.vmestego.ui.mainActivity.event.EventUi
import ru.vmestego.ui.ticketActivity.TicketActivity
import ru.vmestego.ui.ticketActivity.TicketActivityParams
import ru.vmestego.ui.ticketActivity.models.EventRouteDto

class IntentHelper {
    companion object {
        fun createOpenPdfIntent(uri: Uri): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            return intent
        }

        fun createOpenTicketActivityIntent(context: Context, uri: Uri): Intent {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            val intent = Intent(context, TicketActivity::class.java)
            intent.action = Intent.ACTION_SEND
            intent.type = "application/pdf"
            intent.putExtra(Intent.EXTRA_STREAM, uri)

            return intent
        }

        fun createOpenTicketWithEventActivityIntent(
            context: Context,
            uri: Uri,
            eventRouteDto: EventRouteDto
        ): Intent {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            val params = TicketActivityParams(
                uri = uri,
                eventDto = eventRouteDto
            )
            val intent = Intent(context, TicketActivity::class.java)
            intent.action = "TICKET_FOR_EVENT"
            intent.putExtra(Intent.EXTRA_STREAM, params)

            return intent
        }
    }
}