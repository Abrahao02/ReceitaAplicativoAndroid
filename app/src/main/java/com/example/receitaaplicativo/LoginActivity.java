package com.example.receitaaplicativo;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
    private SharedPreferences sharedPreferences;
    private boolean keepMeLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        keepMeLoggedIn = sharedPreferences.getBoolean("keepMeLoggedIn", false);

        editTextLoginUsername = findViewById(R.id.txt_emaillogin);
        editTextLoginPassword = findViewById(R.id.txt_senhalogin2);
        buttonLogin = findViewById(R.id.buttonLogin);
        imageViewKeepMeLoggedIn = findViewById(R.id.keepMeLoggedInCheckBox1);

        TextView esqueciminhasenha = findViewById(R.id.txt_esqueciminhasenha);

        ImageView imageViewMostrarSenha = findViewById(R.id.imageViewMostrarSenhaLogin);
        imageViewMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alterna a visibilidade do campo de senha
                if (editTextLoginPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    editTextLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewMostrarSenha.setImageResource(R.drawable.baseline_check_box_24);
                } else {
                    editTextLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewMostrarSenha.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                }
                // Move o cursor para o final do texto
                editTextLoginPassword.setSelection(editTextLoginPassword.getText().length());
            }
        });

        esqueciminhasenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirecionar para a tela de esqueci_senha (EsqueciSenhaActivity)
                Intent intent = new Intent(LoginActivity.this, EsqueciSenhaActivity.class);
                startActivity(intent);
            }
        });

        if (keepMeLoggedIn) {
            imageViewKeepMeLoggedIn.setImageResource(R.drawable.baseline_check_box_24);
        } else {
            imageViewKeepMeLoggedIn.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
        }

        imageViewKeepMeLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keepMeLoggedIn = !keepMeLoggedIn;
                imageViewKeepMeLoggedIn.setImageResource(keepMeLoggedIn ? R.drawable.baseline_check_box_24 : R.drawable.baseline_check_box_outline_blank_24);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("keepMeLoggedIn", keepMeLoggedIn);
                editor.apply();
            }
        });

        // Verifica se o usuário já está autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            if (keepMeLoggedIn) {
                // Se o usuário já está conectado e "Mantenha-me conectado" está ativado,
                // redirecione para a TelaPrincipalActivity
                Intent intent = new Intent(LoginActivity.this, TelaPrincipalActivity.class);
                intent.putExtra("nomeusuario", currentUser.getEmail());
                startActivity(intent);
                finish();
            } else {
                // Se o usuário está conectado, mas "Mantenha-me conectado" não está ativado,
                // faça logout e redirecione para a Tela de Login
                mAuth.signOut();
            }
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
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null && user.isEmailVerified()) {
                                            Toast.makeText(LoginActivity.this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, TelaPrincipalActivity.class);
                                            intent.putExtra("nomeusuario", username);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Seu e-mail ainda não foi confirmado. Verifique seu e-mail e clique no link de confirmação.", Toast.LENGTH_LONG).show();
                                            mAuth.signOut(); // Faça logout para garantir que o usuário não acesse o aplicativo até confirmar o e-mail.
                                        }
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
