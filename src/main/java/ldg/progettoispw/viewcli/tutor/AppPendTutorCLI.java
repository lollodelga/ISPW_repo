package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.controller.ManageAppointmentCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;

import java.util.List;
import java.util.logging.Logger;

public class AppPendTutorCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(AppPendTutorCLI.class.getName());
    private final ManageAppointmentCtrlApplicativo ctrl;

    public AppPendTutorCLI() {
        super();
        this.ctrl = new ManageAppointmentCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean managing = true;

        while (managing) {
            printHeader("RICHIESTE IN ATTESA");
            LOGGER.info("(Scegli il numero per gestire la richiesta o '0' per uscire)");

            try {
                List<AppointmentBean> pending = ctrl.getAppuntamentiInAttesa();

                if (pending.isEmpty()) {
                    handleEmptyList();
                    return; // Esce dal metodo start, tornando al menu precedente
                }

                // FIX SONARQUBE: Estratta logica di stampa per ridurre complessità
                printAppointmentList(pending);

                String input = readInput("Selezione");

                if (input.equals("0")) {
                    managing = false;
                } else {
                    handleSelection(input, pending);
                }

            } catch (DBException e) {
                showError("Errore DB: " + e.getMessage());
                managing = false;
            }
        }
    }

    /**
     * Gestisce il caso di lista vuota.
     */
    private void handleEmptyList() {
        LOGGER.info("Nessuna richiesta in attesa.");
        LOGGER.info("Premi Invio per tornare alla Dashboard...");
        scanner.nextLine();
    }

    /**
     * Stampa la lista numerata degli appuntamenti.
     */
    private void printAppointmentList(List<AppointmentBean> pending) {
        for (int i = 0; i < pending.size(); i++) {
            AppointmentBean a = pending.get(i);
            String item = String.format("%d. Studente: %s | Data: %s %s",
                    (i + 1), a.getStudenteEmail(), a.getData(), a.getOra());
            LOGGER.info(item);
        }
    }

    /**
     * Gestisce la selezione numerica dell'utente.
     */
    private void handleSelection(String input, List<AppointmentBean> pending) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < pending.size()) {
                processRequest(pending.get(index));
            } else {
                showError("Numero non valido.");
            }
        } catch (NumberFormatException _) {
            showError("Inserisci un numero.");
        }
    }

    private void processRequest(AppointmentBean app) {
        String details = String.format(
                "%n--- Dettagli Richiesta ---%n" +
                        "Studente: %s%n" +
                        "Data:     %s%n" +
                        "Ora:      %s",
                app.getStudenteEmail(), app.getData(), app.getOra()
        );
        LOGGER.info(details);

        LOGGER.info("");
        LOGGER.info("Azioni disponibili:");
        LOGGER.info("1. [ACCETTA] Conferma appuntamento");
        LOGGER.info("2. [RIFIUTA] Rifiuta appuntamento");
        LOGGER.info("0. Torna indietro");

        String action = readInput("Scelta");

        try {
            switch (action) {
                case "1":
                    ctrl.confermaAppuntamento(app);
                    LOGGER.info("Appuntamento CONFERMATO con successo.");
                    scanner.nextLine();
                    break;
                case "2":
                    ctrl.rifiutaAppuntamento(app);
                    LOGGER.info("Appuntamento RIFIUTATO.");
                    scanner.nextLine();
                    break;
                case "0":
                    break;
                default:
                    showError("Scelta non valida.");
            }
        } catch (DBException e) {
            showError("Errore operazione: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Errore logico: " + e.getMessage());
        }
    }
}