package com.patient_traking;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    @FXML
    private Button b_paziente;
    @FXML
    private Button b_medico;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        b_medico.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.changeScene(event, "AccessoMedico.fxml", "Log in medico");
            }
        });

        b_paziente.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.changeScene(event, "AccessoPaziente.fxml", "Log in paziente");
            }
        });
    }
}
