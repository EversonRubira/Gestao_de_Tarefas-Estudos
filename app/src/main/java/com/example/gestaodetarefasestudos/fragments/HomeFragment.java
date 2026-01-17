package com.example.gestaodetarefasestudos.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.PreferenciasApp;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.CalendarioAdapter;
import com.example.gestaodetarefasestudos.models.DiaCalendario;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.viewmodels.HomeViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment para a tela Home (Dashboard).
 *
 * Usa MVVM:
 * - View (este Fragment): Exibe UI, observa LiveData
 * - ViewModel (HomeViewModel): Mantem estado, processa logica
 * - Model (Repository/DAO): Acesso a dados
 */
public class HomeFragment extends Fragment {

    // ViewModel
    private HomeViewModel viewModel;

    // UI Components - Estatisticas
    private TextView textTotalDisciplinas;
    private TextView textPendingTasks;
    private TextView textStudyTime;
    private LinearLayout layoutListaTempo;

    // UI Components - Calendario
    private TextView textMonthYear;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private RecyclerView rvCalendar;
    private CalendarioAdapter calendarAdapter;

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

        inicializarViewModel();
        inicializarComponentes(view);
        configurarCalendario();
        observarViewModel();

        // Carregar dados iniciais
        long usuarioId = new PreferenciasApp(requireContext()).getUsuarioId();
        viewModel.setUsuarioId(usuarioId);
        viewModel.carregarEstatisticas();
        viewModel.carregarCalendario();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.carregarEstatisticas();
    }

    /**
     * Inicializa o ViewModel usando ViewModelProvider
     */
    private void inicializarViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void inicializarComponentes(View view) {
        // Estatisticas
        textTotalDisciplinas = view.findViewById(R.id.tv_total_subjects);
        textPendingTasks = view.findViewById(R.id.tv_pending_tasks);
        textStudyTime = view.findViewById(R.id.tv_study_time);
        layoutListaTempo = view.findViewById(R.id.layout_study_time_list);

        // Calendario
        textMonthYear = view.findViewById(R.id.tv_mes_ano);
        btnPrev = view.findViewById(R.id.btn_mes_anterior);
        btnNext = view.findViewById(R.id.btn_proximo_mes);
        rvCalendar = view.findViewById(R.id.rv_calendario);
    }

    private void configurarCalendario() {
        // Grid com 7 colunas (7 dias da semana)
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        rvCalendar.setLayoutManager(layoutManager);

        calendarAdapter = new CalendarioAdapter(requireContext());
        rvCalendar.setAdapter(calendarAdapter);

        // Click em um dia
        calendarAdapter.setOnDiaClickListener(dia -> {
            viewModel.carregarTarefasDoDia(dia);
        });

        // Navegacao entre meses
        btnPrev.setOnClickListener(v -> viewModel.mesAnterior());
        btnNext.setOnClickListener(v -> viewModel.proximoMes());
    }

    /**
     * Observa as mudancas no ViewModel e atualiza a UI
     */
    private void observarViewModel() {
        // Observar estatisticas
        viewModel.getTotalDisciplinas().observe(getViewLifecycleOwner(), total -> {
            textTotalDisciplinas.setText(String.valueOf(total));
        });

        viewModel.getTarefasPendentes().observe(getViewLifecycleOwner(), pendentes -> {
            textPendingTasks.setText(String.valueOf(pendentes));
        });

        viewModel.getTempoEstudoTotal().observe(getViewLifecycleOwner(), segundos -> {
            textStudyTime.setText(formatarTempoEstudo(segundos));
        });

        viewModel.getTemposPorDisciplina().observe(getViewLifecycleOwner(), tempos -> {
            atualizarListaTempos(tempos);
        });

        // Observar calendario
        viewModel.getDiasCalendario().observe(getViewLifecycleOwner(), dias -> {
            calendarAdapter.setDias(dias);
            atualizarTituloMes();
        });

        // Observar tarefas do dia selecionado
        viewModel.getTarefasDoDia().observe(getViewLifecycleOwner(), tarefas -> {
            if (tarefas != null && !tarefas.isEmpty()) {
                mostrarTarefasDoDia(tarefas);
            }
        });
    }

    /**
     * Atualiza a lista de tempos de estudo por disciplina
     */
    private void atualizarListaTempos(List<HomeViewModel.TempoEstudoDisciplina> tempos) {
        layoutListaTempo.removeAllViews();

        if (tempos == null || tempos.isEmpty()) {
            TextView textoVazio = new TextView(requireContext());
            textoVazio.setText(getString(R.string.no_study_today));
            textoVazio.setTextColor(getResources().getColor(R.color.text_secondary));
            textoVazio.setTextSize(14);
            textoVazio.setPadding(0, 8, 0, 8);
            layoutListaTempo.addView(textoVazio);
        } else {
            for (HomeViewModel.TempoEstudoDisciplina tempo : tempos) {
                adicionarLinhaDisciplina(tempo.getNomeDisciplina(), tempo.getSegundos());
            }
        }
    }

    private void adicionarLinhaDisciplina(String nomeDisciplina, long segundos) {
        LinearLayout linha = new LinearLayout(requireContext());
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setPadding(0, 4, 0, 4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linha.setLayoutParams(params);

        // Nome da disciplina
        TextView textoNome = new TextView(requireContext());
        textoNome.setText(nomeDisciplina);
        textoNome.setTextColor(getResources().getColor(R.color.text_primary));
        textoNome.setTextSize(14);
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
        textoTempo.setTextSize(14);

        linha.addView(textoNome);
        linha.addView(textoTempo);
        layoutListaTempo.addView(linha);
    }

    /**
     * Atualiza o titulo do mes/ano no calendario
     */
    private void atualizarTituloMes() {
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        textMonthYear.setText(formatoMesAno.format(viewModel.getCalendarioAtual().getTime()));
    }

    /**
     * Mostra toast com tarefas do dia selecionado
     */
    private void mostrarTarefasDoDia(List<Tarefa> tarefas) {
        if (tarefas.isEmpty()) return;

        StringBuilder mensagem = new StringBuilder();
        Tarefa primeiraTarefa = tarefas.get(0);

        SimpleDateFormat formatoDia = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        mensagem.append("Tarefas de ").append(formatoDia.format(new Date(primeiraTarefa.getDataEntrega()))).append(":\n\n");

        for (Tarefa tarefa : tarefas) {
            mensagem.append("• ").append(tarefa.getTitulo());
            if (tarefa.getNomeDisciplina() != null) {
                mensagem.append(" (").append(tarefa.getNomeDisciplina()).append(")");
            }
            mensagem.append("\n");
        }

        Toast.makeText(requireContext(), mensagem.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * Converte segundos para formato horas e minutos
     */
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }
}
