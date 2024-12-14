package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textViewRegister = findViewById(R.id.registerNow);

        textViewRegister.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
            finish();
        });

        buttonLogin.setOnClickListener(view -> loginUser());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "Veuillez entrer tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        fetchUserRoleAndNavigate(user.getUid());
                    } else {
                        Toast.makeText(Login.this, "Échec de la connexion", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserRoleAndNavigate(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        navigateBasedOnRole(role);
                    } else {
                        Toast.makeText(Login.this, "Rôle introuvable", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Login.this, "Erreur lors de la récupération du rôle", Toast.LENGTH_SHORT).show());
    }

    private void navigateBasedOnRole(String role) {
        Intent intent;
        if ("Enseignant".equals(role)) {
            intent = new Intent(Login.this, ManageAbsenceActivity.class);
        } else {
            intent = new Intent(Login.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
