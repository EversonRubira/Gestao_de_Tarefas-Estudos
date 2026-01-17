package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gestaodetarefasestudos.models.DiaCalendario;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.repositories.HomeRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel para a tela Home (Dashboard).
 *
 * Responsabilidades:
 * - Manter estatisticas do dashboard
 * - Gerenciar estado do calendario
 * - Calcular tempo de estudo por disciplina
 * - Sobreviver a mudancas de configuracao
 */
public class HomeViewModel extends AndroidViewModel {

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO
    // ═══════════════════════════════════════════════════════════════════════

    private final HomeRepository repository;

    // LiveData para estatisticas
    private final MutableLiveData<Integer> totalDisciplinas = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> tarefasPendentes = new MutableLiveData<>(0);
    private final MutableLiveData<Long> tempoEstudoTotal = new MutableLiveData<>(0L);
    private final MutableLiveData<List<TempoEstudoDisciplina>> temposPorDisciplina = new MutableLiveData<>(new ArrayList<>());

    // LiveData para calendario
    private final MutableLiveData<List<DiaCalendario>> diasCalendario = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> mesAnoAtual = new MutableLiveData<>("");
    private final MutableLiveData<List<Tarefa>> tarefasDoDia = new MutableLiveData<>(new ArrayList<>());

    // Estado interno
    private Calendar calendarioAtual;
    private long usuarioId;

