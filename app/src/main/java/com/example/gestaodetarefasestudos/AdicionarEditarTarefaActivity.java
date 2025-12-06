package com.example.gestaodetarefasestudos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdicionarEditarTarefaActivity extends AppCompatActivity {

    private TextInputLayout inputLayoutTitulo, inputLayoutDescricao, inputLayoutDisciplina, inputLayoutDataEntrega;
    private TextInputEditText editTituloTarefa, editDescricaoTarefa, editDataEntrega;
    private MaterialAutoCompleteTextView spinnerDisciplina;
    private RadioGroup radioGroupPrioridade;
    private MaterialButton btnSalvarTarefa, btnCancelarTarefa;

    private TarefaDAO tarefaDAO;
    private DisciplinaDAO disciplinaDAO;
    private Tarefa tarefaEditando;

    private List<Disciplina> listaDisciplinas;
    private Disciplina disciplinaSelecionada;
    private Calendar calendarioSelecionado;
    private SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_editar_tarefa);

        inicializarComponentes();
        carregarDisciplinas();
        configurarBotoes();
        configurarSeletorData();
        carregarTarefaParaEdicao();
    }

    private void inicializarComponentes() {
        inputLayoutTitulo = findViewById(R.id.inputLayoutTitulo);
        inputLayoutDescricao = findViewById(R.id.inputLayoutDescricao);
        inputLayoutDisciplina = findViewById(R.id.inputLayoutDisciplina);
        inputLayoutDataEntrega = findViewById(R.id.inputLayoutDataEntrega);

        editTituloTarefa = findViewById(R.id.editTituloTarefa);
        editDescricaoTarefa = findViewById(R.id.editDescricaoTarefa);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
        editDataEntrega = findViewById(R.id.editDataEntrega);

        radioGroupPrioridade = findViewById(R.id.radioGroupPrioridade);
        btnSalvarTarefa = findViewById(R.id.btnSalvarTarefa);
        btnCancelarTarefa = findViewById(R.id.btnCancelarTarefa);

        tarefaDAO = new TarefaDAO(this);
        disciplinaDAO = new DisciplinaDAO(this);

        calendarioSelecionado = Calendar.getInstance();
    }

    /**
     * Carrega as disciplinas do banco de dados para o dropdown
     * Se não houver disciplinas, mostra um diálogo perguntando se quer criar uma
     */
    private void carregarDisciplinas() {
        // Buscar todas as disciplinas do banco de dados
        listaDisciplinas = disciplinaDAO.obterTodas();

        // Verificar se não existem disciplinas cadastradas
        if (listaDisciplinas.isEmpty()) {
            // Em vez de fechar o app, mostrar um diálogo útil
            mostrarDialogoSemDisciplinas();
            return;
        }

        // Criar array com os nomes das disciplinas para mostrar no dropdown
        String[] nomesDisciplinas = new String[listaDisciplinas.size()];
        for (int i = 0; i < listaDisciplinas.size(); i++) {
            nomesDisciplinas[i] = listaDisciplinas.get(i).toString();
        }

        // Configurar o adapter do AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, nomesDisciplinas);
        spinnerDisciplina.setAdapter(adapter);

        // Mostrar dropdown ao clicar no campo
        spinnerDisciplina.setOnClickListener(v -> {
            spinnerDisciplina.showDropDown();
        });

        // Capturar seleção do utilizador
        spinnerDisciplina.setOnItemClickListener((parent, view, position, id) -> {
            disciplinaSelecionada = listaDisciplinas.get(position);
            inputLayoutDisciplina.setError(null); // Limpar erro ao selecionar
        });
    }

    /**
     * Mostra um diálogo quando não existem disciplinas
     * Oferece a opção de criar uma disciplina ou voltar
     */
    private void mostrarDialogoSemDisciplinas() {
        // Criar um AlertDialog para informar o utilizador
        new AlertDialog.Builder(this)
            .setTitle("Nenhuma Disciplina Encontrada")
            .setMessage("Para criar uma tarefa, primeiro precisa de criar uma disciplina.\n\nDeseja criar uma disciplina agora?")
            .setPositiveButton("Criar Disciplina", (dialog, which) -> {
                // Abrir a tela de criar disciplina
                Intent intent = new Intent(AdicionarEditarTarefaActivity.this,
                                          AdicionarEditarDisciplinaActivity.class);
                startActivityForResult(intent, 100);
            })
            .setNegativeButton("Voltar", (dialog, which) -> {
                // Fechar esta activity e voltar
                finish();
            })
            .setCancelable(false)
            .show();
    }

    /**
     * Método chamado quando volta de outra Activity
     * Usado aqui para recarregar as disciplinas depois de criar uma nova
     * @deprecated Este método está deprecated mas ainda funciona
     * TODO: No futuro, usar ActivityResultLauncher em vez de onActivityResult
     */
    @Deprecated
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Quando voltar da tela de criar disciplina
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Recarregar as disciplinas
            carregarDisciplinas();
        } else if (requestCode == 100) {
            // Se cancelou a criação da disciplina, voltar
            finish();
        }
    }

    private void configurarBotoes() {
        btnCancelarTarefa.setOnClickListener(v -> finish());

        btnSalvarTarefa.setOnClickListener(v -> salvarTarefa());
    }

    private void configurarSeletorData() {
        editDataEntrega.setOnClickListener(v -> mostrarSeletorData());
        inputLayoutDataEntrega.setEndIconOnClickListener(v -> mostrarSeletorData());
    }

    private void mostrarSeletorData() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, ano, mes, dia) -> {
                    calendarioSelecionado.set(Calendar.YEAR, ano);
                    calendarioSelecionado.set(Calendar.MONTH, mes);
                    calendarioSelecionado.set(Calendar.DAY_OF_MONTH, dia);

                    editDataEntrega.setText(formatoData.format(calendarioSelecionado.getTime()));
                },
                calendarioSelecionado.get(Calendar.YEAR),
                calendarioSelecionado.get(Calendar.MONTH),
                calendarioSelecionado.get(Calendar.DAY_OF_MONTH)
        );

        // Não permitir datas no passado
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void carregarTarefaParaEdicao() {
        long tarefaId = getIntent().getLongExtra("tarefa_id", -1);

        if (tarefaId != -1) {
            tarefaEditando = tarefaDAO.obterPorId(tarefaId);

            if (tarefaEditando != null) {
                setTitle(R.string.edit_task);

                editTituloTarefa.setText(tarefaEditando.getTitulo());
                editDescricaoTarefa.setText(tarefaEditando.getDescricao());

                // Selecionar disciplina
                for (int i = 0; i < listaDisciplinas.size(); i++) {
                    if (listaDisciplinas.get(i).getId() == tarefaEditando.getDisciplinaId()) {
                        spinnerDisciplina.setText(listaDisciplinas.get(i).toString(), false);
                        disciplinaSelecionada = listaDisciplinas.get(i);
                        break;
                    }
                }

                // Configurar data
                calendarioSelecionado.setTimeInMillis(tarefaEditando.getDataEntrega());
                editDataEntrega.setText(formatoData.format(calendarioSelecionado.getTime()));

                // Selecionar prioridade
                switch (tarefaEditando.getPrioridade()) {
                    case BAIXA:
                        radioGroupPrioridade.check(R.id.radioBaixa);
                        break;
                    case MEDIA:
                        radioGroupPrioridade.check(R.id.radioMedia);
                        break;
                    case ALTA:
                        radioGroupPrioridade.check(R.id.radioAlta);
                        break;
                }
            }
        } else {
            setTitle(R.string.add_task);
        }
    }

    private void salvarTarefa() {
        // Limpar erros anteriores
        inputLayoutTitulo.setError(null);
        inputLayoutDescricao.setError(null);
        inputLayoutDisciplina.setError(null);
        inputLayoutDataEntrega.setError(null);

        String titulo = editTituloTarefa.getText().toString().trim();
        String descricao = editDescricaoTarefa.getText().toString().trim();
        String dataTexto = editDataEntrega.getText().toString().trim();

        // Validações
        if (titulo.isEmpty()) {
            inputLayoutTitulo.setError(getString(R.string.error_required_field));
            editTituloTarefa.requestFocus();
            return;
        }

        if (disciplinaSelecionada == null) {
            inputLayoutDisciplina.setError(getString(R.string.error_select_subject));
            spinnerDisciplina.requestFocus();
            return;
        }

        if (dataTexto.isEmpty()) {
            inputLayoutDataEntrega.setError(getString(R.string.error_required_field));
            editDataEntrega.requestFocus();
            return;
        }

        // Obter prioridade selecionada
        Prioridade prioridade;
        int prioridadeId = radioGroupPrioridade.getCheckedRadioButtonId();
        if (prioridadeId == R.id.radioBaixa) {
            prioridade = Prioridade.BAIXA;
        } else if (prioridadeId == R.id.radioAlta) {
            prioridade = Prioridade.ALTA;
        } else {
            prioridade = Prioridade.MEDIA;
        }

        // Salvar ou atualizar
        if (tarefaEditando == null) {
            // Adicionar nova tarefa
            Tarefa novaTarefa = new Tarefa(
                    titulo,
                    descricao,
                    disciplinaSelecionada.getId(),
                    calendarioSelecionado.getTimeInMillis(),
                    prioridade
            );

            long id = tarefaDAO.adicionar(novaTarefa);

            if (id != -1) {
                Toast.makeText(this, R.string.success_task_added, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Atualizar tarefa existente
            tarefaEditando.setTitulo(titulo);
            tarefaEditando.setDescricao(descricao);
            tarefaEditando.setDisciplinaId(disciplinaSelecionada.getId());
            tarefaEditando.setDataEntrega(calendarioSelecionado.getTimeInMillis());
            tarefaEditando.setPrioridade(prioridade);

            int linhas = tarefaDAO.atualizar(tarefaEditando);

            if (linhas > 0) {
                Toast.makeText(this, R.string.success_task_updated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, R.string.error_saving, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
