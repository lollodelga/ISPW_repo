package ldg.progettoispw.viewcli.tutor;

import ldg.progettoispw.viewcli.HomeCLI;

public class HomeTutorCLI extends HomeCLI {

    @Override
    protected String getFixedRole() {
        return "Tutor";
    }

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader("DASHBOARD TUTOR");
            printUserInfo();

            System.out.println("1. Recensioni ricevute (e Statistiche)");
            System.out.println("2. Gestione Richieste in attesa");
            System.out.println("3. Storico Appuntamenti (Concludi/Annulla)");
            System.out.println("0. Logout");

            String choice = readInput("Scegli un'opzione");

            switch (choice) {
                case "1":
                    new ManageReviewsCLI().start();
                    break;
                case "2":
                    new AppPendTutorCLI().start();
                    break;
                case "3":
                    new AppRispostiTutorCLI().start();
                    break;
                case "0":
                    logout();
                    running = false;
                    break;
                default:
                    showError("Opzione non valida.");
            }
        }
    }
}