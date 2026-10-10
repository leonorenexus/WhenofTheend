package com.`when`.oftheend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/** User-initiated only: opens the dialer / SMS composer. Nothing is sent without the user pressing send/call. */
fun launchContact(ctx: Context, c: Contact) {
    val sms = c.pref == "SMS"
    val uri = Uri.parse((if (sms) "smsto:" else "tel:") + Uri.encode(c.phone))
    val intent = Intent(if (sms) Intent.ACTION_SENDTO else Intent.ACTION_DIAL, uri)
    if (sms) intent.putExtra("sms_body", "I missed a scheduled check-in. Sending this manually to let you know.")
    try {
        ctx.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(ctx, "NO APP AVAILABLE FOR THIS ACTION", Toast.LENGTH_SHORT).show()
    }
}
