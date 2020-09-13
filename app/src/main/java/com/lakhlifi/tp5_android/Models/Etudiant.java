package com.lakhlifi.tp5_android.Models;

public class Etudiant {

    private int Code;
    private String Nom;
    private String Prenom;
    private String Classe;

    public Etudiant() {
    }

    public Etudiant(int code, String nom, String prenom, String classe) {
        Code = code;
        Nom = nom;
        Prenom = prenom;
        Classe = classe;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getPrenom() {
        return Prenom;
    }

    public void setPrenom(String prenom) {
        Prenom = prenom;
    }

    public String getClasse() {
        return Classe;
    }

    public void setClasse(String classe) {
        Classe = classe;
    }

    @Override
    public String toString() {
        return  getCode()+" : "+getNom()+ " "+ getPrenom()+"     "+getClasse();
    }
}
