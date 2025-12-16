package com.example.gestaodetarefasestudos;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

/**
 * Classe base para todas as activities
 * Aplica o idioma e tema salvos nas preferências
 */
public class BaseActivity extends AppCompatActivity {

    private PreferenciasApp prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Aplicar tema antes de chamar super.onCreate()
        prefs = new PreferenciasApp(this);
        aplicarTema();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // Aplicar idioma
        PreferenciasApp prefs = new PreferenciasApp(newBase);
        String idioma = prefs.getIdioma();

        Locale locale = new Locale(idioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    /**
     * Aplica o tema (escuro ou claro) conforme salvo nas preferências
     */
    private void aplicarTema() {
        if (prefs.isTemaEscuro()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Método auxiliar para obter as preferências
     */
    protected PreferenciasApp getPrefs() {
        if (prefs == null) {
            prefs = new PreferenciasApp(this);
        }
        return prefs;
    }
}
