package com.patient_traking;

import java.sql.Timestamp;

public class Messaggio {
    private int id;
    private String testo;
    private Timestamp data;

    public Messaggio(int id, String testo, Timestamp data) {
        this.id = id;
        this.testo = testo;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getTesto() {
        return testo;
    }

    public Timestamp getData() {
        return data;
    }
}