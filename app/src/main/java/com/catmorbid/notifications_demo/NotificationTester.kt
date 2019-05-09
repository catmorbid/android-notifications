package com.catmorbid.test_notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.catmorbid.notifications_demo.R

class NotificationTester : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_notiftester, container, false)

    private var m_toast : Toast? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateNotificationChannels()
        view.findViewById<Button>(R.id.buttonNotifyPrivate).also{
            it?.setOnClickListener {_-> sendNotification(App.channelPrivate)}
        }
        view.findViewById<Button>(R.id.buttonNotifyPublic).also{
            it?.setOnClickListener{_->sendNotification(App.channelPublic)}
        }
        view.findViewById<Button>(R.id.buttonNotifySecret).also{
            it?.setOnClickListener{_->sendNotification(App.channelSecret)}
        }
        view.findViewById<Button>(R.id.buttonPriorityNone).also{
            it?.setOnClickListener{_->sendNotification(App.channelImportanceNone)}
        }
        view.findViewById<Button>(R.id.buttonPriorityMin).also{
            it?.setOnClickListener{_->sendNotification(App.channelImportanceMin)}
        }
        view.findViewById<Button>(R.id.buttonPriorityLow).also{
            it?.setOnClickListener{_->sendNotification(App.channelImportanceLow)}
        }
        view.findViewById<Button>(R.id.buttonPriorityDefault).also{
            it?.setOnClickListener{_->sendNotification(App.channelImportanceDefault)}
        }
        view.findViewById<Button>(R.id.buttonPriorityHigh).also{
            it?.setOnClickListener{_->sendNotification(App.channelImportanceHigh)}
        }
        view.findViewById<Button>(R.id.buttonPriorityMax).also{
            it?.setOnClickListener{_->sendNotification(App.channelImportanceMax)}
        }
        view.findViewById<Button>(R.id.buttonNotifyActionReply).also{
            it?.setOnClickListener{_->sendMessageNotificationWithSMSReply()}
        }
        view.findViewById<Button>(R.id.buttonDirectReply).also{
            it?.setOnClickListener{_->sendMessageNotificationWithDirectReply()}
        }

        //Register broadcastReceiver with lifetime tied to application life cycle
        context!!.registerReceiver(BroadcastHandler.Singleton, IntentFilter(App.ACTION_DIRECT_REPLY))

    }

    companion object {
        fun newInstance() : NotificationTester = NotificationTester()
    }

    /**
     * Create a notification with Reply action which opens default SMS service and adds a pre-written reply, waiting to be sent.
     * E.g. to be used with a custom SMS-notification receiver, or any other messaging app, by changing t
     */
    private fun sendMessageNotificationWithSMSReply()
    {
        configChannel(App.channelImportanceHigh)

        val intent: Intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("sms:+000123456789")        //create intent which has data that corresponds to sms app uri, so the sms app can handle it properly.
        intent.putExtra("sms_body","Hello Yes hi!")     //default reply action content should be pre-determined when creating a notification action
        val pi : PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val action: NotificationCompat.Action.Builder = NotificationCompat.Action.Builder(R.drawable.ic_baseline_reply_24px, "Reply", pi);

        val nid: Int = NotificationUtility.nextNotificationId++

        NotificationUtility.notifyMessage(context!!, nid,App.channelImportanceHigh,App.messageDB.messages.toTypedArray(), action.build())
    }

    /**
     * Create a notification which has support for direct reply action, to allow replying in notification without opening the app. e.g. sms app or messaging app.
     */
    private fun sendMessageNotificationWithDirectReply()
    {
        configChannel(App.channelImportanceHigh)

        val conversationId: Int = 5326474   //unique id per conversation!

        val nId: Int = NotificationUtility.nextNotificationId++

        NotificationUtility.notifyMessage(context!!, nId,
                App.channelImportanceHigh,
                App.messageDB.messages.toTypedArray(),
                NotificationUtility.getDirectReplyAction(
                        context!!,
                        conversationId,
                        NotificationUtility.getMessageReplyIntent(nId, conversationId)
                )
        )
    }

    private fun configChannel(channel : String)
    {
        /**
         * I'm using Channel to determine both api < 26 and api 26+ behaviour
         */
        when (channel)
        {
            App.channelPublic -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_HIGH
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            App.channelPrivate -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_HIGH
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            App.channelSecret -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_HIGH
                NotificationUtility.visibility =  NotificationCompat.VISIBILITY_SECRET
            }
            App.channelImportanceNone, App.channelImportanceMin -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_MIN
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            App.channelImportanceLow -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_LOW
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            App.channelImportanceDefault -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_DEFAULT
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            App.channelImportanceHigh -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_HIGH
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            App.channelImportanceMax -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_MAX
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
            else -> {
                NotificationUtility.priority = NotificationCompat.PRIORITY_DEFAULT
                NotificationUtility.visibility = NotificationCompat.VISIBILITY_PRIVATE
            }
        }
    }

    private fun sendNotification(channel : String)
    {
        configChannel(channel)

        val text : String = "Notification on Channel: $channel"
        NotificationUtility.createNotification(context!!, text, channel)
        m_toast?.cancel()
        m_toast = Toast.makeText(context, "Notification sent! id: "+(NotificationUtility.nextNotificationId-1)+" on channel $channel", Toast.LENGTH_SHORT)
        m_toast?.show()
    }

    /**
     * Android 8 (API 26+) requires the use of Notification channels
     * You can configure multiple channels per app with different settings eaach
     * For HeadsUP notifications, the most important setting is HIGH IMPORTANCE
     * User can override any of these settings and you cannot then set them back from code any longer! (ANDROID FEATURE!)
     */
    private fun updateNotificationChannels()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtility.createNotificationChannel(context!!, App.channelPrivate, "Demo-private", "Demo Notifications", NotificationManager.IMPORTANCE_HIGH, NotificationCompat.VISIBILITY_PRIVATE)
            NotificationUtility.createNotificationChannel(context!!, App.channelPublic, "Demo-public", "Public notifications", NotificationManager.IMPORTANCE_HIGH, NotificationCompat.VISIBILITY_PUBLIC)
            NotificationUtility.createNotificationChannel(context!!, App.channelSecret, "Demo-secret", "Secret notifications", NotificationManager.IMPORTANCE_HIGH, NotificationCompat.VISIBILITY_SECRET)
            NotificationUtility.createNotificationChannel(context!!, App.channelImportanceNone, "Demo-ImportanceNone", "Secret notifications", NotificationManager.IMPORTANCE_NONE, NotificationCompat.VISIBILITY_PRIVATE)
            NotificationUtility.createNotificationChannel(context!!, App.channelImportanceLow, "Demo-ImportanceLow", "Secret notifications", NotificationManager.IMPORTANCE_LOW, NotificationCompat.VISIBILITY_PRIVATE)
            NotificationUtility.createNotificationChannel(context!!, App.channelImportanceMin, "Demo-ImportanceMin", "Secret notifications", NotificationManager.IMPORTANCE_MIN, NotificationCompat.VISIBILITY_PRIVATE)
            NotificationUtility.createNotificationChannel(context!!, App.channelImportanceDefault, "Demo-ImportanceDefault", "Secret notifications", NotificationManager.IMPORTANCE_DEFAULT, NotificationCompat.VISIBILITY_PRIVATE)
            NotificationUtility.createNotificationChannel(context!!, App.channelImportanceHigh, "Demo-ImportanceHigh", "Secret notifications", NotificationManager.IMPORTANCE_HIGH, NotificationCompat.VISIBILITY_PRIVATE)
            NotificationUtility.createNotificationChannel(context!!, App.channelImportanceMax, "Demo-ImportanceMax", "Secret notifications", NotificationManager.IMPORTANCE_MAX, NotificationCompat.VISIBILITY_PRIVATE)
        }
    }
}