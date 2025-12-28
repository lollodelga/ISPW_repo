package ldg.progettoispw.viewcli;

public class FirstPageCLI extends BaseCLI {

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader("BENVENUTO IN TUTOR ONLINE");

            Printer.println("1. Login");
            Printer.println("2. Registrazione");
            Printer.println("0. Esci");

            String input = readInput("Scegli un'opzione");

            switch (input) {
                case "1":
                    LoginCLI loginView = new LoginCLI();
                    loginView.start();
                    break;

                case "2":
                    Printer.println("\n>>> Funzionalità REGISTRAZIONE in costruzione...");
                    new RegistrationCLI().start();
                    break;

                case "0":
                    Printer.println("Arrivederci!");
                    running = false;
                    break;

                default:
                    showError("Scelta non valida, riprova.");
            }
        }
    }
}