    // ═══════════════════════════════════════════════════════════════════════
    // CONSTRUTOR
    // ═══════════════════════════════════════════════════════════════════════

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new HomeRepository(application);
        calendarioAtual = Calendar.getInstance();
    }

    // Para testes
    public HomeViewModel(@NonNull Application application, HomeRepository repository) {
        super(application);
        this.repository = repository;
        calendarioAtual = Calendar.getInstance();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LIVEDATA GETTERS
    // ═══════════════════════════════════════════════════════════════════════

    public LiveData<Integer> getTotalDisciplinas() {
        return totalDisciplinas;
    }

    public LiveData<Integer> getTarefasPendentes() {
        return tarefasPendentes;
    }

    public LiveData<Long> getTempoEstudoTotal() {
        return tempoEstudoTotal;
    }

    public LiveData<List<TempoEstudoDisciplina>> getTemposPorDisciplina() {
        return temposPorDisciplina;
    }

    public LiveData<List<DiaCalendario>> getDiasCalendario() {
        return diasCalendario;
    }

    public LiveData<String> getMesAnoAtual() {
        return mesAnoAtual;
    }

    public LiveData<List<Tarefa>> getTarefasDoDia() {
        return tarefasDoDia;
    }

    public Calendar getCalendarioAtual() {
        return calendarioAtual;
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
     * Carrega as estatisticas do dashboard
     */
    public void carregarEstatisticas() {
        repository.obterEstatisticas(usuarioId, stats -> {
            totalDisciplinas.postValue(stats.getTotalDisciplinas());
            tarefasPendentes.postValue(stats.getTarefasPendentes());
        });

        carregarTempoEstudoHoje();
    }

    /**
     * Carrega tempo de estudo de hoje por disciplina
     */
    private void carregarTempoEstudoHoje() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        repository.obterTempoEstudoHoje(inicioDia, cursor -> {
            List<TempoEstudoDisciplina> tempos = new ArrayList<>();
            long tempoTotal = 0;

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("d_nome"));
                    long segundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));

                    if (nomeDisciplina == null || nomeDisciplina.isEmpty()) {
                        nomeDisciplina = "Geral";
                    }

                    tempoTotal += segundos;
                    tempos.add(new TempoEstudoDisciplina(nomeDisciplina, segundos));
                }
                cursor.close();
            }

            tempoEstudoTotal.postValue(tempoTotal);
            temposPorDisciplina.postValue(tempos);
        });
    }

    /**
     * Carrega o calendario do mes atual
     */
    public void carregarCalendario() {
        List<DiaCalendario> dias = gerarDiasDoMes();
        carregarCoresTarefas(dias);
    }

    /**
     * Navega para o mes anterior
     */
    public void mesAnterior() {
        calendarioAtual.add(Calendar.MONTH, -1);
        carregarCalendario();
    }

    /**
     * Navega para o proximo mes
     */
    public void proximoMes() {
        calendarioAtual.add(Calendar.MONTH, 1);
        carregarCalendario();
    }

    /**
     * Carrega tarefas de um dia especifico
     */
    public void carregarTarefasDoDia(DiaCalendario dia) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dia.getTimestamp());
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

        repository.obterTarefasDoDia(inicioDia, fimDia, tarefas -> {
            tarefasDoDia.postValue(tarefas);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // METODOS PRIVADOS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Gera lista de dias do mes atual
     */
    private List<DiaCalendario> gerarDiasDoMes() {
        List<DiaCalendario> dias = new ArrayList<>();
        Calendar cal = (Calendar) calendarioAtual.clone();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int diaDaSemana = cal.get(Calendar.DAY_OF_WEEK);

        // Adicionar dias vazios no inicio
        for (int i = 1; i < diaDaSemana; i++) {
            dias.add(new DiaCalendario(0, 0));
        }

        int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar hoje = Calendar.getInstance();

        for (int dia = 1; dia <= diasNoMes; dia++) {
            cal.set(Calendar.DAY_OF_MONTH, dia);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            DiaCalendario diaCalendario = new DiaCalendario(dia, cal.getTimeInMillis());

            // Marcar se eh hoje
            if (cal.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == hoje.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == hoje.get(Calendar.DAY_OF_MONTH)) {
                diaCalendario.setDiaAtual(true);
            }

            dias.add(diaCalendario);
        }

        return dias;
    }

    /**
     * Carrega cores das tarefas para o calendario
     */
    private void carregarCoresTarefas(List<DiaCalendario> dias) {
        Calendar cal = (Calendar) calendarioAtual.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioMes = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long fimMes = cal.getTimeInMillis();

        repository.obterTarefasCalendario(inicioMes, fimMes, cursor -> {
            Map<String, List<String>> coresPorDia = new HashMap<>();
            Map<String, Integer> contagemPorDia = new HashMap<>();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long dataEntrega = cursor.getLong(cursor.getColumnIndexOrThrow("data_entrega"));
                    String cor = cursor.getString(cursor.getColumnIndexOrThrow("cor_disciplina"));
                    int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

                    Calendar calTemp = Calendar.getInstance();
                    calTemp.setTimeInMillis(dataEntrega);
                    calTemp.set(Calendar.HOUR_OF_DAY, 0);
                    calTemp.set(Calendar.MINUTE, 0);
                    calTemp.set(Calendar.SECOND, 0);
                    calTemp.set(Calendar.MILLISECOND, 0);

                    String chave = String.valueOf(calTemp.getTimeInMillis());

                    if (!coresPorDia.containsKey(chave)) {
                        coresPorDia.put(chave, new ArrayList<>());
                        contagemPorDia.put(chave, 0);
                    }

                    if (cor != null && !cor.isEmpty()) {
                        coresPorDia.get(chave).add(cor);
                    }

                    contagemPorDia.put(chave, contagemPorDia.get(chave) + count);

                } while (cursor.moveToNext());

                cursor.close();
            }

            // Aplicar cores aos dias
            for (DiaCalendario dia : dias) {
                if (!dia.isDiaVazio()) {
                    String chave = String.valueOf(dia.getTimestamp());
                    if (coresPorDia.containsKey(chave)) {
                        List<String> cores = coresPorDia.get(chave);
                        for (String cor : cores) {
                            dia.adicionarCorDisciplina(cor);
                        }
                        dia.setQuantidadeTarefas(contagemPorDia.get(chave));
                    }
                }
            }

            diasCalendario.postValue(dias);
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CLASSES AUXILIARES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Classe para representar tempo de estudo por disciplina
     */
    public static class TempoEstudoDisciplina {
        private final String nomeDisciplina;
        private final long segundos;

        public TempoEstudoDisciplina(String nomeDisciplina, long segundos) {
            this.nomeDisciplina = nomeDisciplina;
            this.segundos = segundos;
        }

        public String getNomeDisciplina() {
            return nomeDisciplina;
        }

        public long getSegundos() {
            return segundos;
        }
    }
}
