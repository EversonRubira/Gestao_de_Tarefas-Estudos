package com.example.gestaodetarefasestudos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.models.DiaCalendario;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para renderizar o calendário com os dias e seus indicadores de tarefas
 */
public class CalendarioAdapter extends RecyclerView.Adapter<CalendarioAdapter.DiaViewHolder> {

    private Context context;
    private List<DiaCalendario> dias;
    private OnDiaClickListener clickListener;

    public interface OnDiaClickListener {
        void onDiaClick(DiaCalendario dia);
    }

    public CalendarioAdapter(Context context) {
        this.context = context;
        this.dias = new ArrayList<>();
    }

    public void setDias(List<DiaCalendario> dias) {
        this.dias = dias;
        notifyDataSetChanged();
    }

    public void setOnDiaClickListener(OnDiaClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public DiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dia_calendario, parent, false);
        return new DiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaViewHolder holder, int position) {
        DiaCalendario dia = dias.get(position);
        holder.bind(dia);
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    /**
     * ViewHolder para cada dia do calendário
     */
    class DiaViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout containerDia;
        private TextView textNumeroDia;
        private LinearLayout layoutDots;

        public DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            containerDia = itemView.findViewById(R.id.layout_dia_container);
            textNumeroDia = itemView.findViewById(R.id.tv_numero_dia);
            layoutDots = itemView.findViewById(R.id.layout_dots);
        }

        /**
         * Vincula os dados do dia à view
         * Método chamado pelo RecyclerView para cada célula do grid
         */
        public void bind(DiaCalendario dia) {
            // Limpar dots de renderizações anteriores
            // RecyclerView reutiliza views, então precisa limpar antes de atualizar
            layoutDots.removeAllViews();

            if (dia.isDiaVazio()) {
                // Dia de outro mês (placeholder vazio)
                // Deixar célula invisível/transparente
                textNumeroDia.setText("");
                containerDia.setBackgroundColor(Color.TRANSPARENT);
                containerDia.setClickable(false);
            } else {
                // Mostrar número do dia (1-31)
                textNumeroDia.setText(String.valueOf(dia.getNumeroDia()));

                // Destacar dia atual com fundo arredondado e cor diferente
                if (dia.isDiaAtual()) {
                    containerDia.setBackgroundResource(R.drawable.bg_rounded);
                    textNumeroDia.setTextColor(context.getResources().getColor(R.color.primary));
                } else {
                    containerDia.setBackgroundColor(Color.TRANSPARENT);
                    textNumeroDia.setTextColor(context.getResources().getColor(R.color.text_primary));
                }

                // Adicionar dots coloridos se houver tarefas neste dia
                if (dia.temTarefas()) {
                    List<String> cores = dia.getCoresDisciplinas();

                    // Se houver mais de 3 tarefas, limitar a 3 dots + badge com número
                    // Evita poluir visualmente o calendário
                    if (dia.getQuantidadeTarefas() > 3) {
                        // Mostrar até 3 cores diferentes
                        // Math.min garante não ultrapassar o tamanho da lista
                        for (int i = 0; i < Math.min(3, cores.size()); i++) {
                            adicionarDot(cores.get(i));
                        }
                        // Adicionar badge mostrando "+X" tarefas a mais
                        adicionarBadgeNumero(dia.getQuantidadeTarefas());
                    } else {
                        // Se tem 3 ou menos, mostrar um dot para cada cor
                        for (String cor : cores) {
                            adicionarDot(cor);
                        }
                    }
                }

                // Configurar click listener para mostrar detalhes do dia
                containerDia.setClickable(true);
                containerDia.setOnClickListener(v -> {
                    // Só aciona callback se dia tiver tarefas
                    if (clickListener != null && dia.temTarefas()) {
                        clickListener.onDiaClick(dia);
                    }
                });
            }
        }

        /**
         * Adiciona um dot colorido ao layout
         * Dot é um círculo pequeno que representa uma disciplina
         */
        private void adicionarDot(String corHex) {
            // Criar view vazia (será apenas um quadrado/círculo colorido)
            View dot = new View(context);

            // Converter dp para pixels baseado na densidade da tela
            // density = pixels por dp (varia por dispositivo)
            // 4dp * density = tamanho em pixels real
            // Isso garante que o dot tenha mesmo tamanho visual em todas as telas
            int tamanho = (int) (4 * context.getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tamanho, tamanho);
            params.setMargins(1, 0, 1, 0); // 1px de espaço entre dots
            dot.setLayoutParams(params);

            // Criar drawable circular com a cor da disciplina
            // GradientDrawable permite criar formas programaticamente
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL); // OVAL em quadrado = círculo

            try {
                // parseColor converte string hex (#FF0000) em int de cor
                shape.setColor(Color.parseColor(corHex));
            } catch (Exception e) {
                // Se corHex for inválida (ex: null, formato errado), usar cor padrão
                shape.setColor(context.getResources().getColor(R.color.primary));
            }

            // Aplicar drawable como background da view
            dot.setBackground(shape);
            // Adicionar view ao container horizontal de dots
            layoutDots.addView(dot);
        }

        /**
         * Adiciona um badge numérico quando há mais de 3 tarefas
         * Mostra "+X" para indicar tarefas adicionais
         */
        private void adicionarBadgeNumero(int quantidade) {
            // Criar TextView programaticamente (sem XML)
            TextView badge = new TextView(context);
            // Calcular quantas tarefas não estão representadas por dots
            // Ex: se tem 7 tarefas e 3 dots, badge mostra "+4"
            badge.setText("+" + (quantidade - 3));
            badge.setTextSize(6); // tamanho pequeno para caber ao lado dos dots
            badge.setTextColor(context.getResources().getColor(R.color.text_secondary));

            // WRAP_CONTENT = tamanho mínimo necessário para o conteúdo
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(1, 0, 0, 0); // 1px de margem à esquerda
            badge.setLayoutParams(params);

            // Adicionar badge ao container de dots
            layoutDots.addView(badge);
        }
    }
}
