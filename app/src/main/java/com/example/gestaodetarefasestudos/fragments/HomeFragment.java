package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView textoTotalDisciplinas;
    private TextView textoTarefasPendentes;
    private TextView textoTempoEstudo;
    private TextView textoTarefasConcluidas;

    private DisciplinaDAO disciplinaDAO;
    private TarefaDAO tarefaDAO;
    private SessaoEstudoDAO sessaoDAO;

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
    }

    private void inicializarComponentes(View view) {
        textoTotalDisciplinas = view.findViewById(R.id.tv_total_subjects);
        textoTarefasPendentes = view.findViewById(R.id.tv_pending_tasks);
        textoTempoEstudo = view.findViewById(R.id.tv_study_time);
        textoTarefasConcluidas = view.findViewById(R.id.tv_completed_tasks);
    }

    /**
     * Carrega as estatísticas do banco de dados e atualiza a interface
     * Este método é chamado sempre que o fragmento fica visível
     */
    private void carregarEstatisticas() {
        // Carregar total de disciplinas cadastradas
        int totalDisciplinas = disciplinaDAO.contarTotal();
        textoTotalDisciplinas.setText(String.valueOf(totalDisciplinas));

        // Carregar número de tarefas pendentes (não concluídas)
        int tarefasPendentes = tarefaDAO.contarPendentes();
        textoTarefasPendentes.setText(String.valueOf(tarefasPendentes));

        // Carregar número de tarefas concluídas
        int tarefasConcluidas = tarefaDAO.contarConcluidas();
        textoTarefasConcluidas.setText(String.valueOf(tarefasConcluidas));

        // Carregar tempo de estudo de hoje
        carregarTempoEstudoHoje();
    }

    /**
     * Carrega e mostra o tempo total de estudo de hoje
     */
    private void carregarTempoEstudoHoje() {
        // Buscar dados do banco usando DAO
        Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina();
        long tempoTotal = 0;

        // Verificar se cursor tem dados e somar o tempo total
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long segundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));
                tempoTotal += segundos;
            }
            cursor.close();
        }

        // Atualizar TextView do tempo total
        String tempoFormatado = formatarTempoEstudo(tempoTotal);
        textoTempoEstudo.setText(tempoFormatado);
    }

    /**
     * Converte segundos em formato legível
     * Exemplo: 3665 segundos -> "1h 01m"
     */
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00min";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        if (horas > 0) {
            return String.format(Locale.getDefault(), "%02dh %02dmin", horas, minutos);
        } else {
            return String.format(Locale.getDefault(), "%02dmin", minutos);
        }
    }
}
