package com.example.gestaodetarefasestudos.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaodetarefasestudos.models.Disciplina;

import java.util.List;

@Dao
public interface DisciplinaRoomDAO {

    @Insert
    long inserir(Disciplina disciplina);

    @Update
    int atualizar(Disciplina disciplina);

    @Delete
    int deletar(Disciplina disciplina);

    @Query("SELECT * FROM disciplinas WHERE id = :id")
    Disciplina obterPorId(long id);

    @Query("SELECT * FROM disciplinas WHERE usuario_id = :usuarioId ORDER BY nome ASC")
    List<Disciplina> obterTodas(long usuarioId);

    @Query("SELECT COUNT(*) FROM disciplinas WHERE usuario_id = :usuarioId")
    int contarTotal(long usuarioId);

    @Query("SELECT COUNT(*) FROM disciplinas WHERE codigo = :codigo AND id != :idExcluir AND usuario_id = :usuarioId")
    int verificarCodigoExiste(String codigo, long idExcluir, long usuarioId);

    @Query("DELETE FROM disciplinas WHERE id = :id")
    int deletarPorId(long id);
}
