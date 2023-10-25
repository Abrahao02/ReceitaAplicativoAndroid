package com.example.receitaaplicativo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class IngredienteAdapter extends RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder> {
    private List<Ingrediente> ingredientesList;
    private boolean isEditing;
    private String nomeReceita;


    public IngredienteAdapter(List<Ingrediente> ingredientesList, boolean isEditing, String nomeReceita) {
        this.ingredientesList = ingredientesList;
        this.isEditing = isEditing;
        this.nomeReceita = nomeReceita;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public boolean isEditing() {
        return isEditing;
    }


    @NonNull
    @Override
    public IngredienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredientes_cadastrados, parent, false);
        return new IngredienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredienteViewHolder holder, int position) {
        Ingrediente ingrediente = ingredientesList.get(position);
        holder.bindIngrediente(ingrediente);

        // Configurar um ouvinte de clique para o ícone de exclusão
        holder.imageViewExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("IngredienteAdapter", "Botão de exclusão clicado");
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Chame a função para excluir o ingrediente
                    excluirIngrediente(adapterPosition);
                }
            }
        });

    }

    public void excluirIngrediente(int position) {
        if (position >= 0 && position < ingredientesList.size()) {
            Ingrediente ingrediente = ingredientesList.get(position);
            String nomeIngrediente = ingrediente.getNomeIngrediente();
            String nomeReceita = this.nomeReceita;

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                Log.d("ExcluirIngrediente", "ID do usuário autenticado: " + userId);
                Log.d("ExcluirIngrediente", "Nome da Receita a ser excluído: " + nomeReceita);

                Log.d("ExcluirIngrediente", "Nome do ingrediente a ser excluído: " + nomeIngrediente);


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Usuarios")
                        .document(userId)
                        .collection("MinhaReceita")
                        .document(nomeReceita)
                        .collection("ingredientes")
                        .whereEqualTo("nomeIngrediente", nomeIngrediente)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                QuerySnapshot ingredientesSnapshot = task.getResult();
                                if (ingredientesSnapshot != null && !ingredientesSnapshot.isEmpty()) {
                                    db.collection("Usuarios")
                                            .document(userId)
                                            .collection("MinhaReceita")
                                            .document(nomeReceita)
                                            .collection("ingredientes")
                                            .document(ingredientesSnapshot.getDocuments().get(0).getId())
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                ingredientesList.remove(position); // Remove o ingrediente da lista
                                                notifyItemRemoved(position); // Notifica o RecyclerView
                                                Log.d("ExcluirIngrediente", "Ingrediente excluído com sucesso");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("ExcluirIngrediente", "Erro ao excluir o ingrediente", e);
                                            });
                                } else {
                                    Log.e("ExcluirIngrediente", "Nenhum ingrediente encontrado com o nome especificado.");
                                }
                            } else {
                                Log.e("ExcluirIngrediente", "Erro ao recuperar ingredientes", task.getException());
                            }
                        });
            }
        }
    }


    @Override
    public int getItemCount() {
        return ingredientesList.size();
    }

    public class IngredienteViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNomeIngrediente;
        private TextView txtPrecoIngrediente;
        private TextView txtQuantidadeIngrediente;
        private TextView txtQuantidadeUtilizada; // Adicione este campo
        private ImageView imageViewExcluir;

        public IngredienteViewHolder(View itemView) {
            super(itemView);
            txtNomeIngrediente = itemView.findViewById(R.id.editTextNomeIngrediente);
            txtPrecoIngrediente = itemView.findViewById(R.id.editTextPrecoIngrediente);
            txtQuantidadeIngrediente = itemView.findViewById(R.id.editTextQuantidadeIngrediente);
            txtQuantidadeUtilizada = itemView.findViewById(R.id.editTextQuantidadeIngredienteUtilizado);
            imageViewExcluir = itemView.findViewById(R.id.imageViewExcluir);
        }

        public void bindIngrediente(Ingrediente ingrediente) {
            txtNomeIngrediente.setText("Nome: " + ingrediente.getNomeIngrediente());
            txtPrecoIngrediente.setText("Preço: R$" + ingrediente.getPreco());
            txtQuantidadeIngrediente.setText("Quantidade: " + ingrediente.getQuantidade());
            txtQuantidadeUtilizada.setText("Quantidade Utilizada: " + ingrediente.getQuantidadeUtilizada());
        }
    }

}

