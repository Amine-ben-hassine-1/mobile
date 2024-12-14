package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAbsenceActivity extends AppCompatActivity {

    private EditText edtAgentId, edtClasse, edtDate, edtSalle, edtHeure;
    private Button btnAddAbsence;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private String enseignantCin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_absence);

        // Initialisation des vues
        edtAgentId = findViewById(R.id.edtAgentId);
        edtClasse = findViewById(R.id.edtClasse);
        edtDate = findViewById(R.id.edtDate);
        edtSalle = findViewById(R.id.edtSalle);
        edtHeure = findViewById(R.id.edtHeure);
        btnAddAbsence = findViewById(R.id.btnAddAbsence);
        progressBar = findViewById(R.id.progressBar);

        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance();

        // Récupérer le CIN de l'enseignant passé dans l'Intent
        enseignantCin = getIntent().getStringExtra("enseignantId");

        // Vérifier si le CIN est passé correctement
        if (enseignantCin == null) {
            Toast.makeText(this, "Erreur : CIN de l'enseignant manquant", Toast.LENGTH_SHORT).show();
            finish();  // Quitter l'activité si CIN manquant
        }

        // Bouton pour ajouter une absence
        btnAddAbsence.setOnClickListener(v -> addAbsence());
    }

    private void addAbsence() {
        String agentId = edtAgentId.getText().toString().trim();
        String classe = edtClasse.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String salle = edtSalle.getText().toString().trim();
        String heure = edtHeure.getText().toString().trim();

        if (TextUtils.isEmpty(agentId) || TextUtils.isEmpty(classe) || TextUtils.isEmpty(date)
                || TextUtils.isEmpty(salle) || TextUtils.isEmpty(heure)) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> absence = new HashMap<>();
        absence.put("agentId", agentId);
        absence.put("classe", classe);
        absence.put("date", date);
        absence.put("heure", heure);
        absence.put("salle", salle);
        absence.put("cin", enseignantCin);  // Utilisation du CIN passé via l'intent

        db.collection("absences")
                .add(absence)
                .addOnSuccessListener(documentReference -> {
                    String absenceId = documentReference.getId();
                    documentReference.update("absenceId", absenceId)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddAbsenceActivity.this, "Absence ajoutée avec succès.", Toast.LENGTH_SHORT).show();
                                clearFields();

                                // Redirection vers MainActivity après l'ajout de l'absence
                                Intent intent = new Intent(AddAbsenceActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Optionnel : Fermer AddAbsenceActivity
                                startActivity(intent);
                                finish(); // Ferme l'activité AddAbsenceActivity
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AddAbsenceActivity.this, "Erreur lors de l'ajout de l'ID : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddAbsenceActivity.this, "Erreur lors de l'ajout : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearFields() {
        edtAgentId.setText("");
        edtClasse.setText("");
        edtDate.setText("");
        edtSalle.setText("");
        edtHeure.setText("");
    }
}
