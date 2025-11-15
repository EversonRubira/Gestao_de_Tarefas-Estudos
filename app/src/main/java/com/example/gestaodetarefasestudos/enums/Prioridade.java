package com.example.gestaodetarefasestudos.enums;

public enum Prioridade {
    BAIXA("Baixa", 1),
    MEDIA("Média", 2),
    ALTA("Alta", 3);

    private final String nome;
    private final int valor;

    Prioridade(String nome, int valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public int getValor() {
        return valor;
    }

    public static Prioridade fromValor(int valor) {
        for (Prioridade prioridade : values()) {
            if (prioridade.valor == valor) {
                return prioridade;
            }
        }
        return MEDIA; // valor padrão
    }
}
