package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipalActivity extends AppCompatActivity {

    private ArrayList<Receita> receitas;

    private Button btnAdicionarReceita;
    private Button btnVerReceitas;
    private Button btnEditarReceita;
    private Button btnCadastrarIngrediente;
    private Button btnListarIngredientes;
    private Button btnEditarIngrediente;
    private Button btnLogout;
    private String nomeUsuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

            receitas = new ArrayList<>();



        nomeUsuario = getIntent().getStringExtra("nomeusuario");
        TextView textViewNomeUsuario = findViewById(R.id.textView6);

        String nomeReceita = getIntent().getStringExtra("nomeReceita");

        // Log para verificar se o nome da receita chegou à TelaPrincipalActivity
        Log.d("TelaPrincipalActivity", "Nome da Receita Recebido: " + nomeReceita);

        // Inicialize os botões com base nos IDs definidos no XML
        btnAdicionarReceita = findViewById(R.id.btnAdicionarReceita);
        btnVerReceitas = findViewById(R.id.btnVerReceitas);
        btnLogout = findViewById(R.id.btnLogout);


        if (nomeUsuario != null) {
            textViewNomeUsuario.setText("Usuario: " + nomeUsuario);
        }


        // Configurar ações de clique para cada botão
        btnAdicionarReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para abrir a tela "Adicionar Receita"
                Intent intent = new Intent(TelaPrincipalActivity.this, AdicionarReceitaActivity.class);
                startActivity(intent);
            }
        });

        btnVerReceitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crie um Intent para abrir a tela "Ver Receitas" sem enviar dados extras
                Intent intent = new Intent(TelaPrincipalActivity.this, VerReceitaActivity.class);
                startActivity(intent);
            }
        });


//        btnCadastrarIngrediente.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Lógica para ação do botão "Cadastrar Ingrediente"
//                Intent intent = new Intent(TelaPrincipalActivity.this, CadastroIngredienteActivity.class);
//                startActivity(intent);
//            }
//        });

//        btnListarIngredientes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Lógica para ação do botão "Listar Ingredientes Cadastrados"
//                Intent intent = new Intent(TelaPrincipalActivity.this, ListarIngredientesActivity.class);
//                startActivity(intent);
//            }
//        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implemente o logout ou qualquer lógica necessária aqui.

                // Inicie a LoginActivity
                Intent intent = new Intent(TelaPrincipalActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(TelaPrincipalActivity.this, "Deslogado com sucesso.", Toast.LENGTH_SHORT).show();
                // Encerre a TelaPrincipalActivity
                finish();
            }
        });
    }
}
