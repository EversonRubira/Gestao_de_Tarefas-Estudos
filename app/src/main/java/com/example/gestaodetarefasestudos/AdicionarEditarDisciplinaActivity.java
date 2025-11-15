package com.example.gestaodetarefasestudos;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AdicionarEditarDisciplinaActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutNome, inputLayoutCodigo;
    private TextInputEditText editNomeDisciplina, editCodigoDisciplina;
    private GridLayout gridCores;
    private MaterialButton btnSalvar, btnCancelar;

    private DisciplinaDAO disciplinaDAO;
    private Disciplina disciplinaEditando;
    private String corSelecionada = "#2196F3"; // cor padrão

    private final String[] coresDisponiveis = {
            "#2196F3", "#FF5722", "#4CAF50", "#FF9800", "#9C27B0", "#795548",
            "#E91E63", "#00BCD4", "#CDDC39", "#FFC107", "#607D8B", "#3F51B5"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_editar_disciplina);

        inicializarComponentes();
        configurarBotoes();
        configurarSeletorCores();
        carregarDisciplinaParaEdicao();
    }

    private void inicializarComponentes() {
        inputLayoutNome = findViewById(R.id.inputLayoutNome);
        inputLayoutCodigo = findViewById(R.id.inputLayoutCodigo);
        editNomeDisciplina = findViewById(R.id.editNomeDisciplina);
        editCodigoDisciplina = findViewById(R.id.editCodigoDisciplina);
        gridCores = findViewById(R.id.gridCores);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);

        disciplinaDAO = new DisciplinaDAO(this);
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
            disciplinaEditando = disciplinaDAO.obterPorId(disciplinaId);

            if (disciplinaEditando != null) {
                setTitle(R.string.edit_subject);
                editNomeDisciplina.setText(disciplinaEditando.getNome());
                editCodigoDisciplina.setText(disciplinaEditando.getCodigo());
                corSelecionada = disciplinaEditando.getCor();
                atualizarSeletorCores();
            }
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

        // Verificar se código já existe
        long idExcluir = disciplinaEditando != null ? disciplinaEditando.getId() : -1;
        if (disciplinaDAO.codigoJaExiste(codigo, idExcluir)) {
            inputLayoutCodigo.setError(getString(R.string.error_code_exists));
            editCodigoDisciplina.requestFocus();
            return;
        }

        // Salvar ou atualizar
        if (disciplinaEditando == null) {
            // Adicionar nova disciplina
            Disciplina novaDisciplina = new Disciplina(nome, codigo, corSelecionada);
            long id = disciplinaDAO.adicionar(novaDisciplina);

            if (id != -1) {
                Toast.makeText(this, R.string.success_subject_added, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Atualizar disciplina existente
            disciplinaEditando.setNome(nome);
            disciplinaEditando.setCodigo(codigo);
            disciplinaEditando.setCor(corSelecionada);

            int linhas = disciplinaDAO.atualizar(disciplinaEditando);

            if (linhas > 0) {
                Toast.makeText(this, R.string.success_subject_updated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
