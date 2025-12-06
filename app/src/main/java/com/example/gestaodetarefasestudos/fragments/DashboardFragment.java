package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.CalendarioAdapter;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.models.DiaCalendario;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard Fragment - Calendário e Estatísticas de Produtividade
 * Mostra calendário de entregas com gráficos de tempo por disciplina e progresso de tarefas
 */
public class DashboardFragment extends Fragment {

    // Tarefa Prioritária
    private View cardFeaturedTask;
    private LinearLayout layoutTaskContent;
    private TextView tvNoFeaturedTask;
    private TextView tvFeaturedTaskTitle;
    private TextView tvFeaturedTaskSubject;
    private TextView tvFeaturedTaskPriority;
    private TextView tvFeaturedTaskDate;

    // Estatísticas
    private LinearLayout layoutListaTempo;
    private TextView tvCompletedTasks;
    private TextView tvPendingTasksDashboard;

    // Gráfico de barras
    private LinearLayout containerChartBars;
    private TextView tvNoChartData;

    // Componentes do calendário
    private TextView textoMesAno;
    private ImageButton btnMesAnterior;
    private ImageButton btnProximoMes;
    private RecyclerView recyclerCalendario;
    private CalendarioAdapter calendarioAdapter;

    // DAOs
    private TarefaDAO tarefaDAO;
    private SessaoEstudoDAO sessaoDAO;

