# Patient Tracking

Applicazione desktop in **Java + JavaFX** per il monitoraggio dei pazienti da parte di un medico. Il paziente registra pressione, assunzione dei farmaci, sintomi, patologie e terapie concomitanti; il medico consulta lo stato di tutti i suoi pazienti, gestisce le terapie e comunica con loro tramite messaggi.

Il database è **H2 in memoria** (modalità compatibile PostgreSQL), creato e popolato con dati di prova a ogni avvio: **non serve installare né configurare nessun database**.

---

## Funzionalità

### Area paziente
- Login con matricola e password
- Registrazione della **pressione** (minima/massima) con controllo dei valori
- Registrazione delle **assunzioni dei farmaci**
- Registrazione di **sintomi**, **patologie** e **terapie concomitanti**
- Tabella di controllo: per ogni farmaco, assunzioni prescritte e assunzioni effettuate oggi
- **Chat** con il proprio medico

### Area medico
- Login con matricola e password
- Elenco dei propri pazienti con **stato** (`Ok`, `Allerta` se non assume farmaci da più di 3 giorni, `unknown`) e data dell'ultima rilevazione
- Resoconto di ogni paziente:
  - grafici della **pressione settimanale e mensile**
  - **terapie** (aggiunta, modifica, eliminazione)
  - **patologie** in corso e pregresse
  - **fattori di rischio**
  - **sintomi** segnalati
- **Chat** con il paziente selezionato

---

## Tecnologie

| | |
|---|---|
| Linguaggio | Java 11 |
| Interfaccia | JavaFX 17 (FXML) |
| Database | H2 2.2 in-memory, modalità PostgreSQL |
| Build | Maven |
| Accesso ai dati | JDBC (`PreparedStatement`) |

---

## Requisiti

- **JDK 11 o superiore** (JavaFX viene scaricato automaticamente da Maven, non serve installarlo a parte)
- **Maven** (oppure un IDE come IntelliJ IDEA / Eclipse / VS Code che lo includa)
- Git

---

## Scaricare ed eseguire il progetto

```bash
# 1. Clona il repository
git clone https://github.com/<tuo-username>/<nome-repository>.git
cd <nome-repository>

# 2. Avvia l'applicazione
mvn clean javafx:run
```

In alternativa, da IDE: apri la cartella come progetto Maven e avvia la classe principale `com.patient_traking.App`.

All'avvio nel terminale compaiono i messaggi:

```
DataBase H2 connesso con successo (In-Memory)!
Tabelle H2 create e inizializzate con successo!
```

> **Nota:** il database è in memoria. A ogni avvio i dati ripartono da quelli di prova, e le modifiche fatte durante l'uso non vengono salvate.

---

## Credenziali di prova

| Ruolo | Nome | Matricola | Password |
|---|---|---|---|
| Paziente | Giuseppe Verdi | `1001` | `pass123` |
| Medico | Mario Rossi | `1` | `doc123` |

Nella schermata iniziale scegli **Paziente** o **Medico** e inserisci le credenziali della tabella.

### Dati di prova presenti

- **Pazienti** del medico 1: Giuseppe Verdi (`1001`), Maria Bianchi (`1002`), Luca Ferrari (`1003`)
- **Farmaci**: Tachipirina 1000, Aspirina 500, Omez 20mg
- **Terapie**: Giuseppe ha Tachipirina (3 assunzioni/giorno) e Omez (1/giorno); Maria ha Aspirina
- **Patologie** di Giuseppe: ipertensione e gastrite (in corso), influenza (pregressa)
- **Pressione** di Giuseppe: rilevazioni negli ultimi 25 giorni
- **Messaggi**: una conversazione tra Giuseppe e il medico

---

## Percorso di test consigliato

**Come paziente (`1001` / `pass123`)**
1. Premi **Aggiorna** (freccia circolare): vedi farmaci prescritti e assunti oggi.
2. **Registra pressione**: inserisci valori come 80/120.
3. **Registra assunzione**: registra una dose di Tachipirina e premi di nuovo Aggiorna.
4. **Contatta medico**: scrivi un messaggio.

**Come medico (`1` / `doc123`)**
1. Nella lista pazienti trovi lo stato di ciascuno.
2. Clicca l'icona del grafico accanto a Giuseppe Verdi per aprire il resoconto.
3. Prova **Pressione settimanale / mensile**, **Terapie** (aggiungi, modifica, elimina), **Patologie**, **Info** (fattori di rischio), **Sintomi** e **Messaggi**.

---

## Struttura del progetto

Package `com.patient_traking`.

### Accesso al database

| Classe | Ruolo |
|---|---|
| `DBConnection` | Apre la connessione H2 in memoria, crea le tabelle e inserisce i dati di prova all'avvio. Espone `getConn()`. |
| `DBUtils` | Classe "model": contiene tutte le query SQL dell'applicazione (vedi sotto). |

### Metodi di `DBUtils`

