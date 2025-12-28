package ldg.progettoispw.viewcli.studente;

import ldg.progettoispw.controller.BookAppointmentCtrlApplicativo;
import ldg.progettoispw.engineering.bean.SubjectBean;
import ldg.progettoispw.engineering.bean.TutorBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.viewcli.BaseCLI;
import ldg.progettoispw.viewcli.Printer; // Import necessario

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SearchTutorCLI extends BaseCLI {

    private final BookAppointmentCtrlApplicativo appCtrl;

    public SearchTutorCLI() {
        super();
        this.appCtrl = new BookAppointmentCtrlApplicativo();
    }

    @Override
    public void start() {
        boolean searching = true;

        while (searching) {
            printHeader("PRENOTA APPUNTAMENTO");
            Printer.println("(Scrivi '0' per tornare alla Dashboard)");

            String subjectInput = readInput("Inserisci Materia");

            // FIX SONARQUBE: Ristrutturato il ciclo per rimuovere 'continue'
            if (subjectInput.equals("0")) {
                searching = false;
            } else if (subjectInput.isEmpty()) {
                showError("Inserisci una materia valida.");
            } else {
                performSearch(subjectInput);
            }
        }
    }

    private void performSearch(String subjectInput) {
        try {
            SubjectBean subjectBean = new SubjectBean(subjectInput);
            List<TutorBean> tutors = appCtrl.searchTutorBySubject(subjectBean);

            if (tutors.isEmpty()) {
                showError("Nessun tutor trovato per: " + subjectInput);
            } else {
                handleTutorSelection(tutors);
            }

        } catch (DBException e) {
            showError("Errore DB: " + e.getMessage());
        }
    }

    private void handleTutorSelection(List<TutorBean> tutors) {
        // Limito a 10 risultati come nella GUI
        List<TutorBean> limitedTutors = tutors.stream().limit(10).toList();

        Printer.println("\n--- Risultati Ricerca ---");
        for (int i = 0; i < limitedTutors.size(); i++) {
            TutorBean t = limitedTutors.get(i);
            String item = String.format("%d. %s %s (Materie: %s)",
                    (i + 1), t.getNome(), t.getCognome(), String.join(", ", t.getMaterie()));
            Printer.println(item);
        }
        Printer.println("0. Torna alla ricerca");

        String choice = readInput("Seleziona il numero del Tutor");

        try {
            int index = Integer.parseInt(choice) - 1;

            if (choice.equals("0")) return;

            if (index >= 0 && index < limitedTutors.size()) {
                TutorBean selectedTutor = limitedTutors.get(index);
                processBooking(selectedTutor);
            } else {
                showError("Selezione non valida.");
            }
        } catch (NumberFormatException _) { // FIX SONARQUBE: Unnamed pattern
            showError("Inserisci un numero valido.");
        }
    }

    private void processBooking(TutorBean tutor) {
        printHeader("Prenotazione con: " + tutor.getNome() + " " + tutor.getCognome());

        // Input Data
        LocalDate date = null;
        while (date == null) {
            String dateStr = readInput("Inserisci Data (YYYY-MM-DD)");
            if (dateStr.equals("0")) return;
            try {
                date = LocalDate.parse(dateStr);
                if (date.isBefore(LocalDate.now())) {
                    showError("Non puoi prenotare nel passato.");
                    date = null;
                }
            } catch (DateTimeParseException _) { // FIX SONARQUBE: Unnamed pattern
                showError("Formato data errato. Usa YYYY-MM-DD (es. 2025-05-20)");
            }
        }

        // Input Ora
        int hour = -1;
        while (hour == -1) {
            String hourStr = readInput("Inserisci Ora (8-18)");
            try {
                int h = Integer.parseInt(hourStr);
                if (h >= 8 && h <= 18) {
                    hour = h;
                } else {
                    showError("L'orario deve essere tra le 08:00 e le 18:00.");
                }
            } catch (NumberFormatException _) { // FIX SONARQUBE: Unnamed pattern
                showError("Inserisci un numero intero.");
            }
        }

        // Conferma finale
        try {
            appCtrl.bookAppointment(tutor, date, hour);
            Printer.printlnBlu("\nAPPUNTAMENTO PRENOTATO CON SUCCESSO!");
            Printer.print("Premi Invio per tornare al menu...");
            scanner.nextLine();
        } catch (DBException e) {
            showError("Errore durante la prenotazione: " + e.getMessage());
        }
    }
}