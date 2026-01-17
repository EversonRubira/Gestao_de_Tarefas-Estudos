package com.example.gestaodetarefasestudos;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Classe para gerenciar preferencias do app de forma SEGURA.
 * Usa EncryptedSharedPreferences para proteger dados sensiveis.
 *
 * Dados armazenados:
 * - Credenciais de login (usuario ID, email)
 * - Configuracoes (tema, idioma)
 * - Flags de controle (primeira execucao, permissao de notificacao)
 */
public class PreferenciasApp {

    private static final String TAG = "PreferenciasApp";
    private static final String NOME_ARQUIVO = "AppPrefsSecure";

    private SharedPreferences prefs;

    public PreferenciasApp(Context context) {
        prefs = criarSharedPreferences(context);
    }

    /**
     * Cria SharedPreferences criptografadas.
     * Em caso de erro, faz fallback para SharedPreferences normais.
     */
    private SharedPreferences criarSharedPreferences(Context context) {
        try {
            // Criar MasterKey para criptografia
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // Criar EncryptedSharedPreferences
            return EncryptedSharedPreferences.create(
                    context,
                    NOME_ARQUIVO,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Erro ao criar EncryptedSharedPreferences, usando fallback", e);
            // Fallback para SharedPreferences normais (dispositivos antigos/problematicos)
            return context.getSharedPreferences(NOME_ARQUIVO + "_fallback", Context.MODE_PRIVATE);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // USUARIO E LOGIN
    // ═══════════════════════════════════════════════════════════════════════

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

    // ═══════════════════════════════════════════════════════════════════════
    // TEMA (ESCURO OU CLARO)
    // ═══════════════════════════════════════════════════════════════════════

    public void salvarTema(boolean isDark) {
        prefs.edit().putBoolean("tema_escuro", isDark).apply();
    }

    public boolean isTemaEscuro() {
        return prefs.getBoolean("tema_escuro", false);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // IDIOMA
    // ═══════════════════════════════════════════════════════════════════════

    public void salvarIdioma(String idioma) {
        prefs.edit().putString("idioma", idioma).apply();
    }

    public String getIdioma() {
        return prefs.getString("idioma", "pt");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PRIMEIRA EXECUCAO
    // ═══════════════════════════════════════════════════════════════════════

    public void setPrimeiraExecucao(boolean valor) {
        prefs.edit().putBoolean("primeira_vez", valor).apply();
    }

    public boolean isPrimeiraExecucao() {
        return prefs.getBoolean("primeira_vez", true);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PERMISSAO DE NOTIFICACAO
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isFirstNotificationRequest() {
        return prefs.getBoolean("first_notification_request", true);
    }

    public void setFirstNotificationRequest(boolean valor) {
        prefs.edit().putBoolean("first_notification_request", valor).apply();
    }
}
