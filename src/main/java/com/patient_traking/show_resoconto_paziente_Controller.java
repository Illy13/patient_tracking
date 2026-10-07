package com.patient_traking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class show_resoconto_paziente_Controller implements Initializable {

    // pid del paziente selezionato dal medico (impostato in setTextField)
    private Integer pidCorrente;

    @FXML
    private Button b_info;
    @FXML
    private Button b_patologie;
    @FXML
    private Button b_logout;
    @FXML
    private Button b_press_mensili;
    @FXML
    private Button b_pressione_set;
    @FXML
    private Button b_terapie;
    @FXML
    private Button b_sintomi;
    @FXML
    private Label label_paz;
    @FXML
    private Button b_messaggi;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        b_pressione_set.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                View.changescenePressSett("Pressione_settimanale.fxml", pidCorrente);
            }
        });
        b_press_mensili.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                View.changescenePressMens("Pressione_mensile.fxml", pidCorrente);
            }
        });
        b_terapie.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                View.changesceneTerapie("terapie.fxml", pidCorrente);
            }
        });
        b_patologie.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                View.changescenePatologie("patologie.fxml", pidCorrente);
            }
        });
        b_info.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                View.changesceneInfo("info.fxml", pidCorrente);
            }
        });
        b_sintomi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                View.changesceneSinto("sintomi.fxml", pidCorrente);
            }
        });
        b_messaggi.setOnAction(new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent actionEvent) {
        apriChat();
    }
});
    }

    void setTextField(String nome, String cognome, Integer id) {
        this.pidCorrente = id;
        label_paz.setText("Resoconto paziente " + nome + " " + cognome);

        b_logout.setOnAction(event -> {
            Connection conn = DBConnection.getConn();
            PreparedStatement pst = null;
            ResultSet rs1 = null;
            Integer mid = null;
            String nomeMed = null;
            String cognomeMed = null;
            try {
                pst = conn.prepareStatement(
                    "SELECT m.matricola, m.nome, m.cognome FROM matmed m " +
                    "JOIN pazienti p ON m.matricola = p.med_ref WHERE p.pid = ?");
                pst.setInt(1, id);
                rs1 = pst.executeQuery();
                if (rs1.next()) {
                    mid = rs1.getInt("matricola");
                    nomeMed = rs1.getString("nome");
                    cognomeMed = rs1.getString("cognome");
                }
            } catch (SQLException e) {
                System.out.println("Errore lettura dati " + e.getMessage());
            } finally {
                try {
                    if (rs1 != null) rs1.close();
                    if (pst != null) pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (mid == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Impossibile trovare il medico del paziente.");
                alert.show();
                return;
            }
            View.changeSceneMed(event, "loggedInMed.fxml", "Benvenuto", mid, nomeMed, cognomeMed);
        });
    }

    private void apriChat() {
        if (pidCorrente == null) {
            return;
        }
        final int pid = pidCorrente;
        final int mid = DBUtils.getMidMed(pid);

        ListView<String> listaMessaggi = new ListView<>();
        TextField campoTesto = new TextField();
        campoTesto.setPromptText("Scrivi un messaggio...");
        HBox.setHgrow(campoTesto, Priority.ALWAYS);
        Button inviaBtn = new Button("Invia");

        inviaBtn.setOnAction(ev -> {
            String testo = campoTesto.getText().trim();
            if (testo.isEmpty()) {
                return;
            }
            DBUtils.insertMessage("m", mid, pid, testo);
            campoTesto.clear();
            caricaMessaggi(mid, pid, listaMessaggi);
        });
        campoTesto.setOnAction(ev -> inviaBtn.fire());   // invio con il tasto Enter

        HBox barraInvio = new HBox(8, campoTesto, inviaBtn);
        barraInvio.setPadding(new Insets(8));

        BorderPane root = new BorderPane();
        root.setCenter(listaMessaggi);
        root.setBottom(barraInvio);

        caricaMessaggi(mid, pid, listaMessaggi);

        Stage stage = new Stage();
        stage.initOwner(b_messaggi.getScene().getWindow());
        stage.setTitle("Messaggi con il paziente");
        stage.setScene(new Scene(root, 500, 400));
        stage.show();
    }

    private void caricaMessaggi(int mid, int pid, ListView<String> lista) {
        ObservableList<String> righe = FXCollections.observableArrayList();
        try (PreparedStatement pst = DBConnection.getConn().prepareStatement(
                "SELECT mittente, testo FROM messaggi WHERE mid = ? AND pid = ? ORDER BY dataora, id")) {
            pst.setInt(1, mid);
            pst.setInt(2, pid);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String testo = rs.getString("testo");
                    // i messaggi di prova non hanno il prefisso "M: " / "P: ", gli altri sì
                    if (!testo.startsWith("M: ") && !testo.startsWith("P: ")) {
                        testo = rs.getString("mittente").toUpperCase() + ": " + testo;
                    }
                    righe.add(testo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Errore lettura messaggi: " + e.getMessage());
        }
        lista.setItems(righe);
        if (!righe.isEmpty()) {
            lista.scrollTo(righe.size() - 1);
        }
    }
}