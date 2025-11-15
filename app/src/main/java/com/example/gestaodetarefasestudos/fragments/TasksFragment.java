package com.example.gestaodetarefasestudos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.AdicionarEditarTarefaActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.TarefaAdapter;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private RecyclerView recyclerViewTarefas;
    private View txtEstadoVazio;
    private FloatingActionButton botaoAdicionar;

    private TarefaAdapter tarefaAdapter;
    private TarefaDAO tarefaDAO;
    private List<Tarefa> listaTarefas;

    public TasksFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarComponentes(view);
        configurarRecyclerView();
        configurarBotaoAdicionar();
        carregarTarefas();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarTarefas();
    }

    private void inicializarComponentes(View view) {
        recyclerViewTarefas = view.findViewById(R.id.rv_tasks);
        txtEstadoVazio = view.findViewById(R.id.empty_state);
        botaoAdicionar = view.findViewById(R.id.fab_add_task);

        tarefaDAO = new TarefaDAO(requireContext());
        listaTarefas = new ArrayList<>();
    }

    private void configurarRecyclerView() {
        recyclerViewTarefas.setLayoutManager(new LinearLayoutManager(requireContext()));

        tarefaAdapter = new TarefaAdapter(requireContext(), listaTarefas,
                this::carregarTarefas);
        recyclerViewTarefas.setAdapter(tarefaAdapter);
    }

    private void configurarBotaoAdicionar() {
        botaoAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdicionarEditarTarefaActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Carrega todas as tarefas do banco de dados
     * Atualiza a RecyclerView com as tarefas encontradas
     */
    private void carregarTarefas() {
        // Buscar todas as tarefas
        listaTarefas = tarefaDAO.obterTodas();

        // Contar tarefas para possíveis estatísticas futuras
        @SuppressWarnings("unused")
        int totalTarefas = listaTarefas.size();

        // Verificar se existem tarefas
        if (listaTarefas.isEmpty()) {
            // Nenhuma tarefa encontrada - mostrar mensagem
            recyclerViewTarefas.setVisibility(View.GONE);
            txtEstadoVazio.setVisibility(View.VISIBLE);
        } else {
            // Existem tarefas - mostrar a lista
            recyclerViewTarefas.setVisibility(View.VISIBLE);
            txtEstadoVazio.setVisibility(View.GONE);
        }

        // Atualizar o RecyclerView com as tarefas
        tarefaAdapter.atualizarLista(listaTarefas);

        // TODO: Adicionar filtro por estado (pendente, em progresso, concluída)
        // TODO: Adicionar filtro por disciplina
        // TODO: Ordenar por data de entrega ou prioridade
    }
}
