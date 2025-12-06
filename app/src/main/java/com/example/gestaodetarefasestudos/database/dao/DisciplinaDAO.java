package com.example.gestaodetarefasestudos.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaodetarefasestudos.database.DatabaseHelper;
import com.example.gestaodetarefasestudos.models.Disciplina;

import java.util.ArrayList;
import java.util.List;

public class DisciplinaDAO {

    private DatabaseHelper dbHelper;
    private Context context;

    public DisciplinaDAO(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.obterInstancia(context);
    }

    // CREATE - Adicionar nova disciplina
    public long adicionar(Disciplina disciplina) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_DISCIPLINA_USUARIO_ID, obterUsuarioLogadoId());
        valores.put(DatabaseHelper.COL_DISCIPLINA_NOME, disciplina.getNome());
        valores.put(DatabaseHelper.COL_DISCIPLINA_CODIGO, disciplina.getCodigo());
        valores.put(DatabaseHelper.COL_DISCIPLINA_COR, disciplina.getCor());
        valores.put(DatabaseHelper.COL_DISCIPLINA_DATA_CRIACAO, disciplina.getDataCriacao());

        long id = db.insert(DatabaseHelper.TABELA_DISCIPLINAS, null, valores);
        db.close();
        return id;
    }

    // READ - Obter todas as disciplinas do usuário logado
    public List<Disciplina> obterTodas() {
        List<Disciplina> listaDisciplinas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DatabaseHelper.TABELA_DISCIPLINAS +
                " WHERE " + DatabaseHelper.COL_DISCIPLINA_USUARIO_ID + " = ?" +
                " ORDER BY " + DatabaseHelper.COL_DISCIPLINA_NOME + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(obterUsuarioLogadoId())});

        if (cursor.moveToFirst()) {
            do {
                Disciplina disciplina = new Disciplina();
                disciplina.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_ID)));
                disciplina.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_NOME)));
                disciplina.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_CODIGO)));
                disciplina.setCor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_COR)));
                disciplina.setDataCriacao(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_DATA_CRIACAO)));

                listaDisciplinas.add(disciplina);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaDisciplinas;
    }

    // READ - Obter disciplina por ID do usuário logado
    public Disciplina obterPorId(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Disciplina disciplina = null;

        String query = "SELECT * FROM " + DatabaseHelper.TABELA_DISCIPLINAS +
                " WHERE " + DatabaseHelper.COL_DISCIPLINA_ID + " = ?" +
                " AND " + DatabaseHelper.COL_DISCIPLINA_USUARIO_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id), String.valueOf(obterUsuarioLogadoId())});

        if (cursor.moveToFirst()) {
            disciplina = new Disciplina();
            disciplina.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_ID)));
            disciplina.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_NOME)));
            disciplina.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_CODIGO)));
            disciplina.setCor(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_COR)));
            disciplina.setDataCriacao(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DISCIPLINA_DATA_CRIACAO)));
        }

        cursor.close();
        db.close();
        return disciplina;
    }

    // UPDATE - Atualizar disciplina
    public int atualizar(Disciplina disciplina) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_DISCIPLINA_NOME, disciplina.getNome());
        valores.put(DatabaseHelper.COL_DISCIPLINA_CODIGO, disciplina.getCodigo());
        valores.put(DatabaseHelper.COL_DISCIPLINA_COR, disciplina.getCor());

        int linhasAfetadas = db.update(DatabaseHelper.TABELA_DISCIPLINAS, valores,
                DatabaseHelper.COL_DISCIPLINA_ID + " = ?",
                new String[]{String.valueOf(disciplina.getId())});

        db.close();
        return linhasAfetadas;
    }

    // DELETE - Deletar disciplina
    public int deletar(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // IMPORTANTE: Ativar foreign keys para que o CASCADE funcione
        db.execSQL("PRAGMA foreign_keys=ON");

        int linhasAfetadas = db.delete(DatabaseHelper.TABELA_DISCIPLINAS,
                DatabaseHelper.COL_DISCIPLINA_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return linhasAfetadas;
    }

    // Contar total de disciplinas do usuário logado
    public int contarTotal() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABELA_DISCIPLINAS +
                " WHERE " + DatabaseHelper.COL_DISCIPLINA_USUARIO_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(obterUsuarioLogadoId())});

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    // Verificar se código já existe para o usuário logado
    public boolean codigoJaExiste(String codigo, long idExcluir) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABELA_DISCIPLINAS +
                " WHERE " + DatabaseHelper.COL_DISCIPLINA_CODIGO + " = ?" +
                " AND " + DatabaseHelper.COL_DISCIPLINA_ID + " != ?" +
                " AND " + DatabaseHelper.COL_DISCIPLINA_USUARIO_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{codigo, String.valueOf(idExcluir), String.valueOf(obterUsuarioLogadoId())});

        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return existe;
    }

    // Helper - Obter ID do usuário logado
    private long obterUsuarioLogadoId() {
        android.content.SharedPreferences preferences = context.getSharedPreferences("GestaoTarefasPrefs", Context.MODE_PRIVATE);
        return preferences.getLong("usuario_id", 0);
    }
}
