package com.example.gestaodetarefasestudos.database;

import androidx.room.TypeConverter;

import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;

public class Converters {

    @TypeConverter
    public static int fromPrioridade(Prioridade prioridade) {
        return prioridade == null ? Prioridade.MEDIA.getValor() : prioridade.getValor();
    }

    @TypeConverter
    public static Prioridade toPrioridade(int valor) {
        return Prioridade.fromValor(valor);
    }

    @TypeConverter
    public static int fromEstadoTarefa(EstadoTarefa estado) {
        return estado == null ? EstadoTarefa.PENDENTE.getValor() : estado.getValor();
    }

    @TypeConverter
    public static EstadoTarefa toEstadoTarefa(int valor) {
        return EstadoTarefa.fromValor(valor);
    }
}
