package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.LoginCtrlApplicativo;
import ldg.progettoispw.engineering.exception.*;
import ldg.progettoispw.viewcli.studente.HomeStudentCLI;
import ldg.progettoispw.viewcli.tutor.HomeTutorCLI;

public class LoginCLI extends BaseCLI {

    private final LoginCtrlApplicativo loginController;

    public LoginCLI() {
        super();
        this.loginController = new LoginCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean executing = true;

        while (executing) {
            printHeader("LOGIN UTENTE");
            Printer.println("(Scrivi '0' come email per tornare indietro)");

            String email = readInput("Inserisci Email");

            if (email.equals("0")) {
                return;
            }

            String password = readInput("Inserisci Password");

            try {
                int role = loginController.verificaCredenziali(email, password);

                // Feedback visivo positivo
                Printer.printlnBlu("\nLogin effettuato con successo!");

                if (role == 1) {
                    Printer.println("Accesso come TUTOR...");
                    new HomeTutorCLI().start();
                    executing = false;
                } else {
                    Printer.println("Accesso come STUDENTE...");
                    new HomeStudentCLI().start();
                    executing = false;
                }

            } catch (InvalidEmailException | UserDoesNotExistException | IncorrectPasswordException e) {
                showError(e.getMessage());
                Printer.print("Premi invio per riprovare...");
                scanner.nextLine();

            } catch (DBException e) {
                showError("Errore critico del database: " + e.getMessage());
                return;
            }
        }
    }
}