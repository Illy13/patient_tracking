package com.patient_traking;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PazienteController implements Initializable {
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
                    DBUtils.logInPaziente(event, tf_matricola.getText(), pf_password.getText());
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
