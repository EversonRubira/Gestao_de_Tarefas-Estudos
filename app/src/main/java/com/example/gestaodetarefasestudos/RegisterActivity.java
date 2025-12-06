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

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilNome, tilEmail, tilSenha, tilConfirmarSenha;
    private TextInputEditText edtNome, edtEmail, edtSenha, edtConfirmarSenha;
    private Button btnRegistrar;
    private TextView txtIrParaLogin;

    private UsuarioDAO usuarioDAO;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        tilNome = findViewById(R.id.tilNomeRegistro);
        tilEmail = findViewById(R.id.tilEmailRegistro);
        tilSenha = findViewById(R.id.tilSenhaRegistro);
        tilConfirmarSenha = findViewById(R.id.tilConfirmarSenhaRegistro);

        edtNome = findViewById(R.id.edtNomeRegistro);
        edtEmail = findViewById(R.id.edtEmailRegistro);
        edtSenha = findViewById(R.id.edtSenhaRegistro);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenhaRegistro);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        txtIrParaLogin = findViewById(R.id.txtIrParaLogin);

        usuarioDAO = new UsuarioDAO(this);
        preferences = getSharedPreferences("GestaoTarefasPrefs", MODE_PRIVATE);
    }

    private void configurarEventos() {
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tentarRegistrar();
            }
        });

        txtIrParaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void tentarRegistrar() {
        // Limpar erros anteriores
        tilNome.setError(null);
        tilEmail.setError(null);
        tilSenha.setError(null);
        tilConfirmarSenha.setError(null);

        String nome = edtNome.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();

        // Validar campos
        boolean valido = true;

        if (nome.isEmpty()) {
            tilNome.setError("Nome é obrigatório");
            valido = false;
        } else if (nome.length() < 3) {
            tilNome.setError("Nome deve ter pelo menos 3 caracteres");
            valido = false;
        }

        if (email.isEmpty()) {
            tilEmail.setError("E-mail é obrigatório");
            valido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("E-mail inválido");
            valido = false;
        } else if (usuarioDAO.emailJaExiste(email)) {
            tilEmail.setError("E-mail já cadastrado");
            valido = false;
        }

        if (senha.isEmpty()) {
            tilSenha.setError("Senha é obrigatória");
            valido = false;
        } else if (senha.length() < 6) {
            tilSenha.setError("Senha deve ter pelo menos 6 caracteres");
            valido = false;
        }

        if (confirmarSenha.isEmpty()) {
            tilConfirmarSenha.setError("Confirme a senha");
            valido = false;
        } else if (!senha.equals(confirmarSenha)) {
            tilConfirmarSenha.setError("As senhas não coincidem");
            valido = false;
        }

        if (!valido) {
            return;
        }

        // Criar novo usuário
        Usuario novoUsuario = new Usuario(nome, email, senha);
        long id = usuarioDAO.adicionar(novoUsuario);

        if (id > 0) {
            // Registro bem-sucedido - fazer login automático
            novoUsuario.setId(id);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong("usuario_id", novoUsuario.getId());
            editor.putString("usuario_nome", novoUsuario.getNome());
            editor.putString("usuario_email", novoUsuario.getEmail());
            editor.putBoolean("logado", true);
            editor.apply();

            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

            // Ir para MainActivity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // Erro ao registrar
            Toast.makeText(this, "Erro ao criar conta. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Permitir voltar para a tela de login
        super.onBackPressed();
    }
}
