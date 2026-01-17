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
        if (position < 0 || position >= dias.size()) return;
        DiaCalendario dia = dias.get(position);
        holder.bind(dia);
    }

    @Override
    public int getItemCount() {
        return dias != null ? dias.size() : 0;
    }

    /**
     * ViewHolder para cada dia do calendário
     */
    class DiaViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout containerDia;
        private TextView textNumeroDia;

        public DiaViewHolder(@NonNull View itemView) {
            super(itemView);
            containerDia = itemView.findViewById(R.id.layout_dia_container);
            textNumeroDia = itemView.findViewById(R.id.tv_numero_dia);
        }

        /**
         * Vincula os dados do dia à view
         * Método chamado pelo RecyclerView para cada célula do grid
         */
        public void bind(DiaCalendario dia) {
            if (dia.isDiaVazio()) {
                // Dia de outro mês (placeholder vazio)
                // Deixar célula invisível/transparente
                textNumeroDia.setText("");
                containerDia.setBackgroundColor(Color.TRANSPARENT);
                containerDia.setClickable(false);
            } else {
                // Mostrar número do dia (1-31)
                textNumeroDia.setText(String.valueOf(dia.getNumeroDia()));

                // Criar drawable com borda arredondada
                GradientDrawable backgroundDrawable = new GradientDrawable();
                backgroundDrawable.setShape(GradientDrawable.RECTANGLE);
                backgroundDrawable.setCornerRadius(20f);

                // Destacar dia atual
                if (dia.isDiaAtual()) {
                    backgroundDrawable.setColor(context.getResources().getColor(R.color.primary_light));
                    textNumeroDia.setTextColor(context.getResources().getColor(R.color.primary));
                } else {
                    backgroundDrawable.setColor(Color.TRANSPARENT);
                    textNumeroDia.setTextColor(context.getResources().getColor(R.color.text_primary));
                }

                // Aplicar borda colorida se houver tarefas neste dia
                if (dia.temTarefas()) {
                    List<String> cores = dia.getCoresDisciplinas();

                    if (!cores.isEmpty()) {
                        try {
                            // Usar a cor da primeira disciplina para a borda
                            int corBorda = Color.parseColor(cores.get(0));
                            // Borda de 2dp de largura
                            int larguraBorda = (int) (2 * context.getResources().getDisplayMetrics().density);
                            backgroundDrawable.setStroke(larguraBorda, corBorda);
                        } catch (Exception e) {
                            // Se falhar ao parsear a cor, usar cor primária
                            int corBorda = context.getResources().getColor(R.color.primary);
                            int larguraBorda = (int) (2 * context.getResources().getDisplayMetrics().density);
                            backgroundDrawable.setStroke(larguraBorda, corBorda);
                        }
                    }
                }

                // Aplicar o drawable como background
                containerDia.setBackground(backgroundDrawable);

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

    }
}
