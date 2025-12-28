package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.LoginCtrlApplicativo;
import ldg.progettoispw.engineering.exception.*;
import ldg.progettoispw.viewcli.studente.HomeStudentCLI;
import ldg.progettoispw.viewcli.tutor.HomeTutorCLI;

import java.util.logging.Logger;

public class LoginCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(LoginCLI.class.getName());
    private final LoginCtrlApplicativo loginController;

    public LoginCLI() {
        super();
        this.loginController = new LoginCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean executing = true;

        while (executing) {
            printHeader("Login Utente");
            LOGGER.info("(Scrivi '0' come email per tornare indietro)");

            String email = readInput("Inserisci Email");

            if (email.equals("0")) {
                return;
            }

            String password = readInput("Inserisci Password");

            try {
                int role = loginController.verificaCredenziali(email, password);

                LOGGER.info("Login effettuato con successo!");

                if (role == 1) {
                    LOGGER.info("Accesso come TUTOR...");
                    new HomeTutorCLI().start();
                    executing = false;
                } else {
                    LOGGER.info("Accesso come STUDENTE...");
                    new HomeStudentCLI().start();
                    executing = false;
                }

            } catch (InvalidEmailException | UserDoesNotExistException | IncorrectPasswordException e) {
                showError(e.getMessage());
                LOGGER.info("Premi invio per riprovare...");
                scanner.nextLine();

            } catch (DBException e) {
                showError("Errore critico del database: " + e.getMessage());
                return;
            }
        }
    }
}