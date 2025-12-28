package ldg.progettoispw.viewCLI;

import ldg.progettoispw.controller.LoginCtrlApplicativo;
import ldg.progettoispw.engineering.exception.*;
import ldg.progettoispw.viewCLI.studente.HomeStudentCLI;
import ldg.progettoispw.viewCLI.tutor.HomeTutorCLI;

public class LoginCLI extends BaseCLI {

    private final LoginCtrlApplicativo loginController;

    public LoginCLI() {
        super(); // Chiama il costruttore di BaseCLI (inizializza scanner)
        this.loginController = new LoginCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean executing = true;

        while (executing) {
            // Usa il metodo helper del padre per il titolo
            printHeader("Login Utente");
            System.out.println("(Scrivi '0' come email per tornare indietro)");

            // Usa il metodo helper per leggere
            String email = readInput("Inserisci Email");

            if (email.equals("0")) {
                return; // Torna alla FirstPage
            }

            String password = readInput("Inserisci Password");

            try {
                int role = loginController.verificaCredenziali(email, password);

                System.out.println("\nLogin effettuato con successo!");

                if (role == 1) {
                    System.out.println(">>> Accesso come TUTOR...");
                    // Avvia la Dashboard Tutor
                    new HomeTutorCLI().start();

                    // Quando l'utente fa Logout dalla Dashboard, il codice riprende qui.
                    // Mettendo executing = false, usciamo dal ciclo di Login e torniamo alla FirstPage.
                    executing = false;
                } else {
                    System.out.println(">>> Accesso come STUDENTE...");
                    // Avvia la Dashboard Studente
                    new HomeStudentCLI().start();

                    executing = false;
                }

            } catch (InvalidEmailException | UserDoesNotExistException | IncorrectPasswordException e) {
                // Gestione errori credenziali
                showError(e.getMessage());
                System.out.println("Premi invio per riprovare...");
                scanner.nextLine(); // Pausa scenica

            } catch (DBException e) {
                // Gestione errori tecnici
                showError("Errore critico del database: " + e.getMessage());
                return;
            }
        }
    }
}