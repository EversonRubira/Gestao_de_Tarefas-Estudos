package com.example.gestaodetarefasestudos.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.gestaodetarefasestudos.MainActivity;
import com.example.gestaodetarefasestudos.R;

/**
 * Helper class para gerenciar todas as notificacoes do app
 */
public class NotificationHelper {

    // Channel IDs
    public static final String CHANNEL_TIMER = "timer_channel";
    public static final String CHANNEL_TASKS = "tasks_channel";

    // Notification IDs
    public static final int NOTIFICATION_TIMER_RUNNING = 1;
    public static final int NOTIFICATION_TIMER_FINISHED = 2;
    public static final int NOTIFICATION_TASK_REMINDER = 100; // Base ID para tarefas

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }

    /**
     * Cria os canais de notificacao (obrigatorio para Android 8+)
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            // Canal do Timer - Alta prioridade
            NotificationChannel timerChannel = new NotificationChannel(
                    CHANNEL_TIMER,
                    context.getString(R.string.notification_channel_timer),
                    NotificationManager.IMPORTANCE_HIGH
            );
            timerChannel.setDescription(context.getString(R.string.notification_channel_timer_desc));
            timerChannel.enableVibration(true);
            timerChannel.setVibrationPattern(new long[]{0, 500, 200, 500});
            manager.createNotificationChannel(timerChannel);

            // Canal de Tarefas - Prioridade padrao
            NotificationChannel tasksChannel = new NotificationChannel(
                    CHANNEL_TASKS,
                    context.getString(R.string.notification_channel_tasks),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            tasksChannel.setDescription(context.getString(R.string.notification_channel_tasks_desc));
            manager.createNotificationChannel(tasksChannel);
        }
    }

    /**
     * Cria notificacao persistente do timer (para Foreground Service)
     */
    public NotificationCompat.Builder createTimerNotification(String title, String content, boolean isWorking) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("open_timer", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        int icon = isWorking ? R.drawable.ic_timer_work : R.drawable.ic_timer_break;

        return new NotificationCompat.Builder(context, CHANNEL_TIMER)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
    }

    /**
     * Notificacao quando o timer termina
     */
    public void showTimerFinishedNotification(boolean wasWorkSession, String subjectName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("open_timer", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title;
        String content;

        if (wasWorkSession) {
            title = context.getString(R.string.notification_work_complete_title);
            content = subjectName != null ?
                    context.getString(R.string.notification_work_complete_message, subjectName) :
                    context.getString(R.string.notification_break_time);
        } else {
            title = context.getString(R.string.notification_break_complete_title);
            content = context.getString(R.string.notification_break_complete_message);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_TIMER)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        try {
            notificationManager.notify(NOTIFICATION_TIMER_FINISHED, builder.build());
        } catch (SecurityException e) {
            // Permissao de notificacao nao concedida
        }
    }

    /**
     * Notificacao de lembrete de tarefa
     */
    public void showTaskReminderNotification(long taskId, String taskTitle, String subjectName, String dueInfo) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("open_tasks", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String content = subjectName + " - " + dueInfo;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_TASKS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(taskTitle)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        try {
            notificationManager.notify(NOTIFICATION_TASK_REMINDER + (int) taskId, builder.build());
        } catch (SecurityException e) {
            // Permissao de notificacao nao concedida
        }
    }

    /**
     * Notificacao de tarefa atrasada
     */
    public void showTaskOverdueNotification(long taskId, String taskTitle, String subjectName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("open_tasks", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (int) taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_TASKS)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_task_overdue_title))
                .setContentText(taskTitle + " (" + subjectName + ")")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setColor(context.getResources().getColor(R.color.error, null));

        try {
            notificationManager.notify(NOTIFICATION_TASK_REMINDER + (int) taskId, builder.build());
        } catch (SecurityException e) {
            // Permissao de notificacao nao concedida
        }
    }

    /**
     * Cancela notificacao do timer
     */
    public void cancelTimerNotification() {
        notificationManager.cancel(NOTIFICATION_TIMER_RUNNING);
    }

    /**
     * Cancela todas as notificacoes
     */
    public void cancelAll() {
        notificationManager.cancelAll();
    }
}
