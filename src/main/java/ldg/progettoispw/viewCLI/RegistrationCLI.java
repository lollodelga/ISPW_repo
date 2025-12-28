package ldg.progettoispw.viewCLI;

import ldg.progettoispw.controller.RegistrationCtrlApplicativo;
import ldg.progettoispw.engineering.bean.UserBean;

public class RegistrationCLI extends BaseCLI {

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
            System.out.println("(Scrivi '0' come Nome per tornare al menu iniziale)");

            // 1. Raccolta Dati Base
            String nome = readInput("Nome");
            if (nome.equals("0")) return; // Back action

            String cognome = readInput("Cognome");
            String nascita = readInput("Data di Nascita (gg/mm/aaaa)");
            String email = readInput("Email");
            String password = readInput("Password");

            // 2. Selezione Ruolo (Ciclo finché non è valido)
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

            // 3. Materie (Prompt dinamico in base al ruolo, come nella GUI)
            String promptMaterie = roleCode.equals("1")
                    ? "Materie trattate (separate da virgola)"
                    : "Materie di studio (separate da virgola)";

            String materie = readInput(promptMaterie);

            // 4. Creazione Bean e Chiamata al Controller
            UserBean bean = new UserBean();
            bean.setName(nome);
            bean.setSurname(cognome);
            bean.setBirthDate(nascita);
            bean.setEmail(email);
            bean.setPassword(password);
            bean.setSubjects(materie);
            bean.setRole(roleCode);

            int result = controller.registerUser(bean);

            // 5. Gestione Risultato
            if (result == 0) {
                System.out.println("\nRegistrazione completata con successo!");
                System.out.println("Ora puoi effettuare il login con le tue credenziali.");

                // Premi invio per continuare
                System.out.println("Premi Invio per tornare al menu principale...");
                scanner.nextLine();
                executing = false; // Esce dal loop e torna alla FirstPageCLI
            } else {
                handleRegistrationError(result);
                System.out.println("Riprova a compilare il form...\n");
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