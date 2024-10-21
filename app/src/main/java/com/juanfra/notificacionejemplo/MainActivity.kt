package com.juanfra.notificacionejemplo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/*
Autor: Juan Francisco Sánchez González
Fecha: 21/10/2024
Clase: Actividad que implementa la construcción y lanzamiento de una notificación, se configura para que
al pulsarla abra una actividad de destino.
*/

class MainActivity : AppCompatActivity() {

    // Constantes necesarias para la notificación
    object NotificacionConstantes {
        const val CANAL_ID = "canal_ejemplo"
        const val CANAL_NOMBRE = "Canal de Ejemplo"
        const val CANAL_DESCRIPCION = "Descripción del canal de ejemplo"
        const val NOTIFICACION_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mostrarNotificacion(this)
    }

   // Se construye y se lanza la notificación
   fun mostrarNotificacion(context: Context) {
       // Verificar el permiso para mostrar la notificación (necesario en Android 13 y superior)
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
           if (ActivityCompat.checkSelfPermission(
                   context,
                   android.Manifest.permission.POST_NOTIFICATIONS
               ) != PackageManager.PERMISSION_GRANTED
           ) {
               // El permiso no ha sido concedido, manejar la situación o solicitar el permiso
               return // Salir de la función si el permiso no está disponible
           }
       }
       // Crear el canal de notificación para Android 8.0 o superior
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           val importancia = NotificationManager.IMPORTANCE_DEFAULT
           val canal = NotificationChannel(NotificacionConstantes.CANAL_ID, NotificacionConstantes.CANAL_NOMBRE, importancia).apply {
               description = NotificacionConstantes.CANAL_DESCRIPCION
           }
           // Registrar el canal en el sistema
           val notificationManager: NotificationManager =
               context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
           notificationManager.createNotificationChannel(canal)
       }
       // Crear el Intent para abrir la actividad de destino
       val intent = Intent(context, DestinoActivity::class.java).apply {
           flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
       }
       // Crear el PendingIntent
       val pendingIntent: PendingIntent = PendingIntent.getActivity(
           context,
           0,
           intent,
           PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
       )
       // Crear la notificación
       val notificacion = NotificationCompat.Builder(context, NotificacionConstantes.CANAL_ID)
           .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono de la notificación
           .setContentTitle(getString(R.string.not_tit_txt))
           .setContentText(getString(R.string.not_mens_txt))
           .setPriority(NotificationCompat.PRIORITY_DEFAULT)
           .setContentIntent(pendingIntent) // Establecer el PendingIntent
           .setAutoCancel(true) // La notificación se cancela al pulsar sobre ella
           .build()
       // Mostrar la notificación
       with(NotificationManagerCompat.from(context)) {
           notify(NotificacionConstantes.NOTIFICACION_ID, notificacion)
       }
   }

}