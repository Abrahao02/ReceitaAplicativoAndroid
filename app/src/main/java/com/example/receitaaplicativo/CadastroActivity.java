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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button btnCadastro;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        mAuth = FirebaseAuth.getInstance();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.txt_senhalogin);
        editTextConfirmPassword = findViewById(R.id.txt_confirm_password);
        btnCadastro = findViewById(R.id.btnCadastro);

        // Adicione isso no método `onCreate` da sua `CadastroActivity` após inicializar os campos de senha.

        ImageView imageViewMostrarSenha = findViewById(R.id.imageViewMostrarSenha);
        ImageView imageViewMostrarConfirmarSenha = findViewById(R.id.imageViewMostrarConfirmarSenha);

        imageViewMostrarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alterna a visibilidade do campo de senha
                if (editTextPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewMostrarSenha.setImageResource(R.drawable.baseline_check_box_24);
                } else {
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewMostrarSenha.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                }

                // Move o cursor para o final do texto
                editTextPassword.setSelection(editTextPassword.getText().length());
            }
        });

        imageViewMostrarConfirmarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Alterna a visibilidade do campo de senha
                if (editTextConfirmPassword.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    editTextConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewMostrarConfirmarSenha.setImageResource(R.drawable.baseline_check_box_24);
                } else {
                    editTextConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewMostrarConfirmarSenha.setImageResource(R.drawable.baseline_check_box_outline_blank_24);
                }

                // Move o cursor para o final do texto
                editTextConfirmPassword.setSelection(editTextConfirmPassword.getText().length());
            }
        });


        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if (password.equals(confirmPassword)) {
                        mAuth.createUserWithEmailAndPassword(username, password)
                                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Registro bem-sucedido. Agora, envie um e-mail de verificação.
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso! Verifique seu e-mail para ativar sua conta.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(CadastroActivity.this, "Erro ao enviar e-mail de verificação.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                            // Você pode redirecionar para a tela principal ou fazer outras ações aqui.
                                            Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(CadastroActivity.this, "Falha ao criar conta. Verifique seus dados e tente novamente.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(CadastroActivity.this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Nome de usuário e senha são obrigatórios.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}