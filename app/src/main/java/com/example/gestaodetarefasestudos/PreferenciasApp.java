package com.example.gestaodetarefasestudos;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe simples para gerenciar as preferências do app
 * Guarda: login do usuário, tema, idioma e primeira execução
 */
public class PreferenciasApp {

    private SharedPreferences prefs;
    private static final String NOME_ARQUIVO = "AppPrefs";

    public PreferenciasApp(Context context) {
        prefs = context.getSharedPreferences(NOME_ARQUIVO, Context.MODE_PRIVATE);
    }

    // ========== USUÁRIO E LOGIN ==========

    public void salvarUsuarioLogado(long id, String email) {
        prefs.edit()
            .putLong("usuario_id", id)
            .putString("usuario_email", email)
            .putBoolean("logado", true)
            .apply();
    }

    public long getUsuarioId() {
        return prefs.getLong("usuario_id", -1);
    }

    public String getUsuarioEmail() {
        return prefs.getString("usuario_email", "");
    }

    public boolean isLogado() {
        return prefs.getBoolean("logado", false);
    }

    public void logout() {
        prefs.edit().clear().apply();
    }

    // ========== TEMA (ESCURO OU CLARO) ==========

    public void salvarTema(boolean isDark) {
        prefs.edit().putBoolean("tema_escuro", isDark).apply();
    }

    public boolean isTemaEscuro() {
        return prefs.getBoolean("tema_escuro", false);
    }

    // ========== IDIOMA ==========

    public void salvarIdioma(String idioma) {
        prefs.edit().putString("idioma", idioma).apply();
    }

    public String getIdioma() {
        return prefs.getString("idioma", "pt");
    }

    // ========== PRIMEIRA EXECUÇÃO ==========

    public void setPrimeiraExecucao(boolean valor) {
        prefs.edit().putBoolean("primeira_vez", valor).apply();
    }

    public boolean isPrimeiraExecucao() {
        return prefs.getBoolean("primeira_vez", true);
    }
}
