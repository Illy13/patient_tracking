package com.patient_traking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sintomi_controller {

    @FXML
    private TableColumn<sintomi, String> col_data;

    @FXML
    private TableColumn<sintomi, String> col_info;

    @FXML
    private TableColumn<sintomi, String> col_sintomo;

    @FXML
    private TableView<sintomi> table_users;

    Integer pid;
    Connection conn=null;
    PreparedStatement pst=null;
    ObservableList<sintomi> listM;
    public void mostratabella(Integer pid){
        this.pid=pid;





        col_sintomo.setCellValueFactory(new PropertyValueFactory<sintomi,String>("Sintomo"));

        col_info.setCellValueFactory(new PropertyValueFactory<sintomi,String>("Info"));
        col_data.setCellValueFactory(new PropertyValueFactory<sintomi,String>("Data"));


        listM = DBUtils.getSintomi(pid);
        table_users.setItems(listM);




    }






}
