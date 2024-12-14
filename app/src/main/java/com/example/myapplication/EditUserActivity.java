package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserActivity extends AppCompatActivity {

    private EditText etUserName, etContact;
    private Spinner etRole;
    private Button btnSave, btnCancel;
    private FirebaseFirestore db;
    private String userId, cin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        etUserName = findViewById(R.id.editName);
        etContact = findViewById(R.id.editContact);
        etRole = findViewById(R.id.editRoleSpinner);
        btnSave = findViewById(R.id.saveButton);
        btnCancel = findViewById(R.id.cancelButton);

        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etRole.setAdapter(adapter);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        cin = intent.getStringExtra("cin");
        etUserName.setText(intent.getStringExtra("name"));
        etContact.setText(intent.getStringExtra("contact"));
        int position = adapter.getPosition(intent.getStringExtra("role"));
        etRole.setSelection(position);

        btnSave.setOnClickListener(v -> saveUser());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveUser() {
        String name = etUserName.getText().toString();
        String contact = etContact.getText().toString();
        String role = etRole.getSelectedItem().toString();

        if (name.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(userId, cin, name, contact, role);

        db.collection("users").document(userId).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditUserActivity.this, "Utilisateur mis à jour.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EditUserActivity.this, "Erreur de mise à jour.", Toast.LENGTH_SHORT).show());
    }
}
