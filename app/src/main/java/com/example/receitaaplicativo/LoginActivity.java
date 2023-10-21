package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginUsername;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextLoginUsername = findViewById(R.id.txt_emaillogin);
        editTextLoginPassword = findViewById(R.id.txt_senhalogin2);
        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextLoginUsername.getText().toString();
                String password = editTextLoginPassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    mAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                                        // Redirecionar para a TelaPrincipalActivity ou executar outras ações aqui.
                                        Intent intent = new Intent(LoginActivity.this, TelaPrincipalActivity.class);
                                        intent.putExtra("nomeusuario", username);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Nome de usuário ou senha incorretos.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(LoginActivity.this, "Nome de usuário e senha são obrigatórios.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
