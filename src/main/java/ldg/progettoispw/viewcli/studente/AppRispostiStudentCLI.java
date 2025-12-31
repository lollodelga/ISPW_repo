package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.controller.AppRispostiStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;
import ldg.progettoispw.viewcli.Printer;

import java.util.List;

public class AppRispostiStudentCLI extends BaseCLI {

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
            Printer.println("(Scegli il numero per gestire l'appuntamento o '0' per uscire)");

            try {
                List<AppointmentBean> apps = ctrl.getAppuntamentiStudente();

                if (apps.isEmpty()) {
                    Printer.println("Nessun appuntamento nello storico.");
                    Printer.print("Premi Invio per uscire...");
                    scanner.nextLine();
                    return;
                }

                // Stampa lista numerata
                for (int i = 0; i < apps.size(); i++) {
                    AppointmentBean a = apps.get(i);
                    String item = String.format("%d. %s - %s (Con: %s) [%s]",
                            (i + 1), a.getData(), a.getOra(), a.getTutorEmail(), a.getStato().toUpperCase());
                    Printer.println(item);
                }

                String input = readInput("Selezione");
                if (input.equals("0")) {
                    viewing = false;
                } else {
                    processSelection(input, apps);
                }

            } catch (DBException e) {
                showError("Errore caricamento storico: " + e.getMessage());
                return; // Esce in caso di errore grave DB
            }
        }
    }

    /**
     * Gestisce la logica di selezione dell'appuntamento.
     */
    private void processSelection(String input, List<AppointmentBean> apps) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < apps.size()) {
                handleAppointmentActions(apps.get(index));
            } else {
                showError("Numero non valido.");
            }
        } catch (NumberFormatException _) {
            showError("Inserisci un numero valido.");
        }
    }

    /**
     * Mostra i dettagli e decide quali azioni (Paga / Recensisci) sono disponibili
     * in base allo stato attuale.
     */
    private void handleAppointmentActions(AppointmentBean app) {
        printDetails(app);

        String stato = app.getStato().toLowerCase();

        // CASO 1: L'appuntamento è completato -> Bisogna pagare
        if ("completato".equals(stato)) {
            Printer.println("Questo appuntamento deve essere ancora pagato.");
            String choice = readInput("Vuoi procedere al pagamento di 15.00€? (s/n)");
            if (choice.equalsIgnoreCase("s")) {
                performPayment(app);
            }
        }
        // CASO 2: L'appuntamento è già pagato -> Si può recensire
        else if ("pagato".equals(stato)) {
            String choice = readInput("Vuoi scrivere una recensione per questo tutor? (s/n)");
            if (choice.equalsIgnoreCase("s")) {
                writeReview(app);
            }
        }
        // CASO 3: Altri stati (es. annullato, confermato) -> Nessuna azione nello storico
        else {
            Printer.print("(Premi Invio per tornare alla lista)");
            scanner.nextLine();
        }
    }

    private void printDetails(AppointmentBean app) {
        String details = String.format(
                "%n--- Dettagli Appuntamento ---%n" +
                        "Tutor: %s%n" +
                        "Data:  %s%n" +
                        "Ora:   %s%n" +
                        "Stato: %s",
                app.getTutorEmail(), app.getData(), app.getOra(), app.getStato().toUpperCase()
        );
        Printer.println(details);
    }

    /**
     * Esegue il pagamento chiamando il controller applicativo.
     */
    private void performPayment(AppointmentBean app) {
        try {
            Printer.print("Elaborazione pagamento in corso...");

            // Chiamata al Controller Applicativo (Logica State Pattern)
            ctrl.pagaAppuntamento(app);

            // Aggiorno il bean locale per riflettere il cambiamento immediato
            app.setStato("pagato");

            Printer.printlnBlu("\nPagamento effettuato con successo!");

            // Workflow: dopo il pagamento, offri subito la recensione
            String choice = readInput("Vuoi scrivere subito una recensione? (s/n)");
            if (choice.equalsIgnoreCase("s")) {
                writeReview(app);
            }

        } catch (DBException e) {
            showError("\nErrore durante il pagamento: " + e.getMessage());
        }
    }

    /**
     * Gestisce l'inserimento e l'invio della recensione.
     */
    private void writeReview(AppointmentBean app) {
        Printer.println("\n--- Nuova Recensione ---");
        Printer.println("Scrivi il testo della tua recensione (Invio per confermare):");
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

            // Chiamata al Controller
            String result = ctrl.inviaRecensione(recBean);

            if (result.startsWith("Errore")) {
                showError(result);
            } else {
                Printer.printlnBlu(result); // Messaggio di successo in blu
                Printer.print("Premi Invio per tornare alla lista...");
                scanner.nextLine();
            }

        } catch (Exception e) {
            showError("Errore nell'invio: " + e.getMessage());
        }
    }
}