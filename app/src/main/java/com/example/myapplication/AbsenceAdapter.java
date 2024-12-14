package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.ViewHolder> {

    private List<Enseignant> enseignantsList;

    public AbsenceAdapter(List<Enseignant> enseignantsList) {
        this.enseignantsList = enseignantsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_enseignant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Enseignant enseignant = enseignantsList.get(position);

        // Remplir les informations de l'enseignant
        holder.nameTextView.setText(enseignant.getName());

        // Gestion du clic sur le bouton pour ajouter une absence
        holder.addAbsenceButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, AddAbsenceActivity.class);

            // Passer l'identifiant de l'enseignant (CIN) à l'activité suivante
            intent.putExtra("enseignantId", enseignant.getCin());
            context.startActivity(intent);
        });

        // Gestion du clic sur le bouton pour consulter les absences
        holder.viewAbsencesButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, ManageAbsenceActivity.class);

            // Passer le CIN de l'enseignant sélectionné
            String enseignantCin = enseignant.getCin();
            intent.putExtra("enseignantCin", enseignantCin);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return enseignantsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        Button addAbsenceButton, viewAbsencesButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            addAbsenceButton = itemView.findViewById(R.id.addAbsenceButton);
            viewAbsencesButton = itemView.findViewById(R.id.viewAbsencesButton);
        }
    }
}