| Metodo | Cosa fa |
|---|---|
| `logInPaziente(event, matricola, password)` | Verifica le credenziali del paziente e apre la sua schermata. |
| `logInMedico(event, matricola, password)` | Verifica le credenziali del medico e apre la lista dei suoi pazienti. |
| `getMidMed(pid)` | Restituisce la matricola del medico di un paziente. |
| `getDatausers(med_ref)` | Elenco pazienti di un medico, con stato (`Ok`/`Allerta`/`unknown`) e data dell'ultima assunzione. |
| `getTerapie(pid)` | Terapie prescritte a un paziente (farmaco, quantità, assunzioni giornaliere, indicazioni). |
| `addTerapia(mid, pid, fid, indicazioni, quantita, assunzioni)` | Inserisce una nuova terapia. |
| `getPatologie(pid, isPregressa)` | Patologie in corso o pregresse di un paziente. |
| `getFattori(pid)` | Fattori di rischio di un paziente. |
| `getSintomi(pid)` | Sintomi registrati da un paziente. |
| `RegistraPressioni(pid, SIS, DIA, dataOra)` | Salva una misurazione della pressione, rifiutando valori non plausibili. |
| `RegistraFarmaco(event, pid, nomeF, quantita, dataOra)` | Registra l'assunzione di un farmaco e controlla il rispetto della terapia. |
| `RegistraSintomi(pid, nomeS, info, data)` | Registra un sintomo. |
| `RegistraPatologia(pid, nomeP, dataI, dataF)` | Registra una patologia con data di inizio ed eventuale fine. |
| `RegistraTerapiaConc(quantita, pid, nomeF, dataI, dataF)` | Registra una terapia concomitante (aggiunge il farmaco se non esiste). |
| `getAssunzioniGiornalierePerFarmaco(pid, fid)` | Assunzioni effettuate oggi per un farmaco. |
| `getAssunzioniPrescrittePerFarmaco(pid, fid)` | Assunzioni giornaliere prescritte per un farmaco. |
| `visualizzaMessaggi(midMed, pidPaz, tabella, chat)` | Carica la conversazione medico-paziente. |
| `insertMessage(mittente, mid, pid, testo)` | Salva un messaggio (`m` = medico, `p` = paziente). |

### Interfaccia (controller e scene)

| Classe | Ruolo |
|---|---|
| `View` | Gestisce il cambio di scena e l'apertura delle finestre (resoconto, grafici, popup, chat). |
| `PazienteController` / `MedicoController` | Schermate di login. |
| `LoggedInPazController` | Schermata principale del paziente e tabella di controllo delle assunzioni. |
| `LoggedInMed` | Schermata principale del medico: tabella dei pazienti con pulsante di accesso al resoconto. |
| `show_resoconto_paziente_Controller` | Resoconto di un paziente visto dal medico; apre grafici, terapie, patologie, info, sintomi e chat. |
| `terapie_controller` | Aggiunta, modifica ed eliminazione delle terapie. |
| `PatologiaController` | Popup del paziente per registrare una patologia (date di inizio/fine con controllo delle date). |
| `fattori_controller` | Scheda "Info" del paziente: età, classificazione della pressione (media degli ultimi 30 giorni) e gestione dei fattori di rischio. |
| `sintomi_controller` | Tabella dei sintomi registrati dal paziente. |
| `Press_sett_controller`, `Press_mens_controller` | Grafici della pressione degli ultimi 6 e 30 giorni (valori massimi giornalieri). |

### Classi del modello

`Users`, `terapie`, `patologie`, `fattori`, `sintomi`, `Messaggio`, `CheckAssunzione` rappresentano le righe mostrate nelle tabelle. `GetPid` conserva il pid del paziente loggato.

---

## Schema del database

| Tabella | Contenuto |
|---|---|
| `matpaz`, `matmed` | Credenziali di accesso di pazienti e medici |
| `pazienti` | Anagrafica, età e medico di riferimento (`med_ref`) |
| `farmaco` | Elenco dei farmaci |
| `terapia` | Terapie prescritte dal medico |
| `terapia_concom` | Terapie concomitanti dichiarate dal paziente |
| `assunzione` | Storico delle assunzioni di farmaci |
| `pressione` | Misurazioni della pressione |
| `patologia`, `patologie_paziente` | Catalogo delle patologie e patologie di ogni paziente |
| `fattore_rischio`, `fattori_rischio_paz` | Fattori di rischio e loro associazione ai pazienti |
| `sintomi`, `registra_sintomi` | Catalogo dei sintomi e sintomi registrati |
| `messaggi` | Chat medico-paziente |

---

## Limiti noti

- Le password nel database di prova sono in chiaro: va bene per una demo, ma in un sistema reale vanno salvate con hash e salt (per esempio BCrypt).
- Il database è in memoria e non persiste tra un avvio e l'altro.
- La chat non si aggiorna automaticamente: la finestra va riaperta per vedere i nuovi messaggi.

---

## Autore

<Nome Cognome> · [LinkedIn](https://www.linkedin.com/in/<tuo-profilo>) · <email>
