package com.example.gestaodetarefasestudos.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.UsuarioRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.models.Usuario;

@Database(
        entities = {Usuario.class, Disciplina.class, Tarefa.class, SessaoEstudo.class},
        version = 3,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UsuarioRoomDAO usuarioDAO();
    public abstract DisciplinaRoomDAO disciplinaDAO();
    public abstract TarefaRoomDAO tarefaDAO();
    public abstract SessaoEstudoRoomDAO sessaoEstudoDAO();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "gestao_tarefas_estudos.db"
            )
            .fallbackToDestructiveMigration() // Apenas durante desenvolvimento
            .build();
        }
        return instance;
    }
}
