package ldg.progettoispw.viewcli;

import java.util.logging.Logger;

public class FirstPageCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(FirstPageCLI.class.getName());

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader("BENVENUTO IN TUTOR ONLINE");

            LOGGER.info("1. Login");
            LOGGER.info("2. Registrazione");
            LOGGER.info("0. Esci");

            String input = readInput("Scegli un'opzione");

            switch (input) {
                case "1":
                    LoginCLI loginView = new LoginCLI();
                    loginView.start();
                    break;

                case "2":
                    LOGGER.info("Funzionalità REGISTRAZIONE in costruzione...");
                    new RegistrationCLI().start();
                    break;

                case "0":
                    LOGGER.info("Arrivederci!");
                    running = false;
                    break;

                default:
                    showError("Scelta non valida, riprova.");
            }
        }
    }
}