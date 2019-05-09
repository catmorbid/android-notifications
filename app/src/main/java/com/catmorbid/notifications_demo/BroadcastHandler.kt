package com.catmorbid.test_notifications

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NotificationCompat

class BroadcastHandler : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == App.ACTION_DIRECT_REPLY) {
            val message = getMessageText(intent)
            val nid: Int = intent.getIntExtra(NotificationUtility.KEY_NOTIFICATION_ID, 0)
            val cid: Int = intent.getIntExtra(NotificationUtility.KEY_CONVERSATION_ID, 0)
            updateMessageNotification(context, nid, cid, message)
            //would send actual sms message here!
        }
    }

    private fun updateMessageNotification(context: Context?, notificationId: Int, conversationId: Int, message: CharSequence?) {

        if (context != null) {

            App.messageDB.messages.add(NotificationCompat.MessagingStyle.Message(message, System.currentTimeMillis(), "Random Brandon"))
            NotificationUtility.notifyMessage(
                    context,
                    notificationId,
                    App.channelImportanceHigh,
                    App.messageDB.messages.toTypedArray(),
                    NotificationUtility.getDirectReplyAction(context, conversationId, NotificationUtility.getMessageReplyIntent(
                            notificationId,
                            conversationId)
                    )
            )
        }
    }

    fun getMessageText(intent: Intent): CharSequence? {
        val remoteInput: Bundle = RemoteInput.getResultsFromIntent(intent)
        if (remoteInput != null) {
            return remoteInput.getCharSequence(NotificationUtility.KEY_TEXT_REPLY)
        }
        return null
    }

    companion object {
        val Singleton: BroadcastHandler = BroadcastHandler()
    }

}