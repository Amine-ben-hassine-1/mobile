package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ScheduleActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private TextView tvScheduleData;
    private FirebaseAuth auth;
    private static final String TAG = "ScheduleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialisation des éléments
        tvScheduleData = findViewById(R.id.tvScheduleData);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Récupérer l'utilisateur actuel
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Récupérer l'ID de l'utilisateur
            firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");
                        String cin = document.getString("cin");
                        // Appeler la méthode pour afficher les emplois du temps en fonction du rôle
                        fetchSchedules(role, cin);
                    }
                }
            });
        }
    }

    // Méthode pour récupérer les emplois du temps selon le rôle de l'utilisateur
    private void fetchSchedules(String role, String cin) {
        if ("Agent de suivi".equals(role)) {
            // Récupérer tous les emplois du temps pour l'Agent de suivi
            firestore.collection("timetables").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder data = new StringBuilder();
                    int emploiIndex = 1; // Index pour afficher "Emploi 1", "Emploi 2", etc.
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String classData = document.getString("class");
                        String date = document.getString("date");
                        String room = document.getString("salle");
                        String teacherId = document.getString("teacherId");

                        // Formater l'affichage avec l'index
                        data.append("Emploi ").append(emploiIndex).append(":\n")
                                .append("Class: ").append(classData).append("\n")
                                .append("Date: ").append(date).append("\n")
                                .append("Salle: ").append(room).append("\n")
                                .append("TeacherId: ").append(teacherId).append("\n\n");

                        emploiIndex++; // Incrémenter l'index pour le prochain emploi
                    }
                    tvScheduleData.setText(data.toString());
                } else {
                    Log.e(TAG, "Erreur lors de la récupération des emplois du temps");
                    tvScheduleData.setText("Erreur de récupération des emplois du temps");
                }
            });
        } else if ("Enseignant".equals(role)) {
            // Récupérer l'emploi du temps de l'enseignant en fonction du cin
            fetchSchedulesForTeacher(cin);
        }
    }

    // Méthode pour récupérer les emplois du temps pour un enseignant spécifique
    private void fetchSchedulesForTeacher(String cin) {
        firestore.collection("timetables")
                .whereEqualTo("teacherId", cin) // Filtrer par teacherId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder data = new StringBuilder();
                        int emploiIndex = 1; // Index pour afficher "Emploi 1", "Emploi 2", etc.
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String classData = document.getString("class");
                            String date = document.getString("date");
                            String room = document.getString("salle");
                            String teacherId = document.getString("teacherId");

                            // Formater l'affichage avec l'index
                            data.append("Emploi ").append(emploiIndex).append(":\n")
                                    .append("Class: ").append(classData).append("\n")
                                    .append("Date: ").append(date).append("\n")
                                    .append("Salle: ").append(room).append("\n")
                                    .append("TeacherId: ").append(teacherId).append("\n\n");

                            emploiIndex++; // Incrémenter l'index pour le prochain emploi
                        }
                        tvScheduleData.setText(data.toString());
                    } else {
                        Log.e(TAG, "Erreur lors de la récupération des emplois du temps pour l'enseignant");
                        tvScheduleData.setText("Erreur de récupération des emplois du temps");
                    }
                });
    }
}
