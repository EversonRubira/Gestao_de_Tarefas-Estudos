package com.example.gestaodetarefasestudos.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.gestaodetarefasestudos.PreferenciasApp;
import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.receivers.TaskNotificationReceiver;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Classe para agendar notificacoes de tarefas
 */
public class TaskNotificationScheduler {

    private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;
    private static final long ONE_HOUR_MILLIS = 60 * 60 * 1000L;

    private final Context context;
    private final AlarmManager alarmManager;
    private final TarefaRoomDAO tarefaDAO;
    private final DisciplinaRoomDAO disciplinaDAO;
    private final PreferenciasApp preferencias;
    private final Executor executor;

    public TaskNotificationScheduler(Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.tarefaDAO = AppDatabase.getInstance(context).tarefaDAO();
        this.disciplinaDAO = AppDatabase.getInstance(context).disciplinaDAO();
        this.preferencias = new PreferenciasApp(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Agenda notificacoes para uma tarefa especifica
     * Cria notificacoes para 24h antes e 1h antes do prazo
     */
    public void scheduleTaskNotifications(Tarefa tarefa) {
        if (tarefa == null || tarefa.getEstado() == EstadoTarefa.CONCLUIDA) {
            return;
        }

        executor.execute(() -> {
            Disciplina disciplina = disciplinaDAO.obterPorId(tarefa.getDisciplinaId());
            String subjectName = disciplina != null ? disciplina.getNome() : "";

            long dueTime = tarefa.getDataEntrega();
            long now = System.currentTimeMillis();

            // Notificacao 24h antes
            long time24h = dueTime - ONE_DAY_MILLIS;
            if (time24h > now) {
                scheduleNotification(
                        tarefa.getId(),
                        tarefa.getTitulo(),
                        subjectName,
                        context.getString(com.example.gestaodetarefasestudos.R.string.notification_due_tomorrow),
                        time24h,
                        false,
                        1 // Request code suffix
                );
            }

            // Notificacao 1h antes
            long time1h = dueTime - ONE_HOUR_MILLIS;
            if (time1h > now) {
                scheduleNotification(
                        tarefa.getId(),
                        tarefa.getTitulo(),
                        subjectName,
                        context.getString(com.example.gestaodetarefasestudos.R.string.notification_due_1hour),
                        time1h,
                        false,
                        2 // Request code suffix
                );
            }

            // Notificacao no momento do vencimento (tarefa atrasada)
            if (dueTime > now) {
                scheduleNotification(
                        tarefa.getId(),
                        tarefa.getTitulo(),
                        subjectName,
                        "",
                        dueTime,
                        true,
                        3 // Request code suffix
                );
            }
        });
    }

    /**
     * Cancela todas as notificacoes de uma tarefa
     */
    public void cancelTaskNotifications(long taskId) {
        for (int suffix = 1; suffix <= 3; suffix++) {
            int requestCode = (int) (taskId * 10 + suffix);
            PendingIntent pendingIntent = createPendingIntent(taskId, "", "", "", false, requestCode);
            if (alarmManager != null && pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    /**
     * Reagenda todas as notificacoes de tarefas pendentes
     * Chamado apos boot do dispositivo
     */
    public void rescheduleAllNotifications() {
        executor.execute(() -> {
            long usuarioId = preferencias.getUsuarioId();
            if (usuarioId <= 0) return;

            List<Tarefa> tarefasPendentes = tarefaDAO.obterPendentesParaNotificacao(usuarioId);

            for (Tarefa tarefa : tarefasPendentes) {
                scheduleTaskNotifications(tarefa);
            }
        });
    }

    private void scheduleNotification(long taskId, String taskTitle, String subjectName,
                                       String dueInfo, long triggerTime, boolean isOverdue, int suffix) {
        int requestCode = (int) (taskId * 10 + suffix);

        PendingIntent pendingIntent = createPendingIntent(
                taskId, taskTitle, subjectName, dueInfo, isOverdue, requestCode);

        if (alarmManager == null || pendingIntent == null) return;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12+ precisa verificar permissao
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    // Fallback para alarme inexato
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } catch (SecurityException e) {
            // Permissao negada, usar alarme inexato
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private PendingIntent createPendingIntent(long taskId, String taskTitle, String subjectName,
                                               String dueInfo, boolean isOverdue, int requestCode) {
        Intent intent = new Intent(context, TaskNotificationReceiver.class);
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_ID, taskId);
        intent.putExtra(TaskNotificationReceiver.EXTRA_TASK_TITLE, taskTitle);
        intent.putExtra(TaskNotificationReceiver.EXTRA_SUBJECT_NAME, subjectName);
        intent.putExtra(TaskNotificationReceiver.EXTRA_DUE_INFO, dueInfo);
        intent.putExtra(TaskNotificationReceiver.EXTRA_IS_OVERDUE, isOverdue);

        return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}
