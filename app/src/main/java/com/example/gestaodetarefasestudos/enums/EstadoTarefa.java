package com.example.gestaodetarefasestudos.enums;

public enum EstadoTarefa {
    PENDENTE("Pendente", 0),
    EM_PROGRESSO("Em Progresso", 1),
    CONCLUIDA("Concluída", 2);

    private final String nome;
    private final int valor;

    EstadoTarefa(String nome, int valor) {
        this.nome = nome;
        this.valor = valor;
    }

    public String getNome() {
        return nome;
    }

    public int getValor() {
        return valor;
    }

    public static EstadoTarefa fromValor(int valor) {
        for (EstadoTarefa estado : values()) {
            if (estado.valor == valor) {
                return estado;
            }
        }
        return PENDENTE; // valor padrão
    }
}
