package com.example.gestaodetarefasestudos.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.adapters.CalendarioAdapter;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
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

public class CalendarFragment extends Fragment {

    // Componentes do calendário
    private TextView textoMesAno;
    private ImageButton btnMesAnterior;
    private ImageButton btnProximoMes;
    private RecyclerView recyclerCalendario;
    private CalendarioAdapter calendarioAdapter;

    private TarefaDAO tarefaDAO;

    // Calendário atual sendo exibido
    private Calendar calendarioAtual;

    public CalendarFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inicializarDAOs();
        inicializarComponentes(view);
        inicializarCalendario();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recarregar calendário quando voltar para este fragmento
        carregarCalendario();
    }

    private void inicializarDAOs() {
        tarefaDAO = new TarefaDAO(requireContext());
    }

    private void inicializarComponentes(View view) {
        textoMesAno = view.findViewById(R.id.tv_mes_ano);
        btnMesAnterior = view.findViewById(R.id.btn_mes_anterior);
        btnProximoMes = view.findViewById(R.id.btn_proximo_mes);
        recyclerCalendario = view.findViewById(R.id.rv_calendario);
    }

    // ==================== MÉTODOS DO CALENDÁRIO ====================

    /**
     * Inicializa o calendário com o mês atual
     */
    private void inicializarCalendario() {
        // Inicializar calendário com o mês atual
        calendarioAtual = Calendar.getInstance();

        // Configurar RecyclerView com GridLayoutManager (7 colunas = 7 dias da semana)
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        recyclerCalendario.setLayoutManager(layoutManager);

        // Criar e configurar adapter
        calendarioAdapter = new CalendarioAdapter(requireContext());
        recyclerCalendario.setAdapter(calendarioAdapter);

        // Configurar listener de clique em dias
        calendarioAdapter.setOnDiaClickListener(dia -> {
            mostrarTarefasDoDia(dia);
        });

        // Configurar botões de navegação entre meses
        btnMesAnterior.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.MONTH, -1);
            carregarCalendario();
        });

        btnProximoMes.setOnClickListener(v -> {
            calendarioAtual.add(Calendar.MONTH, 1);
            carregarCalendario();
        });

        // Carregar calendário inicial
        carregarCalendario();
    }

    /**
     * Carrega e exibe o calendário do mês atual
     */
    private void carregarCalendario() {
        // Atualizar texto do mês/ano
        SimpleDateFormat formatoMesAno = new SimpleDateFormat("MMMM yyyy", new Locale("pt", "PT"));
        textoMesAno.setText(formatoMesAno.format(calendarioAtual.getTime()));

        // Gerar estrutura de dias
        List<DiaCalendario> dias = gerarDiasDoMes();

        // Buscar tarefas do banco e aplicar cores aos dias
        aplicarCoresDasTarefas(dias);

        // Notificar adapter para redesenhar
        calendarioAdapter.setDias(dias);
    }

    /**
     * Gera a lista de dias para preencher o grid do calendário
     */
    private List<DiaCalendario> gerarDiasDoMes() {
        List<DiaCalendario> dias = new ArrayList<>();

        Calendar cal = (Calendar) calendarioAtual.clone();

        // Ir para o primeiro dia do mês
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // Descobrir em que dia da semana começa o mês
        int diaDaSemana = cal.get(Calendar.DAY_OF_WEEK);

        // Adicionar dias vazios no início para alinhar o calendário
        for (int i = 1; i < diaDaSemana; i++) {
            dias.add(new DiaCalendario(0, 0));
        }

        // Adicionar todos os dias do mês atual
        int diasNoMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Calendar hoje = Calendar.getInstance();

        for (int dia = 1; dia <= diasNoMes; dia++) {
            cal.set(Calendar.DAY_OF_MONTH, dia);

            // Zerar horário para pegar timestamp do início do dia
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            DiaCalendario diaCalendario = new DiaCalendario(dia, cal.getTimeInMillis());

            // Marcar se é o dia de hoje
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
     */
    private void aplicarCoresDasTarefas(List<DiaCalendario> dias) {
        // Calcular timestamps de início e fim do mês
        Calendar cal = (Calendar) calendarioAtual.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioMes = cal.getTimeInMillis();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long fimMes = cal.getTimeInMillis();

        // Buscar todas as tarefas do mês agrupadas por dia e disciplina
        Cursor cursor = tarefaDAO.obterTarefasPorPeriodoComCores(inicioMes, fimMes);

        if (cursor != null && cursor.moveToFirst()) {
            Map<String, List<String>> coresPorDia = new HashMap<>();
            Map<String, Integer> contagemPorDia = new HashMap<>();

            do {
                long dataEntrega = cursor.getLong(cursor.getColumnIndexOrThrow("data_entrega"));
                String cor = cursor.getString(cursor.getColumnIndexOrThrow("cor_disciplina"));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));

                // Normalizar timestamp para início do dia
                Calendar calTemp = Calendar.getInstance();
                calTemp.setTimeInMillis(dataEntrega);
                calTemp.set(Calendar.HOUR_OF_DAY, 0);
                calTemp.set(Calendar.MINUTE, 0);
                calTemp.set(Calendar.SECOND, 0);
                calTemp.set(Calendar.MILLISECOND, 0);

                String chave = String.valueOf(calTemp.getTimeInMillis());

                if (!coresPorDia.containsKey(chave)) {
                    coresPorDia.put(chave, new ArrayList<>());
                    contagemPorDia.put(chave, 0);
                }

                if (cor != null && !cor.isEmpty()) {
                    coresPorDia.get(chave).add(cor);
                }

                contagemPorDia.put(chave, contagemPorDia.get(chave) + count);

            } while (cursor.moveToNext());

            cursor.close();

            // Aplicar as cores e contagens aos objetos DiaCalendario
            for (DiaCalendario dia : dias) {
                if (!dia.isDiaVazio()) {
                    String chave = String.valueOf(dia.getTimestamp());

                    if (coresPorDia.containsKey(chave)) {
                        List<String> cores = coresPorDia.get(chave);
                        for (String cor : cores) {
                            dia.adicionarCorDisciplina(cor);
                        }

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

        // Mostrar em um Toast
        Toast.makeText(requireContext(), mensagem.toString(), Toast.LENGTH_LONG).show();
    }
}
