package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.EstatisticaAdapter;
import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Fragment que mostra estatísticas de tempo de estudo
 * Usa uma lista simples ao invés de gráficos complexos
 */
public class StatisticsFragment extends Fragment {

    private RecyclerView recyclerViewEstatisticas;
    private View emptyState;
    private View cardGrafico;
    private HorizontalBarChart chart;
    private EstatisticaAdapter adapter;
    private SessaoEstudoRoomDAO sessaoEstudoDAO;
    private Executor executor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        recyclerViewEstatisticas = view.findViewById(R.id.rv_estatisticas);
        emptyState = view.findViewById(R.id.empty_state);
        cardGrafico = view.findViewById(R.id.card_grafico);
        chart = view.findViewById(R.id.chart_estudo);

        sessaoEstudoDAO = AppDatabase.getInstance(requireContext()).sessaoEstudoDAO();
        executor = Executors.newSingleThreadExecutor();

        configurarGrafico();
        configurarRecyclerView();
        carregarEstatisticas();

        return view;
    }

    /**
     * Configura o gráfico de barras horizontais
     */
    private void configurarGrafico() {
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setFitBars(true);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);
        chart.setHighlightFullBarEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(11f);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value < 60) return String.format(Locale.getDefault(), "%.0fmin", value);
                return String.format(Locale.getDefault(), "%.1fh", value / 60f);
            }
        });

        chart.getAxisRight().setEnabled(false);
    }

    /**
     * Popula o gráfico com os dados carregados
     */
    private void atualizarGrafico(List<EstatisticaAdapter.ItemEstatistica> lista) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Integer> cores = new ArrayList<>();

        for (int i = 0; i < lista.size(); i++) {
            EstatisticaAdapter.ItemEstatistica item = lista.get(i);
            entries.add(new BarEntry(i, item.minutos));
            String label = item.nomeDisciplina.length() > 12
                    ? item.nomeDisciplina.substring(0, 12) + "…"
                    : item.nomeDisciplina;
            labels.add(label);
            try {
                cores.add(Color.parseColor(item.cor));
            } catch (Exception e) {
                cores.add(getResources().getColor(R.color.primary));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(cores);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value < 60) return String.format(Locale.getDefault(), "%.0fmin", value);
                return String.format(Locale.getDefault(), "%.1fh", value / 60f);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setLabelCount(labels.size());
        chart.setData(barData);

        // Ajustar altura dinamicamente (pelo menos 150dp, 50dp por disciplina)
        int alturaDp = Math.max(150, lista.size() * 50);
        int alturaPx = (int) (alturaDp * getResources().getDisplayMetrics().density);
        chart.getLayoutParams().height = alturaPx;

        chart.invalidate();
    }

    /**
     * Configura a RecyclerView com layout linear
     */
    private void configurarRecyclerView() {
        recyclerViewEstatisticas.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new EstatisticaAdapter(requireContext());
        recyclerViewEstatisticas.setAdapter(adapter);
    }

    /**
     * Carrega as estatísticas dos últimos 7 dias
     */
    private void carregarEstatisticas() {
        executor.execute(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            long inicio7Dias = cal.getTimeInMillis();

            List<EstatisticaAdapter.ItemEstatistica> listaEstatisticas = new ArrayList<>();
            Cursor cursor = null;
            try {
                cursor = sessaoEstudoDAO.obterTempoUltimos7DiasPorDisciplina(inicio7Dias);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("nome_disciplina"));
                        float totalMinutos = cursor.getFloat(cursor.getColumnIndexOrThrow("total_minutos"));
                        String cor = cursor.getString(cursor.getColumnIndexOrThrow("cor"));
                        listaEstatisticas.add(new EstatisticaAdapter.ItemEstatistica(nomeDisciplina, cor, totalMinutos));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                android.util.Log.e("StatisticsFragment", "Erro ao carregar estatisticas", e);
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }

            final List<EstatisticaAdapter.ItemEstatistica> lista = listaEstatisticas;
            requireActivity().runOnUiThread(() -> {
                if (lista.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerViewEstatisticas.setVisibility(View.GONE);
                    cardGrafico.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerViewEstatisticas.setVisibility(View.VISIBLE);
                    cardGrafico.setVisibility(View.VISIBLE);
                    adapter.atualizarLista(lista);
                    atualizarGrafico(lista);
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarEstatisticas();
    }
}
