package com.example.receitaaplicativo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter<ReceitaAdapter.ReceitaViewHolder> {

    private List<Receita> receitas;
    private OnItemClickListener onItemClickListener; // Adicionado o OnItemClickListener


    @NonNull
    @Override
    public ReceitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receita, parent, false);
        return new ReceitaViewHolder(itemView);
    }

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
                    onItemClickListener.onItemClick(receita);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return receitas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setNomesDeReceitas(List<String> nomesDeReceitas) {
        List<Receita> receitas = new ArrayList<>();
        for (String nomeReceita : nomesDeReceitas) {
            Log.d("ReceitaApp", "Criado objeto Receita com nome: " + nomeReceita);
            Receita receita = new Receita(nomeReceita);

            // Certifique-se de definir o nome da receita corretamente
            receita.setNomeReceita(nomeReceita);

            receitas.add(receita);
        }
        setReceitas(receitas);
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
        void onItemClick(Receita receita);
    }

    public class ReceitaViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewNomeReceita;

        public ReceitaViewHolder(View itemView) {
            super(itemView);
            textViewNomeReceita = itemView.findViewById(R.id.txtNomeReceita);
        }

        public void bind(Receita receita) {
            textViewNomeReceita.setText(receita.getNomeReceita());

            // Adicione este log para verificar o nome da receita definido no TextView
            Log.d("ReceitaApp", "Nome da receita no ViewHolder: " + receita.getNomeReceita());
        }

    }
}
