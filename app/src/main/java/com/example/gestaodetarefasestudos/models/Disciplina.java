package com.example.gestaodetarefasestudos.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "disciplinas",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "usuario_id",
                onDelete = ForeignKey.CASCADE
        ))
public class Disciplina {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "usuario_id")
    private long usuarioId;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "codigo")
    private String codigo;

    @ColumnInfo(name = "cor")
    private String cor; // formato hexadecimal ex: #FF5722

    @ColumnInfo(name = "data_criacao")
    private long dataCriacao; // timestamp em milissegundos

    // Construtores
    public Disciplina() {
        this.dataCriacao = System.currentTimeMillis();
    }

    @Ignore
    public Disciplina(String nome, String codigo, String cor) {
        this.nome = nome;
        this.codigo = codigo;
        this.cor = cor;
        this.dataCriacao = System.currentTimeMillis();
        this.usuarioId = 0; // Será definido depois
    }

    @Ignore
    public Disciplina(long usuarioId, String nome, String codigo, String cor) {
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.codigo = codigo;
        this.cor = cor;
        this.dataCriacao = System.currentTimeMillis();
    }

    public Disciplina(long id, long usuarioId, String nome, String codigo, String cor, long dataCriacao) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.codigo = codigo;
        this.cor = cor;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return nome + " (" + codigo + ")";
    }
}
