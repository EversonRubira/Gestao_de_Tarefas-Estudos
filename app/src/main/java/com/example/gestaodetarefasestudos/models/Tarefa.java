package com.example.gestaodetarefasestudos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;

@Entity(tableName = "tarefas",
        foreignKeys = @ForeignKey(
                entity = Disciplina.class,
                parentColumns = "id",
                childColumns = "disciplina_id",
                onDelete = ForeignKey.CASCADE
        ))
public class Tarefa {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "descricao")
    private String descricao;

    @ColumnInfo(name = "disciplina_id")
    private long disciplinaId;

    @Ignore
    private String nomeDisciplina; // para exibição - não persiste no banco

    @ColumnInfo(name = "data_entrega")
    private long dataEntrega; // timestamp em milissegundos

    @ColumnInfo(name = "prioridade")
    private Prioridade prioridade;

    @ColumnInfo(name = "estado")
    private EstadoTarefa estado;

    @ColumnInfo(name = "data_criacao")
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
