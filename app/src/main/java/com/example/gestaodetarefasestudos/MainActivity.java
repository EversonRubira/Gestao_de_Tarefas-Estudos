package com.example.gestaodetarefasestudos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.fragments.HomeFragment;
import com.example.gestaodetarefasestudos.fragments.SubjectsFragment;
import com.example.gestaodetarefasestudos.fragments.TasksFragment;
import com.example.gestaodetarefasestudos.fragments.StatisticsFragment;
import com.example.gestaodetarefasestudos.fragments.TimerFragment;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNav;
    private Executor executor;

    // Launcher para solicitar permissao de notificacao
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d("MainActivity", "Permissao de notificacao concedida");
                } else {
                    Log.d("MainActivity", "Permissao de notificacao negada");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicializar componentes
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottom_navigation);
        setSupportActionBar(toolbar);
        executor = Executors.newSingleThreadExecutor();

        setupBottomNav();
        checkFirstRun();
        checkNotificationPermission();

        // carregar o fragment inicial
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            setToolbarTitle(getString(R.string.home_title));
        }
    }

    /**
     * Verifica e solicita permissao de notificacao para Android 13+ (API 33+)
     * Necessario para que as notificacoes do timer e lembretes funcionem
     */
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Verificar se devemos mostrar explicacao
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    // Mostrar dialog explicando por que precisamos da permissao
                    showNotificationPermissionDialog();
                } else {
                    // Primeira vez ou usuario marcou "nao perguntar novamente"
                    // Verificar se eh primeira vez
                    if (getPrefs().isFirstNotificationRequest()) {
                        showNotificationPermissionDialog();
                        getPrefs().setFirstNotificationRequest(false);
                    }
                }
            }
        }
    }

    /**
     * Mostra dialog explicando a importancia das notificacoes
     */
    private void showNotificationPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_notification_title)
                .setMessage(R.string.permission_notification_message)
                .setPositiveButton(R.string.permission_allow, (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                })
                .setNegativeButton(R.string.permission_deny, null)
                .show();
    }

    // setup da navegacao inferior
    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String title = "";

                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                    title = getString(R.string.home_title);
                } else if (id == R.id.navigation_subjects) {
                    selectedFragment = new SubjectsFragment();
                    title = getString(R.string.subjects_title);
                } else if (id == R.id.navigation_tasks) {
                    selectedFragment = new TasksFragment();
                    title = getString(R.string.tasks_title);
                } else if (id == R.id.navigation_statistics) {
                    selectedFragment = new StatisticsFragment();
                    title = getString(R.string.nav_statistics);
                } else if (id == R.id.navigation_timer) {
                    selectedFragment = new TimerFragment();
                    title = getString(R.string.timer_title);
                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                    setToolbarTitle(title);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    // verifica se eh a primeira vez que abre o app
    private void checkFirstRun() {
        if (getPrefs().isPrimeiraExecucao()) {
            createSampleData();
            getPrefs().setPrimeiraExecucao(false);
        }
    }

    // cria dados de exemplo na primeira execucao
    private void createSampleData() {
        long usuarioId = getUserId();
        if (usuarioId == -1) {
            return;
        }

        executor.execute(() -> {
            DisciplinaRoomDAO disciplinaDAO = AppDatabase.getInstance(this).disciplinaDAO();
            TarefaRoomDAO tarefaDAO = AppDatabase.getInstance(this).tarefaDAO();
            SessaoEstudoRoomDAO sessaoDAO = AppDatabase.getInstance(this).sessaoEstudoDAO();

            // criar disciplinas exemplo
            Disciplina pam = new Disciplina("Programação de Aplicações Móveis", "PAM", "#2196F3");
            pam.setUsuarioId(usuarioId);
            Disciplina bd = new Disciplina("Bases de Dados", "BD", "#4CAF50");
            bd.setUsuarioId(usuarioId);
            Disciplina web = new Disciplina("Desenvolvimento Web", "WEB", "#FF9800");
            web.setUsuarioId(usuarioId);
            //Disciplina mat = new Disciplina("Matemática", "MAT", "#E91E63");

            long idPam = disciplinaDAO.inserir(pam);
            long idBd = disciplinaDAO.inserir(bd);
            long idWeb = disciplinaDAO.inserir(web);

            // criar tarefas exemplo
            Calendar cal = Calendar.getInstance();

            cal.add(Calendar.DAY_OF_MONTH, 7);
            Tarefa tarefa1 = new Tarefa(
                "Concluir projeto final",
                "Finalizar aplicação de gestão de tarefas",
                idPam,
                cal.getTimeInMillis(),
                Prioridade.ALTA
            );
            tarefaDAO.inserir(tarefa1);

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 3);
            Tarefa tarefa2 = new Tarefa(
                "Estudar para teste",
                "Rever matéria de normalização",
                idBd,
                cal.getTimeInMillis(),
                Prioridade.MEDIA
            );
            tarefaDAO.inserir(tarefa2);

            cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 10);
            Tarefa tarefa3 = new Tarefa(
                "Fazer exercícios de JavaScript",
                "Completar exercícios do capítulo 5",
                idWeb,
                cal.getTimeInMillis(),
                Prioridade.BAIXA
            );
            tarefaDAO.inserir(tarefa3);

            // adicionar sessoes de estudo exemplo
            SessaoEstudo sessao = new SessaoEstudo(idPam, 1500);
            sessaoDAO.inserir(sessao);
            SessaoEstudo sessao2 = new SessaoEstudo(idBd, 900);
            sessaoDAO.inserir(sessao2);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            showLogoutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // mostra dialogo pra confirmar logout
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirmation)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doLogout();
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // faz o logout do usuario
    private void doLogout() {
        getPrefs().logout();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public long getUserId() {
        return getPrefs().getUsuarioId();
    }
}