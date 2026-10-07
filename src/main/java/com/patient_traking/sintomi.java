package com.patient_traking;

public class sintomi {
    String Sintomo, Info,Data;

    public void setSintomo(String sintomo) {
        Sintomo = sintomo;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getSintomo() {
        return Sintomo;
    }

    public String getInfo() {
        return Info;
    }

    public String getData() {
        return Data;
    }

    public sintomi(String sintomo, String info, String data) {
        Sintomo = sintomo;
        Info = info;
        Data = data;
    }
}
