package com.patient_traking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicoController implements Initializable{
    @FXML
    private Button b_login;
    @FXML
    private TextField tf_matricola;
    @FXML
    private PasswordField pf_password;
    @FXML
    private Button b_home;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        b_login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!tf_matricola.getText().trim().isEmpty() && !pf_password.getText().trim().isEmpty()){
                    DBUtils.logInMedico(event, tf_matricola.getText(), pf_password.getText());
                }
                else{
                    System.out.println("Inserisci i tuoi dati");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Per favore inserisci i tuoi dati");
                    alert.show();
                }
            }
        });

        b_home.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.changeScene(event, "AccessOpt.fxml", "Opzioni di accesso");
            }
        });
    }
}

