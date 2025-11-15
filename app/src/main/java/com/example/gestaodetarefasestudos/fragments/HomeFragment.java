package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private LinearLayout layoutListaTempo;

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
        layoutListaTempo = view.findViewById(R.id.layout_study_time_list);
    }

    /**
     * Carrega as estatísticas do banco de dados e atualiza a interface
     * Este método é chamado sempre que o fragmento fica visível
     * AGORA mostra tempo POR DISCIPLINA!
     */
    private void carregarEstatisticas() {
        // Carregar total de disciplinas cadastradas
        int totalDisciplinas = disciplinaDAO.contarTotal();
        textoTotalDisciplinas.setText(String.valueOf(totalDisciplinas));

        // Carregar número de tarefas pendentes (não concluídas)
        int tarefasPendentes = tarefaDAO.contarPendentes();
        textoTarefasPendentes.setText(String.valueOf(tarefasPendentes));

        // Carregar tempo de estudo de hoje POR DISCIPLINA
        carregarTempoPorDisciplina();
    }

    /**
     * Carrega e mostra o tempo de estudo de hoje separado por disciplina
     * Exemplo: PAM: 50m, BD: 25m, WEB: 15m
     */
    private void carregarTempoPorDisciplina() {
        // Limpar lista anterior
        layoutListaTempo.removeAllViews();

        // Buscar dados do banco
        Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina();
        long tempoTotal = 0;

        // Verificar se há dados
        if (cursor != null && cursor.getCount() > 0) {
            // Para cada disciplina com estudo hoje
            while (cursor.moveToNext()) {
                String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("d_nome"));
                long segundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));

                // Se nome for null, é sessão genérica (id=0)
                if (nomeDisciplina == null || nomeDisciplina.isEmpty()) {
                    nomeDisciplina = "Geral";
                }

                tempoTotal += segundos;

                // Criar linha para esta disciplina
                adicionarLinhaDisciplina(nomeDisciplina, segundos);
            }
            cursor.close();
        }

        // Se não houver dados de hoje, mostrar mensagem
        if (tempoTotal == 0) {
            TextView textoVazio = new TextView(requireContext());
            textoVazio.setText("Nenhum estudo registado hoje");
            textoVazio.setTextColor(getResources().getColor(R.color.text_secondary));
            textoVazio.setTextSize(14);
            textoVazio.setPadding(0, 8, 0, 8);
            layoutListaTempo.addView(textoVazio);
        }

        // Atualizar total
        String tempoFormatado = formatarTempoEstudo(tempoTotal);
        textoTempoEstudo.setText(tempoFormatado);
    }

    /**
     * Adiciona uma linha mostrando disciplina e tempo estudado
     */
    private void adicionarLinhaDisciplina(String nomeDisciplina, long segundos) {
        // Criar layout horizontal para a linha
        LinearLayout linha = new LinearLayout(requireContext());
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setPadding(0, 4, 0, 4);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linha.setLayoutParams(params);

        // Nome da disciplina (à esquerda)
        TextView textoNome = new TextView(requireContext());
        textoNome.setText(nomeDisciplina);
        textoNome.setTextColor(getResources().getColor(R.color.text_primary));
        textoNome.setTextSize(14);
        LinearLayout.LayoutParams paramsNome = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // weight = 1 (ocupa espaço disponível)
        );
        textoNome.setLayoutParams(paramsNome);

        // Tempo (à direita)
        TextView textoTempo = new TextView(requireContext());
        String tempoFormatado = formatarTempoEstudo(segundos);
        textoTempo.setText(tempoFormatado);
        textoTempo.setTextColor(getResources().getColor(R.color.primary));
        textoTempo.setTextSize(14);

        // Adicionar à linha
        linha.addView(textoNome);
        linha.addView(textoTempo);

        // Adicionar linha ao layout
        layoutListaTempo.addView(linha);
    }

    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }
}
