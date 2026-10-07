package com.patient_traking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*CLASSE MODEL: permette le comunicazioni con il DB */
public class DBUtils {
    public static int getMidMed(int pid) {
        Connection connection = DBConnection.getConn();
        PreparedStatement pst_med = null;
        ResultSet rs_med = null;
        Integer mid = 0;
        try {
            pst_med = connection.prepareStatement("SELECT med_ref FROM pazienti WHERE pid = ?");
            pst_med.setInt(1, pid);
            rs_med = pst_med.executeQuery();
            if (rs_med.next()) {
                mid = rs_med.getInt("med_ref");
            }
        } catch (SQLException e) {
            System.out.print("Errore lettura dati " + e.getMessage());
        }

        return mid;
    }

    public static ObservableList<Users> getDatausers(Integer med_ref){
        Connection connection = DBConnection.getConn();
        PreparedStatement pstmt=null;
        ResultSet rs = null, rs1 = null, rs2 = null;
        ObservableList<Users> list = FXCollections.observableArrayList();
        try {
            pstmt = connection.prepareStatement("SELECT pid, nome, cognome FROM pazienti WHERE med_ref = ?");
            pstmt.setInt(1, med_ref);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                PreparedStatement pstmt1 = connection.prepareStatement("SELECT CASE WHEN pr.pid IS NULL THEN 'unknown' WHEN MAX(pr.dataora) < CURRENT_DATE - 3 THEN 'Allerta' ELSE 'Ok' END AS risultato FROM pazienti p LEFT JOIN assunzione pr ON p.pid = pr.pid WHERE p.pid = ? GROUP BY pr.pid");
                pstmt1.setInt(1, rs.getInt("pid"));
                rs1 = pstmt1.executeQuery();

                PreparedStatement pstmt2 = connection.prepareStatement("SELECT p.pid, CASE WHEN pr.pid IS NULL THEN 'unknown' ELSE TO_CHAR(MAX(pr.dataora), 'YYYY-MM-DD') END AS risultato FROM pazienti p LEFT JOIN assunzione pr ON p.pid = pr.pid WHERE p.pid = ? GROUP BY p.pid, pr.pid");
                pstmt2.setInt(1, rs.getInt("pid"));
                rs2 = pstmt2.executeQuery();
                if (rs1.next() && rs2.next()) {
                    Integer id_int = rs.getInt("pid");
                    String nome_str = rs.getString("nome");
                    String cognome_str = rs.getString("cognome");
                    String status_str = rs1.getString("risultato");
                    String rilevazione_str = rs2.getString("risultato");
                    System.out.println("Id: " + id_int + "Nome: " + nome_str + " Cognome: " + cognome_str + " Status: " + status_str + " Rilevazione: " + rilevazione_str);
                    Users utente = new Users(id_int, nome_str, cognome_str, status_str, rilevazione_str );
                    list.add(utente);
                    //list.add(new users(id_int,nome_str,cognome_str,status_str,rilevazione_str));
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs!= null && rs1!=null && rs2!=null) {
                try {
                    rs.close();
                    rs1.close();
                    rs2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static ObservableList<terapie> getTerapie(Integer pid){
        Connection connection = DBConnection.getConn();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ObservableList<terapie> list = FXCollections.observableArrayList();
        try {
            pstmt = connection.prepareStatement(
                    "SELECT t.id, t.fid, f.nome, t.quantita, t.n_ass, t.indicazioni " +
                    "FROM farmaco f JOIN terapia t ON f.fid = t.fid WHERE t.pid = ?");
            pstmt.setInt(1, pid);
            rs = pstmt.executeQuery();

            while (rs.next()){
                int fid=rs.getInt("fid");
                int id=rs.getInt("id");
                String nome=rs.getString("nome");
                int quantita=rs.getInt("quantita");
                int n_ass=rs.getInt("n_ass");
                String indicazioni=rs.getString("indicazioni");
                System.out.println("Id: " + fid + "Nome: " + nome + " quantita: " + quantita+ " Assunzioni: " +n_ass+ " Indicazioni: " +indicazioni);
                list.add(new terapie(id, fid,nome,quantita,n_ass,indicazioni));
                //list.add(new users(id_int,nome_str,cognome_str,status_str,rilevazione_str));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs!= null ) {
                try {
                    rs.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static ObservableList<patologie> getPatologie(Integer pid, Boolean isPregressa) {
        Connection connection = DBConnection.getConn();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ObservableList<patologie> list = FXCollections.observableArrayList();
        try {
            if (isPregressa == true) {
                pstmt = connection.prepareStatement("SELECT pat.nome, papz.data_inizio, papz.data_fine FROM patologia pat JOIN patologie_paziente papz ON pat.id = papz.id_patologia WHERE papz.pid = ? AND papz.data_fine IS NOT NULL");
                pstmt.setInt(1, pid);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    String patologia = rs.getString("nome");
                    String inizio = rs.getString("data_inizio");
                    String fine = rs.getString("data_fine");
                    list.add(new patologie(patologia, inizio, fine));
                    //list.add(new users(id_int,nome_str,cognome_str,status_str,rilevazione_str));
                }
                return list;
            }
            else {
                pstmt = connection.prepareStatement("SELECT pat.nome, papz.data_inizio, papz.data_fine FROM patologia pat JOIN patologie_paziente papz ON pat.id = papz.id_patologia WHERE papz.pid = ? AND papz.data_fine IS NULL");
                pstmt.setInt(1, pid);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    String patologia = rs.getString("nome");
                    String inizio = rs.getString("data_inizio");
                    //String fine = rs.getString("data_fine");
                    String fine="In Corso";
                    list.add(new patologie(patologia, inizio, fine));
                    //list.add(new users(id_int,nome_str,cognome_str,status_str,rilevazione_str));
                }
            }} catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if ( rs!= null ) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static ObservableList<fattori> getFattori(Integer pid){
        Connection connection = DBConnection.getConn();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ObservableList<fattori> list = FXCollections.observableArrayList();
        try {
            pstmt = connection.prepareStatement("SELECT fa.nome FROM fattore_rischio fa JOIN fattori_rischio_paz fapz ON fa.id = fapz.id_fattore WHERE pid = ?");
            pstmt.setInt(1, pid);
            rs = pstmt.executeQuery();

            while (rs.next()){
                String nome=rs.getString("nome");
                //System.out.println("Id: " + fid + "Nome: " + nome + " quantita: " + quantita+ " Assunzioni: " +n_ass+ " Indicazioni: " +indicazioni);
                list.add(new fattori(nome));
                //list.add(new users(id_int,nome_str,cognome_str,status_str,rilevazione_str));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs!= null ) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static ObservableList<sintomi> getSintomi(Integer pid){
        Connection connection = DBConnection.getConn();
        PreparedStatement pstmt=null;
        ResultSet rs=null;
        ObservableList<sintomi> list = FXCollections.observableArrayList();
        try {
            pstmt = connection.prepareStatement("SELECT s.nome, r.info, r.data FROM sintomi s JOIN registra_sintomi r ON s.sid = r.sid WHERE r.pid = ?");
            pstmt.setInt(1, pid);
            rs = pstmt.executeQuery();

            while (rs.next()){
                String sintomo=rs.getString("nome");
                String info=rs.getString("info");
                String data=rs.getString("data");
                //System.out.println("Id: " + fid + "Nome: " + nome + " quantita: " + quantita+ " Assunzioni: " +n_ass+ " Indicazioni: " +indicazioni);
                list.add(new sintomi(sintomo,info,data));
                //list.add(new users(id_int,nome_str,cognome_str,status_str,rilevazione_str));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            if ( rs!= null ) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public static void addTerapia(int mid, int pid, int fid, String indicazioni, int quantita, int assunzioni){
        Connection conn = DBConnection.getConn();
        PreparedStatement pst = null;
        ResultSet resultSet = null;
        try{
            pst = conn.prepareStatement("insert into terapia (indicazioni,quantita,n_ass,mid,pid,fid)values(?,?,?,?,?,? )");
            pst.setString(1, indicazioni);
            pst.setInt(2, quantita);
            pst.setInt(3, assunzioni);
            pst.setInt(4, mid);
            pst.setInt(5, pid);
            pst.setInt(6, fid);
            pst.execute();
            //JOptionPane.showMessageDialog(null, "Users Add succes");
        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null,e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if(Integer.parseInt(e.getSQLState())==23505)
                alert.setContentText("Errore, stai inserendo due terapie uguali");
            else
                alert.setContentText(e.getSQLState()+e.getMessage());
            alert.show();
        }
        finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static void logInPaziente(ActionEvent event, String matricola, String password) {
        Connection connection = DBConnection.getConn();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT matricola, nome, cognome, passw FROM matpaz WHERE matricola = ?");
            preparedStatement.setString(1, matricola);
            resultSet = preparedStatement.executeQuery();

            //controlla che l'utente cercato sia presente
            if (!resultSet.isBeforeFirst()) {
                System.out.println("Paziente non trovato");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("I dati inseriti non sono corretti");
                alert.show();
            } else {
                //scorre la tabella con username = username e se trova che la password fornita coincide con quella presente consente il logIn e cambia scena
                while (resultSet.next()) {
                    String retrievedPassword = resultSet.getString("passw");
                    if (retrievedPassword.equals(password)) {
                        GetPid.getPidPaz(matricola);   // <-- aggiungi questa riga
                        View.changeScenePaz(event, "loggedInPaz.fxml", "Benvenuto", resultSet.getString("nome"), resultSet.getString("cognome"));
                    } else {
                        System.out.println("La password non corrisponde");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("I dati inseriti non sono corretti");
                        alert.show();
                    }
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void logInMedico(ActionEvent event, String matricola, String password) {
    Connection connection = DBConnection.getConn();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    
    try {
        preparedStatement = connection.prepareStatement("SELECT nome, cognome, passw FROM matmed WHERE matricola = ?");
        
        // Se la colonna matricola in DB è INT, proviamo a convertirla
        int idMatricola;
        try {
            idMatricola = Integer.parseInt(matricola);
        } catch (NumberFormatException e) {
            idMatricola = 1; // Fallback di default
        }
        
        preparedStatement.setInt(1, idMatricola);
        resultSet = preparedStatement.executeQuery();

        // Controlla che l'utente cercato sia presente
        if (!resultSet.isBeforeFirst()) {
            System.out.println("Medico non trovato");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("I dati inseriti non sono corretti");
            alert.show();
        } else {
            while (resultSet.next()) {
                String retrievedPassword = resultSet.getString("passw");
                
                if (retrievedPassword != null && retrievedPassword.equals(password)) {
                    // Passiamo direttamente idMatricola senza fare substring(2)
                    View.changeSceneMed(event, "loggedInMed.fxml", "Benvenuto", idMatricola, resultSet.getString("nome"), resultSet.getString("cognome"));
                } else {
                    System.out.println("La password non corrisponde");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("I dati inseriti non sono corretti");
                    alert.show();
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("Errore SQL durante il login medico: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public static void RegistraPressioni(int pid, int SIS, int DIA, Timestamp dataOra) {
        Connection connection = DBConnection.getConn();
        PreparedStatement preparedStatement = null;
        try {
            if (DIA >= SIS || DIA < 50 || SIS > 180) {
                System.out.println("Valori inseriti non ammessi");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Valori fuori dalla media");
                alert.setContentText("Inserisci valori corretti o contatta immediatamente il medico");
                alert.show();
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO pressione (pid, dataora, minima, massima) VALUES (?,?,?,?)");
                preparedStatement.setInt(1, pid);
                preparedStatement.setTimestamp(2, dataOra);
                preparedStatement.setInt(3, DIA);
                preparedStatement.setInt(4, SIS);
                preparedStatement.executeUpdate();
                System.out.println("Row inserted");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText("Operazione andata a buon fine");
                alert.setContentText("Pressione inserita!");
                String imagePath = DBUtils.class.getResource("spunta.png").toExternalForm();
                Image image = new Image(imagePath);
                ImageView spunta = new ImageView(image);
                spunta.setFitHeight(48);
                spunta.setFitWidth(48);
                alert.getDialogPane().setGraphic(spunta);
                alert.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void RegistraFarmaco(ActionEvent event, int pid, String nomeF, int quantita, Timestamp dataOra) {
    Connection connection = DBConnection.getConn();
    PreparedStatement pst_getnomeF = null;
    ResultSet rs_getnomeF = null;
    PreparedStatement pst_select = null;
    ResultSet rs_select = null;
    PreparedStatement pst_count = null;
    ResultSet rs_count = null;
    PreparedStatement pst_insert = null;

    Date data = new Date(dataOra.getTime());
    Integer fid = null;

    try {
        // 1. Recupera l'ID del farmaco
        pst_getnomeF = connection.prepareStatement("SELECT fid FROM farmaco WHERE nome = ?");
        pst_getnomeF.setString(1, nomeF);
        rs_getnomeF = pst_getnomeF.executeQuery();
        if (rs_getnomeF.next()) {
            fid = rs_getnomeF.getInt("fid");
        }

        if (fid == null) {
            System.out.println("Farmaco non trovato");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Farmaco non valido o non trovato!");
            alert.show();
            return;
        }

        // 2. Seleziona la terapia associata al paziente e al farmaco
        pst_select = connection.prepareStatement("SELECT T.quantita, T.n_ass, T.mid, T.pid, T.fid, F.nome FROM terapia AS T JOIN farmaco AS F ON T.fid = F.fid WHERE T.pid = ? AND F.fid = ?");
        pst_select.setInt(1, pid);
        pst_select.setInt(2, fid);
        rs_select = pst_select.executeQuery();

        // 3. Conta le assunzioni giornaliere (Usando CAST(dataora AS DATE) per H2)
        pst_count = connection.prepareStatement("SELECT COUNT(pid) AS n_assPaz FROM assunzione WHERE pid = ? AND (CAST(dataora AS DATE) = ?) AND fid = ?");
        pst_count.setInt(1, pid);
        pst_count.setDate(2, data);
        pst_count.setInt(3, fid);
        rs_count = pst_count.executeQuery();

        if (!rs_select.isBeforeFirst()) {
            System.out.println("Terapia non trovata");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Non hai terapie prescritte per questo farmaco!");
            alert.show();
        } else {
            if (rs_select.next() && rs_count.next()) {
                int quantitaRitornata = rs_select.getInt("quantita");
                int n_assRitornate = rs_count.getInt("n_assPaz");
                int n_assPrescritte = rs_select.getInt("n_ass");

                if (quantitaRitornata != quantita || n_assRitornate >= n_assPrescritte) {
                    System.out.println("Quantità inserite non corrette o assunzioni giornaliere superate");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("I dati inseriti non sono corretti o hai già raggiunto il limite di assunzioni per oggi!");
                    alert.show();
                } else {
                    // Inserimento dell'assunzione
                    pst_insert = connection.prepareStatement("INSERT INTO assunzione(pid, fid, quantita, dataora) VALUES (?,?,?,?)");
                    pst_insert.setInt(1, pid);
                    pst_insert.setInt(2, fid);
                    pst_insert.setInt(3, quantita);
                    pst_insert.setTimestamp(4, dataOra);
                    pst_insert.executeUpdate();

                    System.out.println("Row inserted");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("Operazione andata a buon fine");
                    alert.setContentText("Assunzione inserita!");
                    
                    try {
                        String imagePath = DBUtils.class.getResource("spunta.png").toExternalForm();
                        Image image = new Image(imagePath);
                        ImageView spunta = new ImageView(image);
                        spunta.setFitHeight(48);
                        spunta.setFitWidth(48);
                        alert.getDialogPane().setGraphic(spunta);
                    } catch (Exception e) {
                        // Se l'immagine spunta.png manca, l'alert mostrerà l'icona di default
                    }
                    alert.show();
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("Errore durante la registrazione del farmaco: " + e.getMessage());
        e.printStackTrace();
    } finally {
        try {
            if (rs_getnomeF != null) rs_getnomeF.close();
            if (pst_getnomeF != null) pst_getnomeF.close();
            if (rs_select != null) rs_select.close();
            if (pst_select != null) pst_select.close();
            if (rs_count != null) rs_count.close();
            if (pst_count != null) pst_count.close();
            if (pst_insert != null) pst_insert.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

    public static void RegistraSintomi(int pid, String nomeS, String info, Date data) {
        Connection connection = DBConnection.getConn();
        PreparedStatement pst_sintomi = null;
        PreparedStatement pst_getnomeS;
        ResultSet rs_getnomeS;
        Integer sid = null;
        try {
            pst_getnomeS = connection.prepareStatement("SELECT sid FROM sintomi WHERE nome = ?");
            pst_getnomeS.setString(1, nomeS);
            rs_getnomeS = pst_getnomeS.executeQuery();
            if (rs_getnomeS.next()) {
                sid = rs_getnomeS.getInt("sid");
            }
            pst_sintomi = connection.prepareStatement("INSERT INTO registra_sintomi (pid, sid, info, data) VALUES (?, ?, ?, ?)");
            pst_sintomi.setInt(1, pid);
            pst_sintomi.setInt(2, sid);
            pst_sintomi.setString(3, info);
            pst_sintomi.setDate(4, data);
            pst_sintomi.executeUpdate();
            System.out.println("Row inserted");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Operazione andata a buon fine");
            alert.setContentText("Sintomo inserito!");
            String imagePath = DBUtils.class.getResource("spunta.png").toExternalForm();
            Image image = new Image(imagePath);
            ImageView spunta = new ImageView(image);
            spunta.setFitHeight(48);
            spunta.setFitWidth(48);
            alert.getDialogPane().setGraphic(spunta);
            alert.show();

        } catch (SQLException e) {
            System.out.println("Errore inserimento" + e.getMessage());
        }
    }

    public static void visualizzaMessaggi(int midMed, int pidPaz, TableView<Messaggio> tb_tabella, ObservableList<Messaggio> chat){
        Connection connection = DBConnection.getConn();
        PreparedStatement pst_mess = null;
        ResultSet rs_mess = null;
        try{
            pst_mess = connection.prepareStatement("SELECT * FROM messaggi WHERE mid = ? AND pid = ?");
            pst_mess.setInt(1, midMed);
            pst_mess.setInt(2, pidPaz);
            rs_mess = pst_mess.executeQuery();

            chat.clear();
            while (rs_mess.next()) {
                int id = rs_mess.getInt("id");
                String testo = rs_mess.getString("testo");
                Timestamp data = rs_mess.getTimestamp("dataora");
                Messaggio messaggio = new Messaggio(id, testo, data);
                chat.add(messaggio);
            }
            tb_tabella.setItems(chat);
        }catch(SQLException e){
            System.out.println("Errore lettura dati" + e.getMessage());
        }
    }

    public static void insertMessage(String mittente, int mid, int pid, String testo) {
        String newTesto;
        if (mittente.equals("m")) {
            newTesto = "M: " + testo;
        } else if (mittente.equals("p")) {
            newTesto = "P: " + testo;
        } else {
            // Mittente non valido
            return;
        }
        Connection connection = DBConnection.getConn();
        PreparedStatement pst_mess = null;
        try {
            pst_mess = connection.prepareStatement("INSERT INTO messaggi (mittente, mid, pid, testo, dataora) VALUES (?, ?, ?, ?, ?)");
            Date currentDate = new Date(System.currentTimeMillis());
            Timestamp dataora = new Timestamp(currentDate.getTime());

            pst_mess.setString(1, mittente);
            pst_mess.setInt(2, mid);
            pst_mess.setInt(3, pid);
            pst_mess.setString(4, newTesto);
            pst_mess.setTimestamp(5, dataora);
            pst_mess.executeUpdate();
            System.out.println("Row inserted");
        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento del messaggio: " + e.getMessage());
        }
    }

    public static void RegistraPatologia(int pid, String nomeP, Date dataI, Date dataF) {
        Connection connection = DBConnection.getConn();
        PreparedStatement pst_patologia = null;
        PreparedStatement pst_getnomeP;
        ResultSet rs_getnomeP;
        Integer patId = null;
        try {
            pst_getnomeP = connection.prepareStatement("SELECT id FROM patologia WHERE nome = ?");
            pst_getnomeP.setString(1, nomeP);
            rs_getnomeP = pst_getnomeP.executeQuery();
            if (rs_getnomeP.next()) {
                patId = rs_getnomeP.getInt("id");
            }
            pst_patologia = connection.prepareStatement("INSERT INTO patologie_paziente (id_patologia, pid, data_inizio, data_fine) VALUES (?, ?, ?, ?)");
            pst_patologia.setInt(1, patId);
            pst_patologia.setInt(2, pid);
            pst_patologia.setDate(3, dataI);
            pst_patologia.setDate(4, dataF);
            pst_patologia.executeUpdate();

            System.out.println("Row inserted");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Operazione andata a buon fine");
            alert.setContentText("Patologia inserita!");
            String imagePath = DBUtils.class.getResource("spunta.png").toExternalForm();
            Image image = new Image(imagePath);
            ImageView spunta = new ImageView(image);
            spunta.setFitHeight(48);
            spunta.setFitWidth(48);
            alert.getDialogPane().setGraphic(spunta);
            alert.show();

        } catch (SQLException e) {
            System.out.println("Errore inserimento" + e.getMessage());
        }
    }

    public static void RegistraTerapiaConc(int quantita, int pid, String nomeF, Date dataI, Date dataF) {
        Connection connection = DBConnection.getConn();
        PreparedStatement pst_terapia = null;
        PreparedStatement pst_getnomeF;
        ResultSet rs_getnomeF;
        PreparedStatement pst_insert;
        Integer fid = 0;
        try {
            pst_getnomeF = connection.prepareStatement("SELECT fid FROM farmaco WHERE nome = ?");
            pst_getnomeF.setString(1, nomeF);
            rs_getnomeF = pst_getnomeF.executeQuery();
            if (rs_getnomeF.next()) {
                fid = rs_getnomeF.getInt("fid");
            }
            //se il farmaco non è presente lo aggiunge alla tabella dei farmaci e riesegue la query
            else{
                pst_insert = connection.prepareStatement("INSERT INTO farmaco (nome) VALUES (?)");
                pst_insert.setString(1, nomeF);
                pst_insert.executeUpdate();

                pst_getnomeF = connection.prepareStatement("SELECT fid FROM farmaco WHERE nome = ?");
                pst_getnomeF.setString(1, nomeF);
                rs_getnomeF = pst_getnomeF.executeQuery();
                if (rs_getnomeF.next()) {
                    fid = rs_getnomeF.getInt("fid");
                }
            }

            pst_terapia = connection.prepareStatement("INSERT INTO terapia_concom (quantita, pid, fid, data_inizio, data_fine) VALUES (?, ?, ?, ?, ?)");
            pst_terapia.setInt(1, quantita);
            pst_terapia.setInt(2, pid);
            pst_terapia.setInt(3, fid);
            pst_terapia.setDate(4, dataI);
            pst_terapia.setDate(5, dataF);
            pst_terapia.executeUpdate();

            System.out.println("Row inserted");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Operazione andata a buon fine");
            alert.setContentText("Terapia concomitante inserita!");
            String imagePath = DBUtils.class.getResource("spunta.png").toExternalForm();
            Image image = new Image(imagePath);
            ImageView spunta = new ImageView(image);
            spunta.setFitHeight(48);
            spunta.setFitWidth(48);
            alert.getDialogPane().setGraphic(spunta);
            alert.show();


        } catch (SQLException e) {
            System.out.println("Errore lettura dati" + e.getMessage());
        }
    }

    // Metodo per recuperare le assunzioni giornaliere aggiornato per H2
public static int getAssunzioniGiornalierePerFarmaco(int pid, int fid) {
    Connection connection = DBConnection.getConn();
    PreparedStatement pst = null;
    ResultSet rs = null;
    try {
        pst = connection.prepareStatement("SELECT COUNT(pid) AS n_ass FROM assunzione WHERE pid = ? AND fid = ? AND CAST(dataora AS DATE) = CURRENT_DATE");
        pst.setInt(1, pid);
        pst.setInt(2, fid);
        rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("n_ass");
        }
    } catch (SQLException e) {
        System.out.println("Errore lettura dati: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    return 0;
}

    //assunzioni prescritte al paziente di ogni farmaco
    public static int getAssunzioniPrescrittePerFarmaco(int pid, int fid) {
        Connection connection = DBConnection.getConn();
        PreparedStatement pst = null;
        try {
            pst = connection.prepareStatement("SELECT n_ass FROM terapia WHERE  pid = ? AND fid = ?");
            pst.setInt(1, pid);
            pst.setInt(2, fid);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("n_ass");
            }
        }catch (SQLException e){
            System.out.println("Errore lettura dati" + e.getMessage());
        }
        return 0;
    }
}

