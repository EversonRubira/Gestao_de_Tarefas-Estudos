package com.example.gestaodetarefasestudos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestaodetarefasestudos.AdicionarEditarDisciplinaActivity;
import com.example.gestaodetarefasestudos.DetalhesDisciplinaActivity;
import com.example.gestaodetarefasestudos.R;
import com.example.gestaodetarefasestudos.database.AppDatabase;
import com.example.gestaodetarefasestudos.database.dao.DisciplinaRoomDAO;
import com.example.gestaodetarefasestudos.models.Disciplina;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisciplinaAdapter extends RecyclerView.Adapter<DisciplinaAdapter.DisciplinaViewHolder> {

    private Context context;
    private List<Disciplina> lista;
    private DisciplinaRoomDAO dao;
    private OnDisciplinaChangedListener listener;
    private ExecutorService executor;

    public interface OnDisciplinaChangedListener {
        void onDisciplinaChanged();
    }

    public DisciplinaAdapter(Context context, List<Disciplina> lista,
                             OnDisciplinaChangedListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
        this.dao = AppDatabase.getInstance(context).disciplinaDAO();
        this.executor = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public DisciplinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_disciplina, parent, false);
        return new DisciplinaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplinaViewHolder holder, int position) {
        if (position < 0 || position >= lista.size()) return;
        Disciplina disciplina = lista.get(position);

        holder.txtNome.setText(disciplina.getNome());
        holder.txtCodigo.setText(disciplina.getCodigo());

        // cor da disciplina
        try {
            holder.viewCor.setBackgroundColor(Color.parseColor(disciplina.getCor()));
        } catch (Exception e) {
            holder.viewCor.setBackgroundColor(Color.parseColor("#2196F3"));
        }

        holder.imgOpcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu(v, disciplina, position);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetalhesDisciplinaActivity.class);
                intent.putExtra(DetalhesDisciplinaActivity.EXTRA_DISCIPLINA_ID, disciplina.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    private void showOptionsMenu(View view, Disciplina disciplina, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.menu_item_opcoes);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.menu_editar) {
                    Intent intent = new Intent(context, AdicionarEditarDisciplinaActivity.class);
                    intent.putExtra("disciplina_id", disciplina.getId());
                    context.startActivity(intent);
                    return true;
                } else if (itemId == R.id.menu_deletar) {
                    confirmDelete(disciplina, position);
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void confirmDelete(Disciplina disciplina, int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.confirm_delete_subject_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        executor.execute(() -> {
                            int linhas = dao.deletar(disciplina);

                            ((android.app.Activity) context).runOnUiThread(() -> {
                                if (linhas > 0 && position < lista.size()) {
                                    lista.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, lista.size() - position);

                                    Toast.makeText(context, R.string.success_subject_deleted,
                                            Toast.LENGTH_SHORT).show();

                                    if (listener != null) {
                                        listener.onDisciplinaChanged();
                                    }
                                } else {
                                    Toast.makeText(context, R.string.error_deleting,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void atualizarLista(List<Disciplina> novaLista) {
        this.lista = novaLista;
        notifyDataSetChanged();
    }

    /**
     * Libera recursos do executor. Deve ser chamado quando o adapter não for mais utilizado.
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    static class DisciplinaViewHolder extends RecyclerView.ViewHolder {
        View viewCor;
        TextView txtNome;
        TextView txtCodigo;
        ImageView imgOpcoes;

        public DisciplinaViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCor = itemView.findViewById(R.id.viewCorDisciplina);
            txtNome = itemView.findViewById(R.id.txtNomeDisciplina);
            txtCodigo = itemView.findViewById(R.id.txtCodigoDisciplina);
            imgOpcoes = itemView.findViewById(R.id.imgOpcoesDisciplina);
        }
    }
}
