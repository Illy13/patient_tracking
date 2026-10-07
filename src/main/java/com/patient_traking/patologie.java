package com.patient_traking;

public class patologie {
    String Patologia,Data_Inizio,Data_Fine;

    public String getPatologia() {
        return Patologia;
    }

    public String getData_Inizio() {
        return Data_Inizio;
    }

    public String getData_Fine() {
        return Data_Fine;
    }

    public void setPatologia(String patologia) {
        this.Patologia = patologia;
    }

    public void setData_Inizio(String data_Inizio) {
        Data_Inizio = data_Inizio;
    }

    public void setData_Fine(String data_fine) {
        Data_Fine = data_fine;
    }

    public patologie(String patologia, String Data_Inizio, String Data_Fine) {
        this.Patologia = patologia;
        this.Data_Inizio = Data_Inizio;
        this.Data_Fine = Data_Fine;
    }
}
