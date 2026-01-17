package com.example.gestaodetarefasestudos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.gestaodetarefasestudos.utils.TaskNotificationScheduler;

/**
 * Receiver para reagendar notificacoes de tarefas apos boot do dispositivo
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reagendar todas as notificacoes de tarefas
            TaskNotificationScheduler scheduler = new TaskNotificationScheduler(context);
            scheduler.rescheduleAllNotifications();
        }
    }
}
