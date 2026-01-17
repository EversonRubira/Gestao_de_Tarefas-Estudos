package com.example.gestaodetarefasestudos.fragments;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.gestaodetarefasestudos.PreferenciasApp;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.viewmodels.TimerViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

/**
 * Fragment para o Timer Pomodoro.
 *
 * Usa MVVM:
 * - View (este Fragment): Exibe UI, observa LiveData
 * - ViewModel (TimerViewModel): Gerencia timer, estado, logica de negocio
 * - Model (Repository/DAO): Acesso a dados
 */
public class TimerFragment extends Fragment {

    // ViewModel
    private TimerViewModel viewModel;

    // UI Components
    private TextView textoTimer;
    private TextView textoEstadoTimer;
    private MaterialButton botaoIniciar;
    private MaterialButton botaoParar;
    private MaterialAutoCompleteTextView spinnerDisciplinaTimer;
    private TextInputLayout inputLayoutDisciplinaTimer;
    private ProgressBar progressTimer;

    // Cycle indicators
    private View[] indicadoresCiclo = new View[4];

    // Statistics
    private TextView textoSessoesHoje;
    private TextView textoTempoHoje;
    private TextView textoStreak;

    // Adapter
    private List<Disciplina> listaDisciplinas;

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

        inicializarViewModel();
        inicializarComponentes(view);
        configurarBotoes();
        observarViewModel();

