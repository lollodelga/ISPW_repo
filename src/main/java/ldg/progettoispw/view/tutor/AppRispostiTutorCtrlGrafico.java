package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert; // Import necessario per l'Alert
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.controller.AppRispostiTutorCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppRispostiTutorCtrlGrafico extends HomeCtrlGrafico implements Initializable {

    @FXML private Button btnAnnulla;
    @FXML private Button btnCompletato;
    @FXML private Button btnChiudi;

    @FXML private Label lblStudente;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;

    @FXML private VBox resultsContainer;
    @FXML private AnchorPane appointmentPane;

    private AppRispostiTutorCtrlApplicativo ctrlApp;
    private AppointmentBean selectedAppointment;

    // Logger
    private static final Logger LOGGER = Logger.getLogger(AppRispostiTutorCtrlGrafico.class.getName());
    private static final String WHITE_TEXT_STYLE = "-fx-text-fill: white;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ctrlApp = new AppRispostiTutorCtrlApplicativo();
        appointmentPane.setVisible(false);

        try {
            List<AppointmentBean> appointments = ctrlApp.getAppuntamentiTutor();
            for (AppointmentBean bean : appointments) {
                VBox box = createAppointmentBox(bean);
                resultsContainer.getChildren().add(box);
            }
        } catch (DBException e) {
            // --- CORREZIONE RIGA 52 ---
            // Rimosso il "+ e.getMessage()".
            // Passiamo l'eccezione come parametro separato.
            LOGGER.log(Level.SEVERE, "Errore nel caricamento appuntamenti tutor", e);

            // AGGIUNTA FONDAMENTALE PER ESAME: Avviso l'utente!
            showError("Errore Caricamento", "Impossibile recuperare gli appuntamenti dal database.");
        }
    }

    // ... (metodo createAppointmentBox uguale a prima) ...
    private VBox createAppointmentBox(AppointmentBean bean) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setStyle("-fx-background-color: #3498DB55; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ffffffAA;");
        box.setOnMouseClicked(event -> showAppointmentDetails(bean));

        Label lblStudenteBox = new Label("Studente: " + bean.getStudenteEmail());
        Label lblDataBox = new Label("Data: " + bean.getData());
        Label lblOraBox = new Label("Ora: " + bean.getOra());
        Label lblStatoBox = new Label("Stato: " + bean.getStato());

        lblStudenteBox.setStyle(WHITE_TEXT_STYLE);
        lblDataBox.setStyle(WHITE_TEXT_STYLE);
        lblOraBox.setStyle(WHITE_TEXT_STYLE);
        lblStatoBox.setStyle(WHITE_TEXT_STYLE);

        box.getChildren().addAll(lblStudenteBox, lblDataBox, lblOraBox, lblStatoBox);
        return box;
    }

    private void showAppointmentDetails(AppointmentBean bean) {
        selectedAppointment = bean;

        lblStudente.setText("Studente: " + bean.getStudenteEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato());

        boolean showActions = "confermato".equalsIgnoreCase(bean.getStato());
        btnAnnulla.setVisible(showActions);
        btnCompletato.setVisible(showActions);

        appointmentPane.setVisible(true);
    }

    @FXML
    private void onAnnullaClick(ActionEvent event) {
        if (selectedAppointment != null) {
            // NOTA: Qui dovresti idealmente gestire DBException se updateAppointmentStatus la lancia
            ctrlApp.updateAppointmentStatus(
                    selectedAppointment.getStudenteEmail(),
                    selectedAppointment.getData(),
                    selectedAppointment.getOra(),
                    selectedAppointment.getStato(),
                    "cancel");

            lblStato.setText("Stato: annullato");
            appointmentPane.setVisible(false);
        }
        switchScene("/ldg/progettoispw/AppRispostiTutor.fxml", event);
    }

    @FXML
    private void onCompletatoClick(ActionEvent event) {
        if (selectedAppointment != null) {
            // NOTA: Idem come sopra, se il metodo del controller lancia eccezioni, metti try-catch
            ctrlApp.updateAppointmentStatus(
                    selectedAppointment.getStudenteEmail(),
                    selectedAppointment.getData(),
                    selectedAppointment.getOra(),
                    selectedAppointment.getStato(),
                    "complete");

            lblStato.setText("Stato: completato");
            appointmentPane.setVisible(false);
        }
        switchScene("/ldg/progettoispw/AppRispostiTutor.fxml", event);
    }

    // --- Metodo Helper per Alert ---
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onChiudiClick() {
        appointmentPane.setVisible(false);
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
    }
}