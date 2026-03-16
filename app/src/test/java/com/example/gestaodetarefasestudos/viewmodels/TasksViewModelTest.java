package com.example.gestaodetarefasestudos.viewmodels;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.repositories.TarefaRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Testes unitarios do TasksViewModel.
 * Verifica filtros, ordenacao, busca por texto e operacoes CRUD.
 */
public class TasksViewModelTest {

    // Garante que LiveData execute de forma sincrona nos testes
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private TarefaRepository mockRepository;

    private TasksViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Application mockApp = mock(Application.class);
        viewModel = new TasksViewModel(mockApp, mockRepository);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FILTROS
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void filtroInicial_deveSerALL() {
        assertEquals(TasksViewModel.FilterType.ALL, viewModel.getCurrentFilter());
    }

    @Test
    public void setFilter_deveAtualizarFiltroAtual() {
        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(Collections.emptyList());
            return null;
        }).when(mockRepository).obterTarefasHoje(any());

        viewModel.setFilter(TasksViewModel.FilterType.TODAY);

        assertEquals(TasksViewModel.FilterType.TODAY, viewModel.getCurrentFilter());
    }

    @Test
    public void setFilter_comMesmoFiltro_naoDeveRecarregar() {
        // Filtro inicial ja e ALL, setar ALL novamente nao deve chamar repositorio
        viewModel.setFilter(TasksViewModel.FilterType.ALL);

        // Nenhuma chamada ao repositorio esperada (setFilter so recarrega se filtro mudar)
        // O teste verifica que o filtro permanece ALL sem erro
        assertEquals(TasksViewModel.FilterType.ALL, viewModel.getCurrentFilter());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ORDENACAO
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void ordenacaoInicial_deveSerDATE() {
        assertEquals(TasksViewModel.SortType.DATE, viewModel.getCurrentSort());
    }

    @Test
    public void setSort_PRIORITY_deveChamarObterPorPrioridade() {
        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(Collections.emptyList());
            return null;
        }).when(mockRepository).obterPorPrioridade(any());

        viewModel.setSort(TasksViewModel.SortType.PRIORITY);

        verify(mockRepository).obterPorPrioridade(any());
        assertEquals(TasksViewModel.SortType.PRIORITY, viewModel.getCurrentSort());
    }

    @Test
    public void setSort_SUBJECT_deveChamarObterPorDisciplina() {
        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(Collections.emptyList());
            return null;
        }).when(mockRepository).obterPorDisciplina(any());

        viewModel.setSort(TasksViewModel.SortType.SUBJECT);

        verify(mockRepository).obterPorDisciplina(any());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BUSCA POR TEXTO (filtragem em memória)
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void setSearchQuery_comTextoVazio_deveMostrarTodas() {
        List<Tarefa> todasTarefas = Arrays.asList(
                criarTarefa(1, "Matemática", "exercicios"),
                criarTarefa(2, "Física", "resumo")
        );
        simularCarregamento(todasTarefas);

        viewModel.setSearchQuery("");

        List<Tarefa> resultado = viewModel.getTarefas().getValue();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    @Test
    public void setSearchQuery_comTexto_deveFiltrarPorTitulo() {
        List<Tarefa> todasTarefas = Arrays.asList(
                criarTarefa(1, "Matemática", "exercicios"),
                criarTarefa(2, "Física", "resumo")
        );
        simularCarregamento(todasTarefas);

        viewModel.setSearchQuery("Matem");

        List<Tarefa> resultado = viewModel.getTarefas().getValue();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Matemática", resultado.get(0).getTitulo());
    }

    @Test
    public void setSearchQuery_deveBuscarPorDescricao() {
        List<Tarefa> todasTarefas = Arrays.asList(
                criarTarefa(1, "Tarefa A", "exercicios de algebra"),
                criarTarefa(2, "Tarefa B", "resumo de física")
        );
        simularCarregamento(todasTarefas);

        viewModel.setSearchQuery("algebra");

        List<Tarefa> resultado = viewModel.getTarefas().getValue();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Tarefa A", resultado.get(0).getTitulo());
    }

    @Test
    public void setSearchQuery_deveSerCaseInsensitive() {
        List<Tarefa> todasTarefas = Collections.singletonList(
                criarTarefa(1, "Programação Mobile", "aula prática")
        );
        simularCarregamento(todasTarefas);

        viewModel.setSearchQuery("programação mobile");

        List<Tarefa> resultado = viewModel.getTarefas().getValue();
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    public void setSearchQuery_semMatch_deveRetornarListaVazia() {
        List<Tarefa> todasTarefas = Collections.singletonList(
                criarTarefa(1, "Matemática", "exercicios")
        );
        simularCarregamento(todasTarefas);

        viewModel.setSearchQuery("Biologia");

        List<Tarefa> resultado = viewModel.getTarefas().getValue();
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // OPERACOES CRUD
    // ═══════════════════════════════════════════════════════════════════════

    @Test
    public void toggleTarefaConcluida_pendente_deveMudarParaConcluida() {
        Tarefa tarefa = criarTarefa(1, "Tarefa", "desc");
        tarefa.setEstado(EstadoTarefa.PENDENTE);

        doAnswer(inv -> {
            TarefaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(1);
            return null;
        }).when(mockRepository).atualizar(any(), any());

        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(Collections.emptyList());
            return null;
        }).when(mockRepository).obterTodas(any());

        viewModel.toggleTarefaConcluida(tarefa);

        assertEquals(EstadoTarefa.CONCLUIDA, tarefa.getEstado());
    }

    @Test
    public void toggleTarefaConcluida_concluida_deveMudarParaPendente() {
        Tarefa tarefa = criarTarefa(1, "Tarefa", "desc");
        tarefa.setEstado(EstadoTarefa.CONCLUIDA);

        doAnswer(inv -> {
            TarefaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(1);
            return null;
        }).when(mockRepository).atualizar(any(), any());

        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(Collections.emptyList());
            return null;
        }).when(mockRepository).obterTodas(any());

        viewModel.toggleTarefaConcluida(tarefa);

        assertEquals(EstadoTarefa.PENDENTE, tarefa.getEstado());
    }

    @Test
    public void deletarTarefa_deveChamarRepositorioERecarregar() {
        Tarefa tarefa = criarTarefa(1, "Tarefa", "desc");

        doAnswer(inv -> {
            TarefaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(1);
            return null;
        }).when(mockRepository).deletar(any(), any());

        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(Collections.emptyList());
            return null;
        }).when(mockRepository).obterTodas(any());

        viewModel.deletarTarefa(tarefa);

        verify(mockRepository).deletar(any(), any());
    }

    @Test
    public void deletarTarefa_comFalha_deveSetarMensagemDeErro() {
        Tarefa tarefa = criarTarefa(1, "Tarefa", "desc");

        doAnswer(inv -> {
            TarefaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(0); // 0 linhas afetadas = falha
            return null;
        }).when(mockRepository).deletar(any(), any());

        viewModel.deletarTarefa(tarefa);

        assertNotNull(viewModel.getErrorMessage().getValue());
        assertFalse(viewModel.getErrorMessage().getValue().isEmpty());
    }

    @Test
    public void clearError_deveLimparMensagemDeErro() {
        Tarefa tarefa = criarTarefa(1, "Tarefa", "desc");
        doAnswer(inv -> {
            TarefaRepository.Callback<Integer> cb = inv.getArgument(1);
            cb.onResult(0);
            return null;
        }).when(mockRepository).deletar(any(), any());
        viewModel.deletarTarefa(tarefa);

        viewModel.clearError();

        assertEquals(null, viewModel.getErrorMessage().getValue());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // AUXILIARES
    // ═══════════════════════════════════════════════════════════════════════

    private Tarefa criarTarefa(long id, String titulo, String descricao) {
        Tarefa tarefa = new Tarefa(titulo, descricao, 1L, System.currentTimeMillis(), Prioridade.MEDIA);
        tarefa.setId(id);
        return tarefa;
    }

    private void simularCarregamento(List<Tarefa> tarefas) {
        doAnswer(inv -> {
            TarefaRepository.Callback<List<Tarefa>> cb = inv.getArgument(0);
            cb.onResult(tarefas);
            return null;
        }).when(mockRepository).obterTodas(any());
        viewModel.carregarTarefas();
    }
}
