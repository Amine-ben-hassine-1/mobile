package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageAbsenceActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private AbsenceManageAdapter adapter;
    private List<Absence> absenceList;
    private FirebaseFirestore db;
    private TextView tvEmail;  // Add TextView for email display in the header

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_absences_activity); // Layout with DrawerLayout

        // Initializing DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_absences);

        // Retrieve and display the user's email in the navigation header
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        loadUserEmail();  // Load the email

        // Check if the user is authenticated
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(ManageAbsenceActivity.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize RecyclerView for displaying absences
        recyclerView = findViewById(R.id.absencesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        absenceList = new ArrayList<>();

        // Get the CIN from the intent
        String enseignantCin = getIntent().getStringExtra("enseignantCin");

        // Check if CIN is passed (for agent) and load absences accordingly
        if (enseignantCin != null) {
            loadAbsencesForSpecificTeacher(enseignantCin);
        } else {
            // If CIN is not passed, load all absences for admin or agent
            fetchUserRoleAndCin();
        }

        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(item -> {
            handleMenuClick(item);
            return true;
        });
    }

    // Load the user's email
    private void loadUserEmail() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String email = firebaseAuth.getCurrentUser().getEmail();
            tvEmail.setText("Email: " + (email != null ? email : "Non spécifié"));
        } else {
            tvEmail.setText("Email: Non connecté");
        }
    }

    private void loadAbsencesForSpecificTeacher(String enseignantCin) {
        db.collection("absences")
                .whereEqualTo("cin", enseignantCin)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    absenceList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        absenceList.add(document.toObject(Absence.class));
                    }
                    adapter = new AbsenceManageAdapter(this, absenceList, "Agent");
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("ManageAbsence", "Erreur lors de la récupération des absences : ", e));
    }

    private void fetchUserRoleAndCin() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userRole = documentSnapshot.getString("role");
                        String userCin = documentSnapshot.getString("cin");

                        if ("Enseignant".equals(userRole)) {
                            loadAbsencesForTeacher(userCin);
                        } else {
                            loadAllAbsencesForAdminOrAgent(userRole);
                        }

                        updateNavigationMenu(userRole);  // Update the menu based on role
                    } else {
                        Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e("ManageAbsence", "Erreur lors de la récupération du rôle : ", e));
    }

    private void loadAbsencesForTeacher(String userCin) {
        db.collection("absences")
                .whereEqualTo("cin", userCin)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    absenceList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        absenceList.add(document.toObject(Absence.class));
                    }
                    adapter = new AbsenceManageAdapter(this, absenceList, "Enseignant");
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("ManageAbsence", "Erreur lors de la récupération des absences : ", e));
    }

    private void loadAllAbsencesForAdminOrAgent(String userRole) {
        db.collection("absences")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    absenceList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        absenceList.add(document.toObject(Absence.class));
                    }
                    adapter = new AbsenceManageAdapter(this, absenceList, userRole);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("ManageAbsence", "Erreur lors de la récupération des absences : ", e));
    }

    private void handleMenuClick(MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            Toast.makeText(this, "Accueil sélectionné", Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (item.getItemId() == R.id.nav_view_absences) {
            startActivity(new Intent(this, ManageAbsenceActivity.class));
        } else if (item.getItemId() == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Déconnexion", Toast.LENGTH_SHORT).show();

            // Open login activity
            Intent loginIntent = new Intent(this, Login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();  // Close the current activity
        } else if (item.getItemId() == R.id.nav_manage_users) {
            startActivity(new Intent(this, ManageUsersActivity.class));
        } else if (item.getItemId() == R.id.nav_reports) {
            startActivity(new Intent(this, RapportActivity.class));
        } else if (item.getItemId() == R.id.nav_schedule) {
            // Redirect based on user role
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                                // Redirect Enseignant and Agent de suivi to ScheduleActivity
                                if ("Enseignant".equals(role) || "Agent de suivi".equals(role)) {
                                    startActivity(new Intent(this, ScheduleActivity.class));
                                }
                                // Redirect Administrateur to TimetableActivity
                                else if ("Administrateur".equals(role)) {
                                    startActivity(new Intent(this, TimetableActivity.class));
                                } else {
                                    Toast.makeText(this, "Accès non autorisé à l'emploi de temps", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show());
            }
        }

        drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
    }

    // Update the navigation menu based on the role
    private void updateNavigationMenu(String role) {
        NavigationView navigationView = findViewById(R.id.nav_view_absences);

        if ("Enseignant".equals(role)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_teacher);
        } else if ("Agent de suivi".equals(role)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_agent);
        } else if ("Administrateur".equals(role)) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_admin);
        }
    }
}