package ldg.progettoispw.viewCLI.tutor;

import ldg.progettoispw.controller.ManageAppointmentCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewCLI.BaseCLI;

import java.util.List;

public class AppPendTutorCLI extends BaseCLI {

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
            System.out.println("(Scegli il numero per gestire la richiesta o '0' per uscire)");

            try {
                List<AppointmentBean> pending = ctrl.getAppuntamentiInAttesa();

                if (pending.isEmpty()) {
                    System.out.println("Nessuna richiesta in attesa.");
                    System.out.println("Premi Invio per tornare alla Dashboard...");
                    scanner.nextLine();
                    return;
                }

                // Lista numerata
                for (int i = 0; i < pending.size(); i++) {
                    AppointmentBean a = pending.get(i);
                    System.out.printf("%d. Studente: %s | Data: %s %s%n",
                            (i + 1), a.getStudenteEmail(), a.getData(), a.getOra());
                }

                String input = readInput("Selezione");

                if (input.equals("0")) {
                    managing = false;
                } else {
                    try {
                        int index = Integer.parseInt(input) - 1;
                        if (index >= 0 && index < pending.size()) {
                            processRequest(pending.get(index));
                        } else {
                            showError("Numero non valido.");
                        }
                    } catch (NumberFormatException e) {
                        showError("Inserisci un numero.");
                    }
                }

            } catch (DBException e) {
                showError("Errore DB: " + e.getMessage());
                managing = false;
            }
        }
    }

    private void processRequest(AppointmentBean app) {
        System.out.println("\n--- Dettagli Richiesta ---");
        System.out.println("Studente: " + app.getStudenteEmail());
        System.out.println("Data:     " + app.getData());
        System.out.println("Ora:      " + app.getOra());

        System.out.println("\nAzioni disponibili:");
        System.out.println("1. [ACCETTA] Conferma appuntamento");
        System.out.println("2. [RIFIUTA] Rifiuta appuntamento");
        System.out.println("0. Torna indietro");

        String action = readInput("Scelta");

        try {
            switch (action) {
                case "1":
                    ctrl.confermaAppuntamento(app);
                    System.out.println(">> Appuntamento CONFERMATO con successo.");
                    scanner.nextLine();
                    break;
                case "2":
                    ctrl.rifiutaAppuntamento(app);
                    System.out.println(">> Appuntamento RIFIUTATO.");
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