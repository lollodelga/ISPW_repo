package ldg.progettoispw.viewCLI.tutor;

import ldg.progettoispw.controller.AppRispostiTutorCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewCLI.BaseCLI;

import java.util.List;

public class AppRispostiTutorCLI extends BaseCLI {

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
            System.out.println("(Scegli il numero per gestire o '0' per uscire)");

            try {
                List<AppointmentBean> apps = ctrl.getAppuntamentiTutor();

                if (apps.isEmpty()) {
                    System.out.println("Nessun appuntamento in storico.");
                    System.out.println("Premi Invio per uscire...");
                    scanner.nextLine();
                    return;
                }

                for (int i = 0; i < apps.size(); i++) {
                    AppointmentBean a = apps.get(i);
                    System.out.printf("%d. %s - %s (Stud: %s) [%s]%n",
                            (i + 1), a.getData(), a.getOra(), a.getStudenteEmail(), a.getStato());
                }

                String input = readInput("Selezione");

                if (input.equals("0")) {
                    viewing = false;
                } else {
                    try {
                        int index = Integer.parseInt(input) - 1;
                        if (index >= 0 && index < apps.size()) {
                            manageAppointment(apps.get(index));
                        } else {
                            showError("Numero non valido.");
                        }
                    } catch (NumberFormatException e) {
                        showError("Inserisci un numero.");
                    }
                }

            } catch (DBException e) {
                showError("Errore DB: " + e.getMessage());
                return;
            }
        }
    }

    private void manageAppointment(AppointmentBean app) {
        System.out.println("\n--- Gestione Appuntamento ---");
        System.out.println("Studente: " + app.getStudenteEmail());
        System.out.println("Stato Attuale: " + app.getStato());

        // Se è 'confermato', posso cambiarlo in completato o annullato
        if ("confermato".equalsIgnoreCase(app.getStato())) {
            System.out.println("\nAzioni:");
            System.out.println("C. [COMPLETA] Segna come completato");
            System.out.println("A. [ANNULLA]  Segna come annullato");
            System.out.println("0. Indietro");

            String choice = readInput("Scelta").toUpperCase();

            String newStatus = null;
            if (choice.equals("C")) newStatus = "complete";
            else if (choice.equals("A")) newStatus = "cancel";
            else if (choice.equals("0")) return;
            else {
                showError("Scelta non valida.");
                return;
            }

            ctrl.updateAppointmentStatus(
                    app.getStudenteEmail(),
                    app.getData(),
                    app.getOra(),
                    app.getStato(),
                    newStatus
            );
            System.out.println(">> Stato aggiornato con successo!");
            scanner.nextLine();
        } else {
            System.out.println("\n(Nessuna azione disponibile per questo stato)");
            System.out.println("Premi Invio per tornare...");
            scanner.nextLine();
        }
    }
}