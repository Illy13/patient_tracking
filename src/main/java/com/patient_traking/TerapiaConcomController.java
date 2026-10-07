package com.patient_traking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TerapiaConcomController  implements Initializable {
        static int pidPaz;
        static {
            pidPaz = GetPid.pid;
        }
        @FXML
        private Button b_salva;
        @FXML
        private DatePicker dp_data_I;
        @FXML
        private DatePicker dp_data_F;
        @FXML
        private TextField tf_nomeF;
        @FXML
        private TextField tf_quantita;


        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
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
            dp_data_I.setDayCellFactory(dayCellFactory);
            dp_data_F.setDayCellFactory(dayCellFactory);

            b_salva.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Date dataI = Date.valueOf(dp_data_I.getValue().toString());
                    int quantita = Integer.parseInt(tf_quantita.getText());
                    String nomeF = tf_nomeF.getText().substring(0, 1).toUpperCase() + tf_nomeF.getText().substring(1);
                    if(dp_data_F.getValue() == null){
                        DBUtils.RegistraTerapiaConc(quantita, pidPaz, nomeF , dataI, null);
                    }
                    else {
                        Date dataF = Date.valueOf(dp_data_F.getValue().toString());
                        if (dataF.before(dataI)) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Date non corrette");
                            alert.setContentText("La data finale non può essere maggiore di quella iniziale!");
                            alert.show();
                        } else {
                            DBUtils.RegistraTerapiaConc(quantita, pidPaz, tf_nomeF.getText(), dataI, dataF);
                        }
                    }
                    dp_data_I.setValue(null);
                    dp_data_F.setValue(null);
                }
            });
        }
}
