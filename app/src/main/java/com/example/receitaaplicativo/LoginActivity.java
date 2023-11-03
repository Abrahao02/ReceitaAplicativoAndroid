package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginUsername;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private ImageView imageViewKeepMeLoggedIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextLoginUsername = findViewById(R.id.txt_emaillogin);
        editTextLoginPassword = findViewById(R.id.txt_senhalogin2);
        buttonLogin = findViewById(R.id.buttonLogin);
        imageViewKeepMeLoggedIn = findViewById(R.id.keepMeLoggedInCheckBox1);

        imageViewKeepMeLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean keepMeLoggedIn = toggleKeepMeLoggedInState();

                if (keepMeLoggedIn) {
                    imageViewKeepMeLoggedIn.setImageResource(R.drawable.baseline_check_box_24);
                } else {
                    imageViewKeepMeLoggedIn.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                }
            }
        });

        boolean keepMeLoggedIn = getKeepMeLoggedInState();
        if (keepMeLoggedIn) {
            imageViewKeepMeLoggedIn.setImageResource(R.drawable.baseline_check_box_24);
        } else {
            imageViewKeepMeLoggedIn.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
        }

        // Verifica se o usuário já está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Se o usuário já está conectado, redirecione para a TelaPrincipalActivity
            Intent intent = new Intent(LoginActivity.this, TelaPrincipalActivity.class);
            intent.putExtra("nomeusuario", currentUser.getEmail()); // Você pode passar o email ou outra informação do usuário
            startActivity(intent);
            finish(); // Encerre a LoginActivity para evitar que o usuário volte para ela com o botão "Voltar".
        }

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
                                        Intent intent = new Intent(LoginActivity.this, TelaPrincipalActivity.class);
                                        intent.putExtra("nomeusuario", username);
                                        startActivity(intent);
                                        finish();
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


    private boolean toggleKeepMeLoggedInState() {
        // Adicione a lógica para alternar o estado de "Mantenha-me conectado" no SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        boolean keepMeLoggedIn = sharedPreferences.getBoolean("keepMeLoggedIn", false);
        keepMeLoggedIn = !keepMeLoggedIn;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("keepMeLoggedIn", keepMeLoggedIn);
        editor.apply();
        return keepMeLoggedIn;
    }

    private boolean getKeepMeLoggedInState() {
        // Obtenha o estado atual de "Mantenha-me conectado" do SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("keepMeLoggedIn", false);
    }
}
