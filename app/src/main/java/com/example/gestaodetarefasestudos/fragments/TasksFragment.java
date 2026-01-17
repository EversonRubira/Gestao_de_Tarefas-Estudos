package com.example.gestaodetarefasestudos.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.AdicionarEditarTarefaActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.TarefaAdapter;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.viewmodels.TasksViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment para exibicao e gerenciamento de tarefas.
 *
 * Usa MVVM:
 * - View (este Fragment): Exibe UI, observa LiveData, encaminha acoes ao ViewModel
 * - ViewModel (TasksViewModel): Mantem estado, processa logica, expoe LiveData
 * - Model (Repository/DAO): Acesso a dados
 */
public class TasksFragment extends Fragment {

    // ViewModel
    private TasksViewModel viewModel;

    // UI Components
    private RecyclerView recyclerViewTarefas;
    private LinearLayout emptyState;
    private TextView txtEmptyMessage;
    private FloatingActionButton botaoAdicionar;
    private EditText editSearch;
    private ImageView btnClearSearch;
    private ChipGroup chipGroupFilters;
    private Chip chipAll, chipToday, chipWeek, chipOverdue, chipCompleted;
    private TextView txtTaskCount;
    private TextView btnSort;

    // Adapter
    private TarefaAdapter tarefaAdapter;
    private List<Tarefa> listaTarefas = new ArrayList<>();

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

        inicializarViewModel();
        inicializarComponentes(view);
        configurarRecyclerView();
        configurarBotaoAdicionar();
        configurarBusca();
        configurarFiltros();
        configurarOrdenacao();
        observarViewModel();

