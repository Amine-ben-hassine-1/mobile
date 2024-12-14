package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvRole, tvCin, tvContact;
    private Button btnChangePassword;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

   I
        tvName = findViewById(R.id.tv_name);
        tvEmail = findViewById(R.id.tv_email);
        tvRole = findViewById(R.id.tv_role);
        tvCin = findViewById(R.id.tv_cin);
        tvContact = findViewById(R.id.tv_contact);
        btnChangePassword = findViewById(R.id.btn_change_password);

    
        loadUserProfile();

      
        btnChangePassword.setOnClickListener(v -> {
            // Ouvrir l'écran pour modifier le mot de passe
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }


    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            
            String email = currentUser.getEmail();

         
            DocumentReference userRef = firestore.collection("users").document(currentUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                   
                    String name = documentSnapshot.getString("name");
                    String role = documentSnapshot.getString("role");
                    String cin = documentSnapshot.getString("cin");
                    String contact = documentSnapshot.getString("contact");

                  
                    tvName.setText("Nom: " + (name != null ? name : "Non spécifié"));
                    tvEmail.setText("Email: " + (email != null ? email : "Non spécifié"));
                    tvRole.setText("Rôle: " + (role != null ? role : "Non spécifié"));
                    tvCin.setText("CIN: " + (cin != null ? cin : "Non spécifié"));
                    tvContact.setText("Contact: " + (contact != null ? contact : "Non spécifié"));
                } else {
                    Toast.makeText(ProfileActivity.this, "Aucune donnée trouvée", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Erreur de récupération des données", Toast.LENGTH_SHORT).show();
            });
        } else {
            tvName.setText("Nom: Non spécifié");
            tvEmail.setText("Email: Non spécifié");
            tvRole.setText("Rôle: Non spécifié");
            tvCin.setText("CIN: Non spécifié");
            tvContact.setText("Contact: Non spécifié");
        }
    }
}
