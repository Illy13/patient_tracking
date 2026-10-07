package com.patient_traking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import java.time.LocalDate;

public class patologie_controller {
    Integer pid;
    @FXML
    private Button but_edit;
    @FXML
    private TableColumn<patologie, String> col_fineco;
    @FXML
    private TableColumn<patologie, String> col_finepr;
    @FXML
    private TableColumn<patologie, String> col_inizioco;
    @FXML
    private TableColumn<patologie, String> col_iniziopr;
    @FXML
    private TableColumn<patologie, String> col_patco;
    @FXML
    private TableColumn<patologie, String> col_patpr;
    @FXML
    private DatePicker data_fine;
    @FXML
    private DatePicker data_inizio;
    @FXML
    private ComboBox<String> lista_patologie;
    @FXML
    private TableView<patologie> table_users;
    @FXML
    private TableView<patologie> table_users1;
    Connection conn = DBConnection.getConn();
    PreparedStatement pst = null;
    Integer mid, fid;
    ResultSet rs = null, rs1 = null;
    ObservableList<String> PatologiaList = FXCollections.observableArrayList();
    int index = -1;
    ObservableList<patologie> listM;
    ObservableList<patologie> listM1;

    @FXML
    void update_fid(ActionEvent event) {
    }

    @FXML
    public void Add_users(ActionEvent event) {
        Integer pat_id = null;
        try {
            pst = conn.prepareStatement("select id from patologia where nome= ?");
            pst.setString(1, lista_patologie.getValue());
            rs = pst.executeQuery();
            if (rs.next()) {
                pat_id = rs.getInt("id");
            }
            pst = conn.prepareStatement("insert into patologie_paziente (id_patologia,pid,data_inizio,data_fine)values(?,?,?,?)");
            pst.setInt(1, pat_id);
            pst.setInt(2, this.pid);
            LocalDate localdate = data_inizio.getValue();
            Date sqlDate = Date.valueOf(localdate);
            pst.setDate(3, sqlDate);
            localdate = data_fine.getValue();
            if (localdate != null) {
                sqlDate = Date.valueOf(localdate);
                pst.setDate(4, sqlDate);
            } else
                pst.setDate(4, null);
            pst.execute();
            //JOptionPane.showMessageDialog(null, "Users Add succes");
        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null,e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if (Integer.parseInt(e.getSQLState()) == 23505)
                alert.setContentText("Errore, stai inserendo due patologie uguali");
            else
                alert.setContentText(e.getSQLState() + e.getMessage());
            alert.show();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
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
            /*if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        }
        UpdateTable();
    }

    @FXML
    public void Delete(ActionEvent event) {
        Integer pat_id = null;
        try {
            pst = conn.prepareStatement("select id from patologia where nome= ?");
            pst.setString(1, lista_patologie.getValue());
            rs = pst.executeQuery();
            if (rs.next()) {
                pat_id = rs.getInt("id");
            }
            pst = conn.prepareStatement("DELETE FROM patologie_paziente where pid = ? and id_patologia= ?");
            pst.setInt(1, this.pid);
            pst.setInt(2, pat_id);
            pst.execute();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getSQLState() + e.getMessage());
            alert.show();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
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
            /*if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        }
        UpdateTable();
    }

    @FXML
    public void Edit(ActionEvent event) {
        Integer pat_id = null;
        try {
            pst = conn.prepareStatement("select id from patologia where nome= ?");
            pst.setString(1, lista_patologie.getValue());
            rs = pst.executeQuery();
            if (rs.next()) {
                pat_id = rs.getInt("id");
            }
            pst = conn.prepareStatement("UPDATE patologie_paziente SET data_fine = ? WHERE id_patologia = ? and pid = ?");
            LocalDate localdate = data_fine.getValue();
            Date sqlDate = Date.valueOf(localdate);
            pst.setDate(1, sqlDate);
            pst.setInt(2, pat_id);
            pst.setInt(3, this.pid);
            pst.execute();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getSQLState() + e.getMessage());
            alert.show();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
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
            /*if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        }
        but_edit.setDisable(true);
        UpdateTable();
    }

    @FXML
    public void getSelected1(MouseEvent event) {
        index = table_users.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        lista_patologie.setItems(PatologiaList);
        System.out.println(col_patpr.getCellData(index).toString());
        lista_patologie.setValue(col_patpr.getCellData(index).toString());

        Date sqlDate = Date.valueOf(col_iniziopr.getCellData(index).toString());
        LocalDate localDate = sqlDate.toLocalDate();
        data_inizio.setValue(localDate);

        sqlDate = Date.valueOf(col_finepr.getCellData(index).toString());
        localDate = LocalDate.now();
        data_fine.setValue(localDate);
    }

    @FXML
    public void getSelected(MouseEvent event) {
        but_edit.setDisable(false);
        index = table_users1.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }

        lista_patologie.setItems(PatologiaList);
        System.out.println(col_patco.getCellData(index).toString());
        lista_patologie.setValue(col_patco.getCellData(index).toString());

        Date sqlDate = Date.valueOf(col_inizioco.getCellData(index).toString());
        LocalDate localDate = sqlDate.toLocalDate();
        data_inizio.setValue(localDate);

        localDate = LocalDate.now();
        data_fine.setValue(localDate);
    }

    public void mostratabella(Integer pid) {
        this.pid = pid;
        conn = DBConnection.getConn();
        String getnome = "select nome from patologia ";

        try {
            pst = conn.prepareStatement(getnome);
            rs1 = pst.executeQuery();
            while (rs1.next()) {
                String patologia = rs1.getString("nome");
                PatologiaList.add(patologia);
            }
        } catch (SQLException e) {

        } finally {
            if (rs1 != null) {
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
            /*if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        }


        lista_patologie.setItems(PatologiaList);
        //lista_farmaci.setPromptText(col_farmaco.getCellData(index).toString());

        col_patpr.setCellValueFactory(new PropertyValueFactory<patologie, String>("Patologia"));

        col_iniziopr.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Inizio"));
        col_finepr.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Fine"));
        col_patco.setCellValueFactory(new PropertyValueFactory<patologie, String>("Patologia"));

        col_inizioco.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Inizio"));
        col_fineco.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Fine"));

        listM = DBUtils.getPatologie(pid, true);
        table_users.setItems(listM);
        listM1 = DBUtils.getPatologie(pid, false);
        table_users1.setItems(listM1);
    }


    public void UpdateTable() {
        col_patpr.setCellValueFactory(new PropertyValueFactory<patologie, String>("Patologia"));
        col_iniziopr.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Inizio"));
        col_finepr.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Fine"));
        col_patco.setCellValueFactory(new PropertyValueFactory<patologie, String>("Patologia"));
        col_inizioco.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Inizio"));
        col_fineco.setCellValueFactory(new PropertyValueFactory<patologie, String>("Data_Fine"));
        listM = DBUtils.getPatologie(pid, true);
        table_users.setItems(listM);
        listM1 = DBUtils.getPatologie(pid, false);
        table_users1.setItems(listM1);
        data_fine.setValue(null);
    }
/*
    @FXML
    void update_fid(ActionEvent event) {
        conn = mysqlconnect.ConnectDb();
        try {
            String getfid = "select fid from farmaco where nome= ? ";
            pst = conn.prepareStatement(getfid);
            pst.setString(1, lista_farmaci.getValue());
            ResultSet rs2 = pst.executeQuery();
            if (rs2.next()) {

                fid = rs2.getInt("fid");
                txt_fid.setText(String.valueOf(fid));

            }

        }catch(SQLException e){

        }
        finally {
            // Chiusura della connessione
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Gestisci l'errore di chiusura della connessione
                }
            }
        }
    }
    */
}
