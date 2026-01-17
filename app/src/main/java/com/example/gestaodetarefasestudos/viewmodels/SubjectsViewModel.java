package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.repositories.DisciplinaRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel para a tela de Disciplinas (Subjects).
 *
 * Responsabilidades:
 * - Manter lista de disciplinas do usuario
 * - Sobreviver a mudancas de configuracao
 * - Expor dados via LiveData para a View observar
 * - Delegar operacoes de dados ao Repository
 */
public class SubjectsViewModel extends AndroidViewModel {

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO
    // ═══════════════════════════════════════════════════════════════════════

    private final DisciplinaRepository repository;

    // LiveData para observacao pela View
    private final MutableLiveData<List<Disciplina>> disciplinas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Estado interno
    private long usuarioId;

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUTOR
    // ═══════════════════════════════════════════════════════════════════════

    public SubjectsViewModel(@NonNull Application application) {
        super(application);
        repository = new DisciplinaRepository(application);
    }

    // Para testes
    public SubjectsViewModel(@NonNull Application application, DisciplinaRepository repository) {
        super(application);
        this.repository = repository;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LIVEDATA GETTERS
    // ═══════════════════════════════════════════════════════════════════════

    public LiveData<List<Disciplina>> getDisciplinas() {
        return disciplinas;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ACOES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Define o ID do usuario logado
     */
    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Carrega a lista de disciplinas do usuario
     */
    public void carregarDisciplinas() {
        isLoading.setValue(true);

        repository.obterTodas(usuarioId, result -> {
            disciplinas.postValue(result != null ? result : new ArrayList<>());
            isLoading.postValue(false);
        });
    }

    /**
     * Deleta uma disciplina
     */
    public void deletarDisciplina(Disciplina disciplina) {
        repository.deletar(disciplina, rows -> {
            if (rows > 0) {
                carregarDisciplinas();
            } else {
                errorMessage.postValue("Erro ao deletar disciplina");
            }
        });
    }

    /**
     * Limpa mensagem de erro apos exibida
     */
    public void clearError() {
        errorMessage.setValue(null);
    }
}
