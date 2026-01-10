package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.PreferenciasApp;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.CalendarioAdapter;
import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView textTotalDisciplinas;
    private TextView textPendingTasks;
    private TextView textStudyTime;
    private LinearLayout layoutListaTempo;

    // componentes calendário
    private TextView textMonthYear;
    private ImageButton btnPrev;
    private ImageButton btnNext;
    private RecyclerView rvCalendar;
    private CalendarioAdapter calendarAdapter;

    private DisciplinaRoomDAO disciplinaDAO;
    private TarefaRoomDAO tarefaDAO;
    private SessaoEstudoRoomDAO sessaoDAO;
    private Executor executor;

    private Calendar currentCalendar;

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

        // inicializar DAOs
        disciplinaDAO = AppDatabase.getInstance(requireContext()).disciplinaDAO();
        tarefaDAO = AppDatabase.getInstance(requireContext()).tarefaDAO();
        sessaoDAO = AppDatabase.getInstance(requireContext()).sessaoEstudoDAO();
        executor = Executors.newSingleThreadExecutor();

        // inicializar views
        textTotalDisciplinas = view.findViewById(R.id.tv_total_subjects);
        textPendingTasks = view.findViewById(R.id.tv_pending_tasks);
        textStudyTime = view.findViewById(R.id.tv_study_time);
        layoutListaTempo = view.findViewById(R.id.layout_study_time_list);
        textMonthYear = view.findViewById(R.id.tv_mes_ano);
        btnPrev = view.findViewById(R.id.btn_mes_anterior);
        btnNext = view.findViewById(R.id.btn_proximo_mes);
        rvCalendar = view.findViewById(R.id.rv_calendario);

        setupCalendar();
        loadStats();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
    }

    // carrega as stats do dashboard
    private void loadStats() {
        long usuarioId = new PreferenciasApp(requireContext()).getUsuarioId();

        executor.execute(() -> {
            int totalDisciplinas = disciplinaDAO.contarTotal(usuarioId);
            int tarefasPendentes = tarefaDAO.contarPendentes();

            requireActivity().runOnUiThread(() -> {
                textTotalDisciplinas.setText(String.valueOf(totalDisciplinas));
                textPendingTasks.setText(String.valueOf(tarefasPendentes));
            });
        });

        loadStudyTimeBySubject();
    }

    // mostra o tempo de estudo de hoje por disciplina
    private void loadStudyTimeBySubject() {
        executor.execute(() -> {
            // Calcular início do dia (meia-noite de hoje)
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long inicioDia = cal.getTimeInMillis();

            Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina(inicioDia);
            long tempoTotal = 0;

            List<String> nomesDisciplinas = new ArrayList<>();
            List<Long> segundosPorDisciplina = new ArrayList<>();

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("d_nome"));
                    long segundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));

                    if (nomeDisciplina == null || nomeDisciplina.isEmpty()) {
                        nomeDisciplina = "Geral";
                    }

                    tempoTotal += segundos;
                    nomesDisciplinas.add(nomeDisciplina);
                    segundosPorDisciplina.add(segundos);
                }
                cursor.close();
            }

            long tempoFinal = tempoTotal;
            requireActivity().runOnUiThread(() -> {
                layoutListaTempo.removeAllViews();

                // se nao tiver nada mostra msg
                if (tempoFinal == 0) {
                    TextView textoVazio = new TextView(requireContext());
                    textoVazio.setText(getString(R.string.no_study_today));
                    textoVazio.setTextColor(getResources().getColor(R.color.text_secondary));
                    textoVazio.setTextSize(14);
                    textoVazio.setPadding(0, 8, 0, 8);
                    layoutListaTempo.addView(textoVazio);
                } else {
                    for (int i = 0; i < nomesDisciplinas.size(); i++) {
                        adicionarLinhaDisciplina(nomesDisciplinas.get(i), segundosPorDisciplina.get(i));
                    }
                }

                String tempoFormatado = formatarTempoEstudo(tempoFinal);
                textStudyTime.setText(tempoFormatado);
            });
        });
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

        // nome da disciplina
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

        // tempo
        TextView textoTempo = new TextView(requireContext());
        String tempoFormatado = formatarTempoEstudo(segundos);
        textoTempo.setText(tempoFormatado);
        textoTempo.setTextColor(getResources().getColor(R.color.primary));
        textoTempo.setTextSize(14);

        linha.addView(textoNome);
        linha.addView(textoTempo);
        layoutListaTempo.addView(linha);
    }

    // converte segundos pra formato de horas e minutos
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;
        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }

    // setup do calendário
    private void setupCalendar() {
        currentCalendar = Calendar.getInstance();

        // grid com 7 colunas (7 dias da semana)
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        rvCalendar.setLayoutManager(layoutManager);

        calendarAdapter = new CalendarioAdapter(requireContext());
        rvCalendar.setAdapter(calendarAdapter);

        calendarAdapter.setOnDiaClickListener(dia -> {
            showTasksForDay(dia);
        });

        // botoes pra navegar entre meses
        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            loadCalendar();
        });

        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            loadCalendar();
        });

        loadCalendar();
    }

    // carrega o calendário do mês
    private void loadCalendar() {
        // Usar o locale configurado pelo utilizador (pt ou en)
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        textMonthYear.setText(formatoMesAno.format(currentCalendar.getTime()));

        List<DiaCalendario> dias = generateDaysOfMonth();
        applyTaskColors(dias);
        calendarAdapter.setDias(dias);
    }

    private List<DiaCalendario> generateDaysOfMonth() {
        List<DiaCalendario> dias = new ArrayList<>();
        Calendar cal = (Calendar) currentCalendar.clone();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int diaDaSemana = cal.get(Calendar.DAY_OF_WEEK);

        // adicionar dias vazios no inicio pra alinhar
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

            // marcar se eh hoje
            if (cal.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == hoje.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == hoje.get(Calendar.DAY_OF_MONTH)) {
                diaCalendario.setDiaAtual(true);
            }

            dias.add(diaCalendario);
        }

        return dias;
    }

    // aplica as cores das tarefas aos dias do calendário
    private void applyTaskColors(List<DiaCalendario> dias) {
        Calendar cal = (Calendar) currentCalendar.clone();
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

        executor.execute(() -> {
            Cursor cursor = tarefaDAO.obterTarefasPorPeriodoComCores(inicioMes, fimMes);

            Map<String, List<String>> coresPorDia = new HashMap<>();
            Map<String, Integer> contagemPorDia = new HashMap<>();

            if (cursor != null && cursor.moveToFirst()) {
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
            }

            requireActivity().runOnUiThread(() -> {
                // aplicar as cores aos dias
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
                calendarAdapter.notifyDataSetChanged();
            });
        });
    }

    // mostra tarefas quando clica no dia
    private void showTasksForDay(DiaCalendario dia) {
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

        executor.execute(() -> {
            List<Tarefa> tarefas = tarefaDAO.obterTarefasPorDia(inicioDia, fimDia);

            requireActivity().runOnUiThread(() -> {
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
            });
        });
    }
}
