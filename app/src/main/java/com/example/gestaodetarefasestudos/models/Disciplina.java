package com.example.gestaodetarefasestudos.models;

public class Disciplina {
    private long id;
    private String nome;
    private String codigo;
    private String cor; // formato hexadecimal ex: #FF5722
    private long dataCriacao; // timestamp em milissegundos

    // Construtores
    public Disciplina() {
        this.dataCriacao = System.currentTimeMillis();
    }

    public Disciplina(String nome, String codigo, String cor) {
        this.nome = nome;
        this.codigo = codigo;
        this.cor = cor;
        this.dataCriacao = System.currentTimeMillis();
    }

    public Disciplina(long id, String nome, String codigo, String cor, long dataCriacao) {
        this.id = id;
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
