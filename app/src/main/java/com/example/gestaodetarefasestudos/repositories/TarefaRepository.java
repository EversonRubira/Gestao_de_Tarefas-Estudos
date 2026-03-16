package com.example.gestaodetarefasestudos.repositories;

import android.app.Application;
import android.util.Log;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository para acesso aos dados de Tarefa.
 *
 * O Repository abstrai a fonte de dados (Room, API, cache) do ViewModel.
 * Isso permite:
 * - Trocar a implementacao sem afetar o ViewModel
 * - Adicionar cache facilmente
 * - Combinar multiplas fontes de dados
 * - Testar o ViewModel com mocks
 */
public class TarefaRepository {

    private static final String TAG = "TarefaRepository";

    private final TarefaRoomDAO tarefaDAO;
    private final Executor executor;

    public TarefaRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        tarefaDAO = db.tarefaDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    // Para testes - permite injetar DAO mockado
    public TarefaRepository(TarefaRoomDAO tarefaDAO, Executor executor) {
        this.tarefaDAO = tarefaDAO;
        this.executor = executor;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LEITURA (podem ser chamados da main thread via LiveData no futuro)
    // ═══════════════════════════════════════════════════════════════════════

    public void obterTodas(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.obterTodas();
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter todas as tarefas", e);
                callback.onError(e);
            }
        });
    }

    public void obterPorId(long id, Callback<Tarefa> callback) {
        executor.execute(() -> {
            try {
                Tarefa result = tarefaDAO.obterPorId(id);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefa por id", e);
                callback.onError(e);
            }
        });
    }

    public void obterPendentes(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.obterPendentes();
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas pendentes", e);
                callback.onError(e);
            }
        });
    }

    public void obterConcluidas(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.obterConcluidas();
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas concluidas", e);
                callback.onError(e);
            }
        });
    }

    public void obterAtrasadas(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.obterAtrasadas(System.currentTimeMillis());
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas atrasadas", e);
                callback.onError(e);
            }
        });
    }

    public void obterTarefasHoje(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long inicioDia = cal.getTimeInMillis();

                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                long fimDia = cal.getTimeInMillis();

                List<Tarefa> result = tarefaDAO.obterTarefasHoje(inicioDia, fimDia);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas de hoje", e);
                callback.onError(e);
            }
        });
    }

    public void obterTarefasSemana(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                long inicioSemana = cal.getTimeInMillis();

                cal.add(Calendar.DAY_OF_WEEK, 6);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                long fimSemana = cal.getTimeInMillis();

                List<Tarefa> result = tarefaDAO.obterTarefasSemana(inicioSemana, fimSemana);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas da semana", e);
                callback.onError(e);
            }
        });
    }

    public void obterPorPrioridade(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.obterTodasPorPrioridade();
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas por prioridade", e);
                callback.onError(e);
            }
        });
    }

    public void obterPorDisciplina(Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.obterTodasPorDisciplina();
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao obter tarefas por disciplina", e);
                callback.onError(e);
            }
        });
    }

    public void listarPorDisciplina(long disciplinaId, Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            try {
                List<Tarefa> result = tarefaDAO.listarPorDisciplina(disciplinaId);
                callback.onResult(result);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao listar tarefas por disciplina", e);
                callback.onError(e);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESCRITA
    // ═══════════════════════════════════════════════════════════════════════

    public void inserir(Tarefa tarefa, Callback<Long> callback) {
        executor.execute(() -> {
            try {
                long id = tarefaDAO.inserir(tarefa);
                callback.onResult(id);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao inserir tarefa", e);
                callback.onError(e);
            }
        });
    }

    public void atualizar(Tarefa tarefa, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rows = tarefaDAO.atualizar(tarefa);
                callback.onResult(rows);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar tarefa", e);
                callback.onError(e);
            }
        });
    }

    public void deletar(Tarefa tarefa, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rows = tarefaDAO.deletar(tarefa);
                callback.onResult(rows);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao deletar tarefa", e);
                callback.onError(e);
            }
        });
    }

    public void atualizarEstado(long id, int estado, Callback<Integer> callback) {
        executor.execute(() -> {
            try {
                int rows = tarefaDAO.atualizarEstado(id, estado);
                callback.onResult(rows);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar estado da tarefa", e);
                callback.onError(e);
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CALLBACK INTERFACE
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Interface de callback para operacoes assincronas.
     * No futuro, pode ser substituida por LiveData ou Flow.
     */
    public interface Callback<T> {
        void onResult(T result);
        default void onError(Exception e) {
            Log.e("TarefaRepository.Callback", "Erro nao tratado", e);
        }
    }
}
