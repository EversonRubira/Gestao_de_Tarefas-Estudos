package com.example.gestaodetarefasestudos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.UsuarioRoomDAO;
import com.example.gestaodetarefasestudos.models.Usuario;
import com.example.gestaodetarefasestudos.utils.PasswordHelper;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegistroActivity extends BaseActivity {

    private TextInputEditText editNome, editEmail, editSenha, editConfirmarSenha;
    private Button btnRegistrar;
    private TextView txtIrParaLogin;
    private UsuarioRoomDAO usuarioDAO;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        inicializarComponentes();
        configurarListeners();
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        editConfirmarSenha = findViewById(R.id.editConfirmarSenha);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        txtIrParaLogin = findViewById(R.id.txtIrParaLogin);

        usuarioDAO = AppDatabase.getInstance(this).usuarioDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    private void configurarListeners() {
        btnRegistrar.setOnClickListener(v -> fazerRegistro());
        txtIrParaLogin.setOnClickListener(v -> voltarParaLogin());
    }

    /**
     * Realiza o registro do usuário
     */
    private void fazerRegistro() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim().toLowerCase();
        String senha = editSenha.getText().toString();
        String confirmarSenha = editConfirmarSenha.getText().toString();

        // Validar campos
        if (!validarCampos(nome, email, senha, confirmarSenha)) {
            return;
        }

        // ROOM não pode rodar na thread principal
        executor.execute(() -> {
            try {
                // Verificar se email já existe
                int count = usuarioDAO.verificarEmailExiste(email);
                if (count > 0) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, getString(R.string.error_email_exists), Toast.LENGTH_SHORT).show();
                        editEmail.requestFocus();
                    });
                    return;
                }

                // Criar novo usuário
                String senhaHash = PasswordHelper.gerarHash(senha);
                Usuario novoUsuario = new Usuario(nome, email, senhaHash);

                long usuarioId = usuarioDAO.inserir(novoUsuario);

                runOnUiThread(() -> {
                    if (usuarioId > 0) {
                        // Registro bem-sucedido
                        getPrefs().salvarUsuarioLogado(usuarioId, email);
                        Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();

                        // Ir para MainActivity
                        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.error_register_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.error_database), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Valida os campos do formulário
     */
    private boolean validarCampos(String nome, String email, String senha, String confirmarSenha) {
        if (nome.isEmpty()) {
            editNome.setError(getString(R.string.error_empty_name));
            editNome.requestFocus();
            return false;
        }

        if (nome.length() < 3) {
            editNome.setError(getString(R.string.error_short_name));
            editNome.requestFocus();
            return false;
        }

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

        if (confirmarSenha.isEmpty()) {
            editConfirmarSenha.setError(getString(R.string.error_empty_confirm_password));
            editConfirmarSenha.requestFocus();
            return false;
        }

        if (!senha.equals(confirmarSenha)) {
            editConfirmarSenha.setError(getString(R.string.error_password_mismatch));
            editConfirmarSenha.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Volta para a tela de login
     */
    private void voltarParaLogin() {
        finish(); // Fecha a activity atual e volta para a anterior
    }
}
