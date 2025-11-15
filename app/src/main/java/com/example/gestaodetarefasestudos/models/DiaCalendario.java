package com.example.gestaodetarefasestudos.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um dia no calendário
 * Contém o número do dia e as cores das disciplinas que têm tarefas neste dia
 */
public class DiaCalendario {
    private int numeroDia; // 1-31, ou 0 para dias vazios (de outros meses)
    private long timestamp; // timestamp do dia (00:00:00)
    private List<String> coresDisciplinas; // lista de cores hexadecimais das disciplinas
    private int quantidadeTarefas; // número total de tarefas neste dia
    private boolean diaAtual; // true se for o dia de hoje

    public DiaCalendario() {
        this.coresDisciplinas = new ArrayList<>();
        this.quantidadeTarefas = 0;
        this.diaAtual = false;
    }

    public DiaCalendario(int numeroDia, long timestamp) {
        this.numeroDia = numeroDia;
        this.timestamp = timestamp;
        this.coresDisciplinas = new ArrayList<>();
        this.quantidadeTarefas = 0;
        this.diaAtual = false;
    }

    /**
     * Adiciona uma cor de disciplina à lista
     * Limita a 3 cores para não poluir visualmente
     */
    public void adicionarCorDisciplina(String cor) {
        // Verifica duas condições antes de adicionar:
        // 1. size() < 3: ainda não atingiu o limite de 3 cores
        // 2. !contains(cor): cor ainda não está na lista (evita duplicatas)
        // Ambas precisam ser verdadeiras (operador &&)
        if (coresDisciplinas.size() < 3 && !coresDisciplinas.contains(cor)) {
            coresDisciplinas.add(cor);
        }
    }

    /**
     * Verifica se este dia tem tarefas
     */
    public boolean temTarefas() {
        return quantidadeTarefas > 0;
    }

    /**
     * Verifica se é um dia vazio (placeholder de outro mês)
     */
    public boolean isDiaVazio() {
        return numeroDia == 0;
    }

    // Getters e Setters
    public int getNumeroDia() {
        return numeroDia;
    }

    public void setNumeroDia(int numeroDia) {
        this.numeroDia = numeroDia;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getCoresDisciplinas() {
        return coresDisciplinas;
    }

    public void setCoresDisciplinas(List<String> coresDisciplinas) {
        this.coresDisciplinas = coresDisciplinas;
    }

    public int getQuantidadeTarefas() {
        return quantidadeTarefas;
    }

    public void setQuantidadeTarefas(int quantidadeTarefas) {
        this.quantidadeTarefas = quantidadeTarefas;
    }

    public boolean isDiaAtual() {
        return diaAtual;
    }

    public void setDiaAtual(boolean diaAtual) {
        this.diaAtual = diaAtual;
    }
}
