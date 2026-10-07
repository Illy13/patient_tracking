# Patient Tracking

A **Java + JavaFX** desktop application for monitoring patients. Patients record their blood pressure, medication intake, symptoms, conditions and concomitant therapies; doctors review the status of all their patients, manage therapies and communicate with them through messages.

The database is an **in-memory H2 database** (PostgreSQL compatibility mode), created and populated with sample data on every launch: **no database installation or configuration is required**.

---

## Features

### Patient area
- Login with ID number and password
- **Blood pressure** logging (diastolic/systolic) with value validation
- **Medication intake** logging
- Logging of **symptoms**, **medical conditions** and **concomitant therapies**
- Control table: for each drug, prescribed intakes vs. intakes taken today
- **Chat** with their own doctor

### Doctor area
- Login with ID number and password
- List of the doctor's patients with their **status** (`Ok`, `Allerta` if no medication has been taken for more than 3 days, `unknown`) and the date of the last record
- Patient report:
  - **weekly and monthly blood pressure** charts
  - **therapies** (add, edit, delete)
  - current and past **medical conditions**
  - **risk factors**, age and blood pressure classification
  - reported **symptoms**
- **Chat** with the selected patient

---

## Tech stack

| | |
|---|---|
| Language | Java 11 |
| UI | JavaFX 17 (FXML) |
| Database | H2 2.2, in-memory, PostgreSQL mode |
| Data access | JDBC (`PreparedStatement`) |
| Build | Maven |

---

## Requirements

- **JDK 11 or later** (JavaFX is downloaded automatically by Maven, no separate installation needed)
- **Maven** (or an IDE that bundles it, such as IntelliJ IDEA / Eclipse / VS Code)
- Git

---

## Download and run

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/<repository-name>.git
cd <repository-name>

# 2. Run the application
mvn clean javafx:run
```

Alternatively, from an IDE: open the folder as a Maven project and run the main class `com.patient_traking.App`.

On startup the terminal prints:

```
DataBase H2 connesso con successo (In-Memory)!
Tabelle H2 create e inizializzate con successo!
```

> **Note:** the database is in memory. Data is reset to the sample data on every launch, and changes made while using the app are not saved.

---

## Test credentials

| Role | Name | ID number | Password |
|---|---|---|---|
| Patient | Giuseppe Verdi | `1001` | `pass123` |
| Doctor | Mario Rossi | `1` | `doc123` |

On the start screen choose **Paziente** (patient) or **Medico** (doctor) and enter the credentials above.

### Sample data

- **Patients** of doctor 1: Giuseppe Verdi (`1001`), Maria Bianchi (`1002`), Luca Ferrari (`1003`)
- **Drugs**: Tachipirina 1000, Aspirina 500, Omez 20mg
- **Therapies**: Giuseppe takes Tachipirina (3 intakes/day) and Omez (1/day); Maria takes Aspirina
- **Conditions** of Giuseppe: hypertension and gastritis (ongoing), flu (past)
- **Blood pressure** of Giuseppe: readings over the last 25 days
- **Messages**: one conversation between Giuseppe and the doctor

---

## Suggested test walkthrough

**As a patient (`1001` / `pass123`)**
1. Press the **refresh** button (circular arrow): you see prescribed drugs and intakes taken today.
2. **Registra pressione**: enter values such as 80/120.
3. **Registra assunzione**: log a dose of Tachipirina, then press refresh again.
4. **Contatta medico**: send a message.

**As a doctor (`1` / `doc123`)**
1. The patient list shows each patient's status.
2. Click the chart icon next to Giuseppe Verdi to open his report.
3. Try the weekly/monthly **blood pressure** charts, **Terapie** (add, edit, delete), **Patologie**, **Info** (risk factors), **Sintomi** and **Messaggi**.

---

## Architecture

The code is organized in the package `com.patient_traking` following a simple layered structure:

- **Connection and data access**: `DBConnection` opens the in-memory H2 connection and creates and populates the tables at startup; `DBUtils` contains all the SQL queries used by the application.
- **View**: `View` handles scene changes and the opening of windows.
- **Controllers**: one JavaFX controller per screen (login, patient home, doctor home, patient report, therapies, conditions, risk factors, symptoms, charts, messages).
- **Model classes**: small classes representing the rows shown in the tables (users, therapies, conditions, symptoms, risk factors, messages).

---

## Database schema

| Table | Content |
|---|---|
| `matpaz`, `matmed` | Login credentials of patients and doctors |
| `pazienti` | Patient data: name, age and reference doctor (`med_ref`) |
| `farmaco` | List of drugs |
| `terapia` | Therapies prescribed by the doctor |
| `terapia_concom` | Concomitant therapies declared by the patient |
| `assunzione` | History of medication intakes |
| `pressione` | Blood pressure readings |
| `patologia`, `patologie_paziente` | Catalogue of conditions and conditions of each patient |
| `fattore_rischio`, `fattori_rischio_paz` | Risk factors and their association with patients |
| `sintomi`, `registra_sintomi` | Catalogue of symptoms and symptoms logged by patients |
| `messaggi` | Doctor-patient chat |

---

## Known limitations

- Passwords in the sample database are stored in plain text. This is acceptable for a demo, but a real system should store salted hashes (e.g. BCrypt).
- The database is in memory and does not persist between launches.
- The chat does not refresh automatically: reopen the window to see new messages.

---
