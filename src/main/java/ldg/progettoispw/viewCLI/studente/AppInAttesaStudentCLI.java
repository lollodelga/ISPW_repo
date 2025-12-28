package ldg.progettoispw.viewCLI.studente;

import ldg.progettoispw.controller.AppInAttesaStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewCLI.BaseCLI;

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
                System.out.println("Nessuna richiesta in attesa.");
            } else {
                for (AppointmentBean app : pending) {
                    System.out.println("--------------------------------");
                    System.out.println("Tutor: " + app.getTutorEmail());
                    System.out.println("Data:  " + app.getData());
                    System.out.println("Ora:   " + app.getOra());
                    System.out.println("Stato: " + app.getStato());
                }
                System.out.println("--------------------------------");
            }

            System.out.println("\nPremi Invio per tornare alla Dashboard...");
            scanner.nextLine();

        } catch (DBException e) {
            showError("Errore nel recupero dati: " + e.getMessage());
        }
    }
}