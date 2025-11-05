package ldg.progettoispw.view.studente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ldg.progettoispw.controller.BookAppointmentCtrlApplicativo;
import ldg.progettoispw.engineering.bean.SubjectBean;
import ldg.progettoispw.engineering.bean.TutorBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeGCon;

import java.time.LocalDate;
import java.util.List;

public class BookAppointmentCtrlGrafico extends HomeGCon {

    @FXML private TextField subjectField;
    @FXML private ScrollPane scrollResults;
    @FXML private Label errorLabel;
    @FXML private VBox resultsContainer;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourCombo;
    @FXML private Label statusLabel;
    @FXML private AnchorPane appointmentPane;

    @Override
    @FXML
    public void initialize() {
        for (int h = 8; h <= 18; h++) { // es. 8:00–18:00
            hourCombo.getItems().add(String.format("%02d:00", h));
        }
    }


    private final BookAppointmentCtrlApplicativo appCtrl = new BookAppointmentCtrlApplicativo();
    private TutorBean selectedTutor; // per sapere quale tutor l’utente ha selezionato

    @FXML
    public void searchTutor() {
        resultsContainer.getChildren().clear();
        errorLabel.setVisible(false);

        String subjectInput = subjectField.getText().trim();
        if (subjectInput.isEmpty()) {
            errorLabel.setText("Inserisci una materia.");
            errorLabel.setVisible(true);
            return;
        }

        // ✅ Crea il bean per la materia
        SubjectBean subjectBean = new SubjectBean(subjectInput);

        try {
            // ✅ Chiamata al controller applicativo
            List<TutorBean> tutors = appCtrl.searchTutorBySubject(subjectBean);

            if (tutors.isEmpty()) {
                errorLabel.setText("Nessun tutor trovato per la materia indicata.");
                errorLabel.setVisible(true);
                return;
            }

            // ✅ Mostra al massimo 10 risultati
            List<TutorBean> limitedTutors = tutors.stream().limit(10).toList();

            for (TutorBean tutor : limitedTutors) {
                String materie = String.join(", ", tutor.getMaterie());
                Button tutorButton = new Button(
                        tutor.getNome() + " " + tutor.getCognome() + " — " + materie
                );
                tutorButton.setStyle(
                        "-fx-font-size: 15px; -fx-background-color: #f0f0f0; " +
                                "-fx-padding: 6; -fx-background-radius: 10;"
                );
                tutorButton.setOnAction(e -> selectTutor(tutor));
                resultsContainer.getChildren().add(tutorButton);
            }

            // ✅ Rende visibile la scrollPane solo dopo la ricerca
            scrollResults.setVisible(true);

        } catch (DBException _) {
            errorLabel.setText("Errore durante la ricerca nel database.");
            errorLabel.setVisible(true);
        }
    }

    // quando l'utente clicca su un tutor
    private void selectTutor(TutorBean tutor) {
        this.selectedTutor = tutor;

        errorLabel.setText("Tutor selezionato: " + tutor.getNome() + " " + tutor.getCognome());
        errorLabel.setVisible(true);

        // Mostra il pannello prenotazione
        appointmentPane.setVisible(true);

        // Reset dei campi
        statusLabel.setVisible(false);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void cancelSelection(ActionEvent event) {
        // Nascondi la finestra di prenotazione
        appointmentPane.setVisible(false);

        // Rimuovi la selezione corrente
        selectedTutor = null;

        // Mostra un messaggio informativo
        errorLabel.setText("Nessun tutor selezionato. Seleziona un altro tutor per continuare.");
        errorLabel.setVisible(true);

        // Ripristina eventuali campi
        statusLabel.setVisible(false);
        datePicker.setValue(null);
        hourCombo.getSelectionModel().clearSelection();
    }

    @FXML
    public void bookAppointment(ActionEvent event) {
        statusLabel.setVisible(false);

        // 🔹 Controllo tutor selezionato
        if (selectedTutor == null) {
            statusLabel.setText("Seleziona prima un tutor.");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
            return;
        }

        // 🔹 Recupero data e ora
        LocalDate selectedDate = datePicker.getValue();
        String hourText = hourCombo.getValue();

        if (selectedDate == null || hourText == null || hourText.isEmpty()) {
            statusLabel.setText("Inserisci data e ora (es. 09:00).");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
            return;
        }

        // 🔹 Parsing dell’ora (formato garantito dalla ComboBox: HH:00)
        int hour;
        try {
            hour = Integer.parseInt(hourText.split(":")[0]);
        } catch (NumberFormatException _) {
            statusLabel.setText("Formato ora non valido. Usa HH:00 (es. 09:00).");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
            return;
        }

        // 🔹 Prenotazione effettiva
        try {
            appCtrl.bookAppointment(selectedTutor, selectedDate, hour);
            statusLabel.setText("Appuntamento prenotato con successo!");
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setVisible(true);
            appointmentPane.setVisible(false); // chiudi il pannello dopo la prenotazione
        } catch (DBException e) {
            statusLabel.setText("Errore: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
        }
    }

    @FXML
    public void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
    }
}
