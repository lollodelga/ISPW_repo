package ldg.progettoispw.viewCLI;

import java.util.Scanner;

public class FirstPageCLI {

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n-----------------------------------");
            System.out.println("     BENVENUTO IN TUTOR ONLINE     ");
            System.out.println("-----------------------------------");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("0. Esci");
            System.out.print("Scegli un'opzione: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    // Avvia la vista del Login (CLI)
                    LoginCLI loginView = new LoginCLI();
                    loginView.start();
                    running = false; // Esce dal loop della home se il login va a buon fine o cambia schermata
                    break;

                case "2":
                    System.out.println("\n>>> Funzionalità REGISTRAZIONE in costruzione...");
                    // Qui metterai: new RegisterCLI().start();
                    break;

                case "0":
                    System.out.println("Arrivederci!");
                    running = false;
                    break;

                default:
                    System.out.println("❌ Scelta non valida, riprova.");
            }
        }
    }
}