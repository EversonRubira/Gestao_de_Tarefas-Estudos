package com.example.gestaodetarefasestudos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Informações do banco de dados
    private static final String NOME_BANCO = "gestao_tarefas_estudos.db";
    private static final int VERSAO_BANCO = 1;

    // Nome das tabelas
    public static final String TABELA_DISCIPLINAS = "disciplinas";
    public static final String TABELA_TAREFAS = "tarefas";
    public static final String TABELA_SESSOES_ESTUDO = "sessoes_estudo";

    // Colunas da tabela DISCIPLINAS
    public static final String COL_DISCIPLINA_ID = "id";
    public static final String COL_DISCIPLINA_NOME = "nome";
    public static final String COL_DISCIPLINA_CODIGO = "codigo";
    public static final String COL_DISCIPLINA_COR = "cor";
    public static final String COL_DISCIPLINA_DATA_CRIACAO = "data_criacao";

    // Colunas da tabela TAREFAS
    public static final String COL_TAREFA_ID = "id";
    public static final String COL_TAREFA_TITULO = "titulo";
    public static final String COL_TAREFA_DESCRICAO = "descricao";
    public static final String COL_TAREFA_DISCIPLINA_ID = "disciplina_id";
    public static final String COL_TAREFA_DATA_ENTREGA = "data_entrega";
    public static final String COL_TAREFA_PRIORIDADE = "prioridade";
    public static final String COL_TAREFA_ESTADO = "estado";
    public static final String COL_TAREFA_DATA_CRIACAO = "data_criacao";

    // Colunas da tabela SESSOES_ESTUDO
    public static final String COL_SESSAO_ID = "id";
    public static final String COL_SESSAO_DISCIPLINA_ID = "disciplina_id";
    public static final String COL_SESSAO_DURACAO = "duracao";
    public static final String COL_SESSAO_DATA = "data";

    // SQL para criar tabela DISCIPLINAS
    private static final String CRIAR_TABELA_DISCIPLINAS =
            "CREATE TABLE " + TABELA_DISCIPLINAS + " (" +
            COL_DISCIPLINA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_DISCIPLINA_NOME + " TEXT NOT NULL, " +
            COL_DISCIPLINA_CODIGO + " TEXT NOT NULL UNIQUE, " +
            COL_DISCIPLINA_COR + " TEXT NOT NULL, " +
            COL_DISCIPLINA_DATA_CRIACAO + " INTEGER NOT NULL" +
            ")";

    // SQL para criar tabela TAREFAS
    private static final String CRIAR_TABELA_TAREFAS =
            "CREATE TABLE " + TABELA_TAREFAS + " (" +
            COL_TAREFA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TAREFA_TITULO + " TEXT NOT NULL, " +
            COL_TAREFA_DESCRICAO + " TEXT, " +
            COL_TAREFA_DISCIPLINA_ID + " INTEGER NOT NULL, " +
            COL_TAREFA_DATA_ENTREGA + " INTEGER NOT NULL, " +
            COL_TAREFA_PRIORIDADE + " INTEGER NOT NULL, " +
            COL_TAREFA_ESTADO + " INTEGER NOT NULL, " +
            COL_TAREFA_DATA_CRIACAO + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COL_TAREFA_DISCIPLINA_ID + ") REFERENCES " +
            TABELA_DISCIPLINAS + "(" + COL_DISCIPLINA_ID + ") ON DELETE CASCADE" +
            ")";

    // SQL para criar tabela SESSOES_ESTUDO
    private static final String CRIAR_TABELA_SESSOES_ESTUDO =
            "CREATE TABLE " + TABELA_SESSOES_ESTUDO + " (" +
            COL_SESSAO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_SESSAO_DISCIPLINA_ID + " INTEGER NOT NULL, " +
            COL_SESSAO_DURACAO + " INTEGER NOT NULL, " +
            COL_SESSAO_DATA + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + COL_SESSAO_DISCIPLINA_ID + ") REFERENCES " +
            TABELA_DISCIPLINAS + "(" + COL_DISCIPLINA_ID + ") ON DELETE CASCADE" +
            ")";

    // Singleton
    private static DatabaseHelper instancia;

    private DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    public static synchronized DatabaseHelper obterInstancia(Context context) {
        if (instancia == null) {
            instancia = new DatabaseHelper(context.getApplicationContext());
        }
        return instancia;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ativar chaves estrangeiras
        db.execSQL("PRAGMA foreign_keys=ON");

        // Criar tabelas
        db.execSQL(CRIAR_TABELA_DISCIPLINAS);
        db.execSQL(CRIAR_TABELA_TAREFAS);
        db.execSQL(CRIAR_TABELA_SESSOES_ESTUDO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        // Em caso de atualização, recriar as tabelas
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_SESSOES_ESTUDO);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_TAREFAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_DISCIPLINAS);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Ativar chaves estrangeiras
        db.setForeignKeyConstraintsEnabled(true);
    }
}
