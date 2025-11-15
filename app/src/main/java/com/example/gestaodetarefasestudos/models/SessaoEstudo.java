package com.example.gestaodetarefasestudos.models;

public class SessaoEstudo {
    private long id;
    private long disciplinaId;
    private String nomeDisciplina; // para exibição
    private long duracao; // duração em segundos
    private long data; // timestamp em milissegundos

    // Construtores
    public SessaoEstudo() {
        this.data = System.currentTimeMillis();
    }

    public SessaoEstudo(long disciplinaId, long duracao) {
        this.disciplinaId = disciplinaId;
        this.duracao = duracao;
        this.data = System.currentTimeMillis();
    }

    public SessaoEstudo(long id, long disciplinaId, long duracao, long data) {
        this.id = id;
        this.disciplinaId = disciplinaId;
        this.duracao = duracao;
        this.data = data;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(long disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    public long getDuracao() {
        return duracao;
    }

    public void setDuracao(long duracao) {
        this.duracao = duracao;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    // Métodos auxiliares
    public String getDuracaoFormatada() {
        long horas = duracao / 3600;
        long minutos = (duracao % 3600) / 60;
        long segundos = duracao % 60;

        if (horas > 0) {
            return String.format("%dh %02dmin", horas, minutos);
        } else {
            return String.format("%dmin %02ds", minutos, segundos);
        }
    }
}
