package com.patient_traking;

public class GetPid {
    static int pid;

    public static void getPidPaz(String mat) {
        GetPid.pid = Integer.parseInt(mat.trim());
    }
}
