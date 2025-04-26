package com.gestionpatisserie.model;

import java.math.BigDecimal;
import java.util.Date;

public class Commande {
    private int id_commende;
    private Date date_commande;
    private BigDecimal montant ;
    private String etat_commande ;
    private int id_client;

    public Commande(int id_client, String etat_commande, BigDecimal montant, Date date_commande, int id_commende) {
        this.id_client = id_client;
        this.etat_commande = etat_commande;
        this.montant = montant;
        this.date_commande = date_commande;
        this.id_commende = id_commende;
    }

    public int getId_commende() {
        return id_commende;
    }

    public void setId_commende(int id_commende) {
        this.id_commende = id_commende;
    }

    public Date getDate_commande() {
        return date_commande;
    }

    public void setDate_commande(Date date_commande) {
        this.date_commande = date_commande;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getEtat_commande() {
        return etat_commande;
    }

    public void setEtat_commande(String etat_commande) {
        this.etat_commande = etat_commande;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }
}
