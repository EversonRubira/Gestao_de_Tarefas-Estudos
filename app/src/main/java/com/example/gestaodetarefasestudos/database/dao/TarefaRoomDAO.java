package com.example.gestaodetarefasestudos.database.dao;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaodetarefasestudos.models.Tarefa;

import java.util.List;

@Dao
public interface TarefaRoomDAO {

    @Insert
    long inserir(Tarefa tarefa);

    @Update
    int atualizar(Tarefa tarefa);

    @Delete
    int deletar(Tarefa tarefa);

    @Query("SELECT * FROM tarefas WHERE id = :id")
    Tarefa obterPorId(long id);

    @Query("SELECT t.*, d.nome as nome_disciplina FROM tarefas t " +
           "LEFT JOIN disciplinas d ON t.disciplina_id = d.id " +
           "ORDER BY t.data_entrega ASC")
    List<Tarefa> obterTodas();

    @Query("SELECT t.*, d.nome as nome_disciplina FROM tarefas t " +
           "LEFT JOIN disciplinas d ON t.disciplina_id = d.id " +
           "WHERE t.estado != 2 " +
           "ORDER BY t.data_entrega ASC")
    List<Tarefa> obterPendentes();

    @Query("SELECT t.*, d.nome as nome_disciplina FROM tarefas t " +
           "LEFT JOIN disciplinas d ON t.disciplina_id = d.id " +
           "WHERE t.disciplina_id = :disciplinaId " +
           "ORDER BY t.data_entrega ASC")
    List<Tarefa> listarPorDisciplina(long disciplinaId);

    @Query("UPDATE tarefas SET estado = :estado WHERE id = :id")
    int atualizarEstado(long id, int estado);

    @Query("DELETE FROM tarefas WHERE id = :id")
    int deletarPorId(long id);

    @Query("SELECT COUNT(*) FROM tarefas WHERE estado != 2")
    int contarPendentes();

    @Query("SELECT COUNT(*) FROM tarefas WHERE disciplina_id = :disciplinaId")
    int contarTarefasPorDisciplina(long disciplinaId);

    @Query("SELECT COUNT(*) FROM tarefas WHERE disciplina_id = :disciplinaId AND estado != 2")
    int contarTarefasPendentesPorDisciplina(long disciplinaId);

    @Query("SELECT COUNT(*) FROM tarefas WHERE disciplina_id = :disciplinaId AND estado = 2")
    int contarTarefasConcluidasPorDisciplina(long disciplinaId);

    @Query("SELECT t.*, d.nome as nome_disciplina FROM tarefas t " +
           "LEFT JOIN disciplinas d ON t.disciplina_id = d.id " +
           "WHERE t.data_entrega >= :inicioDia AND t.data_entrega <= :fimDia " +
           "ORDER BY t.data_entrega ASC")
    List<Tarefa> obterTarefasPorDia(long inicioDia, long fimDia);

    @Query("SELECT t.data_entrega as data_entrega, " +
           "t.disciplina_id as disciplina_id, " +
           "d.cor as cor_disciplina, " +
           "COUNT(*) as count " +
           "FROM tarefas t " +
           "LEFT JOIN disciplinas d ON t.disciplina_id = d.id " +
           "WHERE t.data_entrega >= :inicioMes AND t.data_entrega <= :fimMes AND t.estado != 2 " +
           "GROUP BY date(t.data_entrega / 1000, 'unixepoch'), t.disciplina_id " +
           "ORDER BY t.data_entrega ASC")
    Cursor obterTarefasPorPeriodoComCores(long inicioMes, long fimMes);
}
