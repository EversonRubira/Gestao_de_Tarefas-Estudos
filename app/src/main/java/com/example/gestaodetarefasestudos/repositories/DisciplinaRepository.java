package com.example.gestaodetarefasestudos.repositories;

import android.app.Application;

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
            List<Disciplina> result = disciplinaDAO.obterTodas(usuarioId);
            callback.onResult(result);
        });
    }

    public void obterPorId(long id, Callback<Disciplina> callback) {
        executor.execute(() -> {
            Disciplina result = disciplinaDAO.obterPorId(id);
            callback.onResult(result);
        });
    }

    public void contarTotal(long usuarioId, Callback<Integer> callback) {
        executor.execute(() -> {
            int result = disciplinaDAO.contarTotal(usuarioId);
            callback.onResult(result);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESCRITA
    // ═══════════════════════════════════════════════════════════════════════

    public void inserir(Disciplina disciplina, Callback<Long> callback) {
        executor.execute(() -> {
            long id = disciplinaDAO.inserir(disciplina);
            callback.onResult(id);
        });
    }

    public void atualizar(Disciplina disciplina, Callback<Integer> callback) {
        executor.execute(() -> {
            int rows = disciplinaDAO.atualizar(disciplina);
            callback.onResult(rows);
        });
    }

    public void deletar(Disciplina disciplina, Callback<Integer> callback) {
        executor.execute(() -> {
            int rows = disciplinaDAO.deletar(disciplina);
            callback.onResult(rows);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CALLBACK INTERFACE
    // ═══════════════════════════════════════════════════════════════════════

    public interface Callback<T> {
        void onResult(T result);
    }
}
