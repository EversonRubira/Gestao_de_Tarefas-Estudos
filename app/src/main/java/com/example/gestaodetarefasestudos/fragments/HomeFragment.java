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
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;

import java.util.Locale;

public class HomeFragment extends Fragment {

    private TextView textoTarefasPendentes;
    private TextView textoTempoEstudo;

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
        tarefaDAO = new TarefaDAO(requireContext());
        sessaoDAO = new SessaoEstudoDAO(requireContext());
    }

    private void inicializarComponentes(View view) {
        textoTarefasPendentes = view.findViewById(R.id.tv_pending_tasks);
        textoTempoEstudo = view.findViewById(R.id.tv_study_time);
    }

    /**
     * Carrega as estatísticas do banco de dados e atualiza a interface
     */
    private void carregarEstatisticas() {
        // Carregar número de tarefas pendentes (não concluídas)
        int tarefasPendentes = tarefaDAO.contarPendentes();
        textoTarefasPendentes.setText(String.valueOf(tarefasPendentes));

        // Carregar tempo total de estudo de hoje
        carregarTempoEstudo();
    }

    /**
     * Carrega o tempo total de estudo de hoje
     */
    private void carregarTempoEstudo() {
        // Buscar dados do banco usando DAO
        Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina();
        long tempoTotal = 0;

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

    // Converte segundos em formato legível (ex: 3665 segundos -> "1h 01m")
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }

        // Divisão inteira: 3665 / 3600 = 1 hora
        long horas = segundos / 3600;
        // Resto da divisão por 3600, depois dividir por 60
        // Ex: 3665 % 3600 = 65, depois 65 / 60 = 1 minuto
        long minutos = (segundos % 3600) / 60;

        // %d = número decimal, %02d = número com 2 dígitos (preenche com 0)
        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }
}
