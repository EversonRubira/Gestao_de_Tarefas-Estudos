package com.example.gestaodetarefasestudos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.AdicionarEditarDisciplinaActivity;
import com.example.gestaodetarefasestudos.PreferenciasApp;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.DisciplinaAdapter;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.viewmodels.SubjectsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment para exibicao e gerenciamento de disciplinas.
 *
 * Usa MVVM:
 * - View (este Fragment): Exibe UI, observa LiveData
 * - ViewModel (SubjectsViewModel): Mantem estado, processa logica
 * - Model (Repository/DAO): Acesso a dados
 */
public class SubjectsFragment extends Fragment {

    // ViewModel
    private SubjectsViewModel viewModel;

    // UI Components
    private RecyclerView recyclerViewDisciplinas;
    private View emptyState;
    private FloatingActionButton botaoAdicionar;

    // Adapter
    private DisciplinaAdapter disciplinaAdapter;
    private List<Disciplina> listaDisciplinas = new ArrayList<>();

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

        inicializarViewModel();
        inicializarComponentes(view);
        configurarRecyclerView();
        configurarBotaoAdicionar();
        observarViewModel();

        // Carregar dados iniciais
        long usuarioId = new PreferenciasApp(requireContext()).getUsuarioId();
        viewModel.setUsuarioId(usuarioId);
        viewModel.carregarDisciplinas();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.carregarDisciplinas();
    }

    /**
     * Inicializa o ViewModel usando ViewModelProvider
     */
    private void inicializarViewModel() {
        viewModel = new ViewModelProvider(this).get(SubjectsViewModel.class);
    }

    private void inicializarComponentes(View view) {
        recyclerViewDisciplinas = view.findViewById(R.id.rv_subjects);
        emptyState = view.findViewById(R.id.empty_state);
        botaoAdicionar = view.findViewById(R.id.fab_add_subject);
    }

    private void configurarRecyclerView() {
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(requireContext()));

        disciplinaAdapter = new DisciplinaAdapter(requireContext(), listaDisciplinas,
                () -> viewModel.carregarDisciplinas());
        recyclerViewDisciplinas.setAdapter(disciplinaAdapter);
    }

    private void configurarBotaoAdicionar() {
        botaoAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdicionarEditarDisciplinaActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Observa as mudancas no ViewModel e atualiza a UI
     */
    private void observarViewModel() {
        // Observar lista de disciplinas
        viewModel.getDisciplinas().observe(getViewLifecycleOwner(), disciplinas -> {
            listaDisciplinas = disciplinas != null ? disciplinas : new ArrayList<>();
            atualizarUI();
        });

        // Observar estado de loading (para futuro progress indicator)
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Pode adicionar ProgressBar aqui no futuro
        });

        // Observar mensagens de erro
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(recyclerViewDisciplinas, error, Snackbar.LENGTH_SHORT).show();
                viewModel.clearError();
            }
        });
    }

    /**
     * Atualiza a interface com os dados atuais
     */
    private void atualizarUI() {
        if (listaDisciplinas.isEmpty()) {
            recyclerViewDisciplinas.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewDisciplinas.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }

        disciplinaAdapter.atualizarLista(listaDisciplinas);
    }
}
