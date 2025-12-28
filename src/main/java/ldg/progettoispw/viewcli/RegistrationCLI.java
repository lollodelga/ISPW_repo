package ldg.progettoispw.viewcli;

import ldg.progettoispw.controller.RegistrationCtrlApplicativo;
import ldg.progettoispw.engineering.bean.UserBean;

import java.util.logging.Logger;

public class RegistrationCLI extends BaseCLI {

    private static final Logger LOGGER = Logger.getLogger(RegistrationCLI.class.getName());
    private final RegistrationCtrlApplicativo controller;

    public RegistrationCLI() {
        super();
        this.controller = new RegistrationCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean executing = true;

        while (executing) {
            printHeader("REGISTRAZIONE UTENTE");
            LOGGER.info("(Scrivi '0' come Nome per tornare al menu iniziale)");

            String nome = readInput("Nome");
            if (nome.equals("0")) return;

            String cognome = readInput("Cognome");
            String nascita = readInput("Data di Nascita (gg/mm/aaaa)");
            String email = readInput("Email");
            String password = readInput("Password");

            String roleCode = null;
            while (roleCode == null) {
                String choice = readInput("Seleziona Ruolo (1 = Tutor, 2 = Studente)");
                if (choice.equals("1")) {
                    roleCode = "1";
                } else if (choice.equals("2")) {
                    roleCode = "2";
                } else {
                    showError("Inserisci '1' per Tutor o '2' per Studente.");
                }
            }

            String promptMaterie = roleCode.equals("1")
                    ? "Materie trattate (separate da virgola)"
                    : "Materie di studio (separate da virgola)";

            String materie = readInput(promptMaterie);

            UserBean bean = new UserBean();
            bean.setName(nome);
            bean.setSurname(cognome);
            bean.setBirthDate(nascita);
            bean.setEmail(email);
            bean.setPassword(password);
            bean.setSubjects(materie);
            bean.setRole(roleCode);

            int result = controller.registerUser(bean);

            if (result == 0) {
                LOGGER.info("Registrazione completata con successo!");
                LOGGER.info("Ora puoi effettuare il login con le tue credenziali.");
                LOGGER.info("Premi Invio per tornare al menu principale...");
                scanner.nextLine();
                executing = false;
            } else {
                handleRegistrationError(result);
                LOGGER.info("Riprova a compilare il form...");
            }
        }
    }

    private void handleRegistrationError(int code) {
        switch (code) {
            case 1 -> showError("Email già in uso.");
            case 2 -> showError("Riempi tutti i campi obbligatori.");
            case 3 -> showError("Formato email non valido.");
            case 4 -> showError("La password non rispetta i requisiti (min 8 caratteri, ecc).");
            case 5 -> showError("Formato data non valido (usa gg/mm/aaaa).");
            default -> showError("ERRORE DI SISTEMA: Riprova più tardi.");
        }
    }
}