package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.controller.ManageReviewCtrlApplicativo;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.viewcli.BaseCLI;

import java.util.List;
import java.util.logging.Logger;

public class ManageReviewsCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(ManageReviewsCLI.class.getName());
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
            LOGGER.info("\n--- Dettaglio Commenti ---");

            if (reviews.isEmpty()) {
                LOGGER.info("Nessuna recensione ricevuta.");
            } else {
                for (RecensioneBean r : reviews) {
                    // Formattiamo il blocco recensione in un'unica stringa di log
                    String item = String.format(
                            "------------------------------------------------%n" +
                                    "Da: %s%n" +
                                    "Valutazione: %s/5%n" +
                                    "Commento: %s",
                            r.getStudentEmail(), r.getSentimentValue(), r.getRecensione()
                    );
                    LOGGER.info(item);
                }
            }

            LOGGER.info("\nPremi Invio per tornare alla Dashboard...");
            scanner.nextLine();

        } catch (Exception e) {
            showError("Errore caricamento recensioni: " + e.getMessage());
        }
    }

    private void printAsciiChart(List<Integer> values) {
        LOGGER.info("\n[ STATISTICHE SENTIMENT ]");

        int[] counts = new int[5];
        for (int v : values) {
            if (v >= 1 && v <= 5) counts[v - 1]++;
        }

        for (int i = 0; i < 5; i++) {
            int stelle = i + 1;
            int count = counts[i];

            // Crea una barra di asterischi semplice
            String bar = "*".repeat(count);

            // Sostituisco printf con String.format + Logger
            String row = String.format("%d Stelle (%d): %s", stelle, count, bar);
            LOGGER.info(row);
        }
    }
}