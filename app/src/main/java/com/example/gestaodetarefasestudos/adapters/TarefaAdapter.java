package com.example.gestaodetarefasestudos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.AdicionarEditarTarefaActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.dao.TarefaDAO;
import com.example.gestaodetarefasestudos.enums.EstadoTarefa;
import com.example.gestaodetarefasestudos.enums.Prioridade;
import com.example.gestaodetarefasestudos.models.Tarefa;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {

    private Context contexto;
    private List<Tarefa> listaTarefas;
    private TarefaDAO tarefaDAO;
    private OnTarefaChangedListener listener;
    private SimpleDateFormat formatoData = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public interface OnTarefaChangedListener {
        void onTarefaChanged();
    }

    public TarefaAdapter(Context contexto, List<Tarefa> listaTarefas,
                         OnTarefaChangedListener listener) {
        this.contexto = contexto;
        this.listaTarefas = listaTarefas;
        this.listener = listener;
        this.tarefaDAO = new TarefaDAO(contexto);
    }

    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.item_tarefa, parent, false);
        return new TarefaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        Tarefa tarefa = listaTarefas.get(position);

        holder.txtTituloTarefa.setText(tarefa.getTitulo());
        holder.txtDisciplinaTarefa.setText(tarefa.getNomeDisciplina());

        // Formatar data
        Date data = new Date(tarefa.getDataEntrega());
        holder.txtDataEntregaTarefa.setText(formatoData.format(data));

        // Configurar prioridade
        configurarPrioridade(holder, tarefa.getPrioridade());

        // Configurar checkbox
        holder.checkboxTarefa.setChecked(tarefa.estaConcluida());

        // Aplicar estilo de tarefa concluída
        if (tarefa.estaConcluida()) {
            holder.txtTituloTarefa.setPaintFlags(
                    holder.txtTituloTarefa.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.txtTituloTarefa.setAlpha(0.5f);
            holder.txtDisciplinaTarefa.setAlpha(0.5f);
        } else {
            holder.txtTituloTarefa.setPaintFlags(
                    holder.txtTituloTarefa.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.txtTituloTarefa.setAlpha(1.0f);
            holder.txtDisciplinaTarefa.setAlpha(1.0f);
        }

        // Listener do checkbox
        holder.checkboxTarefa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            EstadoTarefa novoEstado = isChecked ? EstadoTarefa.CONCLUIDA : EstadoTarefa.PENDENTE;
            tarefaDAO.atualizarEstado(tarefa.getId(), novoEstado);
            tarefa.setEstado(novoEstado);

            // Atualizar visual
            if (isChecked) {
                holder.txtTituloTarefa.setPaintFlags(
                        holder.txtTituloTarefa.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtTituloTarefa.setAlpha(0.5f);
                holder.txtDisciplinaTarefa.setAlpha(0.5f);
            } else {
                holder.txtTituloTarefa.setPaintFlags(
                        holder.txtTituloTarefa.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.txtTituloTarefa.setAlpha(1.0f);
                holder.txtDisciplinaTarefa.setAlpha(1.0f);
            }

            if (listener != null) {
                listener.onTarefaChanged();
            }
        });

        // Configurar menu de opções
        holder.imgOpcoesTarefa.setOnClickListener(v -> {
            mostrarMenuOpcoes(v, tarefa, position);
        });

        // Click no item abre para edição
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(contexto, AdicionarEditarTarefaActivity.class);
            intent.putExtra("tarefa_id", tarefa.getId());
            contexto.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaTarefas.size();
    }

    private void configurarPrioridade(TarefaViewHolder holder, Prioridade prioridade) {
        int corFundo;
        String texto;

        switch (prioridade) {
            case BAIXA:
                corFundo = ContextCompat.getColor(contexto, R.color.priority_low);
                texto = contexto.getString(R.string.priority_low);
                break;
            case ALTA:
                corFundo = ContextCompat.getColor(contexto, R.color.priority_high);
                texto = contexto.getString(R.string.priority_high);
                break;
            case MEDIA:
            default:
                corFundo = ContextCompat.getColor(contexto, R.color.priority_medium);
                texto = contexto.getString(R.string.priority_medium);
                break;
        }

        holder.txtPrioridadeTarefa.setBackgroundColor(corFundo);
        holder.txtPrioridadeTarefa.setText(texto);
    }

    private void mostrarMenuOpcoes(View view, Tarefa tarefa, int position) {
        PopupMenu popupMenu = new PopupMenu(contexto, view);
        popupMenu.inflate(R.menu.menu_item_opcoes);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_editar) {
                Intent intent = new Intent(contexto, AdicionarEditarTarefaActivity.class);
                intent.putExtra("tarefa_id", tarefa.getId());
                contexto.startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_deletar) {
                confirmarDelecao(tarefa, position);
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    private void confirmarDelecao(Tarefa tarefa, int position) {
        new AlertDialog.Builder(contexto)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_task_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    int linhas = tarefaDAO.deletar(tarefa.getId());

                    if (linhas > 0) {
                        listaTarefas.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaTarefas.size());

                        Toast.makeText(contexto, R.string.success_task_deleted,
                                Toast.LENGTH_SHORT).show();

                        if (listener != null) {
                            listener.onTarefaChanged();
                        }
                    } else {
                        Toast.makeText(contexto, R.string.error_deleting,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void atualizarLista(List<Tarefa> novaLista) {
        this.listaTarefas = novaLista;
        notifyDataSetChanged();
    }

    static class TarefaViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxTarefa;
        TextView txtTituloTarefa;
        TextView txtDisciplinaTarefa;
        TextView txtPrioridadeTarefa;
        TextView txtDataEntregaTarefa;
        ImageView imgOpcoesTarefa;

        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxTarefa = itemView.findViewById(R.id.checkboxTarefa);
            txtTituloTarefa = itemView.findViewById(R.id.txtTituloTarefa);
            txtDisciplinaTarefa = itemView.findViewById(R.id.txtDisciplinaTarefa);
            txtPrioridadeTarefa = itemView.findViewById(R.id.txtPrioridadeTarefa);
            txtDataEntregaTarefa = itemView.findViewById(R.id.txtDataEntregaTarefa);
            imgOpcoesTarefa = itemView.findViewById(R.id.imgOpcoesTarefa);
        }
    }
}
