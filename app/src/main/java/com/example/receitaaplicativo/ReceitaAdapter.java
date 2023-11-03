package com.example.receitaaplicativo;



import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder> {

    private List<Receita> receitas;
    Receita minhaReceita = new Receita("Nome da Receita", "Descrição da Receita");

    private OnItemClickListener onItemClickListener; // Adicionado o OnItemClickListener

    @NonNull
    @Override
    public ReceitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receita, parent, false);
        return new ReceitaViewHolder(itemView);
    }

    // ...
    @Override
    public void onBindViewHolder(@NonNull ReceitaViewHolder holder, int position) {
        Receita receita = receitas.get(position);
        holder.bind(receita);

        Log.d("ReceitaApp", "Receita exibida no RecyclerView: " + receita.getNomeReceita());

        // Configurar o OnClickListener para o item do RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(receita, receita.getDescricaoReceita());
                }
            }
        });

        // Configurar o clique do botão de exclusão
        holder.imageViewExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    // Mostrar um AlertDialog de confirmação
                    showConfirmationDialog(holder.getAdapterPosition(), v.getContext());
                }
            }
        });
    }

// ...

    private void showConfirmationDialog(final int position, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmação");
        builder.setMessage("Tem certeza de que deseja excluir esta receita?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // O usuário confirmou a exclusão, agora você pode chamar o método onExcluirClick
                onItemClickListener.onExcluirClick(position);
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // O usuário cancelou a exclusão, não é necessário fazer nada
            }
        });

        builder.show();
    }


    @Override
    public int getItemCount() {
        return receitas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setNomesEDescricoesDeReceitas(List<String> nomesDeReceitas, List<String> descricoesDeReceitas) {
        List<Receita> receitas = new ArrayList<>();
        for (int i = 0; i < nomesDeReceitas.size(); i++) {
            String nomeReceita = nomesDeReceitas.get(i);
            String descricaoReceita = descricoesDeReceitas.get(i);

            Log.d("ReceitaApp", "Criado objeto Receita com nome: " + nomeReceita + " e descrição: " + descricaoReceita);
            Receita receita = new Receita(nomeReceita, descricaoReceita);

            receitas.add(receita);
        }
        setReceitas(receitas);
    }

    public void removerReceita(int position) {
        if (position >= 0 && position < receitas.size()) {
            Receita receita = receitas.get(position);
            String nomeReceita = receita.getNomeReceita();

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Exclua os ingredientes associados a esta receita
                db.collection("Usuarios")
                        .document(userId)
                        .collection("MinhaReceita")
                        .document(nomeReceita)
                        .collection("ingredientes")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot ingredientesSnapshot = task.getResult();
                                if (ingredientesSnapshot != null && !ingredientesSnapshot.isEmpty()) {
                                    // Exclua cada ingrediente individualmente
                                    for (DocumentSnapshot ingredienteDoc : ingredientesSnapshot.getDocuments()) {
                                        String ingredienteId = ingredienteDoc.getId();
                                        db.collection("Usuarios")
                                                .document(userId)
                                                .collection("MinhaReceita")
                                                .document(nomeReceita)
                                                .collection("ingredientes")
                                                .document(ingredienteId)
                                                .delete();
                                    }
                                }

                                // Agora que os ingredientes foram excluídos, você pode excluir a receita
                                db.collection("Usuarios")
                                        .document(userId)
                                        .collection("MinhaReceita")
                                        .document(nomeReceita)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            receitas.remove(position); // Remove a receita da lista
                                            notifyItemRemoved(position); // Notifica o RecyclerView
                                            Log.d("ExcluirReceita", "Receita excluída com sucesso");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("ExcluirReceita", "Erro ao excluir a receita", e);
                                        });
                            } else {
                                Log.e("ExcluirReceita", "Erro ao recuperar ingredientes", task.getException());
                            }
                        });
            }
        }
    }



    public ReceitaAdapter() {
        this.receitas = new ArrayList<>();
    }

    // Método para definir as receitas no adaptador
    public void setReceitas(List<Receita> receitas) {
        this.receitas = receitas;
        notifyDataSetChanged(); // Notificar o RecyclerView sobre a mudança nos dados

        // Adicione este log para verificar as instâncias de Receita
        for (Receita receita : receitas) {
            Log.d("ReceitaApp", "Receita definida: " + receita.getNomeReceita());
        }
    }

    // Interface OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(Receita receita, String descricaoReceita);
        void onExcluirClick(int position);
    }


    public class ReceitaViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNomeReceita;
        private ImageView imageViewExcluir;

        public ReceitaViewHolder(View itemView) {
            super(itemView);
            textViewNomeReceita = itemView.findViewById(R.id.txtNomeReceita);
            imageViewExcluir = itemView.findViewById(R.id.imageViewExcluirReceita);


        }

        public void bind(Receita receita) {
            textViewNomeReceita.setText(receita.getNomeReceita());

            // Adicione este log para verificar o nome da receita definido no TextView
            Log.d("ReceitaApp", "Nome da receita no ViewHolder: " + receita.getNomeReceita());
        }
    }
}
