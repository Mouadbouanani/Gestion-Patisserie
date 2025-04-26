package com.gestionpatisserie.model;

public class Employee   {
    private int id_emp ;
    private String nom_complet_emp;
    private String post_emp;
    private String  emial;
    private String passowrd ;

    public Employee(int id_emp, String nom_complet_emp, String post_emp, String emial, String passowrd) {
        this.id_emp = id_emp;
        this.nom_complet_emp = nom_complet_emp;
        this.post_emp = post_emp;
        this.emial = emial;
        this.passowrd = passowrd;
    }

    public int getId_emp() {
        return id_emp;
    }

    public void setId_emp(int id_emp) {
        this.id_emp = id_emp;
    }

    public String getNom_complet_emp() {
        return nom_complet_emp;
    }

    public void setNom_complet_emp(String nom_complet_emp) {
        this.nom_complet_emp = nom_complet_emp;
    }

    public String getPost_emp() {
        return post_emp;
    }

    public void setPost_emp(String post_emp) {
        this.post_emp = post_emp;
    }

    public String getEmial() {
        return emial;
    }

    public void setEmial(String emial) {
        this.emial = emial;
    }

    public String getPassowrd() {
        return passowrd;
    }

    public void setPassowrd(String passowrd) {
        this.passowrd = passowrd;
    }
}
