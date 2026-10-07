module com.patient_traking {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;

    opens com.patient_traking to javafx.fxml;
    exports com.patient_traking;
}
