package com.example.gestaodetarefasestudos.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaodetarefasestudos.database.DatabaseHelper;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SessaoEstudoDAO {

    private DatabaseHelper dbHelper;

    public SessaoEstudoDAO(Context context) {
        dbHelper = DatabaseHelper.obterInstancia(context);
    }

    // CREATE - Adicionar nova sessão de estudo
    public long adicionar(SessaoEstudo sessao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_SESSAO_DISCIPLINA_ID, sessao.getDisciplinaId());
        valores.put(DatabaseHelper.COL_SESSAO_DURACAO, sessao.getDuracao());
        valores.put(DatabaseHelper.COL_SESSAO_DATA, sessao.getData());

        long id = db.insert(DatabaseHelper.TABELA_SESSOES_ESTUDO, null, valores);
        db.close();
        return id;
    }

    // READ - Obter todas as sessões de estudo
    public List<SessaoEstudo> obterTodas() {
        List<SessaoEstudo> listaSessoes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT s.*, d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as nome_disciplina " +
                "FROM " + DatabaseHelper.TABELA_SESSOES_ESTUDO + " s " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON s." + DatabaseHelper.COL_SESSAO_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID +
                " ORDER BY s." + DatabaseHelper.COL_SESSAO_DATA + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                SessaoEstudo sessao = criarSessaoDoCursor(cursor);
                listaSessoes.add(sessao);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaSessoes;
    }

    // READ - Obter sessão por ID
    public SessaoEstudo obterPorId(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SessaoEstudo sessao = null;

        String query = "SELECT s.*, d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as nome_disciplina " +
                "FROM " + DatabaseHelper.TABELA_SESSOES_ESTUDO + " s " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON s." + DatabaseHelper.COL_SESSAO_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID +
                " WHERE s." + DatabaseHelper.COL_SESSAO_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            sessao = criarSessaoDoCursor(cursor);
        }

        cursor.close();
        db.close();
        return sessao;
    }

    // DELETE - Deletar sessão de estudo
    public int deletar(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int linhasAfetadas = db.delete(DatabaseHelper.TABELA_SESSOES_ESTUDO,
                DatabaseHelper.COL_SESSAO_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return linhasAfetadas;
    }

    // Obter tempo total de estudo de hoje (em segundos)
    public long obterTempoEstudoHoje() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Calcular início do dia de hoje
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long inicioDia = calendar.getTimeInMillis();

        String query = "SELECT SUM(" + DatabaseHelper.COL_SESSAO_DURACAO + ") " +
                "FROM " + DatabaseHelper.TABELA_SESSOES_ESTUDO +
                " WHERE " + DatabaseHelper.COL_SESSAO_DATA + " >= ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(inicioDia)});

        long tempoTotal = 0;
        if (cursor.moveToFirst()) {
            tempoTotal = cursor.getLong(0);
        }

        cursor.close();
        db.close();
        return tempoTotal;
    }

    // Obter tempo total de estudo de uma disciplina
    public long obterTempoEstudoDisciplina(long disciplinaId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT SUM(" + DatabaseHelper.COL_SESSAO_DURACAO + ") " +
                "FROM " + DatabaseHelper.TABELA_SESSOES_ESTUDO +
                " WHERE " + DatabaseHelper.COL_SESSAO_DISCIPLINA_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(disciplinaId)});

        long tempoTotal = 0;
        if (cursor.moveToFirst()) {
            tempoTotal = cursor.getLong(0);
        }

        cursor.close();
        db.close();
        return tempoTotal;
    }

    /**
     * Obtém tempo de estudo de hoje agrupado por disciplina
     * Retorna um Cursor com: disciplina_id, nome_disciplina, total_segundos
     */
    public Cursor obterTempoHojePorDisciplina() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Calcular início do dia de hoje
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long inicioDia = calendar.getTimeInMillis();

        // Query que agrupa por disciplina e soma a duração
        String query = "SELECT " +
                "s." + DatabaseHelper.COL_SESSAO_DISCIPLINA_ID + ", " +
                "d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as d_nome, " +
                "SUM(s." + DatabaseHelper.COL_SESSAO_DURACAO + ") as total_segundos " +
                "FROM " + DatabaseHelper.TABELA_SESSOES_ESTUDO + " s " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON s." + DatabaseHelper.COL_SESSAO_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID + " " +
                "WHERE s." + DatabaseHelper.COL_SESSAO_DATA + " >= ? " +
                "GROUP BY s." + DatabaseHelper.COL_SESSAO_DISCIPLINA_ID + " " +
                "ORDER BY total_segundos DESC";

        return db.rawQuery(query, new String[]{String.valueOf(inicioDia)});
    }

    // Método auxiliar para criar objeto SessaoEstudo a partir do Cursor
    private SessaoEstudo criarSessaoDoCursor(Cursor cursor) {
        SessaoEstudo sessao = new SessaoEstudo();
        sessao.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SESSAO_ID)));
        sessao.setDisciplinaId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SESSAO_DISCIPLINA_ID)));
        sessao.setDuracao(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SESSAO_DURACAO)));
        sessao.setData(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SESSAO_DATA)));

        // Nome da disciplina do JOIN
        int colIndex = cursor.getColumnIndex("nome_disciplina");
        if (colIndex != -1) {
            sessao.setNomeDisciplina(cursor.getString(colIndex));
        }

        return sessao;
    }
}
