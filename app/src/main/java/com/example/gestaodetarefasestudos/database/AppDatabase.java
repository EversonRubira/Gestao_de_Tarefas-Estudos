package com.example.gestaodetarefasestudos.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.UsuarioRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.models.Usuario;

/**
 * Banco de dados Room da aplicacao.
 *
 * IMPORTANTE: Ao fazer alteracoes no schema (adicionar/remover colunas ou tabelas),
 * voce DEVE:
 * 1. Incrementar a versao do banco (version = X)
 * 2. Criar uma nova Migration (MIGRATION_X_Y)
 * 3. Adicionar a migration ao builder (.addMigrations(...))
 *
 * NUNCA use fallbackToDestructiveMigration() em producao - isso apaga todos os dados!
 */
@Database(
        entities = {Usuario.class, Disciplina.class, Tarefa.class, SessaoEstudo.class},
        version = 4,
        exportSchema = true
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = "AppDatabase";
    private static final String DATABASE_NAME = "gestao_tarefas_estudos.db";

    private static volatile AppDatabase instance;

    public abstract UsuarioRoomDAO usuarioDAO();
    public abstract DisciplinaRoomDAO disciplinaDAO();
    public abstract TarefaRoomDAO tarefaDAO();
    public abstract SessaoEstudoRoomDAO sessaoEstudoDAO();

    // ═══════════════════════════════════════════════════════════════════════
    // MIGRATIONS
    // Adicione novas migrations aqui conforme o banco evolui
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Migration 3 -> 4: Adiciona indices nas colunas de chave estrangeira
     * para melhorar performance das queries com JOIN e WHERE.
     */
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Indice em disciplinas.usuario_id (usado em obterTodas, contarTotal, etc.)
            database.execSQL("CREATE INDEX IF NOT EXISTS index_disciplinas_usuario_id ON disciplinas(usuario_id)");

            // Indice em tarefas.disciplina_id (usado em todos os JOINs com disciplinas)
            database.execSQL("CREATE INDEX IF NOT EXISTS index_tarefas_disciplina_id ON tarefas(disciplina_id)");

            // Indice em sessoes_estudo.disciplina_id (usado em estatisticas e timer)
            database.execSQL("CREATE INDEX IF NOT EXISTS index_sessoes_estudo_disciplina_id ON sessoes_estudo(disciplina_id)");

            Log.d(TAG, "Migration 3 -> 4: indices de FK criados com sucesso");
        }
    };

    // Array com todas as migrations disponiveis
    private static final Migration[] ALL_MIGRATIONS = {
            MIGRATION_3_4,
    };

    // ═══════════════════════════════════════════════════════════════════════

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addMigrations(ALL_MIGRATIONS)
                    // REMOVIDO: .fallbackToDestructiveMigration()
                    // Isso APAGAVA todos os dados do usuario em updates!
                    .build();

                    Log.d(TAG, "Database inicializado com sucesso");
                }
            }
        }
        return instance;
    }

    /**
     * Fecha a instancia do banco de dados.
     * Util para testes ou quando precisa recriar o banco.
     */
    public static void closeDatabase() {
        if (instance != null) {
            if (instance.isOpen()) {
                instance.close();
            }
            instance = null;
        }
    }
}
