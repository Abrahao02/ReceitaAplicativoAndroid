package com.example.receitaaplicativo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;




public class ListarIngredientesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewIngredientes;
    private IngredienteAdapter ingredienteAdapter;
    private List<Ingrediente> ingredientesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_ingredientes);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewIngredientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ingredientesList = new ArrayList<>();
        ingredienteAdapter = new IngredienteAdapter(ingredientesList);
        recyclerView.setAdapter(ingredienteAdapter);

        // Recupere o ID do usuário logado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Recupere as receitas do Firestore associadas ao usuário
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Usuarios")
                    .document(userId)
                    .collection("MinhaReceita")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Para cada receita, acesse a coleção de ingredientes
                                    String receitaId = document.getId();
                                    db.collection("Usuarios")
                                            .document(userId)
                                            .collection("MinhaReceita")
                                            .document(receitaId)
                                            .collection("ingredientes")
                                            .get()
                                            .addOnCompleteListener(ingredientesTask -> {
                                                if (ingredientesTask.isSuccessful()) {
                                                    QuerySnapshot ingredientesSnapshot = ingredientesTask.getResult();
                                                    if (ingredientesSnapshot != null) {
                                                        for (DocumentSnapshot ingredienteDocument : ingredientesSnapshot.getDocuments()) {
                                                            Ingrediente ingrediente = ingredienteDocument.toObject(Ingrediente.class);
                                                            if (ingrediente != null) {
                                                                ingredientesList.add(ingrediente);
                                                            } else {
                                                                Log.e("ListarIngredientesActivity", "Ingrediente nulo para a receita " + receitaId);
                                                            }
                                                        }
                                                        ingredienteAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.d("ListarIngredientesActivity", "Nenhum ingrediente encontrado para a receita " + receitaId);
                                                    }
                                                } else {
                                                    Log.e("ListarIngredientesActivity", "Erro ao recuperar ingredientes da receita " + receitaId, ingredientesTask.getException());
                                                }
                                            });
                                }
                            } else {
                                Log.d("ListarIngredientesActivity", "Nenhuma receita encontrada para o usuário.");
                            }
                        } else {
                            Log.e("ListarIngredientesActivity", "Erro ao recuperar receitas.", task.getException());
                        }
                    });
        }
    }

}