    // Calendário atual sendo exibido
    private Calendar calendarioAtual;

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarDAOs();
        inicializarComponentes(view);
        inicializarCalendario();
        carregarEstatisticas();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarEstatisticas();
    }

    private void inicializarDAOs() {
        tarefaDAO = new TarefaDAO(requireContext());
        sessaoDAO = new SessaoEstudoDAO(requireContext());
    }

    private void inicializarComponentes(View view) {
        // Tarefa Prioritária
        cardFeaturedTask = view.findViewById(R.id.card_featured_task);
        layoutTaskContent = view.findViewById(R.id.layout_task_content);
        tvNoFeaturedTask = view.findViewById(R.id.tv_no_featured_task);
        tvFeaturedTaskTitle = view.findViewById(R.id.tv_featured_task_title);
        tvFeaturedTaskSubject = view.findViewById(R.id.tv_featured_task_subject);
        tvFeaturedTaskPriority = view.findViewById(R.id.tv_featured_task_priority);
        tvFeaturedTaskDate = view.findViewById(R.id.tv_featured_task_date);

        // Estatísticas
        layoutListaTempo = view.findViewById(R.id.layout_study_time_list);
        tvCompletedTasks = view.findViewById(R.id.tv_completed_tasks);
        tvPendingTasksDashboard = view.findViewById(R.id.tv_pending_tasks_dashboard);

        // Gráfico de barras
        containerChartBars = view.findViewById(R.id.container_chart_bars);
        tvNoChartData = view.findViewById(R.id.tv_no_chart_data);

        // Componentes do calendário
        textoMesAno = view.findViewById(R.id.tv_mes_ano);
        btnMesAnterior = view.findViewById(R.id.btn_mes_anterior);
        btnProximoMes = view.findViewById(R.id.btn_proximo_mes);
        recyclerCalendario = view.findViewById(R.id.rv_calendario);
    }

    /**
     * Carrega estatísticas de tarefas e tempo de estudo
     */
    private void carregarEstatisticas() {
        // Tarefa prioritária
        carregarTarefaPrioritaria();

        // Tarefas concluídas
        int totalTarefas = tarefaDAO.contarTotal();
        int tarefasConcluidas = tarefaDAO.contarConcluidas();
        tvCompletedTasks.setText(String.valueOf(tarefasConcluidas));

        // Tarefas pendentes
        int tarefasPendentes = tarefaDAO.contarPendentes();
        tvPendingTasksDashboard.setText(String.valueOf(tarefasPendentes));

        // Tempo de estudo por disciplina
        carregarTempoPorDisciplina();

        // Gráfico de top disciplinas
        carregarGraficoTopDisciplinas();
    }

    /**
     * Carrega e exibe a próxima tarefa prioritária
     */
    private void carregarTarefaPrioritaria() {
        List<Tarefa> tarefasPendentes = tarefaDAO.obterPendentes();

        if (tarefasPendentes != null && !tarefasPendentes.isEmpty()) {
            Tarefa proximaTarefa = tarefasPendentes.get(0);

            // Mostrar conteúdo da tarefa
            layoutTaskContent.setVisibility(View.VISIBLE);
            tvNoFeaturedTask.setVisibility(View.GONE);

            // Preencher dados
            tvFeaturedTaskTitle.setText(proximaTarefa.getTitulo());
            tvFeaturedTaskSubject.setText("📚 " + proximaTarefa.getNomeDisciplina());

            // Prioridade
            String prioridade = "";
            int corPrioridade = R.color.priority_low;
            switch (proximaTarefa.getPrioridade()) {
                case ALTA:
                    prioridade = "Alta";
                    corPrioridade = R.color.priority_high;
                    break;
                case MEDIA:
                    prioridade = "Média";
                    corPrioridade = R.color.priority_medium;
                    break;
                case BAIXA:
                    prioridade = "Baixa";
                    corPrioridade = R.color.priority_low;
                    break;
            }
            tvFeaturedTaskPriority.setText(prioridade);
            tvFeaturedTaskPriority.setBackgroundColor(getResources().getColor(corPrioridade));

            // Data de entrega
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dataFormatada = sdf.format(new Date(proximaTarefa.getDataEntrega()));

            // Verificar se é hoje ou amanhã
            Calendar hoje = Calendar.getInstance();
            Calendar dataEntrega = Calendar.getInstance();
            dataEntrega.setTimeInMillis(proximaTarefa.getDataEntrega());

            String textoData;
            if (hoje.get(Calendar.YEAR) == dataEntrega.get(Calendar.YEAR) &&
                    hoje.get(Calendar.DAY_OF_YEAR) == dataEntrega.get(Calendar.DAY_OF_YEAR)) {
                textoData = "📅 Hoje";
            } else if (hoje.get(Calendar.YEAR) == dataEntrega.get(Calendar.YEAR) &&
                    hoje.get(Calendar.DAY_OF_YEAR) + 1 == dataEntrega.get(Calendar.DAY_OF_YEAR)) {
                textoData = "📅 Amanhã";
            } else {
                textoData = "📅 " + dataFormatada;
            }

            tvFeaturedTaskDate.setText(textoData);
        } else {
            // Sem tarefas pendentes
            layoutTaskContent.setVisibility(View.GONE);
            tvNoFeaturedTask.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Carrega e mostra o tempo de estudo por disciplina (limitado a top 3)
     */
    private void carregarTempoPorDisciplina() {
        layoutListaTempo.removeAllViews();

        Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina();

        if (cursor != null && cursor.getCount() > 0) {
            int contador = 0;
            int LIMITE_DISCIPLINAS = 3; // Mostrar apenas top 3

            while (cursor.moveToNext() && contador < LIMITE_DISCIPLINAS) {
                String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("d_nome"));
                long segundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));

                if (nomeDisciplina == null || nomeDisciplina.isEmpty()) {
                    nomeDisciplina = "Geral";
                }

                adicionarLinhaDisciplina(nomeDisciplina, segundos);
                contador++;
            }
            cursor.close();
        } else {
            TextView textoVazio = new TextView(requireContext());
            textoVazio.setText("Sem dados");
            textoVazio.setTextColor(getResources().getColor(R.color.text_secondary_glass));
            textoVazio.setTextSize(11);
            textoVazio.setPadding(0, 4, 0, 4);
            layoutListaTempo.addView(textoVazio);
        }
    }

    /**
     * Adiciona uma linha compacta mostrando disciplina e tempo estudado
     */
    private void adicionarLinhaDisciplina(String nomeDisciplina, long segundos) {
        LinearLayout linha = new LinearLayout(requireContext());
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setPadding(0, 2, 0, 2);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linha.setLayoutParams(params);

        // Nome da disciplina (truncado se muito longo)
        TextView textoNome = new TextView(requireContext());
        textoNome.setText(nomeDisciplina);
        textoNome.setTextColor(getResources().getColor(R.color.text_secondary_glass));
        textoNome.setTextSize(10);
        textoNome.setMaxLines(1);
        textoNome.setEllipsize(android.text.TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams paramsNome = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        textoNome.setLayoutParams(paramsNome);

        // Tempo
        TextView textoTempo = new TextView(requireContext());
        String tempoFormatado = formatarTempoEstudo(segundos);
        textoTempo.setText(tempoFormatado);
        textoTempo.setTextColor(getResources().getColor(R.color.primary));
        textoTempo.setTextSize(11);
        textoTempo.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

        linha.addView(textoNome);
        linha.addView(textoTempo);

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

    // ==================== CALENDÁRIO ====================

    private void inicializarCalendario() {
        calendarioAtual = Calendar.getInstance();

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        recyclerCalendario.setLayoutManager(layoutManager);

        calendarioAdapter = new CalendarioAdapter(requireContext());
        recyclerCalendario.setAdapter(calendarioAdapter);

        calendarioAdapter.setOnDiaClickListener(dia -> {
            mostrarTarefasDoDia(dia);
        });

        btnMesAnterior.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.MONTH, -1);
            carregarCalendario();
        });

        btnProximoMes.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.MONTH, 1);
            carregarCalendario();
        });

        carregarCalendario();
    }

    private void carregarCalendario() {
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "PT"));
        textoMesAno.setText(formatoMesAno.format(calendarioAtual.getTime()));

        List<DiaCalendario> dias = gerarDiasDoMes();
        aplicarCoresDasTarefas(dias);
        calendarioAdapter.setDias(dias);
    }

    private List<DiaCalendario> gerarDiasDoMes() {
        List<DiaCalendario> dias = new ArrayList<>();

        Calendar cal = (Calendar) calendarioAtual.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int diaDaSemana = cal.get(Calendar.DAY_OF_WEEK);

        for (int i = 1; i < diaDaSemana; i++) {
            dias.add(new DiaCalendario(0, 0));
        }

        int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar hoje = Calendar.getInstance();

        for (int dia = 1; dia <= diasNoMes; dia++) {
            cal.set(Calendar.DAY_OF_MONTH, dia);

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            DiaCalendario diaCalendario = new DiaCalendario(dia, cal.getTimeInMillis());

            if (cal.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == hoje.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == hoje.get(Calendar.DAY_OF_MONTH)) {
                diaCalendario.setDiaAtual(true);
            }

            dias.add(diaCalendario);
        }

        return dias;
    }

    private void aplicarCoresDasTarefas(List<DiaCalendario> dias) {
        Calendar cal = (Calendar) calendarioAtual.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioMes = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long fimMes = cal.getTimeInMillis();

        Cursor cursor = tarefaDAO.obterTarefasPorPeriodoComCores(inicioMes, fimMes);

        if (cursor != null && cursor.moveToFirst()) {
            Map<String, List<String>> coresPorDia = new HashMap<>();
            Map<String, Integer> contagemPorDia = new HashMap<>();

            do {
                long dataEntrega = cursor.getLong(cursor.getColumnIndexOrThrow("data_entrega"));
                String cor = cursor.getString(cursor.getColumnIndexOrThrow("cor_disciplina"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

                Calendar calTemp = Calendar.getInstance();
                calTemp.setTimeInMillis(dataEntrega);
                calTemp.set(Calendar.HOUR_OF_DAY, 0);
                calTemp.set(Calendar.MINUTE, 0);
                calTemp.set(Calendar.SECOND, 0);
                calTemp.set(Calendar.MILLISECOND, 0);

                String chave = String.valueOf(calTemp.getTimeInMillis());

                if (!coresPorDia.containsKey(chave)) {
                    coresPorDia.put(chave, new ArrayList<>());
                    contagemPorDia.put(chave, 0);
                }

                if (cor != null && !cor.isEmpty()) {
                    coresPorDia.get(chave).add(cor);
                }

                contagemPorDia.put(chave, contagemPorDia.get(chave) + count);

            } while (cursor.moveToNext());

            cursor.close();

            for (DiaCalendario dia : dias) {
                if (!dia.isDiaVazio()) {
                    String chave = String.valueOf(dia.getTimestamp());

                    if (coresPorDia.containsKey(chave)) {
                        List<String> cores = coresPorDia.get(chave);
                        for (String cor : cores) {
                            dia.adicionarCorDisciplina(cor);
                        }

                        dia.setQuantidadeTarefas(contagemPorDia.get(chave));
                    }
                }
            }
        }
    }

    private void mostrarTarefasDoDia(DiaCalendario dia) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dia.getTimestamp());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long fimDia = cal.getTimeInMillis();

        List<Tarefa> tarefas = tarefaDAO.obterTarefasPorDia(inicioDia, fimDia);

        StringBuilder mensagem = new StringBuilder();
        SimpleDateFormat formatoDia = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        mensagem.append("Tarefas de ").append(formatoDia.format(new Date(dia.getTimestamp()))).append(":\n\n");

        for (Tarefa tarefa : tarefas) {
            mensagem.append("• ").append(tarefa.getTitulo());
            if (tarefa.getNomeDisciplina() != null) {
                mensagem.append(" (").append(tarefa.getNomeDisciplina()).append(")");
            }
            mensagem.append("\n");
        }

        Toast.makeText(requireContext(), mensagem.toString(), Toast.LENGTH_LONG).show();
    }

    // ==================== GRÁFICO DE BARRAS ====================

    /**
     * Carrega o gráfico de barras com as top disciplinas
     */
    private void carregarGraficoTopDisciplinas() {
        // Limpar barras anteriores
        containerChartBars.removeAllViews();

        // Obter top 5 disciplinas
        Cursor cursor = sessaoDAO.obterTopDisciplinas(5);

        if (cursor == null || cursor.getCount() == 0) {
            // Sem dados - mostrar mensagem
            tvNoChartData.setVisibility(View.VISIBLE);
            containerChartBars.setVisibility(View.GONE);
            if (cursor != null) cursor.close();
            return;
        }

        // Tem dados - ocultar mensagem e mostrar gráfico
        tvNoChartData.setVisibility(View.GONE);
        containerChartBars.setVisibility(View.VISIBLE);

        // Encontrar o valor máximo para normalização
        long maxTempo = 0;
        cursor.moveToFirst();
        do {
            long tempo = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));
            if (tempo > maxTempo) maxTempo = tempo;
        } while (cursor.moveToNext());

        // Renderizar cada barra
        cursor.moveToFirst();
        do {
            String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("nome_disciplina"));
            String corHex = cursor.getString(cursor.getColumnIndexOrThrow("cor_disciplina"));
            long tempoSegundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));

            // Criar view da barra
            View barraView = criarBarraGrafico(nomeDisciplina, corHex, tempoSegundos, maxTempo);
            containerChartBars.addView(barraView);

        } while (cursor.moveToNext());

        cursor.close();
    }

    /**
     * Cria uma view de barra individual do gráfico
     */
    private View criarBarraGrafico(String nome, String corHex, long tempoSegundos, long maxTempo) {
        // Container principal da barra
        LinearLayout barraContainer = new LinearLayout(requireContext());
        barraContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.setMargins(0, 0, 0, dpToPx(8));
        barraContainer.setLayoutParams(containerParams);

        // Row com nome e tempo
        LinearLayout infoRow = new LinearLayout(requireContext());
        infoRow.setOrientation(LinearLayout.HORIZONTAL);
        infoRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Nome da disciplina
        TextView tvNome = new TextView(requireContext());
        tvNome.setText(nome);
        tvNome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_glass));
        tvNome.setTextSize(13);
        tvNome.setMaxLines(1);
        LinearLayout.LayoutParams nomeParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        );
        tvNome.setLayoutParams(nomeParams);

        // Tempo formatado
        TextView tvTempo = new TextView(requireContext());
        tvTempo.setText(formatarTempoEstudo(tempoSegundos));
        tvTempo.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
        tvTempo.setTextSize(12);
        tvTempo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTempo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        infoRow.addView(tvNome);
        infoRow.addView(tvTempo);

        // Container da barra visual
        LinearLayout barraVisualContainer = new LinearLayout(requireContext());
        LinearLayout.LayoutParams barraContainerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(6)
        );
        barraContainerParams.setMargins(0, dpToPx(4), 0, 0);
        barraVisualContainer.setLayoutParams(barraContainerParams);
        barraVisualContainer.setOrientation(LinearLayout.HORIZONTAL);

        // Background da barra (cor clara)
        GradientDrawable bgBarra = new GradientDrawable();
        bgBarra.setCornerRadius(dpToPx(3));
        bgBarra.setColor(Color.parseColor("#30FFFFFF")); // Branco com 20% de opacidade
        barraVisualContainer.setBackground(bgBarra);

        // Barra preenchida (proporcional ao tempo)
        View barraPreenchida = new View(requireContext());
        float porcentagem = (float) tempoSegundos / maxTempo;
        LinearLayout.LayoutParams barraParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                porcentagem
        );
        barraPreenchida.setLayoutParams(barraParams);

        // Aplicar cor da disciplina
        GradientDrawable barraDrawable = new GradientDrawable();
        barraDrawable.setCornerRadius(dpToPx(3));
        try {
            barraDrawable.setColor(Color.parseColor(corHex));
        } catch (Exception e) {
            // Se a cor for inválida, usar cor padrão
            barraDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.accent));
        }
        barraPreenchida.setBackground(barraDrawable);

        // Espaço vazio (para completar a barra)
        View espacoVazio = new View(requireContext());
        LinearLayout.LayoutParams vazioParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f - porcentagem
        );
        espacoVazio.setLayoutParams(vazioParams);

        barraVisualContainer.addView(barraPreenchida);
        barraVisualContainer.addView(espacoVazio);

        // Adicionar tudo ao container principal
        barraContainer.addView(infoRow);
        barraContainer.addView(barraVisualContainer);

        return barraContainer;
    }

    /**
     * Converte dp para pixels
     */
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
