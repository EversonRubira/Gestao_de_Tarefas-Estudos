package com.example.gestaodetarefasestudos.repositories;

import android.app.Application;
import android.database.Cursor;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository para a tela Home (Dashboard).
 *
 * Agrega dados de multiplas fontes (Disciplinas, Tarefas, Sessoes de Estudo)
 * para fornecer uma visao unificada ao ViewModel.
 */
public class HomeRepository {

    private final DisciplinaRoomDAO disciplinaDAO;
    private final TarefaRoomDAO tarefaDAO;
    private final SessaoEstudoRoomDAO sessaoDAO;
    private final Executor executor;

    public HomeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        disciplinaDAO = db.disciplinaDAO();
        tarefaDAO = db.tarefaDAO();
        sessaoDAO = db.sessaoEstudoDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    // Para testes
    public HomeRepository(DisciplinaRoomDAO disciplinaDAO, TarefaRoomDAO tarefaDAO,
                          SessaoEstudoRoomDAO sessaoDAO, Executor executor) {
        this.disciplinaDAO = disciplinaDAO;
        this.tarefaDAO = tarefaDAO;
        this.sessaoDAO = sessaoDAO;
        this.executor = executor;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTATISTICAS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Obtem estatisticas do dashboard de forma assincrona
     */
    public void obterEstatisticas(long usuarioId, Callback<DashboardStats> callback) {
        executor.execute(() -> {
            int totalDisciplinas = disciplinaDAO.contarTotal(usuarioId);
            int tarefasPendentes = tarefaDAO.contarPendentes();

            DashboardStats stats = new DashboardStats(totalDisciplinas, tarefasPendentes);
            callback.onResult(stats);
        });
    }

    /**
     * Obtem tempo de estudo de hoje por disciplina
     */
    public void obterTempoEstudoHoje(long inicioDia, Callback<Cursor> callback) {
        executor.execute(() -> {
            Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina(inicioDia);
            callback.onResult(cursor);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CALENDARIO
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Obtem tarefas por periodo com cores das disciplinas (para calendario)
     */
    public void obterTarefasCalendario(long inicioMes, long fimMes, Callback<Cursor> callback) {
        executor.execute(() -> {
            Cursor cursor = tarefaDAO.obterTarefasPorPeriodoComCores(inicioMes, fimMes);
            callback.onResult(cursor);
        });
    }

    /**
     * Obtem tarefas de um dia especifico
     */
    public void obterTarefasDoDia(long inicioDia, long fimDia, Callback<List<Tarefa>> callback) {
        executor.execute(() -> {
            List<Tarefa> tarefas = tarefaDAO.obterTarefasPorDia(inicioDia, fimDia);
            callback.onResult(tarefas);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CLASSES AUXILIARES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Classe para encapsular estatisticas do dashboard
     */
    public static class DashboardStats {
        private final int totalDisciplinas;
        private final int tarefasPendentes;

        public DashboardStats(int totalDisciplinas, int tarefasPendentes) {
            this.totalDisciplinas = totalDisciplinas;
            this.tarefasPendentes = tarefasPendentes;
        }

        public int getTotalDisciplinas() {
            return totalDisciplinas;
        }

        public int getTarefasPendentes() {
            return tarefasPendentes;
        }
    }

    /**
     * Interface de callback para operacoes assincronas
     */
    public interface Callback<T> {
        void onResult(T result);
    }
}
