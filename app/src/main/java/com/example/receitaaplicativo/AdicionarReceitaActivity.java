package com.example.receitaaplicativo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdicionarReceitaActivity extends AppCompatActivity {
    private EditText editTextNomeReceita;
    private EditText editTextNomeIngrediente;
    private EditText editTextPrecoIngrediente;
    private EditText editTextQuantidadeIngrediente;
    private EditText editTextQuantidadeIngredienteUtilizado;
    private Button btnAdicionarIngrediente;
    private Button btnConcluirReceita;

    private Button btnEditarIngrediente;

    private FirebaseFirestore db;
    private List<Map<String, Object>> ingredientes = new ArrayList<>();
    private int ingredienteCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_receita);

        db = FirebaseFirestore.getInstance();

        editTextNomeReceita = findViewById(R.id.editTextNomeReceita);
        editTextNomeIngrediente = findViewById(R.id.editTextNomeIngrediente);
        editTextPrecoIngrediente = findViewById(R.id.editTextPrecoIngrediente);
        editTextQuantidadeIngrediente = findViewById(R.id.editTextQuantidadeIngrediente);
        editTextQuantidadeIngredienteUtilizado = findViewById(R.id.editTextQuantidadeIngredienteUsado);
        btnAdicionarIngrediente = findViewById(R.id.btnAdicionarIngrediente);
        btnConcluirReceita = findViewById(R.id.btnConcluirReceita);
        btnEditarIngrediente = findViewById(R.id.btnEditarIngrediente);

        btnAdicionarIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nomeReceita = editTextNomeReceita.getText().toString();
                String nomeIngrediente = editTextNomeIngrediente.getText().toString();
                String precoIngredienteText = editTextPrecoIngrediente.getText().toString();
                String quantidadeIngredienteText = editTextQuantidadeIngrediente.getText().toString();
                String quantidadeUtilizadaText = editTextQuantidadeIngredienteUtilizado.getText().toString();

                if (nomeReceita.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Digite um nome para a receita", Toast.LENGTH_SHORT).show();
                } else if (nomeIngrediente.isEmpty() || precoIngredienteText.isEmpty() || quantidadeIngredienteText.isEmpty() || quantidadeUtilizadaText.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Preencha todos os campos do ingrediente", Toast.LENGTH_SHORT).show();
                } else {
                    double precoIngrediente = Double.parseDouble(precoIngredienteText);
                    double quantidadeIngrediente = Double.parseDouble(quantidadeIngredienteText);
                    double quantidadeUtilizada = Double.parseDouble(quantidadeUtilizadaText);

                    // Verifique se a receita já existe no documento do usuário
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DocumentReference userRef = db.collection("Usuarios").document(userId);
                        DocumentReference receitaRef = userRef.collection("MinhaReceita").document(nomeReceita);

                        receitaRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Receita já existe no documento do usuário
                                // Verifique se o ingrediente já existe na coleção 'ingredientes'
                                CollectionReference ingredientesCollection = receitaRef.collection("ingredientes");

                                ingredientesCollection.whereEqualTo("nomeIngrediente", nomeIngrediente)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                // Ingrediente já existe na receita
                                                Toast.makeText(AdicionarReceitaActivity.this, "Este ingrediente já está na receita.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Ingrediente não existe na receita, podemos adicioná-lo

                                                // Usar o nome do ingrediente como nome do documento
                                                DocumentReference novoIngredienteRef = ingredientesCollection.document(nomeIngrediente);

                                                // Preencher os detalhes do ingrediente no documento
                                                Map<String, Object> ingredienteData = new HashMap<>();
                                                ingredienteData.put("nomeIngrediente", nomeIngrediente);
                                                ingredienteData.put("preco", precoIngrediente);
                                                ingredienteData.put("quantidade", quantidadeIngrediente);
                                                ingredienteData.put("quantidadeUtilizada", quantidadeUtilizada);

                                                // Adicionar o documento do ingrediente ao Firestore
                                                novoIngredienteRef.set(ingredienteData).addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(AdicionarReceitaActivity.this, "Ingrediente adicionado à receita com sucesso", Toast.LENGTH_SHORT).show();
                                                    // Limpe os campos de texto para o próximo ingrediente
                                                    editTextNomeIngrediente.setText("");
                                                    editTextPrecoIngrediente.setText("");
                                                    editTextQuantidadeIngrediente.setText("");
                                                    editTextQuantidadeIngredienteUtilizado.setText("");
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(AdicionarReceitaActivity.this, "Erro ao adicionar o ingrediente à receita", Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        });
                            } else {
                                // Receita não encontrada no documento do usuário
                                Toast.makeText(AdicionarReceitaActivity.this, "Receita não encontrada, crie a receita primeiro", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Usuário não está autenticado
                        Toast.makeText(AdicionarReceitaActivity.this, "Usuário não autenticado, faça login primeiro", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button btnEditarIngrediente = findViewById(R.id.btnEditarIngrediente);
        btnEditarIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nomeReceita = editTextNomeReceita.getText().toString();
                String nomeIngrediente = editTextNomeIngrediente.getText().toString();
                String precoIngredienteText = editTextPrecoIngrediente.getText().toString();
                String quantidadeIngredienteText = editTextQuantidadeIngrediente.getText().toString();
                String quantidadeUtilizadaText = editTextQuantidadeIngredienteUtilizado.getText().toString();

                if (nomeReceita.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Digite um nome para a receita", Toast.LENGTH_SHORT).show();
                } else if (nomeIngrediente.isEmpty() || precoIngredienteText.isEmpty() || quantidadeIngredienteText.isEmpty() || quantidadeUtilizadaText.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Preencha todos os campos do ingrediente", Toast.LENGTH_SHORT).show();
                } else {
                    double precoIngrediente = Double.parseDouble(precoIngredienteText);
                    double quantidadeIngrediente = Double.parseDouble(quantidadeIngredienteText);
                    double quantidadeUtilizada = Double.parseDouble(quantidadeUtilizadaText);

                    // Verifique se a receita já existe no documento do usuário
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DocumentReference userRef = db.collection("Usuarios").document(userId);
                        DocumentReference receitaRef = userRef.collection("MinhaReceita").document(nomeReceita);

                        receitaRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Receita já existe no documento do usuário
                                // Verifique se o ingrediente já existe na coleção 'ingredientes'
                                CollectionReference ingredientesCollection = receitaRef.collection("ingredientes");

                                ingredientesCollection.whereEqualTo("nomeIngrediente", nomeIngrediente)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                // Ingrediente já existe na receita
                                                // Execute a lógica de edição do ingrediente
                                                DocumentSnapshot ingredienteSnapshot = task.getResult().getDocuments().get(0);

                                                // Atualize os campos do ingrediente
                                                Map<String, Object> updatedData = new HashMap<>();
                                                updatedData.put("preco", precoIngrediente);
                                                updatedData.put("quantidade", quantidadeIngrediente);
                                                updatedData.put("quantidadeUtilizada", quantidadeUtilizada);

                                                // Atualize o documento do ingrediente
                                                ingredienteSnapshot.getReference().update(updatedData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(AdicionarReceitaActivity.this, "Ingrediente editado com sucesso", Toast.LENGTH_SHORT).show();
                                                            // Limpe os campos de texto para o próximo ingrediente
                                                            editTextNomeIngrediente.setText("");
                                                            editTextPrecoIngrediente.setText("");
                                                            editTextQuantidadeIngrediente.setText("");
                                                            editTextQuantidadeIngredienteUtilizado.setText("");
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(AdicionarReceitaActivity.this, "Erro ao editar o ingrediente", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                // Ingrediente não encontrado na receita
                                                Toast.makeText(AdicionarReceitaActivity.this, "Este ingrediente não está na receita.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Receita não encontrada no documento do usuário
                                Toast.makeText(AdicionarReceitaActivity.this, "Receita não encontrada, crie a receita primeiro", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Usuário não está autenticado
                        Toast.makeText(AdicionarReceitaActivity.this, "Usuário não autenticado, faça login primeiro", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnEditarIngrediente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nomeReceita = editTextNomeReceita.getText().toString();
                String nomeIngrediente = editTextNomeIngrediente.getText().toString();
                String precoIngredienteText = editTextPrecoIngrediente.getText().toString();
                String quantidadeIngredienteText = editTextQuantidadeIngrediente.getText().toString();
                String quantidadeUtilizadaText = editTextQuantidadeIngredienteUtilizado.getText().toString();

                if (nomeReceita.isEmpty() || nomeIngrediente.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Nome da receita e nome do ingrediente são obrigatórios", Toast.LENGTH_SHORT).show();
                } else if (precoIngredienteText.isEmpty() || quantidadeIngredienteText.isEmpty() || quantidadeUtilizadaText.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Preencha todos os campos do ingrediente", Toast.LENGTH_SHORT).show();
                } else {
                    double precoIngrediente = Double.parseDouble(precoIngredienteText);
                    double quantidadeIngrediente = Double.parseDouble(quantidadeIngredienteText);
                    double quantidadeUtilizada = Double.parseDouble(quantidadeUtilizadaText);

                    // Verifique se a receita já existe no documento do usuário
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DocumentReference userRef = db.collection("Usuarios").document(userId);
                        DocumentReference receitaRef = userRef.collection("MinhaReceita").document(nomeReceita);

                        receitaRef.get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                // Receita já existe no documento do usuário
                                // Verifique se o ingrediente já existe na coleção 'ingredientes'
                                CollectionReference ingredientesCollection = receitaRef.collection("ingredientes");

                                ingredientesCollection.whereEqualTo("nomeIngrediente", nomeIngrediente)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                                // Ingrediente já existe na receita
                                                // Execute a lógica de edição do ingrediente
                                                DocumentSnapshot ingredienteSnapshot = task.getResult().getDocuments().get(0);

                                                // Atualize os campos do ingrediente
                                                Map<String, Object> updatedData = new HashMap<>();
                                                updatedData.put("preco", precoIngrediente);
                                                updatedData.put("quantidade", quantidadeIngrediente);
                                                updatedData.put("quantidadeUtilizada", quantidadeUtilizada);

                                                // Atualize o documento do ingrediente
                                                ingredienteSnapshot.getReference().update(updatedData)
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(AdicionarReceitaActivity.this, "Ingrediente editado com sucesso", Toast.LENGTH_SHORT).show();
                                                            // Limpe os campos de texto para o próximo ingrediente
                                                            editTextNomeIngrediente.setText("");
                                                            editTextPrecoIngrediente.setText("");
                                                            editTextQuantidadeIngrediente.setText("");
                                                            editTextQuantidadeIngredienteUtilizado.setText("");
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(AdicionarReceitaActivity.this, "Erro ao editar o ingrediente", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                // Ingrediente não encontrado na receita
                                                Toast.makeText(AdicionarReceitaActivity.this, "Este ingrediente não está na receita.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Receita não encontrada no documento do usuário
                                Toast.makeText(AdicionarReceitaActivity.this, "Receita não encontrada, crie a receita primeiro", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // Usuário não está autenticado
                        Toast.makeText(AdicionarReceitaActivity.this, "Usuário não autenticado, faça login primeiro", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });









        btnConcluirReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nomeReceita = editTextNomeReceita.getText().toString().trim();

                if (nomeReceita.isEmpty()) {
                    Toast.makeText(AdicionarReceitaActivity.this, "Digite um nome para a receita", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verifique se o usuário está autenticado
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // O usuário está autenticado, obtenha o ID do usuário
                    final String userId = user.getUid();

                    // Verifique se a receita já existe no documento do usuário
                    DocumentReference userRef = db.collection("Usuarios").document(userId);
                    DocumentReference receitaRef = userRef.collection("MinhaReceita").document(nomeReceita);

                    receitaRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // A receita já existe no documento do usuário
                            Toast.makeText(AdicionarReceitaActivity.this, "Essa Receita já existe, favor usar adicionar ingrediente.", Toast.LENGTH_SHORT).show();
                        } else {
                            // A receita não existe no documento do usuário, então podemos criar a receita
                            Map<String, Object> novaReceita = new HashMap<>();
                            novaReceita.put("nomeReceita", nomeReceita);

                            userRef.collection("MinhaReceita").document(nomeReceita).set(novaReceita)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdicionarReceitaActivity.this, "Receita adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdicionarReceitaActivity.this, "Erro ao adicionar a receita", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    });
                } else {
                    // Usuário não está autenticado
                    Toast.makeText(AdicionarReceitaActivity.this, "Usuário não autenticado, faça login primeiro", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
