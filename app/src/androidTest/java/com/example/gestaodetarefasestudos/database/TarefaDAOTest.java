package com.example.gestaodetarefasestudos.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.UsuarioRoomDAO;
import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.models.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Testes instrumentados para TarefaRoomDAO
 */
@RunWith(AndroidJUnit4.class)
public class TarefaDAOTest {

    private AppDatabase db;
    private TarefaRoomDAO tarefaDAO;
    private DisciplinaRoomDAO disciplinaDAO;
    private UsuarioRoomDAO usuarioDAO;

    private long usuarioId;
    private long disciplinaId;

    @Before
    public void criarBanco() {
        Context context = ApplicationProvider.getApplicationContext();

        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();

        tarefaDAO = db.tarefaDAO();
        disciplinaDAO = db.disciplinaDAO();
        usuarioDAO = db.usuarioDAO();

        // Criar usuario e disciplina de teste
        Usuario usuario = new Usuario("Teste", "teste@email.com", "hashsenha");
        usuarioId = usuarioDAO.inserir(usuario);

        Disciplina disciplina = new Disciplina("Matematica", "MAT", "#2196F3");
        disciplina.setUsuarioId(usuarioId);
        disciplinaId = disciplinaDAO.inserir(disciplina);
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    @Test
    public void inserir_deveRetornarIdValido() {
        Tarefa tarefa = criarTarefa("Tarefa Teste", Prioridade.MEDIA);

        long id = tarefaDAO.inserir(tarefa);

        assertTrue(id > 0);
    }

    @Test
    public void obterPorId_deveRetornarTarefaCorreta() {
        Tarefa tarefa = criarTarefa("Minha Tarefa", Prioridade.ALTA);
        long id = tarefaDAO.inserir(tarefa);

        Tarefa resultado = tarefaDAO.obterPorId(id);

        assertNotNull(resultado);
        assertEquals("Minha Tarefa", resultado.getTitulo());
        assertEquals(Prioridade.ALTA, resultado.getPrioridade());
    }

    @Test
    public void obterTodas_deveRetornarTodasTarefas() {
        tarefaDAO.inserir(criarTarefa("Tarefa 1", Prioridade.BAIXA));
        tarefaDAO.inserir(criarTarefa("Tarefa 2", Prioridade.MEDIA));
        tarefaDAO.inserir(criarTarefa("Tarefa 3", Prioridade.ALTA));

        List<Tarefa> lista = tarefaDAO.obterTodas();

        assertEquals(3, lista.size());
    }

    @Test
    public void obterPendentes_deveRetornarApenasTarefasNaoConcluidas() {
        // Criar tarefas com diferentes estados
        Tarefa pendente1 = criarTarefa("Pendente 1", Prioridade.MEDIA);
        Tarefa pendente2 = criarTarefa("Pendente 2", Prioridade.BAIXA);
        Tarefa concluida = criarTarefa("Concluida", Prioridade.ALTA);
        concluida.setEstado(EstadoTarefa.CONCLUIDA);

        tarefaDAO.inserir(pendente1);
        tarefaDAO.inserir(pendente2);
        tarefaDAO.inserir(concluida);

        List<Tarefa> pendentes = tarefaDAO.obterPendentes();

        assertEquals(2, pendentes.size());
    }

    @Test
    public void obterConcluidas_deveRetornarApenasTarefasConcluidas() {
        Tarefa pendente = criarTarefa("Pendente", Prioridade.MEDIA);
        Tarefa concluida1 = criarTarefa("Concluida 1", Prioridade.BAIXA);
        concluida1.setEstado(EstadoTarefa.CONCLUIDA);
        Tarefa concluida2 = criarTarefa("Concluida 2", Prioridade.ALTA);
        concluida2.setEstado(EstadoTarefa.CONCLUIDA);

        tarefaDAO.inserir(pendente);
        tarefaDAO.inserir(concluida1);
        tarefaDAO.inserir(concluida2);

        List<Tarefa> concluidas = tarefaDAO.obterConcluidas();

        assertEquals(2, concluidas.size());
    }

    @Test
    public void obterAtrasadas_deveRetornarTarefasComDataPassada() {
        // Tarefa atrasada (data no passado)
        Tarefa atrasada = criarTarefaComData("Atrasada", -5); // 5 dias atras
        atrasada.setEstado(EstadoTarefa.PENDENTE);

        // Tarefa futura
        Tarefa futura = criarTarefaComData("Futura", 5); // 5 dias no futuro

        // Tarefa atrasada mas concluida (nao deve aparecer)
        Tarefa atrasadaConcluida = criarTarefaComData("Atrasada Concluida", -3);
        atrasadaConcluida.setEstado(EstadoTarefa.CONCLUIDA);

        tarefaDAO.inserir(atrasada);
        tarefaDAO.inserir(futura);
        tarefaDAO.inserir(atrasadaConcluida);

        List<Tarefa> atrasadas = tarefaDAO.obterAtrasadas(System.currentTimeMillis());

        assertEquals(1, atrasadas.size());
        assertEquals("Atrasada", atrasadas.get(0).getTitulo());
    }

    @Test
    public void listarPorDisciplina_deveRetornarApenasTarefasDaDisciplina() {
        // Criar outra disciplina
        Disciplina outraDisciplina = new Disciplina("Fisica", "FIS", "#FF0000");
        outraDisciplina.setUsuarioId(usuarioId);
        long outraDisciplinaId = disciplinaDAO.inserir(outraDisciplina);

        // Tarefas da primeira disciplina
        tarefaDAO.inserir(criarTarefa("Tarefa MAT 1", Prioridade.MEDIA));
        tarefaDAO.inserir(criarTarefa("Tarefa MAT 2", Prioridade.BAIXA));

        // Tarefa da outra disciplina
        Tarefa tarefaFis = new Tarefa("Tarefa FIS", "Desc", outraDisciplinaId,
                System.currentTimeMillis() + 86400000, Prioridade.ALTA);
        tarefaDAO.inserir(tarefaFis);

        List<Tarefa> tarefasMAT = tarefaDAO.listarPorDisciplina(disciplinaId);
        List<Tarefa> tarefasFIS = tarefaDAO.listarPorDisciplina(outraDisciplinaId);

        assertEquals(2, tarefasMAT.size());
        assertEquals(1, tarefasFIS.size());
    }

    @Test
    public void atualizar_deveModificarTarefa() {
        Tarefa tarefa = criarTarefa("Original", Prioridade.BAIXA);
        long id = tarefaDAO.inserir(tarefa);

        tarefa.setId(id);
        tarefa.setTitulo("Modificado");
        tarefa.setPrioridade(Prioridade.ALTA);
        int linhas = tarefaDAO.atualizar(tarefa);

        Tarefa atualizada = tarefaDAO.obterPorId(id);
        assertEquals(1, linhas);
        assertEquals("Modificado", atualizada.getTitulo());
        assertEquals(Prioridade.ALTA, atualizada.getPrioridade());
    }

    @Test
    public void atualizarEstado_deveModificarApenasEstado() {
        Tarefa tarefa = criarTarefa("Tarefa", Prioridade.MEDIA);
        long id = tarefaDAO.inserir(tarefa);

        tarefaDAO.atualizarEstado(id, EstadoTarefa.CONCLUIDA.ordinal());

        Tarefa resultado = tarefaDAO.obterPorId(id);
        assertEquals(EstadoTarefa.CONCLUIDA, resultado.getEstado());
    }

    @Test
    public void deletar_deveRemoverTarefa() {
        Tarefa tarefa = criarTarefa("Para Deletar", Prioridade.BAIXA);
        long id = tarefaDAO.inserir(tarefa);
        tarefa.setId(id);

        int linhas = tarefaDAO.deletar(tarefa);

        assertEquals(1, linhas);
        assertNull(tarefaDAO.obterPorId(id));
    }

    @Test
    public void contarPendentes_deveContarCorretamente() {
        tarefaDAO.inserir(criarTarefa("Pendente 1", Prioridade.MEDIA));
        tarefaDAO.inserir(criarTarefa("Pendente 2", Prioridade.BAIXA));

        Tarefa concluida = criarTarefa("Concluida", Prioridade.ALTA);
        concluida.setEstado(EstadoTarefa.CONCLUIDA);
        tarefaDAO.inserir(concluida);

        int count = tarefaDAO.contarPendentes();

        assertEquals(2, count);
    }

    @Test
    public void obterTodasPorPrioridade_deveOrdenarPorPrioridadeDescrescente() {
        tarefaDAO.inserir(criarTarefa("Baixa", Prioridade.BAIXA));
        tarefaDAO.inserir(criarTarefa("Alta", Prioridade.ALTA));
        tarefaDAO.inserir(criarTarefa("Media", Prioridade.MEDIA));

        List<Tarefa> ordenadas = tarefaDAO.obterTodasPorPrioridade();

        assertEquals(3, ordenadas.size());
        // Alta (3) deve vir primeiro, depois Media (2), depois Baixa (1)
        assertEquals(Prioridade.ALTA, ordenadas.get(0).getPrioridade());
        assertEquals(Prioridade.MEDIA, ordenadas.get(1).getPrioridade());
        assertEquals(Prioridade.BAIXA, ordenadas.get(2).getPrioridade());
    }

    // Helpers
    private Tarefa criarTarefa(String titulo, Prioridade prioridade) {
        long dataEntrega = System.currentTimeMillis() + 86400000; // +1 dia
        return new Tarefa(titulo, "Descricao", disciplinaId, dataEntrega, prioridade);
    }

    private Tarefa criarTarefaComData(String titulo, int diasOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, diasOffset);
        return new Tarefa(titulo, "Descricao", disciplinaId, cal.getTimeInMillis(), Prioridade.MEDIA);
    }
}
