package com.example.gestaodetarefasestudos.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.example.gestaodetarefasestudos.utils.NotificationHelper;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Foreground Service para manter o timer Pomodoro rodando em background
 */
public class TimerService extends Service {

    // Actions
    public static final String ACTION_START = "com.example.gestaodetarefasestudos.timer.START";
    public static final String ACTION_PAUSE = "com.example.gestaodetarefasestudos.timer.PAUSE";
    public static final String ACTION_RESUME = "com.example.gestaodetarefasestudos.timer.RESUME";
    public static final String ACTION_STOP = "com.example.gestaodetarefasestudos.timer.STOP";

    // Extras
    public static final String EXTRA_DISCIPLINE_ID = "discipline_id";
    public static final String EXTRA_DISCIPLINE_NAME = "discipline_name";

    // Callback interface para comunicar com o Fragment
    public interface TimerCallback {
        void onTick(long timeRemaining, long totalTime, boolean isWorking, int cycleCount);
        void onFinished(boolean wasWorkSession, int cycleCount);
    }

    private TimerCallback callback;

    // Duracoes fixas do Pomodoro
    private static final long WORK_DURATION = 25 * 60 * 1000L;
    private static final long BREAK_DURATION = 5 * 60 * 1000L;
    private static final long LONG_BREAK_DURATION = 15 * 60 * 1000L;
    private static final int CYCLES_UNTIL_LONG_BREAK = 4;

    // Estado do timer
    private CountDownTimer countDownTimer;
    private long timeRemaining = WORK_DURATION;
    private long totalTime = WORK_DURATION;
    private boolean isWorking = true;
    private boolean isLongBreak = false;
    private boolean isRunning = false;
    private int cycleCount = 0;
    private long sessionStartTime = 0;

    // Disciplina
    private long disciplineId = 0;
    private String disciplineName = "";

    // Helpers
    private NotificationHelper notificationHelper;
    private SessaoEstudoRoomDAO sessaoDAO;
    private Executor executor;

    // Binder para comunicacao com o Fragment
    private final IBinder binder = new TimerBinder();

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
        sessaoDAO = AppDatabase.getInstance(this).sessaoEstudoDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    disciplineId = intent.getLongExtra(EXTRA_DISCIPLINE_ID, 0);
                    disciplineName = intent.getStringExtra(EXTRA_DISCIPLINE_NAME);
                    startTimer();
                    break;
                case ACTION_PAUSE:
                    pauseTimer();
                    break;
                case ACTION_RESUME:
                    resumeTimer();
                    break;
                case ACTION_STOP:
                    stopTimer();
                    break;
            }
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void startTimer() {
        if (isRunning) return;

        isRunning = true;
        sessionStartTime = System.currentTimeMillis();

        // Iniciar como Foreground Service
        Notification notification = buildNotification();
        startForeground(NotificationHelper.NOTIFICATION_TIMER_RUNNING, notification);

        startCountDown();
    }

    private void resumeTimer() {
        if (isRunning) return;

        isRunning = true;

        // Se estava em sessao de trabalho, atualizar tempo de inicio
        if (isWorking && sessionStartTime == 0) {
            sessionStartTime = System.currentTimeMillis();
        }

        startCountDown();
        updateNotification();
    }

    private void pauseTimer() {
        if (!isRunning) return;

        isRunning = false;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        updateNotification();
    }

    private void stopTimer() {
        isRunning = false;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Salvar tempo parcial se estava trabalhando
        if (isWorking && sessionStartTime > 0 && disciplineId > 0) {
            long duration = (System.currentTimeMillis() - sessionStartTime) / 1000;
            if (duration >= 10) {
                saveStudySession(duration);
            }
        }

        // Resetar estado
        resetState();

        // Parar foreground
        stopForeground(true);
        stopSelf();
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                notifyTick();
                updateNotification();
            }

            @Override
            public void onFinish() {
                timeRemaining = 0;
                isRunning = false;
                onTimerFinished();
            }
        }.start();
    }

    private void onTimerFinished() {
        // Guardar estado anterior antes de mudar
        boolean wasWorkSession = isWorking;

        // Notificar que terminou
        notificationHelper.showTimerFinishedNotification(wasWorkSession, disciplineName);

        if (wasWorkSession) {
            // Salvar sessao de estudo
            long duration = (System.currentTimeMillis() - sessionStartTime) / 1000;
            if (disciplineId > 0) {
                saveStudySession(duration);
            }

            cycleCount++;

            if (cycleCount >= CYCLES_UNTIL_LONG_BREAK) {
                // Descanso longo
                isWorking = false;
                isLongBreak = true;
                timeRemaining = LONG_BREAK_DURATION;
                totalTime = LONG_BREAK_DURATION;
                cycleCount = 0;
            } else {
                // Descanso curto
                isWorking = false;
                isLongBreak = false;
                timeRemaining = BREAK_DURATION;
                totalTime = BREAK_DURATION;
            }
        } else {
            // Voltar para trabalho
            isWorking = true;
            isLongBreak = false;
            timeRemaining = WORK_DURATION;
            totalTime = WORK_DURATION;
            sessionStartTime = 0;
        }

        // Notificar callback
        notifyFinished(wasWorkSession);
        updateNotification();
    }

    private void saveStudySession(long durationSeconds) {
        SessaoEstudo session = new SessaoEstudo(disciplineId, durationSeconds);
        executor.execute(() -> sessaoDAO.inserir(session));
    }

    private void resetState() {
        timeRemaining = WORK_DURATION;
        totalTime = WORK_DURATION;
        isWorking = true;
        isLongBreak = false;
        cycleCount = 0;
        sessionStartTime = 0;
        disciplineId = 0;
        disciplineName = "";
    }

    private Notification buildNotification() {
        String title = isWorking ?
                getString(R.string.notification_timer_working) :
                (isLongBreak ? getString(R.string.notification_timer_long_break) : getString(R.string.notification_timer_break));

        String time = formatTime(timeRemaining);
        String content = disciplineName != null && !disciplineName.isEmpty() ?
                disciplineName + " - " + time : time;

        NotificationCompat.Builder builder = notificationHelper.createTimerNotification(
                title, content, isWorking);

        // Adicionar progress
        int progress = (int) ((timeRemaining * 100) / totalTime);
        builder.setProgress(100, progress, false);

        return builder.build();
    }

    private void updateNotification() {
        Notification notification = buildNotification();
        notificationHelper.cancelTimerNotification();
        startForeground(NotificationHelper.NOTIFICATION_TIMER_RUNNING, notification);
    }

    private void notifyTick() {
        if (callback != null) {
            callback.onTick(timeRemaining, totalTime, isWorking, cycleCount);
        }
    }

    private void notifyFinished(boolean wasWorkSession) {
        if (callback != null) {
            callback.onFinished(wasWorkSession, cycleCount);
        }
    }

    public void setCallback(TimerCallback callback) {
        this.callback = callback;
    }

    private String formatTime(long millis) {
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    // Getters para o Fragment
    public long getTimeRemaining() { return timeRemaining; }
    public long getTotalTime() { return totalTime; }
    public boolean isWorking() { return isWorking; }
    public boolean isLongBreak() { return isLongBreak; }
    public boolean isRunning() { return isRunning; }
    public int getCycleCount() { return cycleCount; }

    public void setDiscipline(long id, String name) {
        this.disciplineId = id;
        this.disciplineName = name;
    }

    @Override
    public void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}
