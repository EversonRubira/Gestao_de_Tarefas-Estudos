package com.example.gestaodetarefasestudos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.adapters.TarefaAdapter;
import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Activity que exibe detalhes completos de uma disciplina
 * Mostra: nome, código, cor, tarefas associadas e estatísticas de estudo
 */
public class DetalhesDisciplinaActivity extends AppCompatActivity {

    public static final String EXTRA_DISCIPLINA_ID = "disciplina_id";

    private Toolbar toolbar;
    private CardView cardInfo;
    private TextView tvNomeDisciplina;
    private TextView tvCodigoDisciplina;
    private View viewCorDisciplina;
    private TextView tvTotalTarefas;
    private TextView tvTarefasPendentes;
    private TextView tvTarefasConcluidas;
    private TextView tvTempoEstudo;
    private RecyclerView recyclerViewTarefas;
    private TextView tvEmptyTarefas;

    private long disciplinaId;
    private Disciplina disciplina;
    private DisciplinaRoomDAO disciplinaDAO;
    private TarefaRoomDAO tarefaDAO;
    private SessaoEstudoRoomDAO sessaoEstudoDAO;
    private TarefaAdapter tarefaAdapter;
    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_disciplina);

        // Obter ID da disciplina passado pela intent
        disciplinaId = getIntent().getLongExtra(EXTRA_DISCIPLINA_ID, -1);
        if (disciplinaId == -1) {
            Toast.makeText(this, R.string.error_loading_subject, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarComponentes();
        configurarToolbar();
        inicializarDAOs();
        carregarDadosDisciplina();
        carregarEstatisticas();
        carregarTarefas();
    }

    private void inicializarComponentes() {
        toolbar = findViewById(R.id.toolbar);
        cardInfo = findViewById(R.id.card_info);
        tvNomeDisciplina = findViewById(R.id.tv_nome_disciplina);
        tvCodigoDisciplina = findViewById(R.id.tv_codigo_disciplina);
        viewCorDisciplina = findViewById(R.id.view_cor_disciplina);
        tvTotalTarefas = findViewById(R.id.tv_total_tarefas);
        tvTarefasPendentes = findViewById(R.id.tv_tarefas_pendentes);
        tvTarefasConcluidas = findViewById(R.id.tv_tarefas_concluidas);
        tvTempoEstudo = findViewById(R.id.tv_tempo_estudo);
        recyclerViewTarefas = findViewById(R.id.recycler_tarefas);
        tvEmptyTarefas = findViewById(R.id.tv_empty_tarefas);
    }

    private void configurarToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.subject_details_title);
        }
    }

    private void inicializarDAOs() {
        disciplinaDAO = AppDatabase.getInstance(this).disciplinaDAO();
        tarefaDAO = AppDatabase.getInstance(this).tarefaDAO();
        sessaoEstudoDAO = AppDatabase.getInstance(this).sessaoEstudoDAO();
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Carrega os dados da disciplina do banco de dados
     */
    private void carregarDadosDisciplina() {
        executor.execute(() -> {
            disciplina = disciplinaDAO.obterPorId(disciplinaId);

            runOnUiThread(() -> {
                if (disciplina == null) {
                    Toast.makeText(this, R.string.error_loading_subject, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Exibir informações da disciplina
                tvNomeDisciplina.setText(disciplina.getNome());
                tvCodigoDisciplina.setText(disciplina.getCodigo());

                // Definir a cor da disciplina
                try {
                    int cor = Color.parseColor(disciplina.getCor());
                    viewCorDisciplina.setBackgroundColor(cor);
                    cardInfo.setCardBackgroundColor(cor);
                } catch (Exception e) {
                    viewCorDisciplina.setBackgroundColor(Color.parseColor("#00897B"));
                }
            });
        });
    }

    /**
     * Carrega e exibe as estatísticas da disciplina
     */
    private void carregarEstatisticas() {
        executor.execute(() -> {
            // Total de tarefas
            int totalTarefas = tarefaDAO.contarTarefasPorDisciplina(disciplinaId);

            // Tarefas pendentes (PENDENTE + EM_PROGRESSO)
            int tarefasPendentes = tarefaDAO.contarTarefasPendentesPorDisciplina(disciplinaId);

            // Tarefas concluídas
            int tarefasConcluidas = tarefaDAO.contarTarefasConcluidasPorDisciplina(disciplinaId);

            // Tempo total de estudo (em segundos, converter para minutos)
            long tempoEstudoSegundos = sessaoEstudoDAO.obterTempoEstudoDisciplina(disciplinaId);
            int tempoEstudoMinutos = (int) (tempoEstudoSegundos / 60);

            runOnUiThread(() -> {
                tvTotalTarefas.setText(String.valueOf(totalTarefas));
                tvTarefasPendentes.setText(String.valueOf(tarefasPendentes));
                tvTarefasConcluidas.setText(String.valueOf(tarefasConcluidas));
                tvTempoEstudo.setText(formatarTempo(tempoEstudoMinutos));
            });
        });
    }

    /**
     * Formata o tempo em minutos para formato legível
     */
    private String formatarTempo(int minutos) {
        if (minutos < 60) {
            return minutos + " min";
        } else {
            int horas = minutos / 60;
            int mins = minutos % 60;
            return horas + "h " + mins + "min";
        }
    }

    /**
     * Carrega a lista de tarefas associadas à disciplina
     */
    private void carregarTarefas() {
        executor.execute(() -> {
            List<Tarefa> tarefas = tarefaDAO.listarPorDisciplina(disciplinaId);

            runOnUiThread(() -> {
                if (tarefas.isEmpty()) {
                    tvEmptyTarefas.setVisibility(View.VISIBLE);
                    recyclerViewTarefas.setVisibility(View.GONE);
                } else {
                    tvEmptyTarefas.setVisibility(View.GONE);
                    recyclerViewTarefas.setVisibility(View.VISIBLE);

                    // Usar a interface OnTarefaChangedListener que já existe no TarefaAdapter
                    tarefaAdapter = new TarefaAdapter(this, tarefas, new TarefaAdapter.OnTarefaChangedListener() {
                        @Override
                        public void onTarefaChanged() {
                            // Atualizar estatísticas quando houver mudança nas tarefas
                            carregarEstatisticas();
                        }
                    });

                    recyclerViewTarefas.setLayoutManager(new LinearLayoutManager(this));
                    recyclerViewTarefas.setAdapter(tarefaAdapter);
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalhes_disciplina, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_edit) {
            abrirEditarDisciplina();
            return true;
        } else if (id == R.id.action_delete) {
            confirmarEliminarDisciplina();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirEditarDisciplina() {
        Intent intent = new Intent(this, AdicionarEditarDisciplinaActivity.class);
        intent.putExtra("disciplina_id", disciplinaId);
        startActivity(intent);
    }

    private void confirmarEliminarDisciplina() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_subject_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    executor.execute(() -> {
                        int linhasAfetadas = disciplinaDAO.deletarPorId(disciplinaId);

                        runOnUiThread(() -> {
                            if (linhasAfetadas > 0) {
                                Toast.makeText(this, R.string.success_subject_deleted, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, R.string.error_deleting, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar dados quando voltar de edição
        carregarDadosDisciplina();
        carregarEstatisticas();
        carregarTarefas();
    }
}
