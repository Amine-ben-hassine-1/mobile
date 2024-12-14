package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {

    private EditText nameEditText, contactEditText, cinEditText, emailEditText, passwordEditText;
    private Spinner roleSpinner;
    private Button addUserButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        nameEditText = findViewById(R.id.name);
        contactEditText = findViewById(R.id.contact);
        cinEditText = findViewById(R.id.cin);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        roleSpinner = findViewById(R.id.spinnerRole);
        addUserButton = findViewById(R.id.btn_add_user);
        progressBar = findViewById(R.id.progressBar);

        // Set up Spinner with user roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        addUserButton.setOnClickListener(v -> addUser());
    }

    private void addUser() {
        String name = nameEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String cin = cinEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString();

        // Basic validation
        if (name.isEmpty() || contact.isEmpty() || cin.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(AddUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cin.length() != 8 || !cin.matches("\\d+")) {
            Toast.makeText(AddUserActivity.this, "CIN must be 8 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Prepare user data to save in Firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("contact", contact);
        user.put("cin", cin);
        user.put("email", email); // Add email to Firestore
        user.put("password", password); // Add password to Firestore
        user.put("role", role);  // Role is saved in Firestore

        // Save data in Firestore
        db.collection("users").add(user)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddUserActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddUserActivity.this, ManageUsersActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddUserActivity.this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
