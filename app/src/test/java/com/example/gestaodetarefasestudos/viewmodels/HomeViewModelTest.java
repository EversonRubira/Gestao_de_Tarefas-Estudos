package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.gestaodetarefasestudos.models.DiaCalendario;
import com.example.gestaodetarefasestudos.repositories.HomeRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Testes unitarios do HomeViewModel.
 * Verifica navegacao do calendario e carregamento de estatisticas.
 */
public class HomeViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private HomeRepository mockRepository;

    private HomeViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simular respostas do repositorio para evitar NullPointer
        doAnswer(inv -> {
            HomeRepository.Callback<HomeRepository.DashboardStats> cb = inv.getArgument(1);
            cb.onResult(new HomeRepository.DashboardStats(3, 5));
            return null;
        }).when(mockRepository).obterEstatisticas(anyLong(), any());

        doAnswer(inv -> {
            HomeRepository.Callback<android.database.Cursor> cb = inv.getArgument(1);
            cb.onResult(null);
            return null;
        }).when(mockRepository).obterTempoEstudoHoje(anyLong(), any());

        doAnswer(inv -> {
            HomeRepository.Callback<android.database.Cursor> cb = inv.getArgument(2);
            cb.onResult(null);
            return null;
        }).when(mockRepository).obterTarefasCalendario(anyLong(), anyLong(), any());

        Application mockApp = mock(Application.class);
        viewModel = new HomeViewModel(mockApp, mockRepository);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO INICIAL
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void estadoInicial_totalDisciplinasDeveSerZero() {
        assertEquals(Integer.valueOf(0), viewModel.getTotalDisciplinas().getValue());
    }

    @Test
    public void estadoInicial_tarefasPendentesDeveSerZero() {
        assertEquals(Integer.valueOf(0), viewModel.getTarefasPendentes().getValue());
    }

    @Test
    public void estadoInicial_calendarioNaoDeveSerNull() {
        assertNotNull(viewModel.getCalendarioAtual());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NAVEGACAO DO CALENDARIO
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void mesAnterior_deveRetrocederUmMes() {
        Calendar antes = (Calendar) viewModel.getCalendarioAtual().clone();
        int mesEsperado = antes.get(Calendar.MONTH) - 1;
        if (mesEsperado < 0) mesEsperado = 11; // Dezembro

        viewModel.mesAnterior();

        assertEquals(mesEsperado, viewModel.getCalendarioAtual().get(Calendar.MONTH));
    }

    @Test
    public void proximoMes_deveAvancaUmMes() {
        Calendar antes = (Calendar) viewModel.getCalendarioAtual().clone();
        int mesEsperado = (antes.get(Calendar.MONTH) + 1) % 12;

        viewModel.proximoMes();

        assertEquals(mesEsperado, viewModel.getCalendarioAtual().get(Calendar.MONTH));
    }

    @Test
    public void mesAnterior_seguidoDeProximoMes_deveVoltarAoMesOriginal() {
        int mesOriginal = viewModel.getCalendarioAtual().get(Calendar.MONTH);

        viewModel.mesAnterior();
        viewModel.proximoMes();

        assertEquals(mesOriginal, viewModel.getCalendarioAtual().get(Calendar.MONTH));
    }

    @Test
    public void proximoMes_dozeVezes_deveVoltarAoMesmoMes() {
        int mesOriginal = viewModel.getCalendarioAtual().get(Calendar.MONTH);
        int anoOriginal = viewModel.getCalendarioAtual().get(Calendar.YEAR);

        for (int i = 0; i < 12; i++) {
            viewModel.proximoMes();
        }

        assertEquals(mesOriginal, viewModel.getCalendarioAtual().get(Calendar.MONTH));
        assertEquals(anoOriginal + 1, viewModel.getCalendarioAtual().get(Calendar.YEAR));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CARREGAMENTO DE ESTATISTICAS
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void carregarEstatisticas_deveAtualizarTotalDisciplinas() {
        viewModel.setUsuarioId(1L);
        viewModel.carregarEstatisticas();

        assertEquals(Integer.valueOf(3), viewModel.getTotalDisciplinas().getValue());
    }

    @Test
    public void carregarEstatisticas_deveAtualizarTarefasPendentes() {
        viewModel.setUsuarioId(1L);
        viewModel.carregarEstatisticas();

        assertEquals(Integer.valueOf(5), viewModel.getTarefasPendentes().getValue());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // TAREFAS DO DIA
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void carregarTarefasDoDia_deveChamarRepositorio() {
        doAnswer(inv -> {
            HomeRepository.Callback<java.util.List<com.example.gestaodetarefasestudos.models.Tarefa>> cb = inv.getArgument(2);
            cb.onResult(java.util.Collections.emptyList());
            return null;
        }).when(mockRepository).obterTarefasDoDia(anyLong(), anyLong(), any());

        DiaCalendario dia = new DiaCalendario(15, System.currentTimeMillis());
        viewModel.carregarTarefasDoDia(dia);

        verify(mockRepository).obterTarefasDoDia(anyLong(), anyLong(), any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CALENDARIO — GERACAO DE DIAS
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void carregarCalendario_deveGerarDiasDoMes() {
        viewModel.carregarCalendario();

        java.util.List<DiaCalendario> dias = viewModel.getDiasCalendario().getValue();
        assertNotNull(dias);
        // Um mes tem entre 28 e 31 dias + possivel padding inicial (0-6 dias vazios)
        // Total minimo: 28, maximo: 31 + 6 = 37
        assertTrue(dias.size() >= 28);
        assertTrue(dias.size() <= 37);
    }

    @Test
    public void carregarCalendario_deveMarcarDiaAtualCorreto() {
        viewModel.carregarCalendario();

        java.util.List<DiaCalendario> dias = viewModel.getDiasCalendario().getValue();
        assertNotNull(dias);

        // Deve existir exatamente um dia marcado como atual (se estamos no mes atual)
        int diasAtuais = 0;
        for (DiaCalendario dia : dias) {
            if (dia.isDiaAtual()) diasAtuais++;
        }

        // No mes atual, deve ter exatamente 1 dia atual
        assertEquals(1, diasAtuais);
    }
}
