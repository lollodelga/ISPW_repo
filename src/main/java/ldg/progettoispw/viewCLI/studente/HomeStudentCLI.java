package ldg.progettoispw.viewCLI.studente;

import ldg.progettoispw.viewCLI.HomeCLI;

public class HomeStudentCLI extends HomeCLI {

    @Override
    protected String getFixedRole() {
        return "Studente";
    }

    @Override
    public void start() {
        boolean running = true;

        while (running) {
            printHeader("DASHBOARD STUDENTE");
            printUserInfo();

            System.out.println("1. Cerca Tutor e Prenota");
            System.out.println("2. Richieste in sospeso");
            System.out.println("3. Richieste completate (e Recensioni)");
            System.out.println("0. Logout");

            String choice = readInput("Scegli un'opzione");

            switch (choice) {
                case "1":
                    // ✅ Avvia la ricerca tutor
                    new SearchTutorCLI().start();
                    break;
                case "2":
                    // ✅ Avvia la visualizzazione richieste in attesa
                    new AppInAttesaStudentCLI().start();
                    break;
                case "3":
                    // ✅ Avvia storico e recensioni
                    new AppRispostiStudentCLI().start();
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