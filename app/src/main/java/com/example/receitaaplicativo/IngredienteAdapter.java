package com.example.receitaaplicativo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class IngredienteAdapter extends RecyclerView.Adapter<IngredienteAdapter.IngredienteViewHolder> {
    private List<Ingrediente> ingredientesList;

    public IngredienteAdapter(List<Ingrediente> ingredientesList) {
        this.ingredientesList = ingredientesList;
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

        public IngredienteViewHolder(View itemView) {
            super(itemView);
            txtNomeIngrediente = itemView.findViewById(R.id.editTextNomeIngrediente);
            txtPrecoIngrediente = itemView.findViewById(R.id.editTextPrecoIngrediente);
            txtQuantidadeIngrediente = itemView.findViewById(R.id.editTextQuantidadeIngrediente);
            txtQuantidadeUtilizada = itemView.findViewById(R.id.editTextQuantidadeIngredienteUtilizado); // Inicialize este campo
        }

        public void bindIngrediente(Ingrediente ingrediente) {
            txtNomeIngrediente.setText("Nome: " + ingrediente.getNomeIngrediente());
            txtPrecoIngrediente.setText("Preço: R$" + ingrediente.getPreco());
            txtQuantidadeIngrediente.setText("Quantidade: " + ingrediente.getQuantidade());
            txtQuantidadeUtilizada.setText("Quantidade Utilizada: " + ingrediente.getQuantidadeUtilizada()); // Configure a quantidade utilizada
        }
    }
}
