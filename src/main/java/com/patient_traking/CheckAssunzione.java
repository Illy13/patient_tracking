package com.patient_traking;

public class CheckAssunzione {
    private String nomeF;
    private int n_assP;
    private int n_assG;

    public CheckAssunzione(String nomeF, int n_assP, int n_assG) {
        this.nomeF = nomeF;
        this.n_assP = n_assP;
        this.n_assG = n_assG;
    }

    public String getNomeF() {
        return nomeF;
    }

    public int getN_assP() {
        return n_assP;
    }

    public int getN_assG() {
        return n_assG;
    }
}
