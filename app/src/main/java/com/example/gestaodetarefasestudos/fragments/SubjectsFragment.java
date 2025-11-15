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

import com.example.gestaodetarefasestudos.AdicionarEditarDisciplinaActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.DisciplinaAdapter;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends Fragment {

    private RecyclerView recyclerViewDisciplinas;
    private View txtEstadoVazio;
    private FloatingActionButton botaoAdicionar;

    private DisciplinaAdapter disciplinaAdapter;
    private DisciplinaDAO disciplinaDAO;
    private List<Disciplina> listaDisciplinas;

    public SubjectsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarComponentes(view);
        configurarRecyclerView();
        configurarBotaoAdicionar();
        carregarDisciplinas();
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarDisciplinas();
    }

    private void inicializarComponentes(View view) {
        recyclerViewDisciplinas = view.findViewById(R.id.rv_subjects);
        txtEstadoVazio = view.findViewById(R.id.empty_state);
        botaoAdicionar = view.findViewById(R.id.fab_add_subject);

        disciplinaDAO = new DisciplinaDAO(requireContext());
        listaDisciplinas = new ArrayList<>();
    }

    private void configurarRecyclerView() {
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(requireContext()));

        disciplinaAdapter = new DisciplinaAdapter(requireContext(), listaDisciplinas,
                this::carregarDisciplinas);
        recyclerViewDisciplinas.setAdapter(disciplinaAdapter);
    }

    private void configurarBotaoAdicionar() {
        botaoAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdicionarEditarDisciplinaActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Carrega a lista de disciplinas do banco de dados
     * e atualiza a interface conforme necessário
     */
    private void carregarDisciplinas() {
        // Buscar todas as disciplinas do banco
        listaDisciplinas = disciplinaDAO.obterTodas();

        // Variável para debug - pode ser útil no futuro
        @SuppressWarnings("unused")
        int totalDisciplinas = listaDisciplinas.size();

        // Verificar se a lista está vazia
        if (listaDisciplinas.isEmpty()) {
            // Mostrar mensagem de lista vazia
            recyclerViewDisciplinas.setVisibility(View.GONE);
            txtEstadoVazio.setVisibility(View.VISIBLE);
        } else {
            // Mostrar a lista de disciplinas
            recyclerViewDisciplinas.setVisibility(View.VISIBLE);
            txtEstadoVazio.setVisibility(View.GONE);
        }

        // Atualizar o adapter com os novos dados
        disciplinaAdapter.atualizarLista(listaDisciplinas);

        // TODO: Adicionar funcionalidade de filtro/pesquisa
        // TODO: Ordenar disciplinas alfabeticamente ou por data
    }
}