        // Carregar dados iniciais
        viewModel.carregarTarefas();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.carregarTarefas();
    }

    /**
     * Inicializa o ViewModel usando ViewModelProvider.
     * O ViewModel sobrevive a mudancas de configuracao (rotacao).
     */
    private void inicializarViewModel() {
        viewModel = new ViewModelProvider(this).get(TasksViewModel.class);
    }

    private void inicializarComponentes(View view) {
        recyclerViewTarefas = view.findViewById(R.id.rv_tasks);
        emptyState = view.findViewById(R.id.empty_state);
        txtEmptyMessage = view.findViewById(R.id.txt_empty_message);
        botaoAdicionar = view.findViewById(R.id.fab_add_task);

        // Busca
        editSearch = view.findViewById(R.id.edit_search);
        btnClearSearch = view.findViewById(R.id.btn_clear_search);

        // Filtros
        chipGroupFilters = view.findViewById(R.id.chip_group_filters);
        chipAll = view.findViewById(R.id.chip_all);
        chipToday = view.findViewById(R.id.chip_today);
        chipWeek = view.findViewById(R.id.chip_week);
        chipOverdue = view.findViewById(R.id.chip_overdue);
        chipCompleted = view.findViewById(R.id.chip_completed);

        // Ordenacao e contagem
        txtTaskCount = view.findViewById(R.id.txt_task_count);
        btnSort = view.findViewById(R.id.btn_sort);
    }

    private void configurarRecyclerView() {
        recyclerViewTarefas.setLayoutManager(new LinearLayoutManager(requireContext()));

        tarefaAdapter = new TarefaAdapter(requireContext(), listaTarefas,
                () -> viewModel.carregarTarefas());
        recyclerViewTarefas.setAdapter(tarefaAdapter);

        configurarSwipeParaDeletar();
    }

    private void configurarBotaoAdicionar() {
        botaoAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AdicionarEditarTarefaActivity.class);
            startActivity(intent);
        });
    }

    private void configurarBusca() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                btnClearSearch.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
                viewModel.setSearchQuery(query);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearSearch.setOnClickListener(v -> {
            editSearch.setText("");
            viewModel.setSearchQuery("");
            btnClearSearch.setVisibility(View.GONE);
        });
    }

    private void configurarFiltros() {
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            TasksViewModel.FilterType filter;

            if (checkedId == R.id.chip_all) {
                filter = TasksViewModel.FilterType.ALL;
            } else if (checkedId == R.id.chip_today) {
                filter = TasksViewModel.FilterType.TODAY;
            } else if (checkedId == R.id.chip_week) {
                filter = TasksViewModel.FilterType.WEEK;
            } else if (checkedId == R.id.chip_overdue) {
                filter = TasksViewModel.FilterType.OVERDUE;
            } else if (checkedId == R.id.chip_completed) {
                filter = TasksViewModel.FilterType.COMPLETED;
            } else {
                filter = TasksViewModel.FilterType.ALL;
            }

            viewModel.setFilter(filter);
        });
    }

    private void configurarOrdenacao() {
        btnSort.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), btnSort);
            popup.getMenu().add(0, 0, 0, R.string.sort_by_date);
            popup.getMenu().add(0, 1, 1, R.string.sort_by_priority);
            popup.getMenu().add(0, 2, 2, R.string.sort_by_subject);

            popup.setOnMenuItemClickListener(item -> {
                TasksViewModel.SortType sort;
                int textRes;

                switch (item.getItemId()) {
                    case 1:
                        sort = TasksViewModel.SortType.PRIORITY;
                        textRes = R.string.sort_by_priority;
                        break;
                    case 2:
                        sort = TasksViewModel.SortType.SUBJECT;
                        textRes = R.string.sort_by_subject;
                        break;
                    case 0:
                    default:
                        sort = TasksViewModel.SortType.DATE;
                        textRes = R.string.sort_by_date;
                        break;
                }

                viewModel.setSort(sort);
                btnSort.setText(textRes);
                return true;
            });

            popup.show();
        });
    }

    /**
     * Observa as mudancas no ViewModel e atualiza a UI.
     * Esta e a essencia do padrao MVVM - a View observa o estado.
     */
    private void observarViewModel() {
        // Observar lista de tarefas
        viewModel.getTarefas().observe(getViewLifecycleOwner(), tarefas -> {
            listaTarefas = tarefas != null ? tarefas : new ArrayList<>();
            atualizarUI();
        });

        // Observar estado de loading (para futuro progress indicator)
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // Pode adicionar ProgressBar aqui no futuro
        });

        // Observar mensagens de erro
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Snackbar.make(recyclerViewTarefas, error, Snackbar.LENGTH_SHORT).show();
                viewModel.clearError();
            }
        });
    }

    /**
     * Atualiza a interface com os dados atuais
     */
    private void atualizarUI() {
        // Atualizar contador
        int count = listaTarefas.size();
        if (count == 0) {
            txtTaskCount.setText("");
        } else if (count == 1) {
            txtTaskCount.setText(R.string.tasks_count_singular);
        } else {
            txtTaskCount.setText(getString(R.string.tasks_count, count));
        }

        // Mostrar/esconder lista ou estado vazio
        if (listaTarefas.isEmpty()) {
            recyclerViewTarefas.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);

            // Mensagem apropriada
            String query = viewModel.getSearchQuery();
            if (query != null && !query.isEmpty()) {
                txtEmptyMessage.setText(R.string.no_tasks_found);
            } else {
                txtEmptyMessage.setText(R.string.tasks_empty);
            }
        } else {
            recyclerViewTarefas.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }

        tarefaAdapter.atualizarLista(listaTarefas);
    }

    /**
     * Configura o swipe to dismiss para tarefas
     */
    private void configurarSwipeParaDeletar() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private final ColorDrawable backgroundVerde = new ColorDrawable(Color.parseColor("#4CAF50"));
            private final ColorDrawable backgroundVermelho = new ColorDrawable(Color.parseColor("#F44336"));
            private Drawable iconeCheck;
            private Drawable iconeDelete;

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position < 0 || position >= listaTarefas.size()) return;

                Tarefa tarefa = listaTarefas.get(position);

                if (direction == ItemTouchHelper.RIGHT) {
                    // Marcar como concluida/pendente via ViewModel
                    viewModel.toggleTarefaConcluida(tarefa);

                    String msg = tarefa.estaConcluida() ?
                            getString(R.string.task_mark_incomplete) :
                            getString(R.string.task_mark_complete);
                    Snackbar.make(recyclerViewTarefas, msg, Snackbar.LENGTH_SHORT).show();
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Deletar com undo
                    deletarTarefaComDesfazer(tarefa, position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    backgroundVerde.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int) dX, itemView.getBottom());
                    backgroundVerde.draw(c);

                    if (iconeCheck == null) {
                        iconeCheck = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_agenda);
                        if (iconeCheck != null) iconeCheck.setTint(Color.WHITE);
                    }

                    if (iconeCheck != null) {
                        int iconMargin = (itemView.getHeight() - iconeCheck.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + iconeCheck.getIntrinsicHeight();
                        int iconLeft = itemView.getLeft() + iconMargin;
                        int iconRight = iconLeft + iconeCheck.getIntrinsicWidth();
                        iconeCheck.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        iconeCheck.draw(c);
                    }
                } else if (dX < 0) {
                    backgroundVermelho.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    backgroundVermelho.draw(c);

                    if (iconeDelete == null) {
                        iconeDelete = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete);
                        if (iconeDelete != null) iconeDelete.setTint(Color.WHITE);
                    }

                    if (iconeDelete != null) {
                        int iconMargin = (itemView.getHeight() - iconeDelete.getIntrinsicHeight()) / 2;
                        int iconTop = itemView.getTop() + iconMargin;
                        int iconBottom = iconTop + iconeDelete.getIntrinsicHeight();
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconLeft = iconRight - iconeDelete.getIntrinsicWidth();
                        iconeDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        iconeDelete.draw(c);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTarefas);
    }

    /**
     * Deleta tarefa com opcao de desfazer
     */
    private void deletarTarefaComDesfazer(Tarefa tarefa, int position) {
        // Remover da lista local temporariamente
        listaTarefas.remove(position);
        tarefaAdapter.atualizarLista(listaTarefas);
        atualizarUI();

        Snackbar.make(recyclerViewTarefas, R.string.success_task_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.cancel, v -> {
                    // Desfazer: recarregar do banco
                    viewModel.carregarTarefas();
                })
                .addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            // Confirmar delecao via ViewModel
                            viewModel.deletarTarefa(tarefa);
                        }
                    }
                })
                .show();
    }
}
