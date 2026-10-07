package com.patient_traking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class fattori_controller {
    Integer pid;
    @FXML
    private TableColumn<fattori, String> col_fattori;
    @FXML
    private Label label_class;
    @FXML
    private Label label_eta;
    @FXML
    private Label label_nome;
    @FXML
    private ComboBox<String> lista_fattori;
    @FXML
    private TableView<fattori> table_users;
    Connection conn =null;
    PreparedStatement pst = null;
    ResultSet rs=null,rs1=null;
    Integer mid,fid;
    ObservableList<String> FattoriList = FXCollections.observableArrayList();
    int index=-1;
    ObservableList<fattori> listM;

    @FXML
    public void Add_users(ActionEvent event) {
        Integer fat_id=null;
        conn = DBConnection.getConn();
        try{
            String sql1="select id from fattore_rischio where nome= ? ";
            String sql = "insert into fattori_rischio_paz (pid,id_fattore)values(?,?)";
            pst=conn.prepareStatement(sql1);
            pst.setString(1,lista_fattori.getValue());
            System.out.println(lista_fattori.getValue());
            rs=pst.executeQuery();
            if(rs.next()) {
                fat_id = rs.getInt("id");
            }
            pst = conn.prepareStatement(sql);
            pst.setInt(1, this.pid);
            pst.setInt(2,fat_id);
            pst.execute();
            //JOptionPane.showMessageDialog(null, "Users Add succes");
        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null,e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if(Integer.parseInt(e.getSQLState())==23505)
                alert.setContentText("Errore, stai inserendo due fattori di rischio uguali");
            else
                alert.setContentText(e.getSQLState()+e.getMessage());
            alert.show();
        }
        finally {
            if ( rs!= null ) {
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
        Integer fat_id=null;
        conn = DBConnection.getConn();
        String sql= "DELETE FROM fattori_rischio_paz where pid = ? and id_fattore= ? ";
        try {
            String sql1="select id from fattore_rischio where nome= ? ";
            pst=conn.prepareStatement(sql1);
            pst.setString(1,lista_fattori.getValue());
            rs=pst.executeQuery();
            if(rs.next())
                fat_id=rs.getInt("id");
            pst=conn.prepareStatement(sql);
            pst.setInt(1,this.pid);
            pst.setInt(2,fat_id);
            pst.execute();
        }catch(SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getSQLState()+e.getMessage());
            alert.show();
        }
        finally {
            if ( rs!= null ) {
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
    public void getSelected(MouseEvent event) {
        index = table_users.getSelectionModel().getSelectedIndex();
        if (index <= -1){
            return;
        }
        lista_fattori.setValue(col_fattori.getCellData(index).toString());
    }
    public void mostratabella(Integer pid){
        this.pid=pid;
        Integer minima,massima;
        Integer eta=null;
        conn = DBConnection.getConn();
        String getnome = "select nome from fattore_rischio ";
        try {
            pst = conn.prepareStatement(getnome);
            rs1 = pst.executeQuery();
            while (rs1.next()) {
                String fattore = rs1.getString("nome");
                FattoriList.add(fattore);
            }
            String getvalori="SELECT AVG(minima)::int AS minima, AVG(massima)::int AS massima " +
                    "FROM pressione p " +
                    "WHERE dataora > CURRENT_DATE - 30 " +
                    "AND pid = ? ";
            pst=conn.prepareStatement(getvalori);
            pst.setInt(1,pid);
            rs=pst.executeQuery();
            if(rs.next()) {
                minima = rs.getInt("minima");
                massima=rs.getInt("massima");
                System.out.println(minima);
                System.out.println(massima);
                System.out.println(setClassificazoone(minima,massima));
                label_class.setText("Classificazione: " +setClassificazoone(minima,massima));
            }
            String geteta="select eta from pazienti where pid = ?";
            pst=conn.prepareStatement(geteta);
            pst.setInt(1,pid);
            rs=pst.executeQuery();
            if(rs.next()) {
                eta = rs.getInt("eta");
                label_eta.setText("Età: " + eta);
            }

        }catch(SQLException e){
        }
        finally {
            if ( rs!= null &&rs1!=null ) {
                try {
                    rs.close();
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

        String nome=null,cognome=null;
        String nome_cog="select nome, cognome from pazienti where pid = ? ";
        try {
            pst = conn.prepareStatement(nome_cog);
            pst.setInt(1, pid);
            ResultSet rs1 = pst.executeQuery();
            if (rs1.next()) {
                nome = rs1.getString("nome");
                cognome=rs1.getString("cognome");
                label_nome.setText("" + nome + " " + cognome);
                System.out.println("" + nome + " " + cognome);

            }
        }catch(SQLException e){

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
            /*if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }*/
        }

        lista_fattori.setItems(FattoriList);
        //lista_farmaci.setPromptText(col_farmaco.getCellData(index).toString());
        col_fattori.setCellValueFactory(new PropertyValueFactory<fattori,String>("Nome"));
        listM = DBUtils.getFattori(pid);
        table_users.setItems(listM);
    }
    public String setClassificazoone(Integer pressioneMinima, Integer pressioneMassima)
    {
        if (pressioneMassima ==0 && pressioneMinima ==0) {
            return "Non classificato";
        }
        if (pressioneMassima < 120 && pressioneMinima < 80) {
            return "Ottimale";
        } else if (pressioneMassima < 130 && pressioneMinima < 85) {
            return "Normale";
        } else if (pressioneMassima >= 130 && pressioneMassima <= 139 && pressioneMinima >= 85 && pressioneMinima <= 89) {
            return "Normale - Alta";
        } else if (pressioneMassima >= 140 && pressioneMassima <= 149 && pressioneMinima >= 90 && pressioneMinima <= 94) {
            return "Ipertensione di Grado 1 borderline";
        } else if (pressioneMassima >= 150 && pressioneMassima <= 159 && pressioneMinima >= 95 && pressioneMinima <= 99) {
            return "Ipertensione di Grado 1 lieve";
        } else if (pressioneMassima >= 160 && pressioneMassima <= 179 && pressioneMinima >= 100 && pressioneMinima <= 109) {
            return "Ipertensione di Grado 2 moderata";
        } else if (pressioneMassima >= 180 && pressioneMinima >= 110) {
            return "Ipertensione di Grado 3 grave";
        } else if (pressioneMassima >= 140 && pressioneMassima <= 149 && pressioneMinima < 90) {
            return "Ipertensione sistolica isolata borderline";
        } else if (pressioneMassima >= 150 && pressioneMinima < 90) {
            return "Ipertensione sistolica isolata";
        } else {
            return "Non classificato";
        }
    }

    public void UpdateTable(){
        col_fattori.setCellValueFactory(new PropertyValueFactory<fattori,String>("Nome"));
        listM = DBUtils.getFattori(pid);
        table_users.setItems(listM);
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
