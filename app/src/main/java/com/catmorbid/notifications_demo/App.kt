package com.catmorbid.test_notifications

import android.support.v4.app.NotificationCompat
import android.support.v4.app.Person

object App
{
    val ACTION_DIRECT_REPLY = "Demo.Action.Broadcast";
    val channelPrivate : String = "Demo.Notifications"
    val channelPublic : String = "Demo.Notifications.public"
    val channelSecret : String = "Demo.Notifications.secret"
    val channelImportanceNone : String = "Demo.Priority.None"
    val channelImportanceLow : String = "Demo.Priority.Low"
    val channelImportanceMin : String = "Demo.Priority.Min"
    val channelImportanceDefault : String = "Demo.Priority.Default"
    val channelImportanceHigh : String = "Demo.Priority.High"
    val channelImportanceMax : String = "Demo.Priority.Max"

    /**
     * A helper object to hold dummy data for message service
     */
    object messageDB{
        val messages = createMessages()
        private fun createMessages() : ArrayList<NotificationCompat.MessagingStyle.Message>
        {
            val array: ArrayList<NotificationCompat.MessagingStyle.Message> = ArrayList()
            //using person.builder to in order to not use deprecated constructor for Message class
            val sender: Person = Person.Builder().setName("John Doe").build()
            array.add(NotificationCompat.MessagingStyle.Message("Hello", System.currentTimeMillis(), sender))
            return array
        }
    }
}