package com.davidgyoungtech.cdmdemo

import android.app.NotificationChannel
import android.content.Intent
import android.os.IBinder
import android.os.UserHandle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MyNotificationListenerService: NotificationListenerService() {


    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind called")
        return super.onBind(intent)
    }

    override fun onListenerConnected() {
        Log.d(TAG, "connected")
        super.onListenerConnected()
    }

    override fun onListenerDisconnected() {
        Log.d(TAG, "disconnected")
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?, rankingMap: RankingMap?) {
        Log.d(TAG, "notification posted")
        super.onNotificationPosted(sbn, rankingMap)
    }

    override fun onNotificationRankingUpdate(rankingMap: RankingMap?) {
        super.onNotificationRankingUpdate(rankingMap)
    }

    override fun onNotificationChannelModified(
        pkg: String?,
        user: UserHandle?,
        channel: NotificationChannel?,
        modificationType: Int
    ) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType)
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        super.onNotificationRemoved(sbn, rankingMap, reason)
    }

    companion object {
        val TAG = "MyNotificationListenerService"
    }

}