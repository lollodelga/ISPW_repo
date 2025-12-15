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
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookAppointmentCtrlGrafico extends HomeCtrlGrafico {

    @FXML private TextField subjectField;
    @FXML private ScrollPane scrollResults;
    @FXML private Label errorLabel;
    @FXML private VBox resultsContainer;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> hourCombo;
    @FXML private Label statusLabel;
    @FXML private AnchorPane appointmentPane;

    private static final Logger LOGGER = Logger.getLogger(BookAppointmentCtrlGrafico.class.getName());

    private final BookAppointmentCtrlApplicativo appCtrl = new BookAppointmentCtrlApplicativo();
    private TutorBean selectedTutor;

    @Override
    @FXML
    public void initialize() {
        // 1. FONDAMENTALE: Carica i dati utente dalla classe padre
        super.initialize();

        // Inizializza la combo delle ore
        for (int h = 8; h <= 18; h++) {
            hourCombo.getItems().add(String.format("%02d:00", h));
        }
    }

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

        SubjectBean subjectBean = new SubjectBean(subjectInput);

        try {
            List<TutorBean> tutors = appCtrl.searchTutorBySubject(subjectBean);

            if (tutors.isEmpty()) {
                errorLabel.setText("Nessun tutor trovato per la materia indicata.");
                errorLabel.setVisible(true);
                return;
            }

            // Mostra al massimo 10 risultati
            List<TutorBean> limitedTutors = tutors.stream().limit(10).toList();

            for (TutorBean tutor : limitedTutors) {
                String materie = String.join(", ", tutor.getMaterie());
                Button tutorButton = new Button(
                        tutor.getNome() + " " + tutor.getCognome() + " — " + materie
                );
                tutorButton.setStyle(
                        "-fx-font-size: 15px; -fx-background-color: #f0f0f0; " +
                                "-fx-padding: 6; -fx-background-radius: 10; -fx-cursor: hand;"
                );
                tutorButton.setMaxWidth(Double.MAX_VALUE);

                tutorButton.setOnAction(e -> selectTutor(tutor));
                resultsContainer.getChildren().add(tutorButton);
            }

            scrollResults.setVisible(true);

        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore durante la ricerca tutor", e);
            errorLabel.setText("Errore di connessione al database.");
            errorLabel.setVisible(true);
        }
    }

    private void selectTutor(TutorBean tutor) {
        this.selectedTutor = tutor;

        errorLabel.setText("Tutor selezionato: " + tutor.getNome() + " " + tutor.getCognome());
        errorLabel.setVisible(true);

        appointmentPane.setVisible(true);

        statusLabel.setVisible(false);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void cancelSelection(ActionEvent event) {
        appointmentPane.setVisible(false);
        selectedTutor = null;

        errorLabel.setText("Nessun tutor selezionato. Seleziona un altro tutor per continuare.");
        errorLabel.setVisible(true);

        statusLabel.setVisible(false);
        datePicker.setValue(null);
        hourCombo.getSelectionModel().clearSelection();
    }

    @FXML
    public void bookAppointment(ActionEvent event) {
        statusLabel.setVisible(false);

        if (selectedTutor == null) {
            statusLabel.setText("Seleziona prima un tutor.");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
            return;
        }

        LocalDate selectedDate = datePicker.getValue();
        String hourText = hourCombo.getValue();

        if (selectedDate == null || hourText == null || hourText.isEmpty()) {
            statusLabel.setText("Inserisci data e ora validi.");
            statusLabel.setTextFill(Color.RED);
            statusLabel.setVisible(true);
            return;
        }

        int hour;
        try {
            hour = Integer.parseInt(hourText.split(":")[0]);
        } catch (NumberFormatException _) {
            statusLabel.setText("Formato ora non valido.");
            statusLabel.setVisible(true);
            return;
        }

        try {
            appCtrl.bookAppointment(selectedTutor, selectedDate, hour);

            // CHIUDE il pannello e mostra un POPUP di successo
            appointmentPane.setVisible(false);
            showSuccess("Prenotazione Confermata", "Appuntamento prenotato con successo!");

            // Pulisce la selezione per evitare doppie prenotazioni
            selectedTutor = null;
            errorLabel.setText("");

        } catch (DBException e) {
            // Gestione errore DB con Logger e UI
            LOGGER.log(Level.SEVERE, "Errore prenotazione appuntamento", e);
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