package com.example.gestaodetarefasestudos.database.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.gestaodetarefasestudos.models.SessaoEstudo;

import java.util.List;

@Dao
public interface SessaoEstudoRoomDAO {

    @Insert
    long inserir(SessaoEstudo sessao);

    @Delete
    int deletar(SessaoEstudo sessao);

    @Query("SELECT * FROM sessoes_estudo WHERE id = :id")
    SessaoEstudo obterPorId(long id);

    @Query("SELECT s.*, d.nome as nome_disciplina FROM sessoes_estudo s " +
           "LEFT JOIN disciplinas d ON s.disciplina_id = d.id " +
           "ORDER BY s.data DESC")
    List<SessaoEstudo> obterTodas();

    @Query("SELECT SUM(duracao) FROM sessoes_estudo WHERE data >= :inicioDia")
    long obterTempoEstudoHoje(long inicioDia);

    @Query("SELECT s.disciplina_id, d.nome as d_nome, SUM(s.duracao) as total_segundos " +
           "FROM sessoes_estudo s " +
           "LEFT JOIN disciplinas d ON s.disciplina_id = d.id " +
           "WHERE s.data >= :inicioDia " +
           "GROUP BY s.disciplina_id")
    Cursor obterTempoHojePorDisciplina(long inicioDia);

    @Query("SELECT SUM(duracao) FROM sessoes_estudo WHERE disciplina_id = :disciplinaId")
    long obterTempoEstudoDisciplina(long disciplinaId);

    @Query("SELECT s.disciplina_id, d.nome as nome_disciplina, d.cor as cor, " +
           "SUM(s.duracao) / 60 as total_minutos " +
           "FROM sessoes_estudo s " +
           "LEFT JOIN disciplinas d ON s.disciplina_id = d.id " +
           "WHERE s.data >= :inicio7Dias " +
           "GROUP BY s.disciplina_id " +
           "ORDER BY total_minutos DESC")
    Cursor obterTempoUltimos7DiasPorDisciplina(long inicio7Dias);

    @Query("SELECT date(data/1000, 'unixepoch', 'localtime') as dia, " +
           "SUM(duracao) / 60 as total_minutos " +
           "FROM sessoes_estudo " +
           "WHERE data >= :inicio7Dias " +
           "GROUP BY dia " +
           "ORDER BY dia ASC")
    Cursor obterEvolucaoUltimos7Dias(long inicio7Dias);

    @Query("DELETE FROM sessoes_estudo WHERE id = :id")
    int deletarPorId(long id);

    // Metodos para estatisticas do Timer

    @Query("SELECT COUNT(*) FROM sessoes_estudo s " +
           "INNER JOIN disciplinas d ON s.disciplina_id = d.id " +
           "WHERE d.usuario_id = :usuarioId AND s.data >= :inicioDoDia")
    int contarSessoesHoje(long usuarioId, long inicioDoDia);

    @Query("SELECT COALESCE(SUM(s.duracao), 0) FROM sessoes_estudo s " +
           "INNER JOIN disciplinas d ON s.disciplina_id = d.id " +
           "WHERE d.usuario_id = :usuarioId AND s.data >= :inicioDoDia")
    long tempoTotalHoje(long usuarioId, long inicioDoDia);

    @Query("SELECT COUNT(*) FROM sessoes_estudo s " +
           "INNER JOIN disciplinas d ON s.disciplina_id = d.id " +
           "WHERE d.usuario_id = :usuarioId AND s.data >= :inicioPeriodo AND s.data < :fimPeriodo")
    int contarSessoesPeriodo(long usuarioId, long inicioPeriodo, long fimPeriodo);
}
