package com.patient_traking;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class PressioneController implements Initializable {
    @FXML
    private TextField tf_SIS;
    @FXML
    private TextField tf_DIA;
    @FXML
    private TextField tf_ora;
    @FXML
    private Button b_salva;
    @FXML
    private DatePicker dp_date;

    static int pidPaz;
    static {
        pidPaz = GetPid.pid;
    }
        @Override
        public void initialize (URL url, ResourceBundle resourceBundle){
            //disabilita le date future
            Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    // Disable dates in the future
                    if (item.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;"); // Optional: change cell color for disabled dates
                    }
                }
            };
            dp_date.setDayCellFactory(dayCellFactory);

            b_salva.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (!tf_SIS.getText().trim().isEmpty() && !tf_DIA.getText().trim().isEmpty() && !tf_ora.getText().trim().isEmpty()) {
                        int SIS = Integer.parseInt(tf_SIS.getText());
                        int DIA = Integer.parseInt(tf_DIA.getText());
                        String data = String.valueOf(dp_date.getValue());
                        String dateTime = data.concat(" ").concat(tf_ora.getText());
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date d = dateFormat.parse(dateTime);
                            Timestamp dataOra = new java.sql.Timestamp(d.getTime());
                            DBUtils.RegistraPressioni(pidPaz, SIS, DIA, dataOra);
                        } catch (Exception e) {
                            System.out.println("Errore parse " + e.getMessage());
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setContentText("Ora o data inserite non corrette");
                            alert.show();
                        }

                    }
                    tf_SIS.setText(null);
                    tf_DIA.setText(null);
                    tf_ora.setText(null);
                    dp_date.setValue(null);

                }
            });


        }
}