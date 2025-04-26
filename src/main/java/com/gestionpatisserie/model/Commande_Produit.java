package com.gestionpatisserie.model;

public class Commande_Produit {
    private int id_commande;
    private int  id_produit;
    private int quantite_prod ;

    public Commande_Produit(int id_commande, int id_produit, int quantite_prod) {
        this.id_commande = id_commande;
        this.id_produit = id_produit;
        this.quantite_prod = quantite_prod;
    }

    public int getId_commande() {
        return id_commande;
    }

    public void setId_commande(int id_commande) {
        this.id_commande = id_commande;
    }

    public int getQuantite_prod() {
        return quantite_prod;
    }

    public void setQuantite_prod(int quantite_prod) {
        this.quantite_prod = quantite_prod;
    }

    public int getId_produit() {
        return id_produit;
    }

    public void setId_produit(int id_produit) {
        this.id_produit = id_produit;
    }
}
