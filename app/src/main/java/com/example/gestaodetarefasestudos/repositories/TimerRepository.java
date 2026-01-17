package com.example.gestaodetarefasestudos.repositories;

import android.app.Application;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository para o Timer Pomodoro.
 *
 * Gerencia acesso a:
 * - Sessoes de estudo (salvar, estatisticas)
 * - Disciplinas (para selecao no timer)
 */
public class TimerRepository {

    private final SessaoEstudoRoomDAO sessaoDAO;
    private final DisciplinaRoomDAO disciplinaDAO;
    private final Executor executor;

    public TimerRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        sessaoDAO = db.sessaoEstudoDAO();
        disciplinaDAO = db.disciplinaDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    // Para testes
    public TimerRepository(SessaoEstudoRoomDAO sessaoDAO, DisciplinaRoomDAO disciplinaDAO, Executor executor) {
        this.sessaoDAO = sessaoDAO;
        this.disciplinaDAO = disciplinaDAO;
        this.executor = executor;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DISCIPLINAS
    // ═══════════════════════════════════════════════════════════════════════

    public void obterDisciplinas(long usuarioId, Callback<List<Disciplina>> callback) {
        executor.execute(() -> {
            List<Disciplina> result = disciplinaDAO.obterTodas(usuarioId);
            callback.onResult(result);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SESSOES DE ESTUDO
    // ═══════════════════════════════════════════════════════════════════════

    public void salvarSessao(SessaoEstudo sessao, Callback<Long> callback) {
        executor.execute(() -> {
            long id = sessaoDAO.inserir(sessao);
            callback.onResult(id);
        });
    }

    /**
     * Obtem estatisticas do dia atual
     */
    public void obterEstatisticasDoDia(long usuarioId, Callback<EstatisticasDia> callback) {
        executor.execute(() -> {
            // Calcular timestamp do inicio do dia
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long inicioDoDia = cal.getTimeInMillis();

            int sessoesHoje = sessaoDAO.contarSessoesHoje(usuarioId, inicioDoDia);
            long tempoHojeSegundos = sessaoDAO.tempoTotalHoje(usuarioId, inicioDoDia);
            int streak = calcularStreak(usuarioId);

            EstatisticasDia stats = new EstatisticasDia(sessoesHoje, tempoHojeSegundos, streak);
            callback.onResult(stats);
        });
    }

    /**
     * Calcula o streak de dias consecutivos estudando
     */
    private int calcularStreak(long usuarioId) {
        int streak = 0;
        Calendar cal = Calendar.getInstance();

        // Verificar se estudou hoje primeiro
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // Verificar ultimos 365 dias no maximo
        for (int i = 0; i < 365; i++) {
            long inicioDia = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            long fimDia = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, -1); // Voltar

            int sessoesDia = sessaoDAO.contarSessoesPeriodo(usuarioId, inicioDia, fimDia);

            if (sessoesDia > 0) {
                streak++;
                cal.add(Calendar.DAY_OF_YEAR, -1); // Ir para dia anterior
            } else if (i == 0) {
                // Ainda nao estudou hoje, verificar se estudou ontem para manter streak
                cal.add(Calendar.DAY_OF_YEAR, -1);
            } else {
                break;
            }
        }

        return streak;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CLASSES AUXILIARES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Classe para encapsular estatisticas do dia
     */
    public static class EstatisticasDia {
        private final int sessoesHoje;
        private final long tempoHojeSegundos;
        private final int streak;

        public EstatisticasDia(int sessoesHoje, long tempoHojeSegundos, int streak) {
            this.sessoesHoje = sessoesHoje;
            this.tempoHojeSegundos = tempoHojeSegundos;
            this.streak = streak;
        }

        public int getSessoesHoje() {
            return sessoesHoje;
        }

        public long getTempoHojeSegundos() {
            return tempoHojeSegundos;
        }

        public int getStreak() {
            return streak;
        }
    }

    /**
     * Interface de callback para operacoes assincronas
     */
    public interface Callback<T> {
        void onResult(T result);
    }
}
