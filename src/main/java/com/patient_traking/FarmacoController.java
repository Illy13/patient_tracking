package com.patient_traking;

import javafx.beans.Observable;
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
import java.util.Date;
import java.util.ResourceBundle;

public class FarmacoController implements Initializable {
    static int pidPaz;
    static {
        pidPaz = GetPid.pid;
    }

    @FXML
    private TextField tf_quantita;
    @FXML
    private TextField tf_ora;
    @FXML
    private Button b_salva;
    @FXML
    private DatePicker dp_date;
    @FXML
    private ComboBox <String> cb_listaF;
    ObservableList<String> farmaciList = FXCollections.observableArrayList();

    public void getnomeFarmaci(int pidPaz){
        this.pidPaz = pidPaz;
        Connection conn = DBConnection.getConn();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("SELECT f.nome FROM farmaco f JOIN terapia t ON f.fid = t.fid WHERE t.pid = ?");
            pst.setInt(1, pidPaz);
            rs = pst.executeQuery();
            while(rs.next()){
                String nomeF = rs.getString("nome");
                farmaciList.add(nomeF);
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

        getnomeFarmaci(pidPaz);
        cb_listaF.setItems(farmaciList);
        b_salva.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!tf_quantita.getText().trim().isEmpty() && !tf_ora.getText().trim().isEmpty()) {
                    int quantita = Integer.parseInt(tf_quantita.getText());
                    String data = String.valueOf(dp_date.getValue());
                    String dateTime = data.concat(" ").concat(tf_ora.getText());
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date d = dateFormat.parse(dateTime);
                        Timestamp dataOra = new java.sql.Timestamp(d.getTime());
                        DBUtils.RegistraFarmaco(event, pidPaz, cb_listaF.getValue(), quantita, dataOra);
                    } catch (Exception e) {
                        System.out.println("Errore parse " + e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Ora o data inserite non corrette");
                        alert.show();
                    }
                }
                tf_quantita.setText(null);
                tf_ora.setText(null);
                dp_date.setValue(null);

            }
        });
    }

}
