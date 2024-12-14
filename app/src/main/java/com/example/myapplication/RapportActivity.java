package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RapportActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private BarChart barChart;
    private PieChart pieChart;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapport);

        db = FirebaseFirestore.getInstance();
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);

        // Charger les données des absences
        loadAbsenceData();
    }

    private void loadAbsenceData() {
        // Références aux collections Firestore
        CollectionReference usersRef = db.collection("users");
        CollectionReference absencesRef = db.collection("absences");

        // Map pour stocker les absences par enseignant, classe et salle
        Map<String, Integer> teacherAbsences = new HashMap<>();
        Map<String, Integer> classAbsences = new HashMap<>();
        Map<String, Integer> roomAbsences = new HashMap<>();

        // Récupérer tous les enseignants
        usersRef.whereEqualTo("role", "Enseignant").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot usersSnapshot = task.getResult();
                if (usersSnapshot != null) {
                    // Pour chaque enseignant, récupérer ses absences
                    for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                        String userCIN = userDoc.getString("cin");
                        String userName = userDoc.getString("name");
                        String userClass = userDoc.getString("class");

                        // Comptabiliser les absences de l'utilisateur
                        absencesRef.whereEqualTo("cin", userCIN).get()
                                .addOnCompleteListener(absenceTask -> {
                                    if (absenceTask.isSuccessful()) {
                                        QuerySnapshot absencesSnapshot = absenceTask.getResult();
                                        if (absencesSnapshot != null) {
                                            int absenceCount = absencesSnapshot.size();

                                            // Mise à jour des absences par enseignant, classe et salle
                                            updateAbsenceCount(teacherAbsences, userName, absenceCount);
                                            updateAbsenceCount(classAbsences, userClass, absenceCount);

                                            // Définir la salle par salle d'absence
                                            for (DocumentSnapshot absenceDoc : absencesSnapshot) {
                                                String absenceRoom = absenceDoc.getString("salle");
                                                updateAbsenceCount(roomAbsences, absenceRoom, 1);
                                            }

                                            // Mettre à jour les graphiques
                                            updateBarChart(teacherAbsences);
                                            updatePieChart(roomAbsences);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void updateAbsenceCount(Map<String, Integer> map, String key, int count) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + count);
        } else {
            map.put(key, count);
        }
    }

    private void updateBarChart(Map<String, Integer> dataMap) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Absences par Enseignant");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void updatePieChart(Map<String, Integer> dataMap) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : dataMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Absences par Salle");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
