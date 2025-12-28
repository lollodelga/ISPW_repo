package ldg.progettoispw.viewCLI.studente;

import ldg.progettoispw.controller.AppRispostiStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewCLI.BaseCLI;

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
            System.out.println("(Scegli il numero per i dettagli o '0' per uscire)");

            try {
                List<AppointmentBean> apps = ctrl.getAppuntamentiStudente();

                if (apps.isEmpty()) {
                    System.out.println("Nessun appuntamento nello storico.");
                    System.out.println("Premi Invio per uscire...");
                    scanner.nextLine();
                    return;
                }

                // Stampa lista numerata
                for (int i = 0; i < apps.size(); i++) {
                    AppointmentBean a = apps.get(i);
                    System.out.printf("%d. %s - %s (Con: %s) [%s]%n",
                            (i + 1), a.getData(), a.getOra(), a.getTutorEmail(), a.getStato());
                }

                String input = readInput("Selezione");
                if (input.equals("0")) {
                    viewing = false;
                } else {
                    try {
                        int index = Integer.parseInt(input) - 1;
                        if (index >= 0 && index < apps.size()) {
                            showDetailsAndReview(apps.get(index));
                        } else {
                            showError("Numero non valido.");
                        }
                    } catch (NumberFormatException e) {
                        showError("Inserisci un numero.");
                    }
                }

            } catch (DBException e) {
                showError("Errore caricamento storico: " + e.getMessage());
                return;
            }
        }
    }

    private void showDetailsAndReview(AppointmentBean app) {
        System.out.println("\n--- Dettagli Appuntamento ---");
        System.out.println("Tutor: " + app.getTutorEmail());
        System.out.println("Data:  " + app.getData());
        System.out.println("Ora:   " + app.getOra());
        System.out.println("Stato: " + app.getStato());

        // Se è completato, offro opzione recensione
        if ("completato".equalsIgnoreCase(app.getStato())) {
            String choice = readInput("\nVuoi scrivere una recensione? (s/n)");
            if (choice.equalsIgnoreCase("s")) {
                writeReview(app);
            }
        } else {
            System.out.println("\n(Premi Invio per tornare alla lista)");
            scanner.nextLine();
        }
    }

    private void writeReview(AppointmentBean app) {
        System.out.println("Scrivi la tua recensione (Invio per confermare):");
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
                System.out.println("\n" + result); // Successo
                System.out.println("Premi Invio per continuare...");
                scanner.nextLine();
            }

        } catch (Exception e) {
            showError("Errore nell'invio: " + e.getMessage());
        }
    }
}