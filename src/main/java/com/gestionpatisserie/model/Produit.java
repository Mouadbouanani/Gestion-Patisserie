package com.gestionpatisserie.model;

public class Produit {
    private int id_produit;
    private String nom_produit ;
    private String  prix_unitaire;
    private  int  qte_disponible;

    public Produit(int id_produit, String nom_produit, String prix_unitaire, int qte_disponible) {
        this.id_produit = id_produit;
        this.nom_produit = nom_produit;
        this.prix_unitaire = prix_unitaire;
        this.qte_disponible = qte_disponible;
    }

    public int getId_produit() {
        return id_produit;
    }

    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }

    public String getNom_produit() {
        return nom_produit;
    }

    public void setNom_produit(String nom_produit) {
        this.nom_produit = nom_produit;
    }

    public String getPrix_unitaire() {
        return prix_unitaire;
    }

    public void setPrix_unitaire(String prix_unitaire) {
        this.prix_unitaire = prix_unitaire;
    }

    public int getQte_disponible() {
        return qte_disponible;
    }

    public void setQte_disponible(int qte_disponible) {
        this.qte_disponible = qte_disponible;
    }
}
