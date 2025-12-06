package com.example.gestaodetarefasestudos.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestaodetarefasestudos.database.DatabaseHelper;
import com.example.gestaodetarefasestudos.models.Usuario;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UsuarioDAO {

    private DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = DatabaseHelper.obterInstancia(context);
    }

    // CREATE - Adicionar novo usuário
    public long adicionar(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_USUARIO_NOME, usuario.getNome());
        valores.put(DatabaseHelper.COL_USUARIO_EMAIL, usuario.getEmail().toLowerCase());
        valores.put(DatabaseHelper.COL_USUARIO_SENHA, hashSenha(usuario.getSenha()));
        valores.put(DatabaseHelper.COL_USUARIO_DATA_CRIACAO, usuario.getDataCriacao());

        long id = db.insert(DatabaseHelper.TABELA_USUARIOS, null, valores);
        db.close();
        return id;
    }

    // READ - Obter usuário por ID
    public Usuario obterPorId(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Usuario usuario = null;

        String query = "SELECT * FROM " + DatabaseHelper.TABELA_USUARIOS +
                " WHERE " + DatabaseHelper.COL_USUARIO_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_ID)));
            usuario.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_NOME)));
            usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_EMAIL)));
            usuario.setSenha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_SENHA)));
            usuario.setDataCriacao(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_DATA_CRIACAO)));
        }

        cursor.close();
        db.close();
        return usuario;
    }

    // READ - Obter usuário por email
    public Usuario obterPorEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Usuario usuario = null;

        String query = "SELECT * FROM " + DatabaseHelper.TABELA_USUARIOS +
                " WHERE " + DatabaseHelper.COL_USUARIO_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email.toLowerCase()});

        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_ID)));
            usuario.setNome(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_NOME)));
            usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_EMAIL)));
            usuario.setSenha(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_SENHA)));
            usuario.setDataCriacao(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USUARIO_DATA_CRIACAO)));
        }

        cursor.close();
        db.close();
        return usuario;
    }

    // AUTENTICAR - Verificar credenciais
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = obterPorEmail(email);

        if (usuario != null && usuario.getSenha().equals(hashSenha(senha))) {
            return usuario;
        }

        return null;
    }

    // UPDATE - Atualizar usuário
    public int atualizar(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_USUARIO_NOME, usuario.getNome());
        valores.put(DatabaseHelper.COL_USUARIO_EMAIL, usuario.getEmail().toLowerCase());

        int linhasAfetadas = db.update(DatabaseHelper.TABELA_USUARIOS, valores,
                DatabaseHelper.COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuario.getId())});

        db.close();
        return linhasAfetadas;
    }

    // UPDATE - Atualizar senha
    public int atualizarSenha(long id, String novaSenha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(DatabaseHelper.COL_USUARIO_SENHA, hashSenha(novaSenha));

        int linhasAfetadas = db.update(DatabaseHelper.TABELA_USUARIOS, valores,
                DatabaseHelper.COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return linhasAfetadas;
    }

    // DELETE - Deletar usuário
    public int deletar(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("PRAGMA foreign_keys=ON");

        int linhasAfetadas = db.delete(DatabaseHelper.TABELA_USUARIOS,
                DatabaseHelper.COL_USUARIO_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
        return linhasAfetadas;
    }

    // Verificar se email já existe
    public boolean emailJaExiste(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + DatabaseHelper.TABELA_USUARIOS +
                " WHERE " + DatabaseHelper.COL_USUARIO_EMAIL + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{email.toLowerCase()});

        boolean existe = false;
        if (cursor.moveToFirst()) {
            existe = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return existe;
    }

    // Hash de senha usando SHA-256
    private String hashSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return senha;
        }
    }
}
