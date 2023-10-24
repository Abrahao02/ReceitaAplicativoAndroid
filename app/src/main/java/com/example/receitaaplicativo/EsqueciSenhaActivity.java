package com.example.receitaaplicativo;

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

public class EsqueciSenhaActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);


        emailEditText = findViewById(R.id.txt_emaillogin);
        resetButton = findViewById(R.id.btn_resetemail);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (isValidEmail(email)) {
                    resetPassword(email);
                } else {
                    Toast.makeText(EsqueciSenhaActivity.this, "Informe um endereço de e-mail válido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void resetPassword(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EsqueciSenhaActivity.this, "E-mail de redefinição de senha enviado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(EsqueciSenhaActivity.this, "Falha ao enviar o e-mail de redefinição de senha", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
