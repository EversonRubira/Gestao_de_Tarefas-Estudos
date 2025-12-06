package com.example.gestaodetarefasestudos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaodetarefasestudos.database.dao.UsuarioDAO;
import com.example.gestaodetarefasestudos.models.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilSenha;
    private TextInputEditText edtEmail, edtSenha;
    private Button btnEntrar;
    private TextView txtIrParaRegistro;

    private UsuarioDAO usuarioDAO;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        tilEmail = findViewById(R.id.tilEmailLogin);
        tilSenha = findViewById(R.id.tilSenhaLogin);
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtSenha = findViewById(R.id.edtSenhaLogin);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtIrParaRegistro = findViewById(R.id.txtIrParaRegistro);

        usuarioDAO = new UsuarioDAO(this);
        preferences = getSharedPreferences("GestaoTarefasPrefs", MODE_PRIVATE);
    }

    private void configurarEventos() {
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tentarLogin();
            }
        });

        txtIrParaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void tentarLogin() {
        // Limpar erros anteriores
        tilEmail.setError(null);
        tilSenha.setError(null);

        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        // Validar campos
        boolean valido = true;

        if (email.isEmpty()) {
            tilEmail.setError("E-mail é obrigatório");
            valido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            valido = false;
        }

        if (senha.isEmpty()) {
            tilSenha.setError("Senha é obrigatória");
            valido = false;
        }

        if (!valido) {
            return;
        }

        // Tentar autenticar
        Usuario usuario = usuarioDAO.autenticar(email, senha);

        if (usuario != null) {
            // Login bem-sucedido - salvar sessão
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("usuario_id", usuario.getId());
            editor.putString("usuario_nome", usuario.getNome());
            editor.putString("usuario_email", usuario.getEmail());
            editor.putBoolean("logado", true);
            editor.apply();

            Toast.makeText(this, "Bem-vindo, " + usuario.getNome() + "!", Toast.LENGTH_SHORT).show();

            // Ir para MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // Login falhou
            Toast.makeText(this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show();
            tilSenha.setError("Credenciais inválidas");
        }
    }

    @Override
    public void onBackPressed() {
        // Desabilitar volta para não permitir acesso sem login
        super.onBackPressed();
        finishAffinity();
    }
}
