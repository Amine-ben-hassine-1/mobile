package com.example.myapplication;

import com.example.myapplication.Absence;

import java.util.ArrayList;
import java.util.List;

public class Enseignant {
    private String cin; // Identifiant unique de l'enseignant
    private String name; // Nom de l'enseignant
    private String contact; // Coordonnées de l'enseignant (email, téléphone, etc.)
    private List<Absence> absences; // Liste des absences de l'enseignant

    // Constructeur vide requis pour Firebase
    public Enseignant() {
        this.absences = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Enseignant(String cin, String name, String contact, List<Absence> absences) {
        this.cin = cin;
        this.name = name;
        this.contact = contact;
        this.absences = absences != null ? absences : new ArrayList<>();
    }

    // Getters et Setters
    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public List<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences != null ? absences : new ArrayList<>();
    }

    // Ajouter une absence
    public void addAbsence(Absence absence) {
        if (absence != null) {
            this.absences.add(absence);
        }
    }

    public int getTotalAbsences() {
        return absences != null ? absences.size() : 0;
    }
}
