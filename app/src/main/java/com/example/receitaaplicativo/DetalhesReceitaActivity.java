package com.example.receitaaplicativo;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DetalhesReceitaActivity extends AppCompatActivity {

    private TextView textViewNomeReceita;
    private RecyclerView recyclerViewIngredientes;
    private List<Ingrediente> ingredientesList;
    private IngredienteAdapter ingredienteAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_receita);

        textViewNomeReceita = findViewById(R.id.txtNomeIngrediente);
        recyclerViewIngredientes = findViewById(R.id.recyclerViewIngredientes);
        db = FirebaseFirestore.getInstance();

        // Obtenha o nome da receita da Intent (substitua "nomeReceita" pela chave real)
        String nomeReceita = getIntent().getStringExtra("nomeReceita");

        // Defina o nome da receita na TextView
        textViewNomeReceita.setText(nomeReceita);

        // Configure o RecyclerView
        recyclerViewIngredientes.setLayoutManager(new LinearLayoutManager(this));

        // Crie uma lista de ingredientes vazia
        ingredientesList = new ArrayList<>();

        // Passe a lista de ingredientes para o construtor do adaptador
        ingredienteAdapter = new IngredienteAdapter(ingredientesList);

        recyclerViewIngredientes.setAdapter(ingredienteAdapter);

        // Obtenha os ingredientes da receita a partir do Firestore
        obterIngredientesDaReceita(nomeReceita);
    }

    private void obterIngredientesDaReceita(String nomeReceita) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            CollectionReference ingredientesRef = db.collection("Usuarios")
                    .document(userId)
                    .collection("MinhaReceita")
                    .document(nomeReceita)
                    .collection("ingredientes");

            ingredientesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    ingredientesList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Ingrediente ingrediente = document.toObject(Ingrediente.class);
                        ingredientesList.add(ingrediente);
                    }
                    ingredienteAdapter.notifyDataSetChanged();

                    // Agora que você tem a lista de ingredientes, você pode calcular o custo total
                    double custoTotal = calcularCustoTotal(ingredientesList);

                    // Encontre o TextView no layout e defina o texto
                    TextView txtCustoTotal = findViewById(R.id.text_custoTotal);
                    txtCustoTotal.setText("Custo Total da Receita: R$" + custoTotal);
                } else {
                    // Lide com o caso em que não há ingredientes
                    // Por exemplo, exibindo uma mensagem de erro
                }
            });
        }
    }

    private double calcularCustoTotal(List<Ingrediente> ingredientes) {
        double custoTotal = 0;
        for (Ingrediente ingrediente : ingredientes) {
            custoTotal += ingrediente.calcularCusto();
        }
        return custoTotal;
    }
}
