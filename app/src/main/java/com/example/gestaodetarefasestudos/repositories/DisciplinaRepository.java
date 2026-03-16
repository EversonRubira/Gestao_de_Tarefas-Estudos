package com.example.gestaodetarefasestudos.repositories;

import android.app.Application;
import android.util.Log;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository para acesso aos dados de Disciplina.
 *
 * Abstrai a fonte de dados do ViewModel, permitindo
 * trocar implementacao ou adicionar cache sem afetar
 * o restante do codigo.
 */
public class DisciplinaRepository {

    private static final String TAG = "DisciplinaRepository";

    private final DisciplinaRoomDAO disciplinaDAO;
    private final Executor executor;

    public DisciplinaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        disciplinaDAO = db.disciplinaDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    // Para testes
    public DisciplinaRepository(DisciplinaRoomDAO disciplinaDAO, Executor executor) {
        this.disciplinaDAO = disciplinaDAO;
        this.executor = executor;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LEITURA
    // ═══════════════════════════════════════════════════════════════════════

    public void obterTodas(long usuarioId, Callback<List<Disciplina>> callback) {
        executor.execute(() -> {
            try {
                List<Disciplina> result = disciplinaDAO.obterTodas(usuarioId);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter disciplinas", e);
                callback.onError(e);
            }
        });
    }

    public void obterPorId(long id, Callback<Disciplina> callback) {
        executor.execute(() -> {
            try {
                Disciplina result = disciplinaDAO.obterPorId(id);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter disciplina por id", e);
                callback.onError(e);
            }
        });
    }

    public void contarTotal(long usuarioId, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                int result = disciplinaDAO.contarTotal(usuarioId);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao contar disciplinas", e);
                callback.onError(e);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESCRITA
    // ═══════════════════════════════════════════════════════════════════════

    public void inserir(Disciplina disciplina, Callback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = disciplinaDAO.inserir(disciplina);
                callback.onResult(id);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao inserir disciplina", e);
                callback.onError(e);
            }
        });
    }

    public void atualizar(Disciplina disciplina, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rows = disciplinaDAO.atualizar(disciplina);
                callback.onResult(rows);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar disciplina", e);
                callback.onError(e);
            }
        });
    }

    public void deletar(Disciplina disciplina, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rows = disciplinaDAO.deletar(disciplina);
                callback.onResult(rows);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao deletar disciplina", e);
                callback.onError(e);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CALLBACK INTERFACE
    // ═══════════════════════════════════════════════════════════════════════

    public interface Callback<T> {
        void onResult(T result);
        default void onError(Exception e) {
            Log.e("DisciplinaRepository.Callback", "Erro nao tratado", e);
        }
    }
}
