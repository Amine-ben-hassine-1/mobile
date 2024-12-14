package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private AbsenceAdapter adapter;
    private List<Enseignant> enseignantsList;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);  // Set the layout to the Dashboard layout

       
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

       
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        loadUserEmail();

      
        navigationView.setNavigationItemSelectedListener(item -> {
            handleMenuClick(item);  // Handling all clicks in one method
            return true;
        });

      
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Redirect to login if not authenticated
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish(); 
            return; 
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

   
        db = FirebaseFirestore.getInstance();
        enseignantsList = new ArrayList<>();
        adapter = new AbsenceAdapter(enseignantsList);
        recyclerView.setAdapter(adapter);

    
        loadData();
    }

  
    private void loadUserEmail() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String email = firebaseAuth.getCurrentUser().getEmail();
            tvEmail.setText("Email: " + (email != null ? email : "Non spécifié"));
        } else {
            tvEmail.setText("Email: Non connecté");
        }
    }


    private void loadData() {
        db.collection("users")
                .whereEqualTo("role", "Enseignant")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    enseignantsList.clear();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Enseignant enseignant = document.toObject(Enseignant.class);
                        enseignantsList.add(enseignant);
                    }
                    adapter.notifyDataSetChanged();  
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show());
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

           
            Intent loginIntent = new Intent(this, Login.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish(); 
        } else if (item.getItemId() == R.id.nav_manage_users) {
            startActivity(new Intent(this, ManageUsersActivity.class));
        } else if (item.getItemId() == R.id.nav_reports) {
            startActivity(new Intent(this, RapportActivity.class));
        } else if (item.getItemId() == R.id.nav_schedule) {
          
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            if (firebaseAuth.getCurrentUser() != null) {
                db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String role = documentSnapshot.getString("role");
                             
                                if ("Enseignant".equals(role) || "Agent de suivi".equals(role)) {
                                    startActivity(new Intent(this, ScheduleActivity.class));
                                }
                               
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

  
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            // Redirect "Enseignant" users to another activity
                            if ("Enseignant".equals(role)) {
                                Intent intent = new Intent(MainActivity.this, ManageAbsenceActivity.class); 
                                startActivity(intent);
                                finish();
                            } else {
                                updateNavigationMenu(role); // Update the menu for other roles
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show());
        }
    }

    
    private void updateNavigationMenu(String role) {
        NavigationView navigationView = findViewById(R.id.nav_view);

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
