package com.gestionpatisserie.model;

public class Client {
    private int id_client;
    private String nom_complet_client;
    private String adresse_client;
    private String tels;

    public Client(int id_client, String nom_complet_client, String adresse_client, String tels) {
        this.id_client = id_client;
        this.nom_complet_client = nom_complet_client;
        this.adresse_client = adresse_client;
        this.tels = tels;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public String getNom_complet_client() {
        return nom_complet_client;
    }

    public void setNom_complet_client(String nom_complet_client) {
        this.nom_complet_client = nom_complet_client;
    }

    public String getAdresse_client() {
        return adresse_client;
    }

    public void setAdresse_client(String adresse_client) {
        this.adresse_client = adresse_client;
    }

    public String getTels() {
        return tels;
    }

    public void setTels(String tels) {
        this.tels = tels;
    }
}
