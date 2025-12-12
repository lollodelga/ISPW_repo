package ldg.progettoispw.view.tutor;

import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import ldg.progettoispw.controller.ManageAppointmentCtrlApplicativo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.List;


/**
 * Controller grafico per la gestione delle richieste di appuntamento da parte dei tutor.
 *
 * Quando viene inizializzato:
 *  - recupera la lista di appuntamenti in attesa tramite ManageAppointmentCtrlApplicativo
 *  - crea un pulsante per ciascun appuntamento
 *  - al click sul pulsante mostra un piccolo pannello (appointmentPane)
 *
 * Il pannello sarà in seguito completato con pulsanti di conferma/rifiuto/chiusura.
 */
public class ManageAppointmentCtrlGrafico extends HomeCtrlGrafico {

    @FXML
    private Label lblStudente;
    @FXML
    private Label lblData;
    @FXML
    private Label lblOra;
    @FXML
    private Label lblStato;
    @FXML
    private VBox resultsContainer;
    @FXML
    private AnchorPane appointmentPane;

    private ManageAppointmentCtrlApplicativo ctrlApplicativo;
    private AppointmentBean selectedAppointment;

    @Override
    @FXML
    public void initialize() {
        ctrlApplicativo = new ManageAppointmentCtrlApplicativo();
        appointmentPane.setVisible(false);
        loadPendingAppointments();
    }

    private void loadPendingAppointments() {
        try {
            List<AppointmentBean> pendingAppointments = ctrlApplicativo.getAppuntamentiInAttesa();
            resultsContainer.getChildren().clear();

            if (pendingAppointments.isEmpty()) {
                Label noData = new Label("Nessuna richiesta di appuntamento in attesa.");
                noData.setStyle("-fx-font-size: 16px; -fx-text-fill: black;");
                resultsContainer.getChildren().add(noData);
                return;
            }

            for (AppointmentBean bean : pendingAppointments) {
                Button btn = new Button(bean.getStudenteEmail() + " - " + bean.getData() + " " + bean.getOra());
                btn.setPrefWidth(400);
                btn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 15px; -fx-background-radius: 10;");
                btn.setOnAction(e -> openAppointmentPanel(bean));
                resultsContainer.getChildren().add(btn);
            }

        } catch (DBException e) {
            Label error = new Label("Errore durante il caricamento: " + e.getMessage());
            error.setStyle("-fx-text-fill: red;");
            resultsContainer.getChildren().add(error);
        }
    }

    private void openAppointmentPanel(AppointmentBean bean) {
        // Aggiorna il bean selezionato
        selectedAppointment = bean;

        // Mostra pannello
        appointmentPane.setVisible(true);

        // Aggiorna le label con le informazioni dell'appuntamento
        lblStudente.setText("Studente: " + bean.getStudenteEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato());
    }

    @FXML
    private void onConfermaClick(ActionEvent event) {
        try {
            // 1. Chiamo il Controller Applicativo (che ora lancia DBException)
            ctrlApplicativo.confermaAppuntamento(selectedAppointment);

            // 2. Se non ci sono errori, ricarico la scena per aggiornare la lista
            switchScene("/ldg/progettoispw/AppPendTutor.fxml", event);

        } catch (DBException e) {
            // 3. GESTIONE ERRORE: Mostro il popup all'utente
            showError("Errore Database", "Impossibile confermare l'appuntamento:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Gestisco anche errori di logica (es. stato non valido)
            showError("Attenzione", e.getMessage());
        }
    }

    @FXML
    private void onRifiutaClick(ActionEvent event) {
        try {
            ctrlApplicativo.rifiutaAppuntamento(selectedAppointment);

            switchScene("/ldg/progettoispw/AppPendTutor.fxml", event);

        } catch (DBException e) {
            showError("Errore Database", "Impossibile rifiutare l'appuntamento:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
            showError("Attenzione", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null); // Rimuove l'header per pulizia
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onChiudiClick(ActionEvent event) {
        appointmentPane.setVisible(false);
    }


    @FXML
    private void backAction(ActionEvent event) { switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);    }
}