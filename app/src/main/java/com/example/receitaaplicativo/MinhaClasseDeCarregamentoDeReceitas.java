package com.example.receitaaplicativo;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;
import java.util.List;

public class MinhaClasseDeCarregamentoDeReceitas {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void obterNomesDeReceitas(NomesDeReceitasCallback callback) {
        List<String> nomesDeReceitas = new ArrayList<>();
        CollectionReference usuariosRef = db.collection("Usuarios");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            usuariosRef.document(userId)
                    .collection("MinhaReceita")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nomeReceita = document.getString("nomeReceita");
                                nomesDeReceitas.add(nomeReceita);
                            }

                            callback.onNomesDeReceitasCarregados(nomesDeReceitas);
                        } else {
                            // Lidar com erros ao buscar dados do Firestore
                            Log.e("ReceitaApp", "Erro ao obter nomes de receitas: " + task.getException());
                        }
                    });
        }
    }

    public interface NomesDeReceitasCallback {
        void onNomesDeReceitasCarregados(List<String> nomesDeReceitas);
    }


    public void obterReceitas(ReceitasCallback callback) {
        List<Receita> listaDeReceitas = new ArrayList<>();
        CollectionReference usuariosRef = db.collection("Usuarios");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            Log.d("ReceitaApp", "ID do Usuário: " + userId);
            usuariosRef.document(userId)
                    .collection("MinhaReceita")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nomeReceita = document.getString("nomeReceita");
                                listaDeReceitas.add(new Receita(nomeReceita));
                            }

                            Log.d("ReceitaApp", "Receitas carregadas. Quantidade de receitas: " + listaDeReceitas.size());

                            callback.onReceitasCarregadas(listaDeReceitas);
                        } else {
                            // Lidar com erros ao buscar dados do Firestore
                            Log.e("ReceitaApp", "Erro ao obter receitas: " + task.getException());
                        }
                    });
        }
    }

    public interface ReceitasCallback {
        void onReceitasCarregadas(List<Receita> receitas);
    }
}