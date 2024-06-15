package com.example.benztrack

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class Notification(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "HIGH_CO2_CHANNEL"
        const val CHANNEL_NAME = "High CO2 Consumption"
        const val CHANNEL_DESCRIPTION = "Notifiche di avviso per consumo elevato di CO2"
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002
    }


    // Metodo per creare il canale di notifica
     fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Metodo per verificare e richiedere il permesso per le notifiche
    fun checkAndRequestNotificationPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permesso già concesso, esegui l'operazione che richiedeva l'uso delle notifiche
            enableNotifications()
        } else {
            // Richiedi il permesso all'utente
            requestNotificationPermission(activity)
        }
    }

    private fun requestNotificationPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
            NOTIFICATION_PERMISSION_REQUEST_CODE
        )
    }

    // Metodo per gestire la risposta della richiesta di permesso
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso, esegui l'operazione che richiedeva l'uso delle notifiche
                enableNotifications()
            } else {
                // Permesso negato, informa l'utente
                Toast.makeText(
                    context,
                    "Per utilizzare le notifiche è necessario concedere il permesso",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Metodo per abilitare le notifiche
    private fun enableNotifications() {
        // Esempio di invio di una notifica
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Titolo della notifica")
            .setContentText("Testo della notifica")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // Metodo per verificare se le notifiche sono abilitate per l'app
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    // Metodo per richiedere all'utente di abilitare le notifiche
    fun requestNotificationPermission() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // Metodo per inviare la notifica di avviso per consumo elevato di CO2
    fun sendCO2WarningNotification(co2Value: Double) {
        // Ottieni il NotificationManager dal context
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Costruisci la notifica usando NotificationCompat.Builder
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert) // Icona della notifica
            .setContentTitle("Consumo elevato di CO2")
            .setContentText("Hai consumato troppa CO2: $co2Value") // Testo della notifica
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorità della notifica
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true) // Chiude la notifica quando l'utente la tocca
            .build()

        // Invia la notifica usando notificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // Metodo per inviare la notifica di elogio per basso consumo di CO2
    fun sendCO2PraiseNotification(co2Value: Double) {
        // Ottieni il NotificationManager dal context
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Costruisci la notifica usando NotificationCompat.Builder
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Ottimo lavoro!")
            .setContentText("Hai mantenuto un basso consumo di CO2. Continua così!") // Testo della notifica
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Priorità della notifica
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true) // Chiude la notifica quando l'utente la tocca
            .build()

        // Invia la notifica usando notificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}




/*package com.example.benztrack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity

class Notification(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "HIGH_CO2_CHANNEL"
        const val CHANNEL_NAME = "High CO2 Consumption"
        const val CHANNEL_DESCRIPTION = "Notifiche di avviso per consumo elevato di CO2"
    }

    // Metodo per creare il canale di notifica
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Metodo per inviare la notifica di avviso per consumo elevato di CO2
    fun sendCO2WarningNotification(co2Value: Double) {
        // Ottieni il NotificationManager dal context
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Costruisci la notifica usando NotificationCompat.Builder
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_warning_24) // Icona della notifica
            .setContentTitle("Consumo elevato di CO2")
            .setContentText("Hai consumato troppa CO2: $co2Value") // Testo della notifica
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Priorità della notifica
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true) // Chiude la notifica quando l'utente la tocca
            .build()

        // Invia la notifica usando notificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // Metodo per inviare la notifica di elogio per basso consumo di CO2
    fun sendCO2PraiseNotification(co2Value: Double) {
        // Ottieni il NotificationManager dal context
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Costruisci la notifica usando NotificationCompat.Builder
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_emoji_emotions_24)
            .setContentTitle("Ottimo lavoro!")
            .setContentText("Hai mantenuto un basso consumo di CO2. Continua così!") // Testo della notifica
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Priorità della notifica
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true) // Chiude la notifica quando l'utente la tocca
            .build()

        // Invia la notifica usando notificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    // Metodo per verificare se le notifiche sono abilitate per l'app
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    // Metodo per richiedere all'utente di abilitare le notifiche
    // Metodo per richiedere il permesso di notifica all'utente
    // Richiede all'utente di abilitare le notifiche per l'app
    fun requestNotificationPermission() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}



 */