package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        receitaAdapter.setOnItemClickListener(new ReceitaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Receita receita, String descricaoReceita) {
                // Quando um nome de receita é clicado, abra a atividade "Detalhes da Receita"
                Intent intent = new Intent(VerReceitaActivity.this, DetalhesReceitaActivity.class);
                intent.putExtra("nomeReceita", receita.getNomeReceita());
                intent.putExtra("descricaoReceita", descricaoReceita);
                startActivity(intent);
            }


            public void onExcluirClick(int position) {
                // Trate a exclusão da receita aqui
                receitaAdapter.removerReceita(position);
            }
        });


        // Inicialize a classe de carregamento de receitas
        carregadorDeReceitas = new MinhaClasseDeCarregamentoDeReceitas();
        carregarDadosDoFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Recarregue os dados do RecyclerView quando a atividade for retomada
        carregarDadosDoFirebase();
    }

    private void carregarDadosDoFirebase() {
        carregadorDeReceitas.obterNomesEDescricaoDeReceitas(new MinhaClasseDeCarregamentoDeReceitas.onNomesEDescricaoDeReceitasCarregados() {
            public void onNomesEDescricaoDeReceitasCarregados(List<String> nomesDeReceitas, List<String> descricoesDeReceitas) {
                Log.d("ReceitaApp", "Nomes de receitas carregados. Quantidade de nomes de receitas: " + nomesDeReceitas.size());

                // Adicione este log para verificar os nomes das receitas
                for (int i = 0; i < nomesDeReceitas.size(); i++) {
                    String nomeReceita = nomesDeReceitas.get(i);
                    String descricaoReceita = descricoesDeReceitas.get(i);
                    Log.d("ReceitaApp", "Nome de receita: " + nomeReceita);
                    Log.d("ReceitaApp", "Descrição de receita: " + descricaoReceita);
                }

                // Atualize o adaptador com os nomes das receitas
                receitaAdapter.setNomesEDescricoesDeReceitas(nomesDeReceitas, descricoesDeReceitas);
                recyclerView.setAdapter(receitaAdapter);
            }
        });
    }
}
