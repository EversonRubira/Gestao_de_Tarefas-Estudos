package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.CalendarioAdapter;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.database.dao.SessaoEstudoDAO;
import com.example.gestaodetarefasestudos.models.DiaCalendario;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private TextView textoTotalDisciplinas;
    private TextView textoTarefasPendentes;
    private TextView textoTempoEstudo;
    private LinearLayout layoutListaTempo;

    // Componentes do calendário
    private TextView textoMesAno;
    private ImageButton btnMesAnterior;
    private ImageButton btnProximoMes;
    private RecyclerView recyclerCalendario;
    private CalendarioAdapter calendarioAdapter;

    private DisciplinaDAO disciplinaDAO;
    private TarefaDAO tarefaDAO;
    private SessaoEstudoDAO sessaoDAO;

    // Calendário atual sendo exibido
    private Calendar calendarioAtual;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarDAOs();
        inicializarComponentes(view);
        inicializarCalendario();
        carregarEstatisticas();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarregar estatísticas quando voltar para este fragmento
        carregarEstatisticas();
    }

    private void inicializarDAOs() {
        disciplinaDAO = new DisciplinaDAO(requireContext());
        tarefaDAO = new TarefaDAO(requireContext());
        sessaoDAO = new SessaoEstudoDAO(requireContext());
    }

    private void inicializarComponentes(View view) {
        textoTotalDisciplinas = view.findViewById(R.id.tv_total_subjects);
        textoTarefasPendentes = view.findViewById(R.id.tv_pending_tasks);
        textoTempoEstudo = view.findViewById(R.id.tv_study_time);
        layoutListaTempo = view.findViewById(R.id.layout_study_time_list);

        // Componentes do calendário
        textoMesAno = view.findViewById(R.id.tv_mes_ano);
        btnMesAnterior = view.findViewById(R.id.btn_mes_anterior);
        btnProximoMes = view.findViewById(R.id.btn_proximo_mes);
        recyclerCalendario = view.findViewById(R.id.rv_calendario);
    }

    /**
     * Carrega as estatísticas do banco de dados e atualiza a interface
     * Este método é chamado sempre que o fragmento fica visível
     
     */
    private void carregarEstatisticas() {
        // Carregar total de disciplinas cadastradas
        int totalDisciplinas = disciplinaDAO.contarTotal();
        textoTotalDisciplinas.setText(String.valueOf(totalDisciplinas));

        // Carregar número de tarefas pendentes (não concluídas)
        int tarefasPendentes = tarefaDAO.contarPendentes();
        textoTarefasPendentes.setText(String.valueOf(tarefasPendentes));

        // Carregar tempo de estudo de hoje POR DISCIPLINA
        carregarTempoPorDisciplina();
    }

    /**
     * Carrega e mostra o tempo de estudo de hoje separado por disciplina
     * Exemplo: PAM: 50m, BD: 25m, WEB: 15m
     */
    private void carregarTempoPorDisciplina() {
        // Limpar views criadas anteriormente (evita duplicação)
        layoutListaTempo.removeAllViews();

        // Buscar dados do banco usando DAO
        // Retorna Cursor com tempo total por disciplina de hoje
        Cursor cursor = sessaoDAO.obterTempoHojePorDisciplina();
        long tempoTotal = 0;

        // Verificar se cursor tem dados
        // cursor != null: query não falhou
        // getCount() > 0: há pelo menos uma linha de resultado
        if (cursor != null && cursor.getCount() > 0) {
            // Iterar por cada linha do cursor (cada disciplina)
            // moveToNext() avança para próxima linha e retorna false quando acabar
            while (cursor.moveToNext()) {
                // Ler valores das colunas da linha atual
                // "d_nome" e "total_segundos" vêm do JOIN feito no DAO
                String nomeDisciplina = cursor.getString(cursor.getColumnIndexOrThrow("d_nome"));
                long segundos = cursor.getLong(cursor.getColumnIndexOrThrow("total_segundos"));

                // Se nome for null, é sessão genérica (disciplina_id=0)
                if (nomeDisciplina == null || nomeDisciplina.isEmpty()) {
                    nomeDisciplina = "Geral";
                }

                // Somar ao total geral do dia
                tempoTotal += segundos;

                // Criar e adicionar view para esta disciplina
                adicionarLinhaDisciplina(nomeDisciplina, segundos);
            }
            cursor.close(); // IMPORTANTE: sempre fechar cursor para liberar recursos
        }

        // Se não houver nenhum estudo hoje, mostrar mensagem informativa
        if (tempoTotal == 0) {
            // Criar TextView programaticamente
            TextView textoVazio = new TextView(requireContext());
            textoVazio.setText("Nenhum estudo registado hoje");
            textoVazio.setTextColor(getResources().getColor(R.color.text_secondary));
            textoVazio.setTextSize(14);
            textoVazio.setPadding(0, 8, 0, 8);
            layoutListaTempo.addView(textoVazio);
        }

        // Atualizar TextView do tempo total
        String tempoFormatado = formatarTempoEstudo(tempoTotal);
        textoTempoEstudo.setText(tempoFormatado);
    }

    /**
     * Adiciona uma linha mostrando disciplina e tempo estudado
     */
    private void adicionarLinhaDisciplina(String nomeDisciplina, long segundos) {
        // Criar layout horizontal para a linha
        LinearLayout linha = new LinearLayout(requireContext());
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setPadding(0, 4, 0, 4);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linha.setLayoutParams(params);

        // Nome da disciplina (à esquerda)
        TextView textoNome = new TextView(requireContext());
        textoNome.setText(nomeDisciplina);
        textoNome.setTextColor(getResources().getColor(R.color.text_primary));
        textoNome.setTextSize(14);
        // LayoutParams com weight: distribui espaço entre views
        // width = 0, weight = 1f significa "ocupar todo espaço disponível"
        // Assim, textoNome empurra textoTempo para a direita
        LinearLayout.LayoutParams paramsNome = new LinearLayout.LayoutParams(
                0, // width = 0 quando usa weight
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // weight = 1 (ocupa espaço disponível restante)
        );
        textoNome.setLayoutParams(paramsNome);

        // Tempo (à direita)
        TextView textoTempo = new TextView(requireContext());
        String tempoFormatado = formatarTempoEstudo(segundos);
        textoTempo.setText(tempoFormatado);
        textoTempo.setTextColor(getResources().getColor(R.color.primary));
        textoTempo.setTextSize(14);
        // textoTempo não tem weight, então usa apenas o espaço necessário

        // Adicionar à linha
        linha.addView(textoNome);
        linha.addView(textoTempo);

        // Adicionar linha ao layout
        layoutListaTempo.addView(linha);
    }

    // Converte segundos em formato legível (ex: 3665 segundos -> "1h 01m")
    private String formatarTempoEstudo(long segundos) {
        if (segundos == 0) {
            return "0h 00m";
        }

        // Divisão inteira: 3665 / 3600 = 1 hora
        long horas = segundos / 3600;
        // Resto da divisão por 3600, depois dividir por 60
        // Ex: 3665 % 3600 = 65, depois 65 / 60 = 1 minuto
        long minutos = (segundos % 3600) / 60;

        // %d = número decimal, %02d = número com 2 dígitos (preenche com 0)
        return String.format(Locale.getDefault(), "%dh %02dm", horas, minutos);
    }

    // ==================== MÉTODOS DO CALENDÁRIO ====================

    /**
     * Inicializa o calendário com o mês atual
     */
    private void inicializarCalendario() {
        // Inicializar calendário com o mês atual
        calendarioAtual = Calendar.getInstance();

        // Configurar RecyclerView com GridLayoutManager (7 colunas = 7 dias da semana)
        // GridLayoutManager cria uma grade/tabela
        // 7 colunas = Domingo, Segunda, Terça, Quarta, Quinta, Sexta, Sábado
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        recyclerCalendario.setLayoutManager(layoutManager);

        // Criar e configurar adapter
        // Adapter é responsável por criar e preencher as views de cada dia
        calendarioAdapter = new CalendarioAdapter(requireContext());
        recyclerCalendario.setAdapter(calendarioAdapter);

        // Configurar listener de clique em dias
        // Lambda (dia -> {...}) é uma função anônima chamada quando usuário clica
        calendarioAdapter.setOnDiaClickListener(dia -> {
            mostrarTarefasDoDia(dia);
        });

        // Configurar botões de navegação entre meses
        btnMesAnterior.setOnClickListener(v -> {
            // add() com valor negativo subtrai meses
            calendarioAtual.add(Calendar.MONTH, -1); // Voltar 1 mês
            carregarCalendario(); // Recarregar calendário com novo mês
        });

        btnProximoMes.setOnClickListener(v -> {
            // add() com valor positivo adiciona meses
            calendarioAtual.add(Calendar.MONTH, 1); // Avançar 1 mês
            carregarCalendario(); // Recarregar calendário com novo mês
        });

        // Carregar calendário inicial
        carregarCalendario();
    }

    /**
     * Carrega e exibe o calendário do mês atual
     * AQUI É ONDE GERAMOS OS DIAS E APLICAMOS AS CORES!
     * Fluxo: formatar título -> gerar estrutura de dias -> buscar tarefas -> atualizar UI
     */
    private void carregarCalendario() {
        // Atualizar texto do mês/ano (ex: "novembro 2025")
        // MMMM = nome completo do mês, yyyy = ano com 4 dígitos
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "PT"));
        textoMesAno.setText(formatoMesAno.format(calendarioAtual.getTime()));

        // Passo 1: Gerar estrutura de dias (42 células = 6 semanas * 7 dias)
        List<DiaCalendario> dias = gerarDiasDoMes();

        // Passo 2: Buscar tarefas do banco e aplicar cores aos dias
        aplicarCoresDasTarefas(dias);

        // Passo 3: Notificar adapter para redesenhar RecyclerView
        calendarioAdapter.setDias(dias);
    }

    /**
     * Gera a lista de dias para preencher o grid do calendário
     * Inclui dias vazios do mês anterior para alinhar corretamente
     */
    private List<DiaCalendario> gerarDiasDoMes() {
        List<DiaCalendario> dias = new ArrayList<>();

        // clone() cria uma cópia do objeto para não alterar o original
        // Importante porque vamos modificar cal várias vezes
        Calendar cal = (Calendar) calendarioAtual.clone();

        // Ir para o primeiro dia do mês
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // Descobrir em que dia da semana começa o mês
        // DAY_OF_WEEK retorna: 1=Domingo, 2=Segunda, ... 7=Sábado
        int diaDaSemana = cal.get(Calendar.DAY_OF_WEEK);

        // Adicionar dias vazios no início para alinhar o calendário
        // Exemplo: se o mês começa na Quarta (4), adiciona 3 dias vazios antes
        // Isso faz o primeiro dia aparecer na coluna correta do grid 7x6
        for (int i = 1; i < diaDaSemana; i++) {
            dias.add(new DiaCalendario(0, 0)); // numeroDia=0 indica dia vazio
        }

        // Adicionar todos os dias do mês atual
        // getActualMaximum retorna quantos dias tem no mês (28, 29, 30 ou 31)
        int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar hoje = Calendar.getInstance(); // Calendar com data/hora atual

        for (int dia = 1; dia <= diasNoMes; dia++) {
            cal.set(Calendar.DAY_OF_MONTH, dia);

            // Zerar horário para pegar timestamp do início do dia (meia-noite)
            // Importante para comparações de data (ignorar hora)
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            DiaCalendario diaCalendario = new DiaCalendario(dia, cal.getTimeInMillis());

            // Marcar se é o dia de hoje comparando ano, mês e dia
            // Precisa comparar os 3 campos porque meses se repetem a cada ano
            if (cal.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == hoje.get(Calendar.MONTH) &&
                cal.get(Calendar.DAY_OF_MONTH) == hoje.get(Calendar.DAY_OF_MONTH)) {
                diaCalendario.setDiaAtual(true);
            }

            dias.add(diaCalendario);
        }

        return dias;
    }

    /**
     * Aplica as cores das disciplinas aos dias que têm tarefas
     * ESTE É O CORAÇÃO DA FUNCIONALIDADE HÍBRIDA!
     * Processo: busca tarefas do banco -> organiza em mapas -> aplica aos dias
     */
    private void aplicarCoresDasTarefas(List<DiaCalendario> dias) {
        // Passo 1: Calcular timestamps de início e fim do mês
        Calendar cal = (Calendar) calendarioAtual.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1); // Primeiro dia
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioMes = cal.getTimeInMillis(); // Timestamp: 01/XX/XXXX 00:00:00.000

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH)); // Último dia
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long fimMes = cal.getTimeInMillis(); // Timestamp: 31/XX/XXXX 23:59:59.999

        // Passo 2: Buscar todas as tarefas do mês agrupadas por dia e disciplina
        Cursor cursor = tarefaDAO.obterTarefasPorPeriodoComCores(inicioMes, fimMes);

        if (cursor != null && cursor.moveToFirst()) {
            // Passo 3: Criar estruturas para organizar os dados
            // Map funciona como um dicionário: chave -> valor
            // String chave = timestamp do dia, List<String> valor = lista de cores
            Map<String, List<String>> coresPorDia = new HashMap<>();
            Map<String, Integer> contagemPorDia = new HashMap<>();

            // Passo 4: Processar cada linha do cursor (cada disciplina em cada dia)
            do {
                // Ler dados da linha atual do cursor
                long dataEntrega = cursor.getLong(cursor.getColumnIndexOrThrow("data_entrega"));
                String cor = cursor.getString(cursor.getColumnIndexOrThrow("cor_disciplina"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

                // Normalizar timestamp para início do dia (00:00:00)
                // Importante porque tarefas podem ter horários diferentes no mesmo dia
                Calendar calTemp = Calendar.getInstance();
                calTemp.setTimeInMillis(dataEntrega);
                calTemp.set(Calendar.HOUR_OF_DAY, 0);
                calTemp.set(Calendar.MINUTE, 0);
                calTemp.set(Calendar.SECOND, 0);
                calTemp.set(Calendar.MILLISECOND, 0);

                // Converter timestamp em String para usar como chave do Map
                String chave = String.valueOf(calTemp.getTimeInMillis());

                // Se é a primeira vez que vemos este dia, inicializar estruturas
                if (!coresPorDia.containsKey(chave)) {
                    coresPorDia.put(chave, new ArrayList<>());
                    contagemPorDia.put(chave, 0);
                }

                // Adicionar cor da disciplina à lista deste dia
                if (cor != null && !cor.isEmpty()) {
                    coresPorDia.get(chave).add(cor);
                }

                // Somar quantidade de tarefas desta disciplina ao total do dia
                contagemPorDia.put(chave, contagemPorDia.get(chave) + count);

            } while (cursor.moveToNext()); // Avançar para próxima linha

            cursor.close(); // Sempre fechar cursor para liberar recursos

            // Passo 5: Aplicar as cores e contagens aos objetos DiaCalendario
            for (DiaCalendario dia : dias) {
                if (!dia.isDiaVazio()) {
                    String chave = String.valueOf(dia.getTimestamp());

                    // Se este dia tem tarefas no Map
                    if (coresPorDia.containsKey(chave)) {
                        // Adicionar cores (método adicionarCorDisciplina limita a 3)
                        List<String> cores = coresPorDia.get(chave);
                        for (String cor : cores) {
                            dia.adicionarCorDisciplina(cor);
                        }

                        // Definir quantidade total de tarefas
                        dia.setQuantidadeTarefas(contagemPorDia.get(chave));
                    }
                }
            }
        }
    }

    /**
     * Mostra as tarefas de um dia específico quando o usuário clica
     */
    private void mostrarTarefasDoDia(DiaCalendario dia) {
        // Calcular início e fim do dia
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dia.getTimestamp());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long fimDia = cal.getTimeInMillis();

        // Buscar tarefas do dia
        List<Tarefa> tarefas = tarefaDAO.obterTarefasPorDia(inicioDia, fimDia);

        // Montar mensagem com as tarefas
        StringBuilder mensagem = new StringBuilder();
        SimpleDateFormat formatoDia = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        mensagem.append("Tarefas de ").append(formatoDia.format(new Date(dia.getTimestamp()))).append(":\n\n");

        for (Tarefa tarefa : tarefas) {
            mensagem.append("• ").append(tarefa.getTitulo());
            if (tarefa.getNomeDisciplina() != null) {
                mensagem.append(" (").append(tarefa.getNomeDisciplina()).append(")");
            }
            mensagem.append("\n");
        }

        // Mostrar em um Toast (você pode substituir por um Dialog mais elaborado)
        Toast.makeText(requireContext(), mensagem.toString(), Toast.LENGTH_LONG).show();
    }
}