        // Carregar dados iniciais
        long usuarioId = new PreferenciasApp(requireContext()).getUsuarioId();
        viewModel.setUsuarioId(usuarioId);
        viewModel.carregarDisciplinas();
        viewModel.carregarEstatisticas();
    }

    /**
     * Inicializa o ViewModel usando ViewModelProvider.
     * Usa requireActivity() para compartilhar o ViewModel com a Activity,
     * garantindo que o timer sobreviva a troca de abas.
     */
    private void inicializarViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
    }

    private void inicializarComponentes(View view) {
        textoTimer = view.findViewById(R.id.tv_timer);
        textoEstadoTimer = view.findViewById(R.id.tv_timer_status);
        botaoIniciar = view.findViewById(R.id.btn_start);
        botaoParar = view.findViewById(R.id.btn_stop);
        spinnerDisciplinaTimer = view.findViewById(R.id.spinnerDisciplinaTimer);
        inputLayoutDisciplinaTimer = view.findViewById(R.id.inputLayoutDisciplinaTimer);
        progressTimer = view.findViewById(R.id.progress_timer);

        // Indicadores de ciclo
        indicadoresCiclo[0] = view.findViewById(R.id.indicator_cycle_1);
        indicadoresCiclo[1] = view.findViewById(R.id.indicator_cycle_2);
        indicadoresCiclo[2] = view.findViewById(R.id.indicator_cycle_3);
        indicadoresCiclo[3] = view.findViewById(R.id.indicator_cycle_4);

        // Estatisticas
        textoSessoesHoje = view.findViewById(R.id.tv_sessions_today);
        textoTempoHoje = view.findViewById(R.id.tv_time_today);
        textoStreak = view.findViewById(R.id.tv_streak);
    }

    private void configurarBotoes() {
        botaoIniciar.setOnClickListener(v -> viewModel.iniciar());
        botaoParar.setOnClickListener(v -> viewModel.parar());
    }

    /**
     * Observa as mudancas no ViewModel e atualiza a UI
     */
    private void observarViewModel() {
        // Observar tempo formatado
        viewModel.getTempoFormatado().observe(getViewLifecycleOwner(), tempo -> {
            textoTimer.setText(tempo);
        });

        // Observar progresso
        viewModel.getProgresso().observe(getViewLifecycleOwner(), prog -> {
            progressTimer.setProgress(prog);
        });

        // Observar estado do timer
        viewModel.getTimerState().observe(getViewLifecycleOwner(), state -> {
            atualizarBotaoIniciar(state);
            atualizarSpinner(state);
        });

        // Observar tipo de sessao
        viewModel.getSessionType().observe(getViewLifecycleOwner(), type -> {
            atualizarTextoEstado(type);
        });

        // Observar contador de ciclos
        viewModel.getContadorCiclos().observe(getViewLifecycleOwner(), ciclos -> {
            atualizarIndicadoresCiclo(ciclos);
        });

        // Observar disciplinas
        viewModel.getDisciplinas().observe(getViewLifecycleOwner(), disciplinas -> {
            configurarSpinnerDisciplinas(disciplinas);
        });

        // Observar estatisticas
        viewModel.getSessoesHoje().observe(getViewLifecycleOwner(), sessoes -> {
            textoSessoesHoje.setText(String.valueOf(sessoes));
        });

        viewModel.getTempoHoje().observe(getViewLifecycleOwner(), tempo -> {
            textoTempoHoje.setText(tempo);
        });

        viewModel.getStreak().observe(getViewLifecycleOwner(), streakValue -> {
            textoStreak.setText(String.valueOf(streakValue));
        });

        // Observar mensagens de evento
        viewModel.getMensagemEvento().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                processarMensagemEvento(msg);
                viewModel.clearMensagemEvento();
            }
        });

        // Observar sessao salva
        viewModel.getSessaoSalva().observe(getViewLifecycleOwner(), salva -> {
            if (salva != null && salva) {
                Toast.makeText(requireContext(),
                        getString(R.string.timer_session_saved),
                        Toast.LENGTH_SHORT).show();
                viewModel.clearSessaoSalva();
            }
        });
    }

    private void configurarSpinnerDisciplinas(List<Disciplina> disciplinas) {
        listaDisciplinas = disciplinas;

        if (disciplinas == null || disciplinas.isEmpty()) {
            botaoIniciar.setEnabled(false);
            spinnerDisciplinaTimer.setEnabled(false);
            Toast.makeText(requireContext(),
                    getString(R.string.no_subjects_for_timer),
                    Toast.LENGTH_LONG).show();
            return;
        }

        botaoIniciar.setEnabled(true);
        spinnerDisciplinaTimer.setEnabled(true);

        String[] nomesDisciplinas = new String[disciplinas.size()];
        for (int i = 0; i < disciplinas.size(); i++) {
            nomesDisciplinas[i] = disciplinas.get(i).toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, nomesDisciplinas);
        spinnerDisciplinaTimer.setAdapter(adapter);

        spinnerDisciplinaTimer.setOnClickListener(v -> spinnerDisciplinaTimer.showDropDown());

        spinnerDisciplinaTimer.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.selecionarDisciplina(disciplinas.get(position));
            inputLayoutDisciplinaTimer.setError(null);
        });

        // Restaurar selecao se existir
        Disciplina selecionada = viewModel.getDisciplinaSelecionada().getValue();
        if (selecionada != null) {
            spinnerDisciplinaTimer.setText(selecionada.toString(), false);
        }
    }

    private void atualizarBotaoIniciar(TimerViewModel.TimerState state) {
        switch (state) {
            case RODANDO:
                botaoIniciar.setText(R.string.timer_pause);
                break;
            case PAUSADO:
                botaoIniciar.setText(R.string.timer_resume);
                break;
            case PARADO:
            default:
                botaoIniciar.setText(R.string.timer_start);
                break;
        }
    }

    private void atualizarSpinner(TimerViewModel.TimerState state) {
        boolean habilitado = state != TimerViewModel.TimerState.RODANDO;
        spinnerDisciplinaTimer.setEnabled(habilitado && listaDisciplinas != null && !listaDisciplinas.isEmpty());
    }

    private void atualizarTextoEstado(TimerViewModel.SessionType type) {
        switch (type) {
            case DESCANSO:
                textoEstadoTimer.setText(R.string.timer_break_session);
                break;
            case DESCANSO_LONGO:
                textoEstadoTimer.setText(R.string.timer_long_break_session);
                break;
            case TRABALHO:
            default:
                textoEstadoTimer.setText(R.string.timer_work_session);
                break;
        }
    }

    private void atualizarIndicadoresCiclo(int ciclos) {
        for (int i = 0; i < 4; i++) {
            if (indicadoresCiclo[i] != null) {
                if (i < ciclos) {
                    indicadoresCiclo[i].setBackgroundResource(R.drawable.circle_indicator_active);
                } else {
                    indicadoresCiclo[i].setBackgroundResource(R.drawable.circle_indicator_inactive);
                }
            }
        }
    }

    private void processarMensagemEvento(String msg) {
        switch (msg) {
            case "ERRO_SEM_DISCIPLINA":
                inputLayoutDisciplinaTimer.setError(getString(R.string.error_select_subject));
                break;
            case "TRABALHO_COMPLETO":
                Toast.makeText(requireContext(),
                        getString(R.string.timer_work_complete),
                        Toast.LENGTH_SHORT).show();
                animarProgressReset();
                break;
            case "DESCANSO_LONGO_INICIO":
                Toast.makeText(requireContext(),
                        getString(R.string.timer_long_break_start),
                        Toast.LENGTH_LONG).show();
                animarProgressReset();
                break;
            case "DESCANSO_COMPLETO":
                Toast.makeText(requireContext(),
                        getString(R.string.timer_break_complete),
                        Toast.LENGTH_SHORT).show();
                animarProgressReset();
                break;
            case "DESCANSO_LONGO_COMPLETO":
                Toast.makeText(requireContext(),
                        getString(R.string.timer_long_break_complete),
                        Toast.LENGTH_SHORT).show();
                animarProgressReset();
                break;
        }
    }

    private void animarProgressReset() {
        ObjectAnimator animator = ObjectAnimator.ofInt(progressTimer, "progress", 0, 100);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.carregarEstatisticas();
    }
}
