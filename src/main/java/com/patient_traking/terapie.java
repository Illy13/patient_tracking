package com.patient_traking;

public class terapie {

    int id, fid, quantità, assunzioni ;
    String farmaco, indicazioni;

    public int getId() {
        return id;
    }

    public int getFid() { 
        return fid; 
    }

    public int getQuantità() {
        return quantità;
    }

    public int getAssunzioni() {
        return assunzioni;
    }

    public String getFarmaco() {
        return farmaco;
    }

    public String getIndicazioni() {
        return indicazioni;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQuantità(int quantità) {
        this.quantità = quantità;
    }

    public void setAssunzioni(int assunzioni) {
        this.assunzioni = assunzioni;
    }

    public void setFarmaco(String farmaco) {
        this.farmaco = farmaco;
    }

    public void setIndicazioni(String indicazioni) {
        this.indicazioni = indicazioni;
    }

    public terapie(int id, int fid, String farmaco,int quantità, int assunzioni,  String indicazioni) {
        this.id = id;
        this.fid = fid;
        this.quantità = quantità;
        this.assunzioni = assunzioni;
        this.farmaco = farmaco;
        this.indicazioni = indicazioni;
    }
}

