package com.patient_traking;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.sql.*;
public class Press_sett_controller {
    @FXML
    private LineChart<?, ?> grafico_sett;
    String query = null;
    Connection connection = DBConnection.getConn();
    ResultSet resultSet = null;
    PreparedStatement preparedStatement;

    public void setGraph(Integer id){
        XYChart.Series series = new XYChart.Series();
        XYChart.Series series1 = new XYChart.Series();
        PreparedStatement pstmt = null;
        try {
         pstmt = connection.prepareStatement("SELECT count(distinct to_char(dataora,'YYYY-MM-DD'))::INT as conteggio FROM pressione WHERE PID = ? AND dataora > CURRENT_DATE - 6");
         pstmt.setInt(1, id);

        ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Integer ct=1;
                int conteggio=rs.getInt("conteggio");

                String query1 = "SELECT pid, to_char(dataora, 'YYYY-MM-DD'), max(minima) AS minima, max(massima) AS massima " +
                        "FROM public.pressione " +
                        "WHERE PID = ? AND dataora > CURRENT_DATE - 6 " +
                        "GROUP BY to_char(dataora, 'YYYY-MM-DD'), pid " +
                        "ORDER BY to_char(dataora, 'YYYY-MM-DD') ASC";

                PreparedStatement pstmt1 = connection.prepareStatement(query1);
                pstmt1.setInt(1, id);

                ResultSet rs1 = pstmt1.executeQuery();
                while(rs1.next()&& ct<=conteggio){

                    int minima = rs1.getInt("minima");

                    int massima = rs1.getInt("massima");
                    String ct_str = "" + ct;
                    System.out.println(ct_str);
                    System.out.println(minima);
                    System.out.println(massima);
                    System.out.println("\n");
                    series.getData().add(new XYChart.Data(ct_str,minima));
                    series.setName("Pressione Minima");
                    series1.getData().add(new XYChart.Data(ct_str,massima));
                    series1.setName("Pressione Massima");
                    ct++;

                }




            }





        } catch (SQLException e) {
            e.printStackTrace();
        }
        grafico_sett.getData().addAll(series,series1);
    }





    }

