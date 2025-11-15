package com.example.gestaodetarefasestudos.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Locale;

public class TimerFragment extends Fragment {

    private TextView textoTimer;
    private TextView textoEstadoTimer;
    private MaterialButton botaoIniciar;
    private MaterialButton botaoParar;
    private EditText campoTrabalho;
    private EditText campoDescanso;
    private MaterialAutoCompleteTextView spinnerDisciplinaTimer;
    private TextInputLayout inputLayoutDisciplinaTimer;
    private View cardDisciplinaSelecionada;
    private TextView textoDisciplinaSelecionada;

    private CountDownTimer cronometro;
    private long tempoRestante; // em milissegundos
    private long duracaoTrabalho; // em milissegundos
    private long duracaoDescanso; // em milissegundos
    private boolean estaEmTrabalho = true;
    private boolean cronometroAtivo = false;
    private long tempoInicioSessao; // para calcular tempo total da sessão

    private SessaoEstudoDAO sessaoDAO;
    private DisciplinaDAO disciplinaDAO;
    private List<Disciplina> listaDisciplinas;
    private Disciplina disciplinaSelecionada;

    public TimerFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessaoDAO = new SessaoEstudoDAO(requireContext());
        disciplinaDAO = new DisciplinaDAO(requireContext());
        inicializarComponentes(view);
        carregarDisciplinas();
        configurarDuracoes();
        configurarBotoes();
    }

    private void inicializarComponentes(View view) {
        textoTimer = view.findViewById(R.id.tv_timer);
        textoEstadoTimer = view.findViewById(R.id.tv_timer_status);
        botaoIniciar = view.findViewById(R.id.btn_start);
        botaoParar = view.findViewById(R.id.btn_stop);
        campoTrabalho = view.findViewById(R.id.et_work_duration);
        campoDescanso = view.findViewById(R.id.et_break_duration);
        spinnerDisciplinaTimer = view.findViewById(R.id.spinnerDisciplinaTimer);
        inputLayoutDisciplinaTimer = view.findViewById(R.id.inputLayoutDisciplinaTimer);
        cardDisciplinaSelecionada = view.findViewById(R.id.card_selected_subject);
        textoDisciplinaSelecionada = view.findViewById(R.id.tv_selected_subject);
    }

    /**
     * Carrega as disciplinas disponíveis para o dropdown
     * O utilizador precisa selecionar para qual disciplina está a estudar
     */
    private void carregarDisciplinas() {
        // Buscar todas as disciplinas do banco
        listaDisciplinas = disciplinaDAO.obterTodas();

        // Verificar se existem disciplinas cadastradas
        if (listaDisciplinas.isEmpty()) {
            // Desabilitar o timer se não houver disciplinas
            botaoIniciar.setEnabled(false);
            spinnerDisciplinaTimer.setEnabled(false);
            Toast.makeText(requireContext(),
                    "Crie pelo menos uma disciplina para usar o timer",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Criar array com os nomes das disciplinas
        String[] nomesDisciplinas = new String[listaDisciplinas.size()];
        for (int i = 0; i < listaDisciplinas.size(); i++) {
            nomesDisciplinas[i] = listaDisciplinas.get(i).toString();
        }

        // Configurar o adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, nomesDisciplinas);
        spinnerDisciplinaTimer.setAdapter(adapter);

        // Mostrar dropdown ao clicar
        spinnerDisciplinaTimer.setOnClickListener(v -> {
            spinnerDisciplinaTimer.showDropDown();
        });

        // Capturar seleção
        spinnerDisciplinaTimer.setOnItemClickListener((parent, view, position, id) -> {
            disciplinaSelecionada = listaDisciplinas.get(position);
            inputLayoutDisciplinaTimer.setError(null); // Limpar erro

            // Mostrar indicador de disciplina selecionada
            if (disciplinaSelecionada != null) {
                textoDisciplinaSelecionada.setText(disciplinaSelecionada.getNome());
                cardDisciplinaSelecionada.setVisibility(View.VISIBLE);
            }
        });
    }

    private void configurarDuracoes() {
        // Configurar durações padrão (25 minutos trabalho, 5 minutos descanso)
        int minutosTrabalho = obterMinutosDoEditText(campoTrabalho, 25);
        int minutosDescanso = obterMinutosDoEditText(campoDescanso, 5);

        duracaoTrabalho = minutosTrabalho * 60 * 1000L;
        duracaoDescanso = minutosDescanso * 60 * 1000L;
        tempoRestante = duracaoTrabalho;

        atualizarDisplay();
    }

    private int obterMinutosDoEditText(EditText campo, int valorPadrao) {
        try {
            String texto = campo.getText().toString().trim();
            if (!texto.isEmpty()) {
                int valor = Integer.parseInt(texto);
                if (valor > 0 && valor <= 120) { // limite de 2 horas
                    return valor;
                }
            }
        } catch (NumberFormatException e) {
            // Retorna valor padrão se houver erro
        }
        campo.setText(String.valueOf(valorPadrao));
        return valorPadrao;
    }

    private void configurarBotoes() {
        botaoIniciar.setOnClickListener(v -> {
            if (cronometroAtivo) {
                pausarCronometro();
            } else {
                iniciarCronometro();
            }
        });

        botaoParar.setOnClickListener(v -> {
            pararCronometro();
        });

        // Desabilitar edição dos campos quando timer está ativo
        campoTrabalho.setEnabled(!cronometroAtivo);
        campoDescanso.setEnabled(!cronometroAtivo);
    }

    private void iniciarCronometro() {
        // VALIDAÇÃO: Verificar se selecionou uma disciplina antes de iniciar
        if (disciplinaSelecionada == null && estaEmTrabalho) {
            inputLayoutDisciplinaTimer.setError("Selecione uma disciplina primeiro");
            Toast.makeText(requireContext(),
                    "Escolha a disciplina que vai estudar",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Se for um novo início, atualizar as durações
        if (!cronometroAtivo && tempoRestante == (estaEmTrabalho ? duracaoTrabalho : duracaoDescanso)) {
            configurarDuracoes();
        }

        cronometroAtivo = true;
        campoTrabalho.setEnabled(false);
        campoDescanso.setEnabled(false);
        spinnerDisciplinaTimer.setEnabled(false); // Bloquear mudança de disciplina durante timer

        // Marcar início da sessão de trabalho
        if (estaEmTrabalho && tempoRestante == duracaoTrabalho) {
            tempoInicioSessao = System.currentTimeMillis();
        }

        cronometro = new CountDownTimer(tempoRestante, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tempoRestante = millisUntilFinished;
                atualizarDisplay();
            }

            @Override
            public void onFinish() {
                tempoRestante = 0;
                atualizarDisplay();
                cronometroAtivo = false;

                if (estaEmTrabalho) {
                    // Sessão de trabalho completa - salvar no banco
                    salvarSessaoEstudo();

                    // Mudar para descanso
                    estaEmTrabalho = false;
                    tempoRestante = duracaoDescanso;
                    textoEstadoTimer.setText(R.string.timer_break_session);

                    Toast.makeText(requireContext(),
                        getString(R.string.timer_work_complete),
                        Toast.LENGTH_LONG).show();
                } else {
                    // Descanso completo - voltar para trabalho
                    estaEmTrabalho = true;
                    tempoRestante = duracaoTrabalho;
                    textoEstadoTimer.setText(R.string.timer_work_session);

                    Toast.makeText(requireContext(),
                        getString(R.string.timer_break_complete),
                        Toast.LENGTH_SHORT).show();
                }

                atualizarDisplay();
                botaoIniciar.setText(R.string.timer_start);
                campoTrabalho.setEnabled(true);
                campoDescanso.setEnabled(true);
                spinnerDisciplinaTimer.setEnabled(true); // Reabilitar seleção
            }
        }.start();

        botaoIniciar.setText(R.string.timer_pause);
    }

    private void pausarCronometro() {
        if (cronometro != null) {
            cronometro.cancel();
        }
        cronometroAtivo = false;
        botaoIniciar.setText(R.string.timer_resume);
    }

    private void pararCronometro() {
        if (cronometro != null) {
            cronometro.cancel();
        }

        // NOVO: Salvar tempo parcial se estava em sessão de trabalho
        if (estaEmTrabalho && cronometroAtivo && disciplinaSelecionada != null) {
            // Calcular quanto tempo estudou
            long tempoFim = System.currentTimeMillis();
            long duracaoEstudada = (tempoFim - tempoInicioSessao) / 1000; // em segundos

            // Salvar se estudou pelo menos 10 segundos (evitar salvamentos acidentais)
            if (duracaoEstudada >= 10) {
                SessaoEstudo sessao = new SessaoEstudo(disciplinaSelecionada.getId(), duracaoEstudada);
                long id = sessaoDAO.adicionar(sessao);

                if (id > 0) {
                    String duracao = formatarDuracao(duracaoEstudada);
                    Toast.makeText(requireContext(),
                        "Sessão parcial salva: " + duracao,
                        Toast.LENGTH_SHORT).show();
                }
            }
        }

        cronometroAtivo = false;
        estaEmTrabalho = true;
        tempoRestante = duracaoTrabalho;
        textoEstadoTimer.setText(R.string.timer_work_session);
        atualizarDisplay();
        botaoIniciar.setText(R.string.timer_start);
        campoTrabalho.setEnabled(true);
        campoDescanso.setEnabled(true);
        spinnerDisciplinaTimer.setEnabled(true); // Reabilitar seleção
    }

    private void atualizarDisplay() {
        int minutos = (int) (tempoRestante / 1000) / 60;
        int segundos = (int) (tempoRestante / 1000) % 60;
        String tempoFormatado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
        textoTimer.setText(tempoFormatado);
    }

    /**
     * Salva a sessão de estudo no banco de dados
     * Calcula quanto tempo o utilizador realmente estudou
     * AGORA associa à disciplina selecionada!
     */
    private void salvarSessaoEstudo() {
        // Calcular duração real da sessão em segundos
        long tempoFim = System.currentTimeMillis();
        long duracaoReal = (tempoFim - tempoInicioSessao) / 1000;

        // Verificar se há disciplina selecionada (segurança)
        long disciplinaId = (disciplinaSelecionada != null) ? disciplinaSelecionada.getId() : 0;

        // Criar e salvar sessão COM A DISCIPLINA SELECIONADA
        SessaoEstudo sessao = new SessaoEstudo(disciplinaId, duracaoReal);
        long id = sessaoDAO.adicionar(sessao);

        // Verificar se foi salvo com sucesso
        if (id > 0) {
            String duracao = formatarDuracao(duracaoReal);
            String nomeDisciplina = (disciplinaSelecionada != null) ?
                disciplinaSelecionada.getNome() : "Geral";

            Toast.makeText(requireContext(),
                nomeDisciplina + " - " + getString(R.string.timer_session_saved) + ": " + duracao,
                Toast.LENGTH_SHORT).show();
        }
    }

    private String formatarDuracao(long segundos) {
        long horas = segundos / 3600;
        long minutos = (segundos % 3600) / 60;

        if (horas > 0) {
            return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
        } else {
            return String.format(Locale.getDefault(), "%dm", minutos);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cronometro != null) {
            cronometro.cancel();
        }
    }
}
