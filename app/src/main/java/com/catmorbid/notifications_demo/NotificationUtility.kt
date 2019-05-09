package com.catmorbid.test_notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import com.catmorbid.notifications_demo.R

/**
 * Utility object for sending Notifications
 * NOTE!!! You need to reinstall app for new notification settings to apply automatically. Otherwise they need manual change from app settings (this is a built-in android 'feature' apparently!)
 */
object NotificationUtility{

    const val KEY_TEXT_REPLY = "key_text_reply"
    const val KEY_NOTIFICATION_ID: String = "key_notification_id"
    const val KEY_CONVERSATION_ID: String = "key_conversation_id"
    var visibility = NotificationCompat.VISIBILITY_PRIVATE
    var category = NotificationCompat.CATEGORY_SYSTEM
    var priority = NotificationCompat.PRIORITY_HIGH

    var nextNotificationId : Int = 1


    fun createNotification(c : Context, s: String, channelId : String, vararg actions : NotificationCompat.Action) {

        val notificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager      //java Notificationmanager.class

        val publicBuilder : NotificationCompat.Builder = NotificationCompat.Builder(c, channelId)
                .setContentTitle("// New Message")
                .setContentText("** Hidden Content **")
//                .setSmallIcon(android.R.drawable.stat_notify_chat)
                //.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(c, channelId )
                .setContentTitle("// New Message")
                .setContentText(s)
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setStyle(NotificationCompat.BigTextStyle().bigText(s))
                .setPriority(priority)                                 //this is the same things for api < 26 IMPORTANCE_HIGH for channel
                .setVisibility(visibility)                              //determines if notification should show on lock screen
                .setCategory(category)                                  //usability feature
                .setDefaults(Notification.DEFAULT_ALL)                  //adds sounds and vibration to notification
        builder.setPublicVersion(publicBuilder.build())

        for (action: NotificationCompat.Action in actions) {
            builder.addAction(action)
        }

        notificationManager.notify(nextNotificationId, builder.build())
     }

    /**
     * Create a notification which corresponds to a received message from e.g. sms or messaging app
     */
    fun notifyMessage(c : Context, notificationId:Int, channelId : String, messages: Array<NotificationCompat.MessagingStyle.Message>, vararg actions : NotificationCompat.Action) {

        val builder: NotificationCompat.Builder = createMessageNotificationBase(c, channelId, messages)

        for (action: NotificationCompat.Action in actions) {
            builder.addAction(action)
        }

        getManager(c).notify(notificationId, builder.build())
    }

    /**
     * create messaging notification from bunch of messages, but without any actions
     */
    fun createMessageNotificationBase(c: Context, channelId: String, messages: Array<NotificationCompat.MessagingStyle.Message> ) : NotificationCompat.Builder
    {
        val publicBuilder : NotificationCompat.Builder = NotificationCompat.Builder(c, channelId)
                .setContentTitle("// Hidden Messages")//
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(c, channelId )
                .setSmallIcon(android.R.drawable.stat_notify_chat)
                .setStyle(NotificationCompat.MessagingStyle("John Doe")
                        .also {
                            it.conversationTitle = "// Messages"
                            for (msg: NotificationCompat.MessagingStyle.Message in messages)
                                it.addMessage(msg)
                        })
                .setPriority(priority)                                 //this is the same things for api < 26 IMPORTANCE_HIGH for channel
                .setVisibility(visibility)                              //determines if notification should show on lock screen
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)                                  //usability feature
                .setDefaults(Notification.DEFAULT_ALL)                  //adds sounds and vibrati
        builder.setPublicVersion(publicBuilder.build())

        return builder
    }


    fun createNotificationChannel(c : Context, channelId : String, name : String, description : String, importance : Int, visibility: Int)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.lockscreenVisibility = visibility
            channel.enableVibration(true)
            channel.setShowBadge(true)

            getManager(c).createNotificationChannel(channel)
        }
    }

    fun getDirectReplyAction(context: Context, conversationId: Int, intent: Intent) : NotificationCompat.Action
    {
        val remoteInput: RemoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel("Reply")
                .build()

        val replyIntent : PendingIntent = PendingIntent.getBroadcast(context.applicationContext, conversationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Action.Builder(R.drawable.ic_baseline_reply_24px, "Reply", replyIntent)
                .addRemoteInput(remoteInput)
                .build()

    }


    fun getMessageReplyIntent(notificationId : Int, conversationId: Int): Intent {
        return Intent(App.ACTION_DIRECT_REPLY)
                .putExtra(KEY_NOTIFICATION_ID, notificationId)
                .putExtra(KEY_CONVERSATION_ID, conversationId)

    }

    fun getChannel(context : Context, channel: String): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getManager(context).getNotificationChannel(channel)
        }
        return null
    }

    fun getManager(c: Context): NotificationManager {
        return c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
    }

}