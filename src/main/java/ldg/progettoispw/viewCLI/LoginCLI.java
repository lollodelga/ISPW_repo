package ldg.progettoispw.viewCLI;

import ldg.progettoispw.controller.LoginCtrlApplicativo;
import ldg.progettoispw.engineering.exception.*;

import java.util.Scanner;

public class LoginCLI {

    private final LoginCtrlApplicativo loginController;
    private final Scanner scanner;

    public LoginCLI() {
        this.loginController = new LoginCtrlApplicativo();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean executing = true;

        while (executing) {
            System.out.println("\n-----------------------------");
            System.out.println("           LOGIN             ");
            System.out.println("-----------------------------");
            System.out.println("(Premi '0' come email per tornare indietro)");

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            // Gestione pulsante "Indietro" (Back Action)
            if (email.equals("0")) {
                return; // Esce dal metodo start() e torna al menu precedente (FirstPageCLI)
            }

            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            try {
                // 1. Chiamata al Controller Applicativo (Stesso della GUI)
                int role = loginController.verificaCredenziali(email, password);

                System.out.println("✅ Login effettuato con successo!");

                // 2. Reindirizzamento in base al ruolo (Switch Scene)
                if (role == 1) {
                    System.out.println(">>> Accesso come TUTOR...");
                    // HomeTutorCLI homeTutor = new HomeTutorCLI();
                    // homeTutor.start();
                    executing = false; // Esce dal loop dopo il logout
                } else {
                    System.out.println(">>> Accesso come STUDENTE...");
                    // HomeStudentCLI homeStudent = new HomeStudentCLI();
                    // homeStudent.start();
                    executing = false; // Esce dal loop dopo il logout
                }

            } catch (InvalidEmailException | UserDoesNotExistException | IncorrectPasswordException e) {
                // 3. Gestione Errori (Show Warning)
                System.out.println("❌ ERRORE: " + e.getMessage());
                System.out.println("Riprova...");

            } catch (DBException e) {
                // Errore grave DB
                System.out.println("⛔ ERRORE DI SISTEMA: " + e.getMessage());
                return;
            }
        }
    }
}