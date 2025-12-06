package com.example.gestaodetarefasestudos.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gestaodetarefasestudos.LoginActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.database.dao.UsuarioDAO;
import com.example.gestaodetarefasestudos.models.Usuario;
import com.google.android.material.card.MaterialCardView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fragmento Home com saudação personalizada e estatísticas principais
 * Design simplificado focado no essencial: boas-vindas + visão geral do dia
 */
public class HomeFragment extends Fragment {

    // Componentes de UI
    private TextView tvGreeting;
    private TextView tvTotalSubjects;
    private TextView tvPendingTasks;
    private TextView tvStudyTime;
    private MaterialCardView btnLogout;

    // DAOs
    private DisciplinaDAO disciplinaDAO;
    private TarefaDAO tarefaDAO;
    private SessaoEstudoDAO sessaoDAO;
    private UsuarioDAO usuarioDAO;

    // SharedPreferences para ID do usuário logado
    private static final String PREFS_NAME = "GestaoTarefasPrefs";
    private static final String KEY_USER_ID = "usuario_id";

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarDAOs();
        inicializarComponentes(view);
        configurarSaudacao();
        carregarEstatisticas();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarregar estatísticas quando voltar para este fragmento
        carregarEstatisticas();
    }

    private void inicializarDAOs() {
        disciplinaDAO = new DisciplinaDAO(requireContext());
        tarefaDAO = new TarefaDAO(requireContext());
        sessaoDAO = new SessaoEstudoDAO(requireContext());
        usuarioDAO = new UsuarioDAO(requireContext());
    }

    private void inicializarComponentes(View view) {
        // Saudação
        tvGreeting = view.findViewById(R.id.tv_greeting);

        // Cards de estatísticas
        tvTotalSubjects = view.findViewById(R.id.tv_total_subjects);
        tvPendingTasks = view.findViewById(R.id.tv_pending_tasks);
        tvStudyTime = view.findViewById(R.id.tv_study_time);

        // Botão de logout
        btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> mostrarDialogoLogout());
    }

    /**
     * Obtém o nome do usuário logado a partir do banco de dados
     */
    private String obterNomeUsuario() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        long usuarioId = prefs.getLong(KEY_USER_ID, -1);

        if (usuarioId != -1) {
            Usuario usuario = usuarioDAO.obterPorId(usuarioId);
            if (usuario != null) {
                return usuario.getNome();
            }
        }

        return "";
    }

    /**
     * Configura a saudação baseada no período do dia + nome do usuário
     * Manhã (5h-12h): Bom dia, [Nome]!
     * Tarde (12h-18h): Boa tarde, [Nome]!
     * Noite (18h-5h): Boa noite, [Nome]!
     */
    private void configurarSaudacao() {
        Calendar agora = Calendar.getInstance();
        int hora = agora.get(Calendar.HOUR_OF_DAY);

        String periodoSaudacao;
        if (hora >= 5 && hora < 12) {
            periodoSaudacao = "Bom dia";
        } else if (hora >= 12 && hora < 18) {
            periodoSaudacao = "Boa tarde";
        } else {
            periodoSaudacao = "Boa noite";
        }

        String nomeUsuario = obterNomeUsuario();
        String saudacao;

        if (!nomeUsuario.isEmpty()) {
            saudacao = periodoSaudacao + ", " + nomeUsuario + "!";
        } else {
            saudacao = periodoSaudacao + "!";
        }

        tvGreeting.setText(saudacao);
    }

    /**
     * Carrega as estatísticas principais
     */
    private void carregarEstatisticas() {
        // Total de disciplinas
        int totalDisciplinas = disciplinaDAO.contarTotal();
        tvTotalSubjects.setText(String.valueOf(totalDisciplinas));

        // Tarefas pendentes
        int tarefasPendentes = tarefaDAO.contarPendentes();
        tvPendingTasks.setText(String.valueOf(tarefasPendentes));

        // Tempo de estudo hoje
        long tempoTotalSegundos = sessaoDAO.obterTempoEstudoHoje();
        String tempoFormatado = formatarTempoEstudo(tempoTotalSegundos);
        tvStudyTime.setText(tempoFormatado);
    }

    /**
     * Formata segundos em horas e minutos
     * Ex: 3665 segundos -> "1h 01m"
     */
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }

    /**
     * Mostra diálogo de confirmação para logout
     */
    private void mostrarDialogoLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Sair")
                .setMessage("Tem certeza que deseja sair da sua conta?")
                .setPositiveButton("Sim", (dialog, which) -> fazerLogout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Realiza o logout do usuário
     */
    private void fazerLogout() {
        // Limpar SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Redirecionar para LoginActivity
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
