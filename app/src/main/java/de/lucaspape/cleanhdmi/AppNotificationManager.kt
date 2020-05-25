package de.lucaspape.cleanhdmi

import java.util.*

class AppNotificationManager {
    interface NotificationListener {
        fun onNotify(message: String?)
    }

    private val listeners =
        ArrayList<NotificationListener>()

    fun notify(message: String?) {
        for (listener in listeners) listener.onNotify(
            message
        )
    }

    fun addListener(listener: NotificationListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: NotificationListener) {
        listeners.remove(listener)
    }

    companion object {
        val instance = AppNotificationManager()
    }
}
