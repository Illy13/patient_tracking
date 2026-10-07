package com.patient_traking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;

import java.util.ResourceBundle;

public class ContattaController implements Initializable {
    static int pidPaz;
    static int midMed;
    
    @FXML
    private TextArea ta_messaggio;
    @FXML
    private Button b_invia;
    @FXML
    private TableView<Messaggio> tb_tabella;
    private ObservableList<Messaggio> chat = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Messaggio, String> tc_testocolonna;
    @FXML
    private TableColumn<Messaggio, String> tc_datacolonna;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Recupera il PID in modo dinamico
        pidPaz = GetPid.pid;
        
        // Fallback per test: se non è impostato il pid del paziente loggato, usa il valore di prova 1001
        if (pidPaz <= 0) {
            pidPaz = 1001; 
        }

        // 2. Recupera l'ID del medico referente
        midMed = DBUtils.getMidMed(pidPaz);

        // 3. Collega le colonne ai getter della classe Messaggio (getTesto() e getData())
        if (tc_testocolonna != null) {
            tc_testocolonna.setCellValueFactory(new PropertyValueFactory<>("testo"));
        }
        if (tc_datacolonna != null) {
            tc_datacolonna.setCellValueFactory(new PropertyValueFactory<>("data"));
        }

        // 4. Carica i messaggi esistenti se presenti nel DB
        if (tb_tabella != null) {
            DBUtils.visualizzaMessaggi(midMed, pidPaz, tb_tabella, chat);
        }

        // 5. Gestione invio messaggio
        if (b_invia != null) {
            b_invia.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (ta_messaggio.getText() != null && !ta_messaggio.getText().trim().isEmpty()) {
                        DBUtils.insertMessage("p", midMed, pidPaz, ta_messaggio.getText());
                        DBUtils.visualizzaMessaggi(midMed, pidPaz, tb_tabella, chat);
                        ta_messaggio.setText("");
                    }
                }
            });
        }
    }
}
