package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Fragment que mostra estatísticas de tempo de estudo
 * Usa uma lista simples ao invés de gráficos complexos
 */
public class StatisticsFragment extends Fragment {

    private RecyclerView recyclerViewEstatisticas;
    private View emptyState;
    private EstatisticaAdapter adapter;
    private SessaoEstudoRoomDAO sessaoEstudoDAO;
    private Executor executor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        recyclerViewEstatisticas = view.findViewById(R.id.rv_estatisticas);
        emptyState = view.findViewById(R.id.empty_state);

        sessaoEstudoDAO = AppDatabase.getInstance(requireContext()).sessaoEstudoDAO();
        executor = Executors.newSingleThreadExecutor();

        configurarRecyclerView();
        carregarEstatisticas();

        return view;
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
            // Calcular início dos últimos 7 dias
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7);
            long inicio7Dias = cal.getTimeInMillis();

            Cursor cursor = sessaoEstudoDAO.obterTempoUltimos7DiasPorDisciplina(inicio7Dias);

            List<EstatisticaAdapter.ItemEstatistica> listaEstatisticas = new ArrayList<>();

            // Processar os dados do cursor
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("nome_disciplina"));
                    float totalMinutos = cursor.getFloat(cursor.getColumnIndexOrThrow("total_minutos"));
                    String cor = cursor.getString(cursor.getColumnIndexOrThrow("cor"));

                    // Criar item de estatística
                    EstatisticaAdapter.ItemEstatistica item =
                        new EstatisticaAdapter.ItemEstatistica(nomeDisciplina, cor, totalMinutos);
                    listaEstatisticas.add(item);

                } while (cursor.moveToNext());
                cursor.close();
            }

            // Atualizar a UI na thread principal
            requireActivity().runOnUiThread(() -> {
                if (listaEstatisticas.isEmpty()) {
                    // Mostrar mensagem de lista vazia
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerViewEstatisticas.setVisibility(View.GONE);
                } else {
                    // Mostrar a lista de estatísticas
                    emptyState.setVisibility(View.GONE);
                    recyclerViewEstatisticas.setVisibility(View.VISIBLE);
                    adapter.atualizarLista(listaEstatisticas);
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
