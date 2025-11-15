package com.example.gestaodetarefasestudos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int DURACAO_SPLASH = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        inicializarAnimacoes();
        navegarParaTelaInicial();
    }

    private void inicializarAnimacoes() {
        ImageView logo = findViewById(R.id.splash_logo);
        TextView nomeApp = findViewById(R.id.splash_app_name);
        TextView subtitulo = findViewById(R.id.splash_subtitle);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        logo.startAnimation(fadeIn);
        nomeApp.startAnimation(slideUp);
        subtitulo.startAnimation(slideUp);
    }

    /**
     * Aguarda o tempo definido e depois navega para a tela principal (MainActivity)
     * Usa Handler.postDelayed para criar um atraso sem bloquear a interface
     */
    private void navegarParaTelaInicial() {
        new Handler().postDelayed(() -> {
            Intent intencao = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intencao);
            finish(); // Fecha a SplashActivity para que o utilizador não volte para ela ao pressionar "voltar"
        }, DURACAO_SPLASH);
    }
}
