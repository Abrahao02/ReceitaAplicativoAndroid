package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class VerReceitaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReceitaAdapter receitaAdapter;
    private MinhaClasseDeCarregamentoDeReceitas carregadorDeReceitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_receita);

        Log.d("ReceitaApp", "Activity VerReceitaActivity criada.");

        // Inicialize o RecyclerView e o adaptador
        recyclerView = findViewById(R.id.recyclerViewReceitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        receitaAdapter = new ReceitaAdapter();

        Log.d("ReceitaApp", "RecyclerView e adaptador inicializados.");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(receitaAdapter);

        // Configure um ouvinte de clique para os itens do RecyclerView
        receitaAdapter.setOnItemClickListener(new ReceitaAdapter.OnItemClickListener() {
            public void onItemClick(Receita receita) {
                // Quando um nome de receita é clicado, abra a atividade "Detalhes da Receita"
                Intent intent = new Intent(VerReceitaActivity.this, DetalhesReceitaActivity.class);
                intent.putExtra("nomeReceita", receita.getNomeReceita());
                startActivity(intent);
            }
        });

        // Inicialize a classe de carregamento de receitas
        carregadorDeReceitas = new MinhaClasseDeCarregamentoDeReceitas();

        carregadorDeReceitas.obterNomesDeReceitas(new MinhaClasseDeCarregamentoDeReceitas.NomesDeReceitasCallback() {
            public void onNomesDeReceitasCarregados(List<String> nomesDeReceitas) {
                Log.d("ReceitaApp", "Nomes de receitas carregados. Quantidade de nomes de receitas: " + nomesDeReceitas.size());

                // Adicione este log para verificar os nomes das receitas
                for (String nomeReceita : nomesDeReceitas) {
                    Log.d("ReceitaApp", "Nome de receita: " + nomeReceita);
                }

                // Atualize o adaptador com os nomes das receitas
                receitaAdapter.setNomesDeReceitas(nomesDeReceitas);
                recyclerView.setAdapter(receitaAdapter);
            }
        });


    }
}

