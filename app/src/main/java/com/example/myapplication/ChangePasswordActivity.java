package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button changePasswordButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

    
        mAuth = FirebaseAuth.getInstance();

        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            if (newPassword.equals(confirmPassword)) {
                // Si les mots de passe correspondent, changer le mot de passe
                changePassword(newPassword);
            } else {
                // Afficher un message d'erreur si les mots de passe ne correspondent pas
                Toast.makeText(ChangePasswordActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            }
        });
    }

   
    private void changePassword(String newPassword) {
        mAuth.getCurrentUser().updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "Mot de passe modifié avec succès", Toast.LENGTH_SHORT).show();
                        finish(); // Retour à l'écran précédent
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Erreur lors de la modification du mot de passe", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
