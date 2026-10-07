package com.patient_traking;

public class Users {
    Integer id;
    String nome;
    String cognome;
    String status;
    String rilevazione;

    public String getRilevazione() {
        return rilevazione;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setRilevazione(String rilevazione) {
        this.rilevazione = rilevazione;
    }

    public void setnome(String nome) {
        this.nome = nome;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setcognome(String cognome) {
        this.cognome = cognome;
    }

    public String getStatus() {
        return status;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public Users(int id,String nome, String cognome, String status, String rilevazione) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.status = status;
        this.rilevazione = rilevazione;

    }
    public Users(String nome, String cognome,String status,String rilevazione) {
        this.nome = nome;
        this.cognome = cognome;
        this.status = status;
        this.rilevazione = rilevazione;
    }
    public String toString() {
        return nome + " " + cognome + " " + status;
    }
}