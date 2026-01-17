package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.repositories.TarefaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel para a tela de Tarefas.
 *
 * Responsabilidades:
 * - Manter o estado da UI (lista filtrada, filtro atual, ordenacao)
 * - Sobreviver a mudancas de configuracao (rotacao de tela)
 * - Expor dados via LiveData para a View observar
 * - Delegar operacoes de dados ao Repository
 *
 * NAO deve conter:
 * - Referencias a Views ou Activities
 * - Logica de UI (cores, visibilidade, etc)
 * - Acesso direto ao banco de dados
 */
public class TasksViewModel extends AndroidViewModel {

    // ═══════════════════════════════════════════════════════════════════════
    // ENUMS
    // ═══════════════════════════════════════════════════════════════════════

    public enum FilterType {
        ALL, TODAY, WEEK, OVERDUE, COMPLETED
    }

    public enum SortType {
        DATE, PRIORITY, SUBJECT
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO
    // ═══════════════════════════════════════════════════════════════════════

    private final TarefaRepository repository;

    // LiveData para observacao pela View
    private final MutableLiveData<List<Tarefa>> tarefas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Estado interno
    private FilterType currentFilter = FilterType.ALL;
    private SortType currentSort = SortType.DATE;
    private String searchQuery = "";
    private List<Tarefa> allTarefas = new ArrayList<>();

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUTOR
    // ═══════════════════════════════════════════════════════════════════════

    public TasksViewModel(@NonNull Application application) {
        super(application);
        repository = new TarefaRepository(application);
    }

    // Para testes
    public TasksViewModel(@NonNull Application application, TarefaRepository repository) {
        super(application);
        this.repository = repository;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LIVEDATA GETTERS (para observacao pela View)
    // ═══════════════════════════════════════════════════════════════════════

    public LiveData<List<Tarefa>> getTarefas() {
        return tarefas;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public FilterType getCurrentFilter() {
        return currentFilter;
    }

    public SortType getCurrentSort() {
        return currentSort;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ACOES (chamadas pela View)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Carrega as tarefas com base no filtro e ordenacao atuais
     */
    public void carregarTarefas() {
        isLoading.setValue(true);

        TarefaRepository.Callback<List<Tarefa>> callback = result -> {
            allTarefas = result != null ? result : new ArrayList<>();
            aplicarFiltroTexto();
            isLoading.postValue(false);
        };

        // Selecionar fonte baseado no filtro e ordenacao
        if (currentSort == SortType.PRIORITY) {
            repository.obterPorPrioridade(callback);
        } else if (currentSort == SortType.SUBJECT) {
            repository.obterPorDisciplina(callback);
        } else {
            // Ordenacao por data - aplicar filtro de categoria
            switch (currentFilter) {
                case TODAY:
                    repository.obterTarefasHoje(callback);
                    break;
                case WEEK:
                    repository.obterTarefasSemana(callback);
                    break;
                case OVERDUE:
                    repository.obterAtrasadas(callback);
                    break;
                case COMPLETED:
                    repository.obterConcluidas(callback);
                    break;
                case ALL:
                default:
                    repository.obterTodas(callback);
                    break;
            }
        }
    }

    /**
     * Define o filtro de categoria
     */
    public void setFilter(FilterType filter) {
        if (this.currentFilter != filter) {
            this.currentFilter = filter;
            carregarTarefas();
        }
    }

    /**
     * Define a ordenacao
     */
    public void setSort(SortType sort) {
        if (this.currentSort != sort) {
            this.currentSort = sort;
            carregarTarefas();
        }
    }

    /**
     * Define o texto de busca
     */
    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query.trim() : "";
        aplicarFiltroTexto();
    }

    /**
     * Marca tarefa como concluida ou pendente
     */
    public void toggleTarefaConcluida(Tarefa tarefa) {
        boolean novoEstado = !tarefa.estaConcluida();
        tarefa.setEstado(novoEstado ? EstadoTarefa.CONCLUIDA : EstadoTarefa.PENDENTE);

        repository.atualizar(tarefa, rows -> {
            if (rows > 0) {
                carregarTarefas();
            } else {
                errorMessage.postValue("Erro ao atualizar tarefa");
            }
        });
    }

    /**
     * Deleta uma tarefa
     */
    public void deletarTarefa(Tarefa tarefa) {
        repository.deletar(tarefa, rows -> {
            if (rows > 0) {
                carregarTarefas();
            } else {
                errorMessage.postValue("Erro ao deletar tarefa");
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODOS PRIVADOS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Aplica filtro de texto na lista carregada
     */
    private void aplicarFiltroTexto() {
        if (searchQuery.isEmpty()) {
            tarefas.postValue(new ArrayList<>(allTarefas));
        } else {
            String queryLower = searchQuery.toLowerCase();
            List<Tarefa> filtered = new ArrayList<>();

            for (Tarefa tarefa : allTarefas) {
                boolean matchTitulo = tarefa.getTitulo() != null &&
                        tarefa.getTitulo().toLowerCase().contains(queryLower);
                boolean matchDescricao = tarefa.getDescricao() != null &&
                        tarefa.getDescricao().toLowerCase().contains(queryLower);

                if (matchTitulo || matchDescricao) {
                    filtered.add(tarefa);
                }
            }

            tarefas.postValue(filtered);
        }
    }

    /**
     * Limpa mensagem de erro apos exibida
     */
    public void clearError() {
        errorMessage.setValue(null);
    }
}
