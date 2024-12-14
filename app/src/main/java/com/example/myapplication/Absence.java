package com.example.myapplication;

public class Absence {
    private String absenceId;
    private String agentId;
    private String cin;
    private String classe;
    private String date;
    private String heure;
    private String salle;

   
    public Absence() {}

    public Absence(String absenceId, String agentId, String cin, String classe, String date, String heure, String salle) {
        this.absenceId = absenceId;
        this.agentId = agentId;
        this.cin = cin;
        this.classe = classe;
        this.date = date;
        this.heure = heure;
        this.salle = salle;
    }

    public String getAbsenceId() {
        return absenceId;
    }

    public void setAbsenceId(String absenceId) {
        this.absenceId = absenceId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }
}
