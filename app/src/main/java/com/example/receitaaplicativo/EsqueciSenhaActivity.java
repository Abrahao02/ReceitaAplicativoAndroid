package com.example.receitaaplicativo;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EsqueciSenhaActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        emailEditText = findViewById(R.id.txt_emaillogin);
        resetPasswordButton = findViewById(R.id.btn_resetemail);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();

                if (!TextUtils.isEmpty(email)) {
                    // Gere um token de redefinição de senha (pode ser um UUID simples)
                    String resetToken = java.util.UUID.randomUUID().toString();

                    // Envie um e-mail usando o Gmail
                    sendPasswordResetEmail(email, resetToken);

                    // Após o envio do e-mail, você pode exibir uma mensagem de sucesso.
                    Toast.makeText(EsqueciSenhaActivity.this, "E-mail de redefinição de senha enviado.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EsqueciSenhaActivity.this, "Digite um e-mail válido.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPasswordResetEmail(String toEmail, String resetToken) {
        String fromEmail = "receitaaplicativo@gmail.com"; // Insira o seu endereço de e-mail do Gmail
        String fromPassword = "Flamengo123"; // Insira a senha do seu e-mail

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject("Redefinição de Senha");
            message.setText("Clique no link a seguir para redefinir sua senha: https://seusite.com/reset?token=" + resetToken);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
