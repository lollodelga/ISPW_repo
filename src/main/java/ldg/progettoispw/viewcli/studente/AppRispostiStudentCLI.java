package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.controller.AppRispostiStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;

import java.util.List;
import java.util.logging.Logger;

public class AppRispostiStudentCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(AppRispostiStudentCLI.class.getName());
    private final AppRispostiStudenteCtrlApplicativo ctrl;

    public AppRispostiStudentCLI() {
        super();
        this.ctrl = new AppRispostiStudenteCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean viewing = true;

        while (viewing) {
            printHeader("STORICO APPUNTAMENTI");
            LOGGER.info("(Scegli il numero per i dettagli o '0' per uscire)");

            try {
                List<AppointmentBean> apps = ctrl.getAppuntamentiStudente();

                if (apps.isEmpty()) {
                    LOGGER.info("Nessun appuntamento nello storico.");
                    LOGGER.info("Premi Invio per uscire...");
                    scanner.nextLine();
                    return;
                }

                // Stampa lista numerata
                for (int i = 0; i < apps.size(); i++) {
                    AppointmentBean a = apps.get(i);
                    String item = String.format("%d. %s - %s (Con: %s) [%s]",
                            (i + 1), a.getData(), a.getOra(), a.getTutorEmail(), a.getStato());
                    LOGGER.info(item);
                }

                String input = readInput("Selezione");
                if (input.equals("0")) {
                    viewing = false;
                } else {
                    // FIX SONARQUBE: Estratto il blocco try innestato in un metodo dedicato
                    processSelection(input, apps);
                }

            } catch (DBException e) {
                showError("Errore caricamento storico: " + e.getMessage());
                return;
            }
        }
    }

    /**
     * Gestisce la logica di selezione dell'appuntamento per evitare try-catch annidati.
     */
    private void processSelection(String input, List<AppointmentBean> apps) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < apps.size()) {
                showDetailsAndReview(apps.get(index));
            } else {
                showError("Numero non valido.");
            }
        } catch (NumberFormatException _) {
            showError("Inserisci un numero.");
        }
    }

    private void showDetailsAndReview(AppointmentBean app) {
        String details = String.format(
                "%n--- Dettagli Appuntamento ---%n" +
                        "Tutor: %s%n" +
                        "Data:  %s%n" +
                        "Ora:   %s%n" +
                        "Stato: %s",
                app.getTutorEmail(), app.getData(), app.getOra(), app.getStato()
        );
        LOGGER.info(details);

        // Se è completato, offro opzione recensione
        if ("completato".equalsIgnoreCase(app.getStato())) {
            String choice = readInput("Vuoi scrivere una recensione? (s/n)");
            if (choice.equalsIgnoreCase("s")) {
                writeReview(app);
            }
        } else {
            LOGGER.info("(Premi Invio per tornare alla lista)");
            scanner.nextLine();
        }
    }

    private void writeReview(AppointmentBean app) {
        LOGGER.info("Scrivi la tua recensione (Invio per confermare):");
        String testo = scanner.nextLine().trim();

        if (testo.isEmpty()) {
            showError("La recensione non può essere vuota.");
            return;
        }

        try {
            RecensioneBean recBean = new RecensioneBean();
            recBean.setTutorEmail(app.getTutorEmail());
            recBean.setStudentEmail(app.getStudenteEmail());
            recBean.setRecensione(testo);

            String result = ctrl.inviaRecensione(recBean);

            if (result.startsWith("Errore")) {
                showError(result);
            } else {
                LOGGER.info(result); // Successo
                LOGGER.info("Premi Invio per continuare...");
                scanner.nextLine();
            }

        } catch (Exception e) {
            showError("Errore nell'invio: " + e.getMessage());
        }
    }
}