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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class SintomoController implements Initializable {
    static int pidPaz;
    static {
        pidPaz = GetPid.pid;
    }
    @FXML
    private TextArea ta_descrizione;
    @FXML
    private Button b_salva;
    @FXML
    private DatePicker dp_date;
    @FXML
    private ComboBox<String> cb_listaSintomi;
    ObservableList<String> sintomiList = FXCollections.observableArrayList();

    public void getnomeSintomi(int pidPaz){
        this.pidPaz = pidPaz;
        Connection conn = DBConnection.getConn();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("SELECT nome FROM sintomi ");
            rs = pst.executeQuery();
            while(rs.next()){
                String nomeS = rs.getString("nome");
                sintomiList.add(nomeS);
            }

        } catch (SQLException e) {
            System.out.println("Errore lettura dati" + " " + e.getMessage());
        }
    }

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
        dp_date.setDayCellFactory(dayCellFactory);

        getnomeSintomi(pidPaz);
        cb_listaSintomi.setItems(sintomiList);
        b_salva.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!ta_descrizione.getText().trim().isEmpty()) {
                    String data = dp_date.getValue().toString();
                    DBUtils.RegistraSintomi(pidPaz, cb_listaSintomi.getValue(), ta_descrizione.getText(), Date.valueOf(data));
                }
                else{
                    System.out.println("Indicazioni non inserite");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Informazioni assenti");
                    alert.setContentText("Inserzisci informazioni utili");
                    alert.show();
                }
                ta_descrizione.setText(null);
                dp_date.setValue(null);

            }
        });
    }
}
