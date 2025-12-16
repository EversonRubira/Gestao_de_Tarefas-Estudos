package com.example.gestaodetarefasestudos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.UsuarioRoomDAO;
import com.example.gestaodetarefasestudos.models.Usuario;
import com.example.gestaodetarefasestudos.utils.PasswordHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginActivity extends BaseActivity {

    private TextInputEditText editEmail, editSenha;
    private Button btnLogin, btnGoToRegister;
    private UsuarioRoomDAO dao;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // inicializar views
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btnLogin = findViewById(R.id.btnEntrar);
        btnGoToRegister = findViewById(R.id.btnIrParaRegistro);

        dao = AppDatabase.getInstance(this).usuarioDAO();
        executor = Executors.newSingleThreadExecutor();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
    }

    // faz login
    private void doLogin() {
        String email = editEmail.getText().toString().trim().toLowerCase();
        String senha = editSenha.getText().toString();

        if (!validateFields(email, senha)) {
            return;
        }

        // ROOM não pode rodar na thread principal
        executor.execute(() -> {
            try {
                Usuario usuario = dao.obterPorEmail(email);

                runOnUiThread(() -> {
                    if (usuario != null && PasswordHelper.verificarSenha(senha, usuario.getSenhaHash())) {
                        // Login bem-sucedido
                        getPrefs().salvarUsuarioLogado(usuario.getId(), usuario.getEmail());
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Credenciais invalidas
                        Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                        editSenha.setText("");
                        editSenha.requestFocus();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.error_database), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private boolean validateFields(String email, String senha) {
        if (email.isEmpty()) {
            editEmail.setError(getString(R.string.error_empty_email));
            editEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError(getString(R.string.error_invalid_email));
            editEmail.requestFocus();
            return false;
        }

        if (senha.isEmpty()) {
            editSenha.setError(getString(R.string.error_empty_password));
            editSenha.requestFocus();
            return false;
        }

        if (senha.length() < 6) {
            editSenha.setError(getString(R.string.error_short_password));
            editSenha.requestFocus();
            return false;
        }

        return true;
    }

    private void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
        startActivity(intent);
    }
}
