package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.controller.ManageReviewCtrlApplicativo;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.viewcli.BaseCLI;

import java.util.List;

public class ManageReviewsCLI extends BaseCLI {

    private final ManageReviewCtrlApplicativo ctrl;

    public ManageReviewsCLI() {
        super();
        this.ctrl = new ManageReviewCtrlApplicativo();
    }

    @Override
    public void start() {
        printHeader("LE TUE RECENSIONI");

        try {
            ManageReviewCtrlApplicativo.RecensioniResult result = ctrl.getRecensioniEValoriSentiment();
            List<RecensioneBean> reviews = result.getRecensioni();
            List<Integer> sentimentValues = result.getSentimentValues();

            // 1. Stampa il Grafico ASCII
            printAsciiChart(sentimentValues);

            // 2. Stampa la lista recensioni
            System.out.println("\n--- Dettaglio Commenti ---");
            if (reviews.isEmpty()) {
                System.out.println("Nessuna recensione ricevuta.");
            } else {
                for (RecensioneBean r : reviews) {
                    System.out.println("------------------------------------------------");
                    System.out.println("Da: " + r.getStudentEmail());
                    System.out.println("Valutazione: " + r.getSentimentValue() + "/5");
                    System.out.println("Commento: " + r.getRecensione());
                }
            }

            System.out.println("\nPremi Invio per tornare alla Dashboard...");
            scanner.nextLine();

        } catch (Exception e) {
            showError("Errore caricamento recensioni: " + e.getMessage());
        }
    }

    private void printAsciiChart(List<Integer> values) {
        System.out.println("\n[ STATISTICHE SENTIMENT ]");

        int[] counts = new int[5];
        for (int v : values) {
            if (v >= 1 && v <= 5) counts[v - 1]++;
        }

        for (int i = 0; i < 5; i++) {
            int stelle = i + 1;
            int count = counts[i];

            // Crea una barra di asterischi semplice
            String bar = "*".repeat(count);

            System.out.printf("%d Stelle (%d): %s%n", stelle, count, bar);
        }
    }
}