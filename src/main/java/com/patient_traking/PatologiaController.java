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

public class PatologiaController implements Initializable {
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
    private ComboBox<String> cb_listaPatologie;
    ObservableList<String> patologieList = FXCollections.observableArrayList();
    public void getNomePatologie(){
        Connection conn = DBConnection.getConn();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement("SELECT nome FROM patologia ");
            rs = pst.executeQuery();
            while(rs.next()){
                String nomeP = rs.getString("nome");
                patologieList.add(nomeP);
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
        dp_data_I.setDayCellFactory(dayCellFactory);
        dp_data_F.setDayCellFactory(dayCellFactory);

        getNomePatologie();
        cb_listaPatologie.setItems(patologieList);
        b_salva.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Date dataI = Date.valueOf(dp_data_I.getValue().toString());
                if(dp_data_F.getValue() == null){
                    DBUtils.RegistraPatologia(pidPaz, cb_listaPatologie.getValue(), dataI, null);
                }
                else {
                    Date dataF = Date.valueOf(dp_data_F.getValue().toString());
                    if (dataF.before(dataI)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Date non corrette");
                        alert.setContentText("La data finale non può essere maggiore di quella iniziale!");
                        alert.show();
                    } else {
                        DBUtils.RegistraPatologia(pidPaz, cb_listaPatologie.getValue(), dataI, dataF);
                    }
                }
                dp_data_I.setValue(null);
                dp_data_F.setValue(null);
            }
        });
    }
}
