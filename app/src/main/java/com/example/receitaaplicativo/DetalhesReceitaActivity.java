package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;


import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DetalhesReceitaActivity extends AppCompatActivity {

    private String userId;
    private TextView textViewNomeReceita;
    private RecyclerView recyclerViewIngredientes;
    private List<Ingrediente> ingredientesList;
    private IngredienteAdapter ingredienteAdapter;
    private FirebaseFirestore db;
    private EditText editTextNomeReceita;
    private TextView textViewDescricaoReceita;
    private EditText editTextDescricaoReceita;
    private Button btnEditar;
    private Button btnSalvar;
    private boolean isEditing = false;
    private String nomeReceita;
    private String descricaoReceita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_receita);

        textViewNomeReceita = findViewById(R.id.txtNomeIngrediente);
        textViewDescricaoReceita = findViewById(R.id.txtDescricaoReceita);
        recyclerViewIngredientes = findViewById(R.id.recyclerViewIngredientes);
        db = FirebaseFirestore.getInstance();

        // Obtenha o nome da receita da Intent (substitua "nomeReceita" pela chave real)
        nomeReceita = getIntent().getStringExtra("nomeReceita");

        // Obtenha a descrição da Intent
        descricaoReceita = getIntent().getStringExtra("descricaoReceita");

        // Verifique se a descrição é nula antes de definir o texto
        if (descricaoReceita != null) {
            textViewDescricaoReceita.setText(descricaoReceita);
        } else {
            // Caso a descrição seja nula, defina um texto padrão ou uma mensagem alternativa
            textViewDescricaoReceita.setText("Descrição não criada ainda");
        }

        // Defina o nome da receita na TextView
        textViewNomeReceita.setText(nomeReceita);

        // Configure o RecyclerView
        recyclerViewIngredientes.setLayoutManager(new LinearLayoutManager(this));

        // Crie uma lista de ingredientes vazia
        ingredientesList = new ArrayList<>();

        // Passe a lista de ingredientes para o construtor do adaptador
        ingredienteAdapter = new IngredienteAdapter(ingredientesList, isEditing, nomeReceita);


        recyclerViewIngredientes.setAdapter(ingredienteAdapter);

        editTextNomeReceita = findViewById(R.id.editTextNomeReceita);
        editTextDescricaoReceita = findViewById(R.id.editTextDescricaoReceita);
        recyclerViewIngredientes = findViewById(R.id.recyclerViewIngredientes);
        btnEditar = findViewById(R.id.btnEditar);
        btnSalvar = findViewById(R.id.btnSalvar);
        db = FirebaseFirestore.getInstance();

        // Obtenha os ingredientes da receita a partir do Firestore
        obterIngredientesDaReceita(nomeReceita);

        // Defina um ouvinte para o botão "Editar"
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = !isEditing; // Alternar o estado de edição
                habilitarEdicao(isEditing); // Habilitar ou desabilitar a edição
                ingredienteAdapter.setEditing(isEditing); // Atualizar o estado de edição no adaptador
                ingredienteAdapter.notifyDataSetChanged();// Notificar o adaptador de que os dados foram alterados

                // Preencha os campos de edição com os valores atuais
                editTextNomeReceita.setText(nomeReceita);
                editTextDescricaoReceita.setText(descricaoReceita != null ? descricaoReceita : "Descrição não criada ainda");
            }
        });

        // Defina um ouvinte para o botão "Salvar"
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEditing = false;
                ingredienteAdapter.setEditing(isEditing);
                ingredienteAdapter.notifyDataSetChanged();
                tentarSalvarEdicoes();
            }
        });

        // Inicialmente, desabilite a edição
        habilitarEdicao(false);
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

    private void habilitarEdicao(boolean habilitar) {
        // Habilite ou desabilite a edição de nome e descrição da receita
        editTextNomeReceita.setEnabled(habilitar);
        editTextDescricaoReceita.setEnabled(habilitar);

        // Alterne a visibilidade dos elementos conforme necessário
        if (habilitar) {
            textViewNomeReceita.setVisibility(View.GONE);
            textViewDescricaoReceita.setVisibility(View.GONE);
            editTextNomeReceita.setVisibility(View.VISIBLE);
            editTextDescricaoReceita.setVisibility(View.VISIBLE);
            btnEditar.setVisibility(View.GONE);
            btnSalvar.setVisibility(View.VISIBLE);
        } else {
            textViewNomeReceita.setVisibility(View.VISIBLE);
            textViewDescricaoReceita.setVisibility(View.VISIBLE);
            editTextNomeReceita.setVisibility(View.GONE);
            editTextDescricaoReceita.setVisibility(View.GONE);
            btnEditar.setVisibility(View.VISIBLE);
            btnSalvar.setVisibility(View.GONE);
        }
    }

    private void tentarSalvarEdicoes() {
        // Obtenha o nome e a descrição editados
        String novoNomeReceita = editTextNomeReceita.getText().toString();
        String novaDescricaoReceita = editTextDescricaoReceita.getText().toString();

        // Verifique se o novo nome da receita já existe
        if (!novoNomeReceita.equals(nomeReceita)) {
            receitaJaExiste(novoNomeReceita, novaDescricaoReceita);
        } else {
            // Nome da receita não foi alterado, atualize apenas a descrição
            atualizarDescricaoReceita(nomeReceita, novaDescricaoReceita);
        }
    }

    private void atualizarReceita(String novoNomeReceita, String novaDescricaoReceita) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Obtenha uma referência para a coleção "MinhaReceita"
            CollectionReference receitaCollection = db.collection("Usuarios")
                    .document(userId)
                    .collection("MinhaReceita");

            // Execute uma consulta para encontrar o documento com base no campo "nomeReceita"
            Query query = receitaCollection.whereEqualTo("nomeReceita", nomeReceita);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Encontramos um documento correspondente
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        // Crie um mapa com os novos valores
                        Map<String, Object> newRecipeData = new HashMap<>();
                        newRecipeData.put("nomeReceita", novoNomeReceita);
                        newRecipeData.put("descricaoReceita", novaDescricaoReceita);

                        // Crie um novo documento com o novo nome (novoNomeReceita)
                        DocumentReference newRecipeDocument = receitaCollection.document(novoNomeReceita);

                        newRecipeDocument.set(newRecipeData)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso ao criar o novo documento

                                    // Agora, você precisa copiar os ingredientes do documento antigo para o novo
                                    CollectionReference ingredientesCollection = documentSnapshot.getReference()
                                            .collection("ingredientes");

                                    ingredientesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
                                        for (DocumentSnapshot ingredienteSnapshot : queryDocumentSnapshots) {
                                            Map<String, Object> ingredienteData = ingredienteSnapshot.getData();
                                            newRecipeDocument.collection("ingredientes")
                                                    .document(ingredienteSnapshot.getId())
                                                    .set(ingredienteData);
                                        }

                                        // Agora que os ingredientes foram copiados, você pode excluir o documento antigo
                                        documentSnapshot.getReference().delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Sucesso ao excluir o documento antigo
                                                    Toast.makeText(DetalhesReceitaActivity.this, "Receita atualizada com sucesso.", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent();
                                                    intent.putExtra("descricaoReceita", novaDescricaoReceita);
                                                    setResult(RESULT_OK, intent);
                                                    finish();

                                                    // Atualize o nomeReceita e descricaoReceita localmente
                                                    nomeReceita = novoNomeReceita;
                                                    descricaoReceita = novaDescricaoReceita;

                                                    // Atualize os TextViews com os novos valores
                                                    textViewNomeReceita.setText(nomeReceita);
                                                    textViewDescricaoReceita.setText(descricaoReceita);

                                                    // Desabilite a edição
                                                    habilitarEdicao(false);
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Lidar com erros na exclusão do documento antigo
                                                    Toast.makeText(DetalhesReceitaActivity.this, "Erro ao excluir a receita antiga.", Toast.LENGTH_SHORT).show();
                                                });
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Lidar com erros na criação do novo documento
                                    Toast.makeText(DetalhesReceitaActivity.this, "Erro ao criar a nova receita.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Não encontramos um documento correspondente
                        Toast.makeText(DetalhesReceitaActivity.this, "Receita não encontrada.", Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG", "Nome da receita na consulta: " + nomeReceita);

                    }
                } else {
                    // Lidar com erros na consulta
                    Toast.makeText(DetalhesReceitaActivity.this, "Erro ao consultar a receita.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void atualizarDescricaoReceita(String nomeReceita, String novaDescricaoReceita) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            // Obtenha uma referência para a coleção "MinhaReceita"
            CollectionReference receitaCollection = db.collection("Usuarios")
                    .document(userId)
                    .collection("MinhaReceita");

            // Execute uma consulta para encontrar o documento com base no campo "nomeReceita"
            Query query = receitaCollection.whereEqualTo("nomeReceita", nomeReceita);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Encontramos um documento correspondente
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                        // Crie um mapa com a nova descrição
                        Map<String, Object> newRecipeData = new HashMap<>();
                        newRecipeData.put("descricaoReceita", novaDescricaoReceita);

                        // Atualize o documento existente com a nova descrição
                        documentSnapshot.getReference().update(newRecipeData)
                                .addOnSuccessListener(aVoid -> {
                                    // Sucesso ao atualizar o documento com a nova descrição

                                    Toast.makeText(DetalhesReceitaActivity.this, "Descrição da receita atualizada com sucesso.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.putExtra("descricaoReceita", novaDescricaoReceita);
                                    setResult(RESULT_OK, intent);
                                    finish();

                                    // Atualize a descrição da receita localmente
                                    descricaoReceita = novaDescricaoReceita;

                                    // Atualize o TextView com a nova descrição
                                    textViewDescricaoReceita.setText(descricaoReceita);

                                    // Desabilite a edição
                                    habilitarEdicao(false);
                                })
                                .addOnFailureListener(e -> {
                                    // Lidar com erros na atualização da descrição
                                    Toast.makeText(DetalhesReceitaActivity.this, "Erro ao atualizar a descrição da receita.", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Não encontramos um documento correspondente
                        Toast.makeText(DetalhesReceitaActivity.this, "Receita não encontrada.", Toast.LENGTH_SHORT).show();
                        Log.d("DEBUG", "Nome da receita na consulta: " + nomeReceita);
                    }
                } else {
                    // Lidar com erros na consulta
                    Toast.makeText(DetalhesReceitaActivity.this, "Erro ao consultar a receita.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void receitaJaExiste(String novoNomeReceita, String novaDescricaoReceita) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            CollectionReference minhaReceitaRef = db.collection("Usuarios")
                    .document(userId)
                    .collection("MinhaReceita");

            // Realize uma consulta para verificar se o nome da receita já existe
            minhaReceitaRef.whereEqualTo("nomeReceita", novoNomeReceita)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // O nome da receita já existe
                            // Exiba uma mensagem de erro
                            Toast.makeText(DetalhesReceitaActivity.this, "Uma receita com esse nome já existe.", Toast.LENGTH_SHORT).show();
                        } else {
                            // O nome da receita é único
                            // Prossiga com as edições
                            atualizarReceita(novoNomeReceita, novaDescricaoReceita);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Lidar com erros na consulta
                        Toast.makeText(DetalhesReceitaActivity.this, "Erro ao verificar a existência da receita.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Lidar com o caso em que o usuário não está autenticado
            // Exiba uma mensagem de erro ou execute alguma ação apropriada
            Toast.makeText(DetalhesReceitaActivity.this, "O usuário não está autenticado.", Toast.LENGTH_SHORT).show();
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