package com.example.gestaodetarefasestudos.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter simples para mostrar estatísticas de tempo de estudo
 * Cada item mostra: nome da disciplina, cor e tempo estudado
 */
public class EstatisticaAdapter extends RecyclerView.Adapter<EstatisticaAdapter.ViewHolder> {

    private Context context;
    private List<ItemEstatistica> listaEstatisticas;
    private float tempoMaximo = 0;

    public EstatisticaAdapter(Context context) {
        this.context = context;
        this.listaEstatisticas = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_estatistica, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < 0 || position >= listaEstatisticas.size()) return;
        ItemEstatistica item = listaEstatisticas.get(position);

        // Nome da disciplina
        holder.tvNomeDisciplina.setText(item.nomeDisciplina);

        // Tempo de estudo formatado
        holder.tvTempoEstudo.setText(item.tempoFormatado);

        // Cor da disciplina
        int corDisciplina;
        try {
            corDisciplina = Color.parseColor(item.cor);
            holder.viewCor.setBackgroundColor(corDisciplina);
        } catch (Exception e) {
            corDisciplina = Color.parseColor("#00897B");
            holder.viewCor.setBackgroundColor(corDisciplina);
        }

        // Configurar a barra de progresso
        if (tempoMaximo > 0) {
            // Calcular percentual em relação ao tempo máximo
            int percentual = (int) ((item.minutos / tempoMaximo) * 100);
            holder.progressBar.setProgress(percentual);
            holder.progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(corDisciplina));
        } else {
            holder.progressBar.setProgress(0);
        }
    }

    @Override
    public int getItemCount() {
        return listaEstatisticas != null ? listaEstatisticas.size() : 0;
    }

    /**
     * Atualiza a lista de estatísticas
     */
    public void atualizarLista(List<ItemEstatistica> novaLista) {
        this.listaEstatisticas = novaLista;

        // Calcular o tempo máximo para a barra de progresso
        tempoMaximo = 0;
        for (ItemEstatistica item : listaEstatisticas) {
            if (item.minutos > tempoMaximo) {
                tempoMaximo = item.minutos;
            }
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNomeDisciplina;
        TextView tvTempoEstudo;
        View viewCor;
        android.widget.ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomeDisciplina = itemView.findViewById(R.id.tv_nome_disciplina);
            tvTempoEstudo = itemView.findViewById(R.id.tv_tempo_estudo);
            viewCor = itemView.findViewById(R.id.view_cor_disciplina);
            progressBar = itemView.findViewById(R.id.progress_bar_tempo);
        }
    }

    /**
     * Classe simples para representar um item de estatística
     */
    public static class ItemEstatistica {
        public String nomeDisciplina;
        public String cor;
        public float minutos;
        public String tempoFormatado;

        public ItemEstatistica(String nomeDisciplina, String cor, float minutos) {
            this.nomeDisciplina = nomeDisciplina;
            this.cor = cor;
            this.minutos = minutos;
            this.tempoFormatado = formatarTempo(minutos);
        }

        /**
         * Formata os minutos para exibição
         */
        private String formatarTempo(float minutos) {
            if (minutos < 60) {
                return String.format(Locale.getDefault(), "%.0f min", minutos);
            } else {
                float horas = minutos / 60;
                return String.format(Locale.getDefault(), "%.1f h", horas);
            }
        }
    }
}
