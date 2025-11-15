package com.example.gestaodetarefasestudos.models;

import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;

public class Tarefa {
    private long id;
    private String titulo;
    private String descricao;
    private long disciplinaId;
    private String nomeDisciplina; // para exibição
    private long dataEntrega; // timestamp em milissegundos
    private Prioridade prioridade;
    private EstadoTarefa estado;
    private long dataCriacao; // timestamp em milissegundos

    // Construtores
    public Tarefa() {
        this.dataCriacao = System.currentTimeMillis();
        this.estado = EstadoTarefa.PENDENTE;
        this.prioridade = Prioridade.MEDIA;
    }

    public Tarefa(String titulo, String descricao, long disciplinaId, long dataEntrega,
                  Prioridade prioridade) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.disciplinaId = disciplinaId;
        this.dataEntrega = dataEntrega;
        this.prioridade = prioridade;
        this.estado = EstadoTarefa.PENDENTE;
        this.dataCriacao = System.currentTimeMillis();
    }

    public Tarefa(long id, String titulo, String descricao, long disciplinaId,
                  long dataEntrega, Prioridade prioridade, EstadoTarefa estado,
                  long dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.disciplinaId = disciplinaId;
        this.dataEntrega = dataEntrega;
        this.prioridade = prioridade;
        this.estado = estado;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public long getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(long dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public EstadoTarefa getEstado() {
        return estado;
    }

    public void setEstado(EstadoTarefa estado) {
        this.estado = estado;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean estaPendente() {
        return estado == EstadoTarefa.PENDENTE;
    }

    public boolean estaConcluida() {
        return estado == EstadoTarefa.CONCLUIDA;
    }
}
