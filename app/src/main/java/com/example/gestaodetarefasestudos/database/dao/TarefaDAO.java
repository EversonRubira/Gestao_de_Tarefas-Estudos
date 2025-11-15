package com.example.gestaodetarefasestudos.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaodetarefasestudos.database.DatabaseHelper;
import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.util.ArrayList;
import java.util.List;

public class TarefaDAO {

    private DatabaseHelper dbHelper;

    public TarefaDAO(Context context) {
        dbHelper = DatabaseHelper.obterInstancia(context);
    }

    // CREATE - Adicionar nova tarefa
    public long adicionar(Tarefa tarefa) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_TAREFA_TITULO, tarefa.getTitulo());
        valores.put(DatabaseHelper.COL_TAREFA_DESCRICAO, tarefa.getDescricao());
        valores.put(DatabaseHelper.COL_TAREFA_DISCIPLINA_ID, tarefa.getDisciplinaId());
        valores.put(DatabaseHelper.COL_TAREFA_DATA_ENTREGA, tarefa.getDataEntrega());
        valores.put(DatabaseHelper.COL_TAREFA_PRIORIDADE, tarefa.getPrioridade().getValor());
        valores.put(DatabaseHelper.COL_TAREFA_ESTADO, tarefa.getEstado().getValor());
        valores.put(DatabaseHelper.COL_TAREFA_DATA_CRIACAO, tarefa.getDataCriacao());

        long id = db.insert(DatabaseHelper.TABELA_TAREFAS, null, valores);
        db.close();
        return id;
    }

    // READ - Obter todas as tarefas com nome da disciplina
    public List<Tarefa> obterTodas() {
        List<Tarefa> listaTarefas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.*, d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as nome_disciplina " +
                "FROM " + DatabaseHelper.TABELA_TAREFAS + " t " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID +
                " ORDER BY t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Tarefa tarefa = criarTarefaDoCursor(cursor);
                listaTarefas.add(tarefa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaTarefas;
    }

    // READ - Obter tarefas pendentes
    public List<Tarefa> obterPendentes() {
        List<Tarefa> listaTarefas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.*, d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as nome_disciplina " +
                "FROM " + DatabaseHelper.TABELA_TAREFAS + " t " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID +
                " WHERE t." + DatabaseHelper.COL_TAREFA_ESTADO + " != ? " +
                "ORDER BY t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(EstadoTarefa.CONCLUIDA.getValor())});

        if (cursor.moveToFirst()) {
            do {
                Tarefa tarefa = criarTarefaDoCursor(cursor);
                listaTarefas.add(tarefa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaTarefas;
    }

    // READ - Obter tarefa por ID
    public Tarefa obterPorId(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Tarefa tarefa = null;

        String query = "SELECT t.*, d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as nome_disciplina " +
                "FROM " + DatabaseHelper.TABELA_TAREFAS + " t " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID +
                " WHERE t." + DatabaseHelper.COL_TAREFA_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            tarefa = criarTarefaDoCursor(cursor);
        }

        cursor.close();
        db.close();
        return tarefa;
    }

    // UPDATE - Atualizar tarefa
    public int atualizar(Tarefa tarefa) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_TAREFA_TITULO, tarefa.getTitulo());
        valores.put(DatabaseHelper.COL_TAREFA_DESCRICAO, tarefa.getDescricao());
        valores.put(DatabaseHelper.COL_TAREFA_DISCIPLINA_ID, tarefa.getDisciplinaId());
        valores.put(DatabaseHelper.COL_TAREFA_DATA_ENTREGA, tarefa.getDataEntrega());
        valores.put(DatabaseHelper.COL_TAREFA_PRIORIDADE, tarefa.getPrioridade().getValor());
        valores.put(DatabaseHelper.COL_TAREFA_ESTADO, tarefa.getEstado().getValor());

        int linhasAfetadas = db.update(DatabaseHelper.TABELA_TAREFAS, valores,
                DatabaseHelper.COL_TAREFA_ID + " = ?",
                new String[]{String.valueOf(tarefa.getId())});

        db.close();
        return linhasAfetadas;
    }

    // UPDATE - Atualizar apenas o estado da tarefa
    public int atualizarEstado(long id, EstadoTarefa estado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_TAREFA_ESTADO, estado.getValor());

        int linhasAfetadas = db.update(DatabaseHelper.TABELA_TAREFAS, valores,
                DatabaseHelper.COL_TAREFA_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return linhasAfetadas;
    }

    // DELETE - Deletar tarefa
    public int deletar(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int linhasAfetadas = db.delete(DatabaseHelper.TABELA_TAREFAS,
                DatabaseHelper.COL_TAREFA_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return linhasAfetadas;
    }

    // Contar tarefas pendentes
    public int contarPendentes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABELA_TAREFAS +
                " WHERE " + DatabaseHelper.COL_TAREFA_ESTADO + " != ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(EstadoTarefa.CONCLUIDA.getValor())});

        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return total;
    }

    /**
     * Obtém tarefas de um período específico (mês) com informações da disciplina e cor
     * MÉTODO CRIADO PARA O CALENDÁRIO!
     *
     * @param inicioMes timestamp do primeiro dia do mês (00:00:00)
     * @param fimMes timestamp do último momento do mês (23:59:59)
     * @return Cursor com: data_entrega, disciplina_id, cor_disciplina, count (quantidade de tarefas)
     */
    public Cursor obterTarefasPorPeriodoComCores(long inicioMes, long fimMes) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Query que agrupa tarefas por dia e disciplina, retornando a cor e quantidade
        // Esta query é complexa por usar JOIN e GROUP BY simultaneamente
        String query = "SELECT " +
                // Colunas que queremos retornar
                "t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " as data_entrega, " +
                "t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " as disciplina_id, " +
                "d." + DatabaseHelper.COL_DISCIPLINA_COR + " as cor_disciplina, " +
                "COUNT(*) as count " + // COUNT(*) conta quantas tarefas existem por grupo
                "FROM " + DatabaseHelper.TABELA_TAREFAS + " t " +
                // LEFT JOIN conecta tabela tarefas com disciplinas para pegar a cor
                // LEFT JOIN mantém tarefas mesmo se não tiver disciplina associada
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID + " " +
                // WHERE filtra apenas tarefas dentro do período do mês
                "WHERE t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " >= ? " +
                "AND t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " <= ? " +
                "AND t." + DatabaseHelper.COL_TAREFA_ESTADO + " != ? " + // Ignorar tarefas concluídas
                // GROUP BY agrupa as tarefas por dia E por disciplina
                // date() converte timestamp (milissegundos) em data (dividindo por 1000)
                // Resultado: cada linha representa uma disciplina em um dia específico
                "GROUP BY date(t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " / 1000, 'unixepoch'), " +
                "t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " " +
                "ORDER BY t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " ASC";

        // Os '?' são substituídos pelos valores no array (inicioMes, fimMes, estado)
        // Isso previne SQL Injection
        return db.rawQuery(query, new String[]{
                String.valueOf(inicioMes),
                String.valueOf(fimMes),
                String.valueOf(EstadoTarefa.CONCLUIDA.getValor())
        });
    }

    /**
     * Obtém todas as tarefas de um dia específico
     * Útil para mostrar detalhes ao clicar em um dia do calendário
     *
     * @param inicioDia timestamp do início do dia (00:00:00)
     * @param fimDia timestamp do fim do dia (23:59:59)
     * @return Lista de tarefas do dia
     */
    public List<Tarefa> obterTarefasPorDia(long inicioDia, long fimDia) {
        List<Tarefa> listaTarefas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT t.*, d." + DatabaseHelper.COL_DISCIPLINA_NOME + " as nome_disciplina " +
                "FROM " + DatabaseHelper.TABELA_TAREFAS + " t " +
                "LEFT JOIN " + DatabaseHelper.TABELA_DISCIPLINAS + " d " +
                "ON t." + DatabaseHelper.COL_TAREFA_DISCIPLINA_ID + " = d." + DatabaseHelper.COL_DISCIPLINA_ID + " " +
                "WHERE t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " >= ? " +
                "AND t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " <= ? " +
                "ORDER BY t." + DatabaseHelper.COL_TAREFA_DATA_ENTREGA + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(inicioDia),
                String.valueOf(fimDia)
        });

        if (cursor.moveToFirst()) {
            do {
                Tarefa tarefa = criarTarefaDoCursor(cursor);
                listaTarefas.add(tarefa);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listaTarefas;
    }

    // Método auxiliar para criar objeto Tarefa a partir do Cursor
    // Cursor é como um ponteiro para uma linha da tabela do banco de dados
    private Tarefa criarTarefaDoCursor(Cursor cursor) {
        Tarefa tarefa = new Tarefa();

        // getColumnIndexOrThrow() busca o índice da coluna pelo nome
        // Se a coluna não existir, lança uma exceção (throw)
        // cursor.getLong()/getString()/getInt() pega o valor na posição indicada
        tarefa.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_ID)));
        tarefa.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_TITULO)));
        tarefa.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_DESCRICAO)));
        tarefa.setDisciplinaId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_DISCIPLINA_ID)));
        tarefa.setDataEntrega(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_DATA_ENTREGA)));

        // fromValor() converte número inteiro de volta para o enum
        tarefa.setPrioridade(Prioridade.fromValor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_PRIORIDADE))));
        tarefa.setEstado(EstadoTarefa.fromValor(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_ESTADO))));
        tarefa.setDataCriacao(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAREFA_DATA_CRIACAO)));

        // Nome da disciplina vem do JOIN (não é coluna da tabela tarefas)
        // getColumnIndex() retorna -1 se a coluna não existir (diferente de OrThrow)
        int colIndex = cursor.getColumnIndex("nome_disciplina");
        if (colIndex != -1) {
            tarefa.setNomeDisciplina(cursor.getString(colIndex));
        }

        return tarefa;
    }
}
