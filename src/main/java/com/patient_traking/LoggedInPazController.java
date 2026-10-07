package com.patient_traking;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LoggedInPazController implements Initializable{
    @FXML
    private Button b_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private Button b_pressione;
    @FXML
    private Button b_sintomi;
    @FXML
    private Button b_farmaci;
    @FXML
    private Button b_segnalazioni;
    @FXML
    private Button b_contatta;
    @FXML
    private Button b_terapie;
    @FXML
    private Button b_refresh;

    @FXML
    private TableView<CheckAssunzione> tb_checkassunzioni;
    ObservableList<CheckAssunzione> alert = FXCollections.observableArrayList();
    @FXML
    private TableColumn<CheckAssunzione, String> tc_farmaco;
    @FXML
    private TableColumn<CheckAssunzione, Integer> tc_assunzioniGiornaliere;
    @FXML
    private TableColumn<CheckAssunzione, Integer> tc_assunzioniPrescritte;

    public void visualizzaAssunzioni(int pidPaz){
        System.out.println("Entrato");
        Connection connection = DBConnection.getConn();
        PreparedStatement pst = null;
        ResultSet rs = null;
        try{
            pst = connection.prepareStatement(
                    "SELECT F.nome, T.n_ass, " +
                    "(SELECT COUNT(pid) FROM assunzione WHERE pid = T.pid AND fid = F.fid AND CAST(dataora AS DATE) = CURRENT_DATE) AS n_assG " +
                    "FROM terapia T JOIN farmaco F ON T.fid = F.fid WHERE T.pid = ?");
            pst.setInt(1, pidPaz);
            rs = pst.executeQuery();

            alert.clear();

            while (rs.next()) {
                String nomeF = rs.getString("nome");
                int n_assP = rs.getInt("n_ass");
                int n_assG = rs.getInt("n_assg");
                System.out.println("nomeF: " + nomeF);
                System.out.println("prescritto prima: " + n_assP);
                System.out.println("assunto prima: " + n_assG);
                CheckAssunzione assunzione = new CheckAssunzione(nomeF, n_assP, n_assG);
                alert.add(assunzione);
            }
            tb_checkassunzioni.setItems(alert);
        }catch(SQLException e){
            System.out.println("Errore lettura dati " + e.getMessage());
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
            // Esegui il confronto tra assunzioni giornaliere e prescritte per questo farmaco
            if (assunzioniGiornaliereFarmaco < assunzioniPrescritteFarmaco) {
                String message = "Il farmaco " + fid + " è sottoprescritto. Assunzioni giornaliere inferiori alle prescrizioni.";
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText(message);
                alert.setHeaderText("REGISTRA ASSUNZIONI");
                alert.show();
            }
        }*/

        tc_farmaco.setCellValueFactory(new PropertyValueFactory<>("nomeF"));
        tc_assunzioniPrescritte.setCellValueFactory(new PropertyValueFactory<>("n_assP"));
        tc_assunzioniGiornaliere.setCellValueFactory(new PropertyValueFactory<>("n_assG"));
        b_refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                visualizzaAssunzioni(GetPid.pid);
            }
        });

        b_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) { View.changeScene(event, "AccessOpt.fxml", "Log in!");
            }
        });
        b_pressione.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.popUpScene(event, "RegistraPressione.fxml", "Registra le pressioni giornaliere");
            }
        });
        b_farmaci.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.popUpScene(event, "RegistraAssunzione.fxml", "Registra le assunzioni di farmaci");
            }
        });
        b_contatta.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.messageScene(event, "Contatta.fxml", "Contatta medico");
            }
        });
        b_sintomi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.popUpScene(event, "RegistraSintomo.fxml", "Registra i sintomi");
            }
        });
        b_segnalazioni.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.popUpScene(event, "RegistraPatologie.fxml", "Registra le patologie");
            }
        });
        b_terapie.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.popUpScene(event, "RegistraTerapia.fxml", "Registra terapie concomitanti");
            }
        });

    }

    public void setUserInformation(String nome, String cognome){
        label_welcome.setText("Benvenut@ "+nome+" "+cognome+"!");
    }


}
