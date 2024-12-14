package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyAbsenceActivity extends AppCompatActivity {

    private EditText editTextDate, editTextHeure, editTextSalle, editTextClasse;
    private String absenceId;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_absence);

        absenceId = getIntent().getStringExtra("absenceId");

        editTextDate = findViewById(R.id.editTextDate);
        editTextHeure = findViewById(R.id.editTextHeure);
        editTextSalle = findViewById(R.id.editTextSalle);
        editTextClasse = findViewById(R.id.editTextClasse);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        db = FirebaseFirestore.getInstance();

        loadAbsenceData();

        saveButton.setOnClickListener(v -> saveAbsenceData());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void loadAbsenceData() {
        db.collection("absences").document(absenceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Absence absence = documentSnapshot.toObject(Absence.class);
                        if (absence != null) {
                            editTextDate.setText(absence.getDate());
                            editTextHeure.setText(absence.getHeure());
                            editTextSalle.setText(absence.getSalle());
                            editTextClasse.setText(absence.getClasse());
                        }
                    } else {
                        Toast.makeText(this, "Absence non trouvée", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur de récupération des données", Toast.LENGTH_SHORT).show();
                    Log.e("ModifyAbsence", "Erreur : ", e);
                });
    }

    private void saveAbsenceData() {
        String newDate = editTextDate.getText().toString();
        String newHeure = editTextHeure.getText().toString();
        String newSalle = editTextSalle.getText().toString();
        String newClasse = editTextClasse.getText().toString();

        if (newDate.isEmpty() || newHeure.isEmpty() || newSalle.isEmpty() || newClasse.isEmpty()) {
            Toast.makeText(this, "Tous les champs doivent être remplis", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("absences").document(absenceId)
                .update("date", newDate, "heure", newHeure, "salle", newSalle, "classe", newClasse)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Absence modifiée", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show();
                    Log.e("ModifyAbsence", "Erreur : ", e);
                });
    }
}
