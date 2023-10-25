//package com.example.receitaaplicativo;
//
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class CadastroIngredienteActivity extends AppCompatActivity {
//
//    private EditText editTextNomeIngrediente;
//    private EditText editTextPrecoIngrediente;
//    private EditText editTextQuantidadeIngrediente;
//    private Button btnAdicionarIngrediente;
//    private Button btnEditarIngrediente;
//    private RecyclerView recyclerViewIngredientes;
//    private IngredienteAdapter ingredienteAdapter;
//    private List<Ingrediente> ingredientesList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cadastro_ingrediente);
//
//        editTextNomeIngrediente = findViewById(R.id.editTextNomeIngrediente);
//        editTextPrecoIngrediente = findViewById(R.id.editTextPrecoIngrediente);
//        editTextQuantidadeIngrediente = findViewById(R.id.editTextQuantidadeIngrediente);
//        btnAdicionarIngrediente = findViewById(R.id.btnAdicionarIngrediente);
//        btnEditarIngrediente = findViewById(R.id.btnEditarIngrediente);
//        recyclerViewIngredientes = findViewById(R.id.recyclerViewIngredientes);
//
//        ingredientesList = new ArrayList<>();
//        boolean isEditing = false; // Defina como true se desejar iniciar a atividade com edição habilitada
//        ingredienteAdapter = new IngredienteAdapter(ingredientesList, isEditing, nomeReceita);
//
//
//
//        recyclerViewIngredientes.setLayoutManager(new LinearLayoutManager(this));
//        recyclerViewIngredientes.setAdapter(ingredienteAdapter);
//
//        btnAdicionarIngrediente.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String nome = editTextNomeIngrediente.getText().toString().trim();
//                String precoStr = editTextPrecoIngrediente.getText().toString().trim();
//                String quantidadeStr = editTextQuantidadeIngrediente.getText().toString().trim();
//
//                if (nome.isEmpty() || precoStr.isEmpty() || quantidadeStr.isEmpty()) {
//                    Toast.makeText(CadastroIngredienteActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                // Verifique se o ingrediente já existe na lista
//                boolean ingredienteExistente = false;
//                for (Ingrediente ingrediente : ingredientesList) {
//                    if (ingrediente.getNomeIngrediente().equals(nome)) {
//                        ingredienteExistente = true;
//                        break;
//                    }
//                }
//
//                if (ingredienteExistente) {
//                    Toast.makeText(CadastroIngredienteActivity.this, "Ingrediente já existe. Use a opção de editar.", Toast.LENGTH_SHORT).show();
//                } else {
//                    // Converte as strings para números (preço e quantidade)
//                    double preco = Double.parseDouble(precoStr);
//                    double quantidade = Double.parseDouble(quantidadeStr);
//
//                    // Adicione o novo ingrediente à lista
//                    Ingrediente novoIngrediente = new Ingrediente();
//                    novoIngrediente.setNomeIngrediente(nome);
//                    novoIngrediente.setPreco(preco);
//                    novoIngrediente.setQuantidade(quantidade);
//
//                    ingredientesList.add(novoIngrediente); // Adicione o ingrediente à lista
//                    ingredienteAdapter.notifyDataSetChanged(); // Notifique o adaptador
//                }
//            }
//        });
//
//
//
//        btnEditarIngrediente.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String nome = editTextNomeIngrediente.getText().toString().trim();
//                String precoStr = editTextPrecoIngrediente.getText().toString().trim();
//                String quantidadeStr = editTextQuantidadeIngrediente.getText().toString().trim();
//
//                // ... (Verifique se os campos estão preenchidos)
//
//                int indiceIngredienteExistente = -1;
//
//                for (int i = 0; i < ingredientesList.size(); i++) {
//                    if (ingredientesList.get(i).getNomeIngrediente().equals(nome)) {
//                        indiceIngredienteExistente = i;
//                        break;
//                    }
//                }
//
//                if (indiceIngredienteExistente != -1) {
//                    // Atualize o ingrediente existente
//                    Ingrediente ingrediente = ingredientesList.get(indiceIngredienteExistente);
//                    ingrediente.setPreco(Double.parseDouble(precoStr));
//                    ingrediente.setQuantidade(Double.parseDouble(quantidadeStr));
//
//                    // Notifique o adaptador sobre a mudança no conjunto de dados
//                    ingredienteAdapter.notifyItemChanged(indiceIngredienteExistente);
//
//                    Toast.makeText(CadastroIngredienteActivity.this, "Ingrediente atualizado com sucesso", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(CadastroIngredienteActivity.this, "Ingrediente não encontrado. Use a opção de adicionar.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
//
//    }
//}
//
