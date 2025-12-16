package com.example.gestaodetarefasestudos.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gestaodetarefasestudos.models.Usuario;

@Dao
public interface UsuarioRoomDAO {

    @Insert
    long inserir(Usuario usuario);

    @Update
    int atualizar(Usuario usuario);

    @Delete
    int deletar(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario obterPorId(long id);

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    Usuario obterPorEmail(String email);

    @Query("SELECT COUNT(*) FROM usuarios")
    int contarTotal();

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    int verificarEmailExiste(String email);
}
