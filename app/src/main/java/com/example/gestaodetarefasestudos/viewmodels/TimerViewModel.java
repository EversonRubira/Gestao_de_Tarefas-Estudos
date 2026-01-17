package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.example.gestaodetarefasestudos.repositories.TimerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel para o Timer Pomodoro.
 *
 * Responsabilidades:
 * - Gerenciar estado do timer (tempo, ciclos, modo trabalho/descanso)
 * - Manter estatisticas do dia
 * - Controlar disciplinas disponiveis
 * - Sobreviver a mudancas de configuracao
 *
 * O timer em si (CountDownTimer) roda aqui para sobreviver
 * a rotacoes de tela e troca de abas.
 */
public class TimerViewModel extends AndroidViewModel {

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTANTES POMODORO
    // ═══════════════════════════════════════════════════════════════════════

    public static final long DURACAO_TRABALHO_MS = 25 * 60 * 1000L;
    public static final long DURACAO_DESCANSO_MS = 5 * 60 * 1000L;
    public static final long DURACAO_DESCANSO_LONGO_MS = 15 * 60 * 1000L;
    public static final int CICLOS_ATE_DESCANSO_LONGO = 4;

    // ═══════════════════════════════════════════════════════════════════════
    // ENUMS
    // ═══════════════════════════════════════════════════════════════════════

    public enum TimerState {
        PARADO,     // Timer resetado
        RODANDO,    // Timer contando
        PAUSADO     // Timer pausado
    }

    public enum SessionType {
        TRABALHO,
        DESCANSO,
        DESCANSO_LONGO
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO
    // ═══════════════════════════════════════════════════════════════════════

    private final TimerRepository repository;

    // LiveData para UI
    private final MutableLiveData<Long> tempoRestante = new MutableLiveData<>(DURACAO_TRABALHO_MS);
    private final MutableLiveData<Integer> progresso = new MutableLiveData<>(100);
    private final MutableLiveData<TimerState> timerState = new MutableLiveData<>(TimerState.PARADO);
    private final MutableLiveData<SessionType> sessionType = new MutableLiveData<>(SessionType.TRABALHO);
    private final MutableLiveData<Integer> contadorCiclos = new MutableLiveData<>(0);
    private final MutableLiveData<String> tempoFormatado = new MutableLiveData<>("25:00");

    // LiveData para disciplinas
    private final MutableLiveData<List<Disciplina>> disciplinas = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Disciplina> disciplinaSelecionada = new MutableLiveData<>();

    // LiveData para estatisticas
    private final MutableLiveData<Integer> sessoesHoje = new MutableLiveData<>(0);
    private final MutableLiveData<String> tempoHoje = new MutableLiveData<>("0min");
    private final MutableLiveData<Integer> streak = new MutableLiveData<>(0);

    // LiveData para eventos
    private final MutableLiveData<String> mensagemEvento = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sessaoSalva = new MutableLiveData<>(false);

    // Estado interno
    private CountDownTimer cronometro;
    private long tempoInicioSessao;
    private long usuarioId;

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUTOR
    // ═══════════════════════════════════════════════════════════════════════

    public TimerViewModel(@NonNull Application application) {
        super(application);
        repository = new TimerRepository(application);
    }

