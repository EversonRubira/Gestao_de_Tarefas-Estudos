package com.example.gestaodetarefasestudos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

/**
 * Tela de configurações do app
 * Permite trocar idioma e tema
 */
public class ConfiguracoesActivity extends BaseActivity {

    private RadioGroup radioGroupIdioma;
    private RadioGroup radioGroupTema;
    private Button botaoSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings_title);
        }

        // Inicializar views
        radioGroupIdioma = findViewById(R.id.radioGroupIdioma);
        radioGroupTema = findViewById(R.id.radioGroupTema);
        botaoSalvar = findViewById(R.id.botaoSalvar);

        // Carregar configurações atuais
        carregarConfiguracoes();

        // Botão salvar
        botaoSalvar.setOnClickListener(v -> salvarConfiguracoes());
    }

    private void carregarConfiguracoes() {
        // Carregar idioma atual
        String idiomaAtual = getPrefs().getIdioma();
        if (idiomaAtual.equals("pt")) {
            ((RadioButton) findViewById(R.id.radioPortugues)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioIngles)).setChecked(true);
        }

        // Carregar tema atual
        boolean temaEscuro = getPrefs().isTemaEscuro();
        if (temaEscuro) {
            ((RadioButton) findViewById(R.id.radioTemaEscuro)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioTemaClaro)).setChecked(true);
        }
    }

    private void salvarConfiguracoes() {
        // Salvar idioma
        int idiomaId = radioGroupIdioma.getCheckedRadioButtonId();
        String novoIdioma = (idiomaId == R.id.radioPortugues) ? "pt" : "en";
        getPrefs().salvarIdioma(novoIdioma);

        // Salvar tema
        int temaId = radioGroupTema.getCheckedRadioButtonId();
        boolean temaEscuro = (temaId == R.id.radioTemaEscuro);
        getPrefs().salvarTema(temaEscuro);

        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();

        // Reiniciar activity para aplicar mudanças
        recreate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
