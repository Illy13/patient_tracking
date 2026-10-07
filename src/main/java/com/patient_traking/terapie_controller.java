package com.patient_traking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class terapie_controller {
    Integer pid;
    @FXML
    private TableColumn<terapie, Integer> col_assunzioni;
    @FXML
    private TableColumn<terapie, String> col_farmaco;
    @FXML
    private TableColumn<terapie, Integer> col_id;
    @FXML
    private TableColumn<terapie, String> col_indicazioni;
    @FXML
    private TableColumn<terapie, Integer> col_quantita;
    @FXML
    private TextField filterField;
    @FXML
    private TableView<terapie> table_users;
    @FXML
    private TextField txt_assunzioni;
    @FXML
    private TextField txt_indicazioni;
    @FXML
    private TextField txt_fid;
    @FXML
    private TextField txt_quantita;
    @FXML
    private ComboBox<String> lista_farmaci;
    Connection conn =DBConnection.getConn();
    ResultSet rs = null;
    PreparedStatement pst = null;
    Integer mid,fid;
    ObservableList<String> farmacoList = FXCollections.observableArrayList();

    @FXML
    public void Add_users(ActionEvent event) {
        // Protezione anti-null per mid e pid
        int medicoId = (this.mid != null) ? this.mid : 1; 
        int pazienteId = (this.pid != null) ? this.pid : 1001;

        if(!txt_quantita.getText().trim().isEmpty() && !txt_assunzioni.getText().trim().isEmpty() && !txt_fid.getText().trim().isEmpty()){
            try {
                int quantita = Integer.parseInt(txt_quantita.getText().trim());
                int assunzioni = Integer.parseInt(txt_assunzioni.getText().trim());
                int fid = Integer.parseInt(txt_fid.getText().trim());
                String indicazioni = txt_indicazioni.getText();

                // Usiamo le variabili sicure al posto di this.mid
                DBUtils.addTerapia(medicoId, pazienteId, fid, indicazioni, quantita, assunzioni);
                
                txt_fid.clear();
                txt_quantita.clear();
                txt_assunzioni.clear();
                txt_indicazioni.clear();

            } catch (NumberFormatException e) {
                e.printStackTrace(System.out);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Quantità, Assunzioni e FID devono essere numeri validi.");
                alert.show();
            }
        }
        else{
            System.out.println("Indicazioni non inserite");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Informazioni assenti");
            alert.setContentText("Inserisci informazioni utili");
            alert.show();
        }
        UpdateTable();
    }

    @FXML
    public void Delete(ActionEvent event) {
        // Prendi la terapia attualmente selezionata nella tabella
        terapie selectedTerapia = table_users.getSelectionModel().getSelectedItem();
        
        if (selectedTerapia == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleziona prima una terapia dalla tabella da eliminare.");
            alert.show();
            return;
        }

        try {
            // Usa l'ID univoco della terapia selezionata
            pst = conn.prepareStatement("DELETE FROM terapia WHERE id = ?");
            pst.setInt(1, selectedTerapia.getId());
            pst.execute();
            System.out.println("Terapia eliminata con successo!");
            
        } catch(SQLException e) {
            e.printStackTrace(System.out);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Errore durante la cancellazione: " + e.getMessage());
            alert.show();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        UpdateTable();
    }

    @FXML
    public void Edit(ActionEvent event) {
        // Prendi la terapia attualmente selezionata nella tabella
        terapie selectedTerapia = table_users.getSelectionModel().getSelectedItem();
        
        if (selectedTerapia == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Seleziona prima una terapia dalla tabella da modificare.");
            alert.show();
            return;
        }

        try {
            // Aggiorna usando l'ID univoco della terapia selezionata
            pst = conn.prepareStatement("UPDATE terapia SET indicazioni = ?, quantita = ?, n_ass = ? WHERE id = ?");
            
            pst.setString(1, txt_indicazioni.getText());
            pst.setInt(2, Integer.parseInt(txt_quantita.getText().trim()));
            pst.setInt(3, Integer.parseInt(txt_assunzioni.getText().trim()));
            pst.setInt(4, selectedTerapia.getId()); // ID sicuro preso direttamente dall'oggetto
            
            pst.executeUpdate();
            System.out.println("Terapia aggiornata con successo!");
            
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace(System.out);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Errore durante l'aggiornamento: " + e.getMessage());
            alert.show();
        } finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        UpdateTable();
    }

    int index = -1;
    ObservableList<terapie> listM;

    @FXML
    public void getSelected(MouseEvent event) {
        ResultSet rs1 = null;
        try {
            pst = conn.prepareStatement("select nome from farmaco");
            rs1 = pst.executeQuery();
            while (rs1.next()) {
                String farmaco = rs1.getString("nome");
                farmacoList.add(farmaco);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs1!= null ) {
                try {
                    rs1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        terapie sel = table_users.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        lista_farmaci.setItems(farmacoList);
        lista_farmaci.setValue(sel.getFarmaco());
        txt_fid.setText(String.valueOf(sel.getFid()));
        txt_quantita.setText(String.valueOf(sel.getQuantità()));
        txt_assunzioni.setText(String.valueOf(sel.getAssunzioni()));
        txt_indicazioni.setText(sel.getIndicazioni());
    }

    @FXML
    public void search_user(KeyEvent event) {
    }

    public void mostratabella(Integer pid){
        this.pid=pid;
        conn = DBConnection.getConn();
        ResultSet rs1=null;
        try {
            pst = conn.prepareStatement("select nome from farmaco");
            rs1 = pst.executeQuery();
            while (rs1.next()) {
                String farmaco = rs1.getString("nome");
                farmacoList.add(farmaco);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs1!= null ) {
                try {
                    rs1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            pst = conn.prepareStatement("select med_ref from pazienti where pid= ?");
            pst.setInt(1, this.pid);
            rs1 = pst.executeQuery();
            if (rs1.next()) {
                this.mid = rs1.getInt("med_ref");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs1!= null ) {
                try {
                    rs1.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        lista_farmaci.setItems(farmacoList);
        //lista_farmaci.setPromptText(col_farmaco.getCellData(index).toString());
        col_id.setCellValueFactory(new PropertyValueFactory<terapie,Integer>("id"));
        col_farmaco.setCellValueFactory(new PropertyValueFactory<terapie,String>("farmaco"));
        col_quantita.setCellValueFactory(new PropertyValueFactory<terapie,Integer>("quantità"));
        col_assunzioni.setCellValueFactory(new PropertyValueFactory<terapie,Integer>("assunzioni"));
        col_indicazioni.setCellValueFactory(new PropertyValueFactory<terapie,String>("indicazioni"));
        listM = DBUtils.getTerapie(pid);
        table_users.setItems(listM);
    }

    public void UpdateTable(){
        col_id.setCellValueFactory(new PropertyValueFactory<terapie,Integer>("id"));
        col_farmaco.setCellValueFactory(new PropertyValueFactory<terapie,String>("farmaco"));
        col_quantita.setCellValueFactory(new PropertyValueFactory<terapie,Integer>("quantità"));
        col_assunzioni.setCellValueFactory(new PropertyValueFactory<terapie,Integer>("assunzioni"));
        col_indicazioni.setCellValueFactory(new PropertyValueFactory<terapie,String>("indicazioni"));
        listM = DBUtils.getTerapie(pid);
        table_users.setItems(listM);
    }
    @FXML
    void update_fid(ActionEvent event) {
        conn = DBConnection.getConn();
        ResultSet rs2=null;
        try {
            String getfid = "select fid from farmaco where nome= ? ";
            pst = conn.prepareStatement(getfid);
            pst.setString(1, lista_farmaci.getValue());
            rs2 = pst.executeQuery();
            if (rs2.next()) {
                fid = rs2.getInt("fid");
                txt_fid.setText(String.valueOf(fid));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs2!= null ) {
                try {
                    rs2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
        }
    }





























}
