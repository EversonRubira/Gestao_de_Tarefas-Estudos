package com.example.gestaodetarefasestudos;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AdicionarEditarDisciplinaActivity extends AppCompatActivity {

    private static final String TAG = "AddEditDisciplina";

    private MaterialToolbar toolbar;
    private TextInputLayout inputLayoutNome, inputLayoutCodigo;
    private TextInputEditText editNomeDisciplina, editCodigoDisciplina;
    private GridLayout gridCores;
    private MaterialButton btnSalvar, btnCancelar;

    private DisciplinaRoomDAO disciplinaDAO;
    private Disciplina disciplinaEditando;
    private String corSelecionada = "#2196F3"; // cor padrão
    private long usuarioId;
    private Executor executor;

    private final String[] coresDisponiveis = {
            "#2196F3", "#FF5722", "#4CAF50", "#FF9800", "#9C27B0", "#795548",
            "#E91E63", "#00BCD4", "#CDDC39", "#FFC107", "#607D8B", "#3F51B5"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_editar_disciplina);

        // Obter o ID do usuário logado
        usuarioId = new PreferenciasApp(this).getUsuarioId();
        Log.d(TAG, "Usuario ID ao criar disciplina: " + usuarioId);

        inicializarComponentes();
        configurarToolbar();
        configurarBotoes();
        configurarSeletorCores();
        carregarDisciplinaParaEdicao();
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbar);
        inputLayoutNome = findViewById(R.id.inputLayoutNome);
        inputLayoutCodigo = findViewById(R.id.inputLayoutCodigo);
        editNomeDisciplina = findViewById(R.id.editNomeDisciplina);
        editCodigoDisciplina = findViewById(R.id.editCodigoDisciplina);
        gridCores = findViewById(R.id.gridCores);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);

        disciplinaDAO = AppDatabase.getInstance(this).disciplinaDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void configurarBotoes() {
        btnCancelar.setOnClickListener(v -> finish());

        btnSalvar.setOnClickListener(v -> salvarDisciplina());
    }

    private void configurarSeletorCores() {
        int tamanhoBotao = (int) (getResources().getDisplayMetrics().density * 48);
        int margem = (int) (getResources().getDisplayMetrics().density * 8);

        for (String cor : coresDisponiveis) {
            View botaoCor = new View(this);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = tamanhoBotao;
            params.height = tamanhoBotao;
            params.setMargins(margem, margem, margem, margem);

            botaoCor.setLayoutParams(params);
            botaoCor.setBackgroundColor(Color.parseColor(cor));

            // Adicionar borda se for a cor selecionada
            if (cor.equals(corSelecionada)) {
                botaoCor.setElevation(8);
                botaoCor.setScaleX(1.2f);
                botaoCor.setScaleY(1.2f);
            }

            botaoCor.setOnClickListener(v -> {
                corSelecionada = cor;
                atualizarSeletorCores();
            });

            gridCores.addView(botaoCor);
        }
    }

    private void atualizarSeletorCores() {
        for (int i = 0; i < gridCores.getChildCount(); i++) {
            View botaoCor = gridCores.getChildAt(i);
            String cor = coresDisponiveis[i];

            if (cor.equals(corSelecionada)) {
                botaoCor.setElevation(8);
                botaoCor.setScaleX(1.2f);
                botaoCor.setScaleY(1.2f);
            } else {
                botaoCor.setElevation(0);
                botaoCor.setScaleX(1.0f);
                botaoCor.setScaleY(1.0f);
            }
        }
    }

    private void carregarDisciplinaParaEdicao() {
        long disciplinaId = getIntent().getLongExtra("disciplina_id", -1);

        if (disciplinaId != -1) {
            executor.execute(() -> {
                disciplinaEditando = disciplinaDAO.obterPorId(disciplinaId);

                runOnUiThread(() -> {
                    if (disciplinaEditando != null) {
                        setTitle(R.string.edit_subject);
                        editNomeDisciplina.setText(disciplinaEditando.getNome());
                        editCodigoDisciplina.setText(disciplinaEditando.getCodigo());
                        corSelecionada = disciplinaEditando.getCor();
                        atualizarSeletorCores();
                    }
                });
            });
        } else {
            setTitle(R.string.add_subject);
        }
    }

    private void salvarDisciplina() {
        // Limpar erros anteriores
        inputLayoutNome.setError(null);
        inputLayoutCodigo.setError(null);

        String nome = editNomeDisciplina.getText().toString().trim();
        String codigo = editCodigoDisciplina.getText().toString().trim().toUpperCase();

        // Validações
        if (nome.isEmpty()) {
            inputLayoutNome.setError(getString(R.string.error_required_field));
            editNomeDisciplina.requestFocus();
            return;
        }

        if (codigo.isEmpty()) {
            inputLayoutCodigo.setError(getString(R.string.error_required_field));
            editCodigoDisciplina.requestFocus();
            return;
        }

        if (codigo.length() < 2) {
            inputLayoutCodigo.setError(getString(R.string.error_code_too_short));
            editCodigoDisciplina.requestFocus();
            return;
        }

        // Verificar se código já existe e salvar em background thread
        executor.execute(() -> {
            try {
                long idExcluir = disciplinaEditando != null ? disciplinaEditando.getId() : -1;
                int count = disciplinaDAO.verificarCodigoExiste(codigo, idExcluir, usuarioId);
                boolean codigoExiste = count > 0;

                if (codigoExiste) {
                    runOnUiThread(() -> {
                        inputLayoutCodigo.setError(getString(R.string.error_code_exists));
                        editCodigoDisciplina.requestFocus();
                    });
                    return;
                }

                // Salvar ou atualizar
                if (disciplinaEditando == null) {
                    // Adicionar nova disciplina
                    Log.d(TAG, "Criando disciplina: " + nome + ", " + codigo + ", usuario: " + usuarioId);

                    Disciplina novaDisciplina = new Disciplina(nome, codigo, corSelecionada);
                    novaDisciplina.setUsuarioId(usuarioId);
                    novaDisciplina.setDataCriacao(System.currentTimeMillis());

                    long id = disciplinaDAO.inserir(novaDisciplina);
                    Log.d(TAG, "ID retornado ao adicionar: " + id);

                    runOnUiThread(() -> {
                        if (id != -1) {
                            Toast.makeText(this, R.string.success_subject_added, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Atualizar disciplina existente
                    disciplinaEditando.setNome(nome);
                    disciplinaEditando.setCodigo(codigo);
                    disciplinaEditando.setCor(corSelecionada);

                    int linhas = disciplinaDAO.atualizar(disciplinaEditando);

                    runOnUiThread(() -> {
                        if (linhas > 0) {
                            Toast.makeText(this, R.string.success_subject_updated, Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao salvar disciplina: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