    // Para testes
    public TimerViewModel(@NonNull Application application, TimerRepository repository) {
        super(application);
        this.repository = repository;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LIVEDATA GETTERS
    // ═══════════════════════════════════════════════════════════════════════

    public LiveData<Long> getTempoRestante() {
        return tempoRestante;
    }

    public LiveData<Integer> getProgresso() {
        return progresso;
    }

    public LiveData<TimerState> getTimerState() {
        return timerState;
    }

    public LiveData<SessionType> getSessionType() {
        return sessionType;
    }

    public LiveData<Integer> getContadorCiclos() {
        return contadorCiclos;
    }

    public LiveData<String> getTempoFormatado() {
        return tempoFormatado;
    }

    public LiveData<List<Disciplina>> getDisciplinas() {
        return disciplinas;
    }

    public LiveData<Disciplina> getDisciplinaSelecionada() {
        return disciplinaSelecionada;
    }

    public LiveData<Integer> getSessoesHoje() {
        return sessoesHoje;
    }

    public LiveData<String> getTempoHoje() {
        return tempoHoje;
    }

    public LiveData<Integer> getStreak() {
        return streak;
    }

    public LiveData<String> getMensagemEvento() {
        return mensagemEvento;
    }

    public LiveData<Boolean> getSessaoSalva() {
        return sessaoSalva;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ACOES PUBLICAS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Define o ID do usuario logado
     */
    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Carrega disciplinas disponiveis
     */
    public void carregarDisciplinas() {
        repository.obterDisciplinas(usuarioId, result -> {
            disciplinas.postValue(result != null ? result : new ArrayList<>());
        });
    }

    /**
     * Carrega estatisticas do dia
     */
    public void carregarEstatisticas() {
        repository.obterEstatisticasDoDia(usuarioId, stats -> {
            sessoesHoje.postValue(stats.getSessoesHoje());
            tempoHoje.postValue(formatarTempoCompacto(stats.getTempoHojeSegundos()));
            streak.postValue(stats.getStreak());
        });
    }

    /**
     * Define disciplina selecionada
     */
    public void selecionarDisciplina(Disciplina disciplina) {
        disciplinaSelecionada.setValue(disciplina);
    }

    /**
     * Inicia ou retoma o timer
     */
    public void iniciar() {
        TimerState estado = timerState.getValue();

        if (estado == TimerState.RODANDO) {
            pausar();
            return;
        }

        // Validar disciplina em sessao de trabalho
        if (sessionType.getValue() == SessionType.TRABALHO && disciplinaSelecionada.getValue() == null) {
            mensagemEvento.setValue("ERRO_SEM_DISCIPLINA");
            return;
        }

        // Marcar inicio da sessao de trabalho
        if (sessionType.getValue() == SessionType.TRABALHO &&
            tempoRestante.getValue() == DURACAO_TRABALHO_MS) {
            tempoInicioSessao = System.currentTimeMillis();
        }

        iniciarCronometro();
        timerState.setValue(TimerState.RODANDO);
    }

    /**
     * Pausa o timer
     */
    public void pausar() {
        if (cronometro != null) {
            cronometro.cancel();
        }
        timerState.setValue(TimerState.PAUSADO);
    }

    /**
     * Para e reseta o timer
     */
    public void parar() {
        if (cronometro != null) {
            cronometro.cancel();
        }

        // Salvar tempo parcial se estava em sessao de trabalho
        if (sessionType.getValue() == SessionType.TRABALHO &&
            tempoInicioSessao > 0 &&
            disciplinaSelecionada.getValue() != null) {

            long tempoFim = System.currentTimeMillis();
            long duracaoEstudada = (tempoFim - tempoInicioSessao) / 1000;

            if (duracaoEstudada >= 10) {
                salvarSessao(duracaoEstudada);
            }
        }

        // Resetar estado
        timerState.setValue(TimerState.PARADO);
        sessionType.setValue(SessionType.TRABALHO);
        tempoRestante.setValue(DURACAO_TRABALHO_MS);
        progresso.setValue(100);
        contadorCiclos.setValue(0);
        tempoInicioSessao = 0;
        atualizarTempoFormatado(DURACAO_TRABALHO_MS);
    }

    /**
     * Limpa mensagem de evento apos exibida
     */
    public void clearMensagemEvento() {
        mensagemEvento.setValue(null);
    }

    /**
     * Limpa flag de sessao salva
     */
    public void clearSessaoSalva() {
        sessaoSalva.setValue(false);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODOS PRIVADOS
    // ═══════════════════════════════════════════════════════════════════════

    private void iniciarCronometro() {
        Long tempo = tempoRestante.getValue();
        if (tempo == null) tempo = DURACAO_TRABALHO_MS;

        long duracaoTotal = obterDuracaoTotal();

        cronometro = new CountDownTimer(tempo, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempoRestante.postValue(millisUntilFinished);
                atualizarTempoFormatado(millisUntilFinished);

                int prog = (int) ((millisUntilFinished * 100) / duracaoTotal);
                progresso.postValue(prog);
            }

            @Override
            public void onFinish() {
                tempoRestante.postValue(0L);
                progresso.postValue(0);
                timerState.postValue(TimerState.PARADO);

                processarFimSessao();
            }
        }.start();
    }

    private void processarFimSessao() {
        SessionType tipoAtual = sessionType.getValue();

        if (tipoAtual == SessionType.TRABALHO) {
            // Salvar sessao de trabalho
            salvarSessaoTrabalho();

            Integer ciclos = contadorCiclos.getValue();
            if (ciclos == null) ciclos = 0;
            ciclos++;
            contadorCiclos.postValue(ciclos);

            if (ciclos >= CICLOS_ATE_DESCANSO_LONGO) {
                // Descanso longo
                sessionType.postValue(SessionType.DESCANSO_LONGO);
                tempoRestante.postValue(DURACAO_DESCANSO_LONGO_MS);
                atualizarTempoFormatado(DURACAO_DESCANSO_LONGO_MS);
                contadorCiclos.postValue(0);
                mensagemEvento.postValue("DESCANSO_LONGO_INICIO");
            } else {
                // Descanso curto
                sessionType.postValue(SessionType.DESCANSO);
                tempoRestante.postValue(DURACAO_DESCANSO_MS);
                atualizarTempoFormatado(DURACAO_DESCANSO_MS);
                mensagemEvento.postValue("TRABALHO_COMPLETO");
            }
        } else {
            // Descanso completo
            String msg = tipoAtual == SessionType.DESCANSO_LONGO ?
                    "DESCANSO_LONGO_COMPLETO" : "DESCANSO_COMPLETO";

            sessionType.postValue(SessionType.TRABALHO);
            tempoRestante.postValue(DURACAO_TRABALHO_MS);
            atualizarTempoFormatado(DURACAO_TRABALHO_MS);
            mensagemEvento.postValue(msg);
        }

        progresso.postValue(100);
    }

    private void salvarSessaoTrabalho() {
        long tempoFim = System.currentTimeMillis();
        long duracaoReal = (tempoFim - tempoInicioSessao) / 1000;

        salvarSessao(duracaoReal);

        // Resetar tempo de inicio para proxima sessao
        tempoInicioSessao = System.currentTimeMillis();
    }

    private void salvarSessao(long duracaoSegundos) {
        Disciplina disciplina = disciplinaSelecionada.getValue();
        long disciplinaId = (disciplina != null) ? disciplina.getId() : 0;

        SessaoEstudo sessao = new SessaoEstudo(disciplinaId, duracaoSegundos);

        repository.salvarSessao(sessao, id -> {
            if (id > 0) {
                sessaoSalva.postValue(true);
                carregarEstatisticas();
            }
        });
    }

    private long obterDuracaoTotal() {
        SessionType tipo = sessionType.getValue();
        if (tipo == SessionType.DESCANSO_LONGO) {
            return DURACAO_DESCANSO_LONGO_MS;
        } else if (tipo == SessionType.DESCANSO) {
            return DURACAO_DESCANSO_MS;
        }
        return DURACAO_TRABALHO_MS;
    }

    private void atualizarTempoFormatado(long millis) {
        int minutos = (int) (millis / 1000) / 60;
        int segundos = (int) (millis / 1000) % 60;
        String formatado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
        tempoFormatado.postValue(formatado);
    }

    private String formatarTempoCompacto(long segundos) {
        if (segundos < 60) {
            return "0min";
        }

        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        if (horas > 0) {
            return String.format(Locale.getDefault(), "%dh%02d", horas, minutos);
        } else {
            return String.format(Locale.getDefault(), "%dmin", minutos);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (cronometro != null) {
            cronometro.cancel();
        }
    }
}
