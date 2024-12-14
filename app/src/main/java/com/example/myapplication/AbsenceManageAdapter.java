package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class AbsenceManageAdapter extends RecyclerView.Adapter<AbsenceManageAdapter.AbsenceViewHolder> {

    private Context context;
    private List<Absence> absenceList;
    private String userRole;

    public AbsenceManageAdapter(Context context, List<Absence> absenceList, String userRole) {
        this.context = context;
        this.absenceList = absenceList;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public AbsenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_absence, parent, false);
        return new AbsenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceViewHolder holder, int position) {
        Absence absence = absenceList.get(position);

        holder.agentIdTextView.setText("AgentID :    "+ absence.getAgentId());
        holder.cinTextView.setText("CIN Enseignant:  "+ absence.getCin());
        holder.classeTextView.setText("Classe:       "+ absence.getClasse());
        holder.dateTextView.setText("Date:           "+ absence.getDate());
        holder.heureTextView.setText("Heure:         "+ absence.getHeure());
        holder.salleTextView.setText("Salle:         "+ absence.getSalle());

       
        if ("Enseignant".equals(userRole)) {
            holder.modifyButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            holder.modifyButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.modifyButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, ModifyAbsenceActivity.class);
                intent.putExtra("absenceId", absence.getAbsenceId());
                context.startActivity(intent);
            });

            holder.deleteButton.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("absences").document(absence.getAbsenceId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Absence supprimée", Toast.LENGTH_SHORT).show();
                            absenceList.remove(position);
                            notifyItemRemoved(position);
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show());
            });
        }
    }

    @Override
    public int getItemCount() {
        return absenceList.size();
    }

    public static class AbsenceViewHolder extends RecyclerView.ViewHolder {
        TextView agentIdTextView, cinTextView, classeTextView, dateTextView, heureTextView, salleTextView;
        Button modifyButton, deleteButton;

        public AbsenceViewHolder(@NonNull View itemView) {
            super(itemView);
            agentIdTextView = itemView.findViewById(R.id.agentIdTextView);
            cinTextView = itemView.findViewById(R.id.cinTextView);
            classeTextView = itemView.findViewById(R.id.classeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            heureTextView = itemView.findViewById(R.id.heureTextView);
            salleTextView = itemView.findViewById(R.id.salleTextView);
            modifyButton = itemView.findViewById(R.id.modifyButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
