package ldg.progettoispw.view.studente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.controller.AppRispostiStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AppRispostiStudenteCtrlGrafico extends HomeCtrlGrafico implements Initializable {

    @FXML private VBox resultsContainer;
    @FXML private AnchorPane appointmentPane;
    @FXML private Label lblTutor;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;

    // Elementi dinamici (Paga vs Recensione)
    @FXML private Button btnPaga;
    @FXML private TextArea txtRecensione;
    @FXML private Button btnInviaRecensione;
    @FXML private Label lblErroreRecensione;

    private AppRispostiStudenteCtrlApplicativo ctrlApp;
    private AppointmentBean selectedAppointment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ctrlApp = new AppRispostiStudenteCtrlApplicativo();
        appointmentPane.setVisible(false);
        caricaLista();
    }

    private void caricaLista() {
        resultsContainer.getChildren().clear();
        try {
            List<AppointmentBean> list = ctrlApp.getAppuntamentiStudente();

            if (list.isEmpty()) {
                Label empty = new Label("Non hai lezioni completate nello storico.");
                empty.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                resultsContainer.getChildren().add(empty);
                return;
            }

            for (AppointmentBean bean : list) {
                resultsContainer.getChildren().add(createBox(bean));
            }

        } catch (DBException _) {
            showError("Errore Database", "Impossibile recuperare lo storico.");
        } catch (IllegalStateException e) {
            showError("Errore Sessione", e.getMessage());
        }
    }

    // Apre il popup e decide cosa mostrare in base allo stato
    private void openDetails(AppointmentBean bean) {
        selectedAppointment = bean;

        // Popolamento Labels (con toString automatico di Date/Time)
        lblTutor.setText("Tutor: " + bean.getTutorEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato().toUpperCase());

        // LOGICA DI VISIBILITÀ (Disaccoppiata dal DB, basata sul Bean)
        String stato = bean.getStato().toLowerCase();
        boolean isCompletato = "completato".equals(stato);
        boolean isPagato = "pagato".equals(stato);

        // 1. Bottone PAGA: visibile solo se la lezione è completata ma non pagata
        btnPaga.setVisible(isCompletato);

        // 2. Form RECENSIONE: visibile solo se la lezione è già pagata
        txtRecensione.setVisible(isPagato);
        btnInviaRecensione.setVisible(isPagato);

        // Reset errori e visibilità pannello
        lblErroreRecensione.setVisible(false);
        appointmentPane.setVisible(true);
        centerPopup();
    }

    @FXML
    private void onPagaClick() {
        try {
            // 1. Chiamata al Controller Applicativo (Logica Pura)
            ctrlApp.pagaAppuntamento(selectedAppointment);

            // 2. Aggiornamento Bean Locale (Riflette il cambio stato nella View)
            selectedAppointment.setStato("pagato");

            // 3. Feedback utente e Aggiornamento Popup
            showSuccess("Pagamento Riuscito", "Transazione di 15.00€ completata.\nOra puoi lasciare una recensione.");

            // Ricarica il popup per nascondere "Paga" e mostrare "Recensione"
            openDetails(selectedAppointment);

        } catch (DBException e) {
            showError("Errore Pagamento", "Transazione fallita: " + e.getMessage());
        }
    }

    @FXML
    private void onInviaRecensioneClick() {
        String testo = txtRecensione.getText().trim();
        if (testo.isEmpty()) {
            lblErroreRecensione.setText("Scrivi un commento prima di inviare.");
            lblErroreRecensione.setVisible(true);
            return;
        }

        RecensioneBean bean = new RecensioneBean();
        bean.setTutorEmail(selectedAppointment.getTutorEmail());
        bean.setStudentEmail(selectedAppointment.getStudenteEmail());
        bean.setRecensione(testo);

        // Chiamata Applicativo
        String esito = ctrlApp.inviaRecensione(bean);

        if (esito.startsWith("Errore")) {
            lblErroreRecensione.setText(esito);
            lblErroreRecensione.setVisible(true);
        } else {
            showSuccess("Grazie!", esito);
            appointmentPane.setVisible(false);
            txtRecensione.clear();
        }
    }

    @FXML private void onChiudiClick() { appointmentPane.setVisible(false); }

    @FXML private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
    }

    // Helper creazione riga lista
    private VBox createBox(AppointmentBean bean) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setStyle("-fx-background-color: #3498DB55; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: white; -fx-cursor: hand;");

        Label t = new Label("Tutor: " + bean.getTutorEmail());
        Label s = new Label("Stato: " + bean.getStato());
        Label d = new Label("Data: " + bean.getData() + " " + bean.getOra());

        // Stile label bianche
        String whiteText = "-fx-text-fill: white; -fx-font-weight: bold;";
        t.setStyle(whiteText);
        s.setStyle(whiteText);
        d.setStyle("-fx-text-fill: #ecf0f1; -fx-font-style: italic;");

        box.getChildren().addAll(t, d, s);

        box.setOnMouseClicked(e -> openDetails(bean));
        return box;
    }

    private void centerPopup() {
        if (appointmentPane.getScene() != null) {
            double sceneWidth = appointmentPane.getScene().getWidth();
            double paneWidth = appointmentPane.getPrefWidth();
            appointmentPane.setLayoutX((sceneWidth - paneWidth) / 2);
        }
    }
}