package com.patient_traking;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/*CLASSE VIEW: permette il cambio delle scene e la visualizzazione dei risultati richiesti*/
public class View {
    public static void changeScene(ActionEvent event, String fxmlFile, String title) {
        Parent root = null;
        try {
            root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void changeSceneMed(ActionEvent event, String fxmlFile, String title, Integer id, String nome, String cognome) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            LoggedInMed loggedInMed = loader.getController();
            loggedInMed.setUserInformation(id, nome, cognome);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void changeScenePaz(ActionEvent event, String fxmlFile, String title, String nome, String cognome) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlFile));
            root = loader.load();
            LoggedInPazController loggedInPaz = loader.getController();
            loggedInPaz.setUserInformation(nome, cognome);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 555, 371));
        stage.show();
    }

    public static void popUpScene(ActionEvent event, String fxmlFile, String title) {
        Parent root = null;
        try {
            root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image icon = new Image(View.class.getResourceAsStream("/AppIcon.png"));
        Stage stage = new Stage();
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setScene(new Scene(root, 323, 297));
        stage.setTitle(title);
        stage.getIcons().add(icon);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.show();
    }

    public static void messageScene(ActionEvent event, String fxmlFile, String title) {
        Parent root = null;
        try {
            root = FXMLLoader.load(DBUtils.class.getResource(fxmlFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image icon = new Image(View.class.getResourceAsStream("/AppIcon.png"));
        Stage stage = new Stage();
        stage.initOwner(((Node) event.getSource()).getScene().getWindow());
        stage.setScene(new Scene(root, 600, 400));
        stage.setTitle(title);
        stage.getIcons().add(icon);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.show();
    }

    public static void changescenePressSett(String fxmlFile,Integer pid)
    {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(DBUtils.class.getResource(fxmlFile));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Press_sett_controller graf_sett_pz = loader.getController();
        graf_sett_pz.setGraph(pid);
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public static void changescenePressMens(String fxmlFile,Integer pid)
    {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(DBUtils.class.getResource(fxmlFile));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Press_mens_controller graf_mens_pz = loader.getController();
        graf_mens_pz.setGraph(pid);
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
    }
    public static void changesceneTerapie(String fxmlFile,Integer pid)
    {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(DBUtils.class.getResource(fxmlFile));
        try {
            loader.load();
            terapie_controller tp = loader.getController();
            tp.mostratabella(pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public static void changescenePatologie(String fxmlFile,Integer pid)
    {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(DBUtils.class.getResource(fxmlFile));
        try {
            loader.load();
            patologie_controller pt = loader.getController();
            pt.mostratabella(pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public static void changesceneInfo(String fxmlFile,Integer pid)
    {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(DBUtils.class.getResource(fxmlFile));
        try {
            loader.load();
            fattori_controller pt = loader.getController();
            pt.mostratabella(pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
    }

    public static void changesceneSinto(String fxmlFile,Integer pid)
    {
        FXMLLoader loader = new FXMLLoader ();
        loader.setLocation(DBUtils.class.getResource(fxmlFile));
        try {
            loader.load();
            sintomi_controller pt = loader.getController();
            pt.mostratabella(pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent parent = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(parent));
        stage.show();
    }
}


