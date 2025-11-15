package com.example.gestaodetarefasestudos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.AdicionarEditarDisciplinaActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;

import java.util.List;

public class DisciplinaAdapter extends RecyclerView.Adapter<DisciplinaAdapter.DisciplinaViewHolder> {

    private Context contexto;
    private List<Disciplina> listaDisciplinas;
    private DisciplinaDAO disciplinaDAO;
    private OnDisciplinaChangedListener listener;

    public interface OnDisciplinaChangedListener {
        void onDisciplinaChanged();
    }

    public DisciplinaAdapter(Context contexto, List<Disciplina> listaDisciplinas,
                             OnDisciplinaChangedListener listener) {
        this.contexto = contexto;
        this.listaDisciplinas = listaDisciplinas;
        this.listener = listener;
        this.disciplinaDAO = new DisciplinaDAO(contexto);
    }

    @NonNull
    @Override
    public DisciplinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(contexto).inflate(R.layout.item_disciplina, parent, false);
        return new DisciplinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplinaViewHolder holder, int position) {
        Disciplina disciplina = listaDisciplinas.get(position);

        holder.txtNomeDisciplina.setText(disciplina.getNome());
        holder.txtCodigoDisciplina.setText(disciplina.getCodigo());

        // Aplicar cor da disciplina
        try {
            holder.viewCorDisciplina.setBackgroundColor(Color.parseColor(disciplina.getCor()));
        } catch (Exception e) {
            holder.viewCorDisciplina.setBackgroundColor(Color.parseColor("#2196F3"));
        }

        // Configurar menu de opções
        holder.imgOpcoesDisciplina.setOnClickListener(v -> {
            mostrarMenuOpcoes(v, disciplina, position);
        });

        // Click no item abre para edição
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(contexto, AdicionarEditarDisciplinaActivity.class);
            intent.putExtra("disciplina_id", disciplina.getId());
            contexto.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaDisciplinas.size();
    }

    private void mostrarMenuOpcoes(View view, Disciplina disciplina, int position) {
        PopupMenu popupMenu = new PopupMenu(contexto, view);
        popupMenu.inflate(R.menu.menu_item_opcoes);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_editar) {
                Intent intent = new Intent(contexto, AdicionarEditarDisciplinaActivity.class);
                intent.putExtra("disciplina_id", disciplina.getId());
                contexto.startActivity(intent);
                return true;
            } else if (itemId == R.id.menu_deletar) {
                confirmarDelecao(disciplina, position);
                return true;
            }

            return false;
        });

        popupMenu.show();
    }

    private void confirmarDelecao(Disciplina disciplina, int position) {
        new AlertDialog.Builder(contexto)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_subject_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    int linhas = disciplinaDAO.deletar(disciplina.getId());

                    if (linhas > 0) {
                        listaDisciplinas.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, listaDisciplinas.size());

                        Toast.makeText(contexto, R.string.success_subject_deleted,
                                Toast.LENGTH_SHORT).show();

                        if (listener != null) {
                            listener.onDisciplinaChanged();
                        }
                    } else {
                        Toast.makeText(contexto, R.string.error_deleting,
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void atualizarLista(List<Disciplina> novaLista) {
        this.listaDisciplinas = novaLista;
        notifyDataSetChanged();
    }

    static class DisciplinaViewHolder extends RecyclerView.ViewHolder {
        View viewCorDisciplina;
        TextView txtNomeDisciplina;
        TextView txtCodigoDisciplina;
        ImageView imgOpcoesDisciplina;

        public DisciplinaViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCorDisciplina = itemView.findViewById(R.id.viewCorDisciplina);
            txtNomeDisciplina = itemView.findViewById(R.id.txtNomeDisciplina);
            txtCodigoDisciplina = itemView.findViewById(R.id.txtCodigoDisciplina);
            imgOpcoesDisciplina = itemView.findViewById(R.id.imgOpcoesDisciplina);
        }
    }
}
