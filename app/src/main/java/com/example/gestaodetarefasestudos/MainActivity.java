package com.example.gestaodetarefasestudos;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.fragments.DashboardFragment;
import com.example.gestaodetarefasestudos.fragments.HomeFragment;
import com.example.gestaodetarefasestudos.fragments.SubjectsFragment;
import com.example.gestaodetarefasestudos.fragments.TasksFragment;
import com.example.gestaodetarefasestudos.fragments.TimerFragment;
import com.example.gestaodetarefasestudos.models.Disciplina;
import com.example.gestaodetarefasestudos.models.Tarefa;
import com.example.gestaodetarefasestudos.models.SessaoEstudo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Toolbar barraFerramentas;
    private BottomNavigationView navegacaoInferior;

    // Nome do arquivo de preferências para guardar configurações do app
    private static final String PREFS_NAME = "AppPreferences";
    // Chave para verificar se é a primeira vez que o app é aberto
    private static final String KEY_PRIMEIRA_EXECUCAO = "primeira_execucao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar os componentes da interface
        inicializarComponentes();
        configurarBarraFerramentas();
        configurarNavegacaoInferior();

        // Verificar se é a primeira vez que o app é aberto
        // Se sim, criar alguns dados de exemplo para facilitar os testes
        verificarPrimeiraExecucao();

        // Carregar fragmento inicial apenas na primeira vez que a activity é criada
        if (savedInstanceState == null) {
            carregarFragmento(new HomeFragment());
            definirTituloBarraFerramentas(getString(R.string.home_title));
        }
    }

    private void inicializarComponentes() {
        barraFerramentas = findViewById(R.id.toolbar);
        navegacaoInferior = findViewById(R.id.bottom_navigation);
    }

    private void configurarBarraFerramentas() {
        setSupportActionBar(barraFerramentas);
    }

    /**
     * Configura a navegação inferior (Bottom Navigation)
     * Define qual fragmento será exibido quando o utilizador tocar num item do menu
     */
    private void configurarNavegacaoInferior() {
        navegacaoInferior.setOnItemSelectedListener(item -> {
            Fragment fragmentoSelecionado = null;
            String titulo = "";

            int idItem = item.getItemId();

            // Determina qual fragmento carregar baseado no item selecionado
            if (idItem == R.id.navigation_home) {
                fragmentoSelecionado = new HomeFragment();
                titulo = getString(R.string.home_title);
            } else if (idItem == R.id.navigation_dashboard) {
                fragmentoSelecionado = new DashboardFragment();
                titulo = "Dashboard";
            } else if (idItem == R.id.navigation_subjects) {
                fragmentoSelecionado = new SubjectsFragment();
                titulo = getString(R.string.subjects_title);
            } else if (idItem == R.id.navigation_tasks) {
                fragmentoSelecionado = new TasksFragment();
                titulo = getString(R.string.tasks_title);
            } else if (idItem == R.id.navigation_timer) {
                fragmentoSelecionado = new TimerFragment();
                titulo = getString(R.string.timer_title);
            }

            if (fragmentoSelecionado != null) {
                carregarFragmento(fragmentoSelecionado);
                definirTituloBarraFerramentas(titulo);
                return true;
            }

            return false;
        });
    }

    /**
     * Carrega um fragmento no container principal
     * @param fragmento O fragmento a ser exibido
     */
    private void carregarFragmento(Fragment fragmento) {
        FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
        transacao.replace(R.id.fragment_container, fragmento);
        transacao.commit();
    }

    private void definirTituloBarraFerramentas(String titulo) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
        }
    }

    /**
     * Verifica se é a primeira vez que o app é executado
     * Se for, cria alguns dados de exemplo para o utilizador poder testar
     */
    private void verificarPrimeiraExecucao() {
        // Aceder às preferências partilhadas para verificar se já foi executado antes
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean primeiraVez = prefs.getBoolean(KEY_PRIMEIRA_EXECUCAO, true);

        // Se for a primeira vez, criar dados de exemplo
        if (primeiraVez) {
            criarDadosExemplo();

            // Marcar que o app já foi aberto
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_PRIMEIRA_EXECUCAO, false);
            editor.apply();
        }
    }

    /**
     * Cria alguns dados de exemplo no banco de dados
     * Isto ajuda o utilizador a perceber como a aplicação funciona
     * TODO: No futuro, poderia mostrar um tutorial em vez de criar dados automáticos
     */
    private void criarDadosExemplo() {
        // Inicializar os DAOs para aceder ao banco de dados
        DisciplinaDAO disciplinaDAO = new DisciplinaDAO(this);
        TarefaDAO tarefaDAO = new TarefaDAO(this);
        SessaoEstudoDAO sessaoDAO = new SessaoEstudoDAO(this);

        // Criar algumas disciplinas de exemplo
        Disciplina pam = new Disciplina("Programação de Aplicações Móveis", "PAM", "#2196F3");
        Disciplina bd = new Disciplina("Bases de Dados", "BD", "#4CAF50");
        Disciplina web = new Disciplina("Desenvolvimento Web", "WEB", "#FF9800");

        long idPam = disciplinaDAO.adicionar(pam);
        long idBd = disciplinaDAO.adicionar(bd);
        long idWeb = disciplinaDAO.adicionar(web);

        // Criar algumas tarefas de exemplo
        // Calcular datas para as tarefas
        Calendar calendario = Calendar.getInstance();

        // Tarefa 1: Projeto PAM para daqui a 7 dias
        calendario.add(Calendar.DAY_OF_MONTH, 7);
        Tarefa tarefa1 = new Tarefa(
            "Concluir projeto final",
            "Finalizar aplicação de gestão de tarefas",
            idPam,
            calendario.getTimeInMillis(),
            Prioridade.ALTA
        );
        tarefaDAO.adicionar(tarefa1);

        // Tarefa 2: Estudo BD para daqui a 3 dias
        calendario = Calendar.getInstance();
        calendario.add(Calendar.DAY_OF_MONTH, 3);
        Tarefa tarefa2 = new Tarefa(
            "Estudar para teste",
            "Rever matéria de normalização",
            idBd,
            calendario.getTimeInMillis(),
            Prioridade.MEDIA
        );
        tarefaDAO.adicionar(tarefa2);

        // Tarefa 3: Exercício Web para daqui a 10 dias
        calendario = Calendar.getInstance();
        calendario.add(Calendar.DAY_OF_MONTH, 10);
        Tarefa tarefa3 = new Tarefa(
            "Fazer exercícios de JavaScript",
            "Completar exercícios do capítulo 5",
            idWeb,
            calendario.getTimeInMillis(),
            Prioridade.BAIXA
        );
        tarefaDAO.adicionar(tarefa3);

        // Criar uma sessão de estudo de exemplo (25 minutos = 1500 segundos)
        // Simular que estudou hoje
        SessaoEstudo sessao = new SessaoEstudo(idPam, 1500);
        sessaoDAO.adicionar(sessao);

        // Adicionar outra sessão de 15 minutos
        SessaoEstudo sessao2 = new SessaoEstudo(idBd, 900);
        sessaoDAO.adicionar(sessao2);
    }

}