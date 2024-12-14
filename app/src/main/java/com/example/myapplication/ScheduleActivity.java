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

        
        tvScheduleData = findViewById(R.id.tvScheduleData);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

   
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Récupérer l'ID de l'utilisateur
            firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");
                        String cin = document.getString("cin");
                      
                        fetchSchedules(role, cin);
                    }
                }
            });
        }
    }

 
    private void fetchSchedules(String role, String cin) {
        if ("Agent de suivi".equals(role)) {
      
            firestore.collection("timetables").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    StringBuilder data = new StringBuilder();
                    int emploiIndex = 1; 
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String classData = document.getString("class");
                        String date = document.getString("date");
                        String room = document.getString("salle");
                        String teacherId = document.getString("teacherId");

                        
                        data.append("Emploi ").append(emploiIndex).append(":\n")
                                .append("Class: ").append(classData).append("\n")
                                .append("Date: ").append(date).append("\n")
                                .append("Salle: ").append(room).append("\n")
                                .append("TeacherId: ").append(teacherId).append("\n\n");

                        emploiIndex++; 
                    }
                    tvScheduleData.setText(data.toString());
                } else {
                    Log.e(TAG, "Erreur lors de la récupération des emplois du temps");
                    tvScheduleData.setText("Erreur de récupération des emplois du temps");
                }
            });
        } else if ("Enseignant".equals(role)) {
           
            fetchSchedulesForTeacher(cin);
        }
    }

  
    private void fetchSchedulesForTeacher(String cin) {
        firestore.collection("timetables")
                .whereEqualTo("teacherId", cin) 
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder data = new StringBuilder();
                        int emploiIndex = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String classData = document.getString("class");
                            String date = document.getString("date");
                            String room = document.getString("salle");
                            String teacherId = document.getString("teacherId");

                      
                            data.append("Emploi ").append(emploiIndex).append(":\n")
                                    .append("Class: ").append(classData).append("\n")
                                    .append("Date: ").append(date).append("\n")
                                    .append("Salle: ").append(room).append("\n")
                                    .append("TeacherId: ").append(teacherId).append("\n\n");

                            emploiIndex++; 
                        }
                        tvScheduleData.setText(data.toString());
                    } else {
                        Log.e(TAG, "Erreur lors de la récupération des emplois du temps pour l'enseignant");
                        tvScheduleData.setText("Erreur de récupération des emplois du temps");
                    }
                });
    }
}
