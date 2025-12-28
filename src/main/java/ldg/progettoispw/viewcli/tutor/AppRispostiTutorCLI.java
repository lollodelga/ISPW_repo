package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.controller.AppRispostiTutorCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;

import java.util.List;
import java.util.logging.Logger;

public class AppRispostiTutorCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(AppRispostiTutorCLI.class.getName());
    private final AppRispostiTutorCtrlApplicativo ctrl;

    public AppRispostiTutorCLI() {
        super();
        this.ctrl = new AppRispostiTutorCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean viewing = true;

        while (viewing) {
            printHeader("STORICO E GESTIONE APPUNTAMENTI");
            LOGGER.info("(Scegli il numero per gestire o '0' per uscire)");

            try {
                List<AppointmentBean> apps = ctrl.getAppuntamentiTutor();

                if (apps.isEmpty()) {
                    handleEmptyList();
                    return; // Esce dal loop
                }

                // FIX SONARQUBE: Estratto metodo per ridurre complessità
                printAppointmentList(apps);

                String input = readInput("Selezione");

                if (input.equals("0")) {
                    viewing = false;
                } else {
                    handleSelection(input, apps);
                }

            } catch (DBException e) {
                showError("Errore DB: " + e.getMessage());
                return;
            }
        }
    }

    /**
     * Gestisce il caso di lista vuota.
     */
    private void handleEmptyList() {
        LOGGER.info("Nessun appuntamento in storico.");
        LOGGER.info("Premi Invio per uscire...");
        scanner.nextLine();
    }

    /**
     * Stampa la lista degli appuntamenti.
     */
    private void printAppointmentList(List<AppointmentBean> apps) {
        for (int i = 0; i < apps.size(); i++) {
            AppointmentBean a = apps.get(i);
            String item = String.format("%d. %s - %s (Stud: %s) [%s]",
                    (i + 1), a.getData(), a.getOra(), a.getStudenteEmail(), a.getStato());
            LOGGER.info(item);
        }
    }

    /**
     * Gestisce la selezione numerica dell'utente.
     */
    private void handleSelection(String input, List<AppointmentBean> apps) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < apps.size()) {
                manageAppointment(apps.get(index));
            } else {
                showError("Numero non valido.");
            }
        } catch (NumberFormatException _) {
            showError("Inserisci un numero.");
        }
    }

    private void manageAppointment(AppointmentBean app) {
        String details = String.format(
                "%n--- Gestione Appuntamento ---%n" +
                        "Studente: %s%n" +
                        "Stato Attuale: %s",
                app.getStudenteEmail(), app.getStato()
        );
        LOGGER.info(details);

        // Se l'appuntamento non è 'confermato', non si può fare nulla
        if (!"confermato".equalsIgnoreCase(app.getStato())) {
            LOGGER.info("");
            LOGGER.info("(Nessuna azione disponibile per questo stato)");
            LOGGER.info("Premi Invio per tornare...");
            scanner.nextLine();
            return;
        }

        // Logica di gestione stato
        LOGGER.info("");
        LOGGER.info("Azioni:");
        LOGGER.info("C. [COMPLETA] Segna come completato");
        LOGGER.info("A. [ANNULLA]  Segna come annullato");
        LOGGER.info("0. Indietro");

        String choice = readInput("Scelta").toUpperCase();

        // Determina il nuovo stato
        String newStatus = getNewStatus(choice);

        if (newStatus != null) {
            ctrl.updateAppointmentStatus(
                    app.getStudenteEmail(),
                    app.getData(),
                    app.getOra(),
                    app.getStato(),
                    newStatus
            );
            LOGGER.info("Stato aggiornato con successo!");
            scanner.nextLine();
        }
    }

    /**
     * Helper per convertire l'input utente nel codice di stato DB.
     */
    private String getNewStatus(String choice) {
        switch (choice) {
            case "C":
                return "complete";
            case "A":
                return "cancel";
            case "0":
                return null; // Torna indietro
            default:
                showError("Scelta non valida.");
                return null;
        }
    }
}