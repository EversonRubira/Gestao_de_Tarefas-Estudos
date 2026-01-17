package com.example.gestaodetarefasestudos.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.gestaodetarefasestudos.utils.NotificationHelper;

/**
 * Receiver para mostrar notificacoes de tarefas agendadas
 */
public class TaskNotificationReceiver extends BroadcastReceiver {

    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_TASK_TITLE = "task_title";
    public static final String EXTRA_SUBJECT_NAME = "subject_name";
    public static final String EXTRA_DUE_INFO = "due_info";
    public static final String EXTRA_IS_OVERDUE = "is_overdue";

    @Override
    public void onReceive(Context context, Intent intent) {
        long taskId = intent.getLongExtra(EXTRA_TASK_ID, 0);
        String taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE);
        String subjectName = intent.getStringExtra(EXTRA_SUBJECT_NAME);
        String dueInfo = intent.getStringExtra(EXTRA_DUE_INFO);
        boolean isOverdue = intent.getBooleanExtra(EXTRA_IS_OVERDUE, false);

        if (taskId == 0 || taskTitle == null) return;

        NotificationHelper notificationHelper = new NotificationHelper(context);

        if (isOverdue) {
            notificationHelper.showTaskOverdueNotification(taskId, taskTitle, subjectName);
        } else {
            notificationHelper.showTaskReminderNotification(taskId, taskTitle, subjectName, dueInfo);
        }
    }
}
