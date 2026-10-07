package com.patient_traking;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;


import java.net.URL;
import java.util.ResourceBundle;

import java.io.IOException;

import javafx.util.Callback;

public class LoggedInMed implements Initializable {
    @FXML
    private Button button_logout;
    @FXML
    private Label label_welcome;
    @FXML
    private TableView<Users> lista_paz;
    @FXML
    private TableColumn<Users,String> col_id;
    @FXML
    private TableColumn<Users,String> col_nome;
    @FXML
    private TableColumn<Users,String> col_cognome;
    @FXML
    private TableColumn<Users,String> col_status;
    @FXML
    private TableColumn<Users,String> col_rilevazione;
    @FXML
    private TableColumn<Users,String> col_action;
    ObservableList<Users> listM = FXCollections.observableArrayList();
    Users user1;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                View.changeScene(event, "AccessOpt.fxml", "Log in!");
            }
        });
    }

    public void setUserInformation(Integer med_ref, String nome, String cognome){
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_nome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        col_cognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("status"));
        col_rilevazione.setCellValueFactory(new PropertyValueFactory<>("rilevazione"));
        label_welcome.setText("Benvenuto, "+nome+" "+cognome+"!");
        listM = DBUtils.getDatausers(med_ref);
        System.out.println("LISTAM:\n" + listM);
        lista_paz.setItems(listM);
        System.out.println("LISTAPAZ\n");
    
        //add cell of button edit
        Callback<TableColumn<Users, String>, TableCell<Users, String>> cellFoctory = (TableColumn<Users, String> param) -> {
            // make cell containing buttons
            final TableCell<Users, String> cell = new TableCell<Users, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    // that cell created only on non-empty rows
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        String imagePath = LoggedInMed.class.getResource("chart_icon.png").toExternalForm();
                        Image img = new Image(imagePath);
                        ImageView view = new ImageView(img);
                        view.setFitHeight(20);
                        view.setFitWidth(20);
                        view.setPreserveRatio(false);
                        
                        Button up = new Button();
                        up.setGraphic(view);
                        
                        up.setOnAction(event -> {
                            // SOLUZIONE: Ottieni l'oggetto 'Users' direttamente dalla riga corrente, non dalla selezione
                            Users currentRowUser = (Users) getTableRow().getItem();
                            
                            if (currentRowUser != null) {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("show_resoconto_paziente.fxml"));
                                    Parent root = loader.load();
                                    
                                    show_resoconto_paziente_Controller pz = loader.getController();
                                    
                                    // Usa currentRowUser invece di user1
                                    pz.setTextField(currentRowUser.getNome(), currentRowUser.getCognome(), currentRowUser.getId());
                                    
                                    Stage stage = (Stage) up.getScene().getWindow();
                                    Scene scene = new Scene(root);
                                    stage.setScene(scene);
                                    stage.show();
                                } catch (IOException e) {
                                    System.out.println("Errore nel caricamento del file FXML:");
                                    e.printStackTrace();
                                }
                            } else {
                                System.out.println("Errore: Impossibile recuperare il paziente della riga corrente.");
                            }
                        });
                        
                        setGraphic(up);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        col_action.setCellFactory(cellFoctory);
        lista_paz.setItems(listM);
        }
}
