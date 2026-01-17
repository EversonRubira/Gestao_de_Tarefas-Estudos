package com.example.gestaodetarefasestudos.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.database.dao.UsuarioRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Testes instrumentados para DisciplinaRoomDAO
 * Requer dispositivo/emulador Android para executar
 */
@RunWith(AndroidJUnit4.class)
public class DisciplinaDAOTest {

    private AppDatabase db;
    private DisciplinaRoomDAO disciplinaDAO;
    private UsuarioRoomDAO usuarioDAO;
    private long usuarioId;

    @Before
    public void criarBanco() {
        Context context = ApplicationProvider.getApplicationContext();

        // Criar banco em memoria (nao persiste)
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // Permitir queries na main thread para testes
                .build();

        disciplinaDAO = db.disciplinaDAO();
        usuarioDAO = db.usuarioDAO();

        // Criar usuario de teste
        Usuario usuario = new Usuario("Teste", "teste@email.com", "hashsenha");
        usuarioId = usuarioDAO.inserir(usuario);
    }

    @After
    public void fecharBanco() {
        db.close();
    }

    @Test
    public void inserir_deveRetornarIdValido() {
        Disciplina disciplina = criarDisciplina("Matematica", "MAT");

        long id = disciplinaDAO.inserir(disciplina);

        assertTrue(id > 0);
    }

    @Test
    public void obterPorId_deveRetornarDisciplinaCorreta() {
        Disciplina disciplina = criarDisciplina("Fisica", "FIS");
        long id = disciplinaDAO.inserir(disciplina);

        Disciplina resultado = disciplinaDAO.obterPorId(id);

        assertNotNull(resultado);
        assertEquals("Fisica", resultado.getNome());
        assertEquals("FIS", resultado.getCodigo());
    }

    @Test
    public void obterPorId_deveRetornarNullParaIdInexistente() {
        Disciplina resultado = disciplinaDAO.obterPorId(999L);

        assertNull(resultado);
    }

    @Test
    public void obterTodas_deveRetornarListaVaziaInicial() {
        List<Disciplina> lista = disciplinaDAO.obterTodas(usuarioId);

        assertNotNull(lista);
        assertEquals(0, lista.size());
    }

    @Test
    public void obterTodas_deveRetornarTodasDisciplinas() {
        disciplinaDAO.inserir(criarDisciplina("Disc 1", "D1"));
        disciplinaDAO.inserir(criarDisciplina("Disc 2", "D2"));
        disciplinaDAO.inserir(criarDisciplina("Disc 3", "D3"));

        List<Disciplina> lista = disciplinaDAO.obterTodas(usuarioId);

        assertEquals(3, lista.size());
    }

    @Test
    public void obterTodas_deveRetornarApenasDisciplinasDoUsuario() {
        // Criar outro usuario
        Usuario outroUsuario = new Usuario("Outro", "outro@email.com", "hash");
        long outroUsuarioId = usuarioDAO.inserir(outroUsuario);

        // Disciplinas do primeiro usuario
        disciplinaDAO.inserir(criarDisciplina("Minha Disc 1", "MD1"));
        disciplinaDAO.inserir(criarDisciplina("Minha Disc 2", "MD2"));

        // Disciplina do outro usuario
        Disciplina discOutro = new Disciplina("Disc Outro", "DO", "#FF0000");
        discOutro.setUsuarioId(outroUsuarioId);
        disciplinaDAO.inserir(discOutro);

        // Verificar que cada usuario ve apenas suas disciplinas
        List<Disciplina> minhasDisciplinas = disciplinaDAO.obterTodas(usuarioId);
        List<Disciplina> disciplinasOutro = disciplinaDAO.obterTodas(outroUsuarioId);

        assertEquals(2, minhasDisciplinas.size());
        assertEquals(1, disciplinasOutro.size());
    }

    @Test
    public void atualizar_deveModificarDisciplina() {
        Disciplina disciplina = criarDisciplina("Nome Original", "NO");
        long id = disciplinaDAO.inserir(disciplina);

        // Modificar
        disciplina.setId(id);
        disciplina.setNome("Nome Atualizado");
        disciplina.setCodigo("NA");
        int linhasAfetadas = disciplinaDAO.atualizar(disciplina);

        // Verificar
        assertEquals(1, linhasAfetadas);
        Disciplina atualizada = disciplinaDAO.obterPorId(id);
        assertEquals("Nome Atualizado", atualizada.getNome());
        assertEquals("NA", atualizada.getCodigo());
    }

    @Test
    public void deletar_deveRemoverDisciplina() {
        Disciplina disciplina = criarDisciplina("Para Deletar", "PD");
        long id = disciplinaDAO.inserir(disciplina);
        disciplina.setId(id);

        int linhasAfetadas = disciplinaDAO.deletar(disciplina);

        assertEquals(1, linhasAfetadas);
        assertNull(disciplinaDAO.obterPorId(id));
    }

    @Test
    public void verificarCodigoExiste_deveRetornarTrueParaCodigoExistente() {
        disciplinaDAO.inserir(criarDisciplina("Disciplina", "CODIGO"));

        boolean existe = disciplinaDAO.verificarCodigoExiste("CODIGO", usuarioId);

        assertTrue(existe);
    }

    @Test
    public void verificarCodigoExiste_deveRetornarFalseParaCodigoInexistente() {
        boolean existe = disciplinaDAO.verificarCodigoExiste("NAO_EXISTE", usuarioId);

        assertFalse(existe);
    }

    // Helper para criar disciplina com usuario atual
    private Disciplina criarDisciplina(String nome, String codigo) {
        Disciplina disciplina = new Disciplina(nome, codigo, "#2196F3");
        disciplina.setUsuarioId(usuarioId);
        return disciplina;
    }
}
