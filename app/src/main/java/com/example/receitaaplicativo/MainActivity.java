package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Encontre o botão de login por ID
        Button loginButton = findViewById(R.id.btn_login);

        // Defina um ouvinte de clique para o botão de login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crie um Intent para abrir a LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Encontre o botão de cadastro por ID
        Button cadastroButton = findViewById(R.id.btn_cadastro);

        // Defina um ouvinte de clique para o botão de cadastro
        cadastroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirecionar para a tela de cadastro (CadastroActivity)
                Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });
    }
}
