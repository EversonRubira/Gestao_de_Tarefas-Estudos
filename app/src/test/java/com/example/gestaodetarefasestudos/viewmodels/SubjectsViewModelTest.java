package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.repositories.DisciplinaRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testes unitarios do SubjectsViewModel.
 * Verifica carregamento, exclusao e propagacao de erros.
 */
public class SubjectsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private DisciplinaRepository mockRepository;

    private SubjectsViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Application mockApp = mock(Application.class);
        viewModel = new SubjectsViewModel(mockApp, mockRepository);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ESTADO INICIAL
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void estadoInicial_disciplinasDeveSerListaVazia() {
        List<Disciplina> disciplinas = viewModel.getDisciplinas().getValue();
        assertNotNull(disciplinas);
        assertTrue(disciplinas.isEmpty());
    }

    @Test
    public void estadoInicial_isLoadingDeveFalse() {
        assertFalse(viewModel.getIsLoading().getValue());
    }

    @Test
    public void estadoInicial_errorMessageDeveSerNull() {
        assertNull(viewModel.getErrorMessage().getValue());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CARREGAMENTO
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void carregarDisciplinas_devePopularLiveData() {
        List<Disciplina> disciplinasMock = Arrays.asList(
                criarDisciplina(1L, "Matemática", "#FF5722"),
                criarDisciplina(2L, "Física", "#2196F3"),
                criarDisciplina(3L, "Química", "#4CAF50")
        );
        simularCarregamento(disciplinasMock);

        viewModel.carregarDisciplinas();

        List<Disciplina> resultado = viewModel.getDisciplinas().getValue();
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
    }

    @Test
    public void carregarDisciplinas_comListaVazia_deveRetornarListaVazia() {
        simularCarregamento(Collections.emptyList());

        viewModel.carregarDisciplinas();

        List<Disciplina> resultado = viewModel.getDisciplinas().getValue();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void carregarDisciplinas_comResultadoNull_deveRetornarListaVazia() {
        doAnswer(inv -> {
            DisciplinaRepository.Callback<List<Disciplina>> cb = inv.getArgument(1);
            cb.onResult(null);
            return null;
        }).when(mockRepository).obterTodas(anyLong(), any());

        viewModel.carregarDisciplinas();

        List<Disciplina> resultado = viewModel.getDisciplinas().getValue();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void carregarDisciplinas_deveUsarUsuarioIdDefinido() {
        viewModel.setUsuarioId(42L);
        simularCarregamento(Collections.emptyList());

        viewModel.carregarDisciplinas();

        verify(mockRepository).obterTodas(anyLong(), any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXCLUSAO
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void deletarDisciplina_comSucesso_deveRecarregarLista() {
        Disciplina disciplina = criarDisciplina(1L, "Matemática", "#FF5722");
        simularCarregamento(Collections.emptyList());

        doAnswer(inv -> {
            DisciplinaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(1);
            return null;
        }).when(mockRepository).deletar(any(), any());

        viewModel.deletarDisciplina(disciplina);

        // Verificar que obterTodas foi chamado (para recarregar apos delete)
        verify(mockRepository).obterTodas(anyLong(), any());
    }

    @Test
    public void deletarDisciplina_comFalha_deveSetarMensagemDeErro() {
        Disciplina disciplina = criarDisciplina(1L, "Matemática", "#FF5722");

        doAnswer(inv -> {
            DisciplinaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(0); // 0 = nenhuma linha deletada = falha
            return null;
        }).when(mockRepository).deletar(any(), any());

        viewModel.deletarDisciplina(disciplina);

        assertNotNull(viewModel.getErrorMessage().getValue());
        assertFalse(viewModel.getErrorMessage().getValue().isEmpty());
    }

    @Test
    public void clearError_deveLimparMensagemDeErro() {
        Disciplina disciplina = criarDisciplina(1L, "Matemática", "#FF5722");
        doAnswer(inv -> {
            DisciplinaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(0);
            return null;
        }).when(mockRepository).deletar(any(), any());
        viewModel.deletarDisciplina(disciplina);
        assertNotNull(viewModel.getErrorMessage().getValue());

        viewModel.clearError();

        assertNull(viewModel.getErrorMessage().getValue());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // AUXILIARES
    // ═══════════════════════════════════════════════════════════════════════

    private Disciplina criarDisciplina(long id, String nome, String cor) {
        Disciplina d = new Disciplina(1L, nome, nome.substring(0, 3).toUpperCase(), cor);
        d.setId(id);
        return d;
    }

    private void simularCarregamento(List<Disciplina> disciplinas) {
        doAnswer(inv -> {
            DisciplinaRepository.Callback<List<Disciplina>> cb = inv.getArgument(1);
            cb.onResult(disciplinas);
            return null;
        }).when(mockRepository).obterTodas(anyLong(), any());
    }
}
