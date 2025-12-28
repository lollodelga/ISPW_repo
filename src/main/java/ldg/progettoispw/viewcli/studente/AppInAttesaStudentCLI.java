package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.controller.AppInAttesaStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;
import ldg.progettoispw.viewcli.Printer; // Import necessario

import java.util.List;

public class AppInAttesaStudentCLI extends BaseCLI {

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
                Printer.println("Nessuna richiesta in attesa.");
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
                    Printer.println(details);
                }
                Printer.println("--------------------------------");
            }

            Printer.print("\nPremi Invio per tornare alla Dashboard...");
            scanner.nextLine();

        } catch (DBException e) {
            showError("Errore nel recupero dati: " + e.getMessage());
        }
    }
}