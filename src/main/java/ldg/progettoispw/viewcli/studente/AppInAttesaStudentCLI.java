package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.controller.AppInAttesaStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;

import java.util.List;
import java.util.logging.Logger;

public class AppInAttesaStudentCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(AppInAttesaStudentCLI.class.getName());
    private final AppInAttesaStudenteCtrlApplicativo ctrl;

    public AppInAttesaStudentCLI() {
        super();
        this.ctrl = new AppInAttesaStudenteCtrlApplicativo();
    }

    @Override
    public void start() {
        printHeader("RICHIESTE IN ATTESA");

        try {
            List<AppointmentBean> pending = ctrl.getAppuntamentiInAttesa();

            if (pending.isEmpty()) {
                LOGGER.info("Nessuna richiesta in attesa.");
            } else {
                for (AppointmentBean app : pending) {
                    // Formattiamo l'output in un'unica stringa per pulizia
                    String details = String.format(
                            "--------------------------------%n" +
                                    "Tutor: %s%n" +
                                    "Data:  %s%n" +
                                    "Ora:   %s%n" +
                                    "Stato: %s",
                            app.getTutorEmail(), app.getData(), app.getOra(), app.getStato()
                    );
                    LOGGER.info(details);
                }
                LOGGER.info("--------------------------------");
            }

            LOGGER.info("Premi Invio per tornare alla Dashboard...");
            scanner.nextLine();

        } catch (DBException e) {
            showError("Errore nel recupero dati: " + e.getMessage());
        }
    }
}