package ldg.progettoispw.view.studente;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.event.ActionEvent;
import ldg.progettoispw.controller.AppInAttesaStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInAttesaStudenteCtrlGrafico extends HomeCtrlGrafico {

    @FXML private VBox requestsContainer;
    @FXML private AnchorPane requestPane;

    @FXML private Label lblTutor;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;

    private static final Logger LOGGER = Logger.getLogger(AppInAttesaStudenteCtrlGrafico.class.getName());

    private final AppInAttesaStudenteCtrlApplicativo ctrlApplicativo = new AppInAttesaStudenteCtrlApplicativo();

    private AppointmentBean selectedAppointment;

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        loadPendingRequests();
    }

    /**
     * Carica tutte le richieste in attesa per lo studente loggato.
     * Gestisce in modo sicuro le eccezioni DB per evitare crash.
     */
    private void loadPendingRequests() {
        // Inizializziamo a lista vuota per evitare NullPointerException nel blocco successivo
        List<AppointmentBean> pendingAppointments = new ArrayList<>();

        try {
            pendingAppointments = ctrlApplicativo.getAppuntamentiInAttesa();
        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero degli appuntamenti in attesa per l'utente.", e);
            // Avvisa l'utente tramite il metodo ereditato dal padre
            showError("Errore Connessione", "Impossibile recuperare le richieste dal database.");
            return; // Interrompe l'esecuzione per evitare errori grafici
        }

        requestsContainer.getChildren().clear();

        if (pendingAppointments.isEmpty()) {
            Label emptyLabel = new Label("Nessuna richiesta in attesa.");
            // Testo nero/scuro per leggibilità su sfondo chiaro (se il container è chiaro)
            emptyLabel.setStyle("-fx-text-fill: black; -fx-font-size: 16;");
            requestsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (AppointmentBean app : pendingAppointments) {
            Label item = new Label(
                    app.getData() + " - " + app.getOra() + " con " + app.getTutorEmail()
            );

            // Stile: Sfondo blu, testo bianco, cursore a mano
            item.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;");
            item.setMaxWidth(Double.MAX_VALUE);

            item.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> showAppointmentDetails(app));
            requestsContainer.getChildren().add(item);
        }
    }

    /**
     * Mostra il popup con i dettagli della richiesta
     */
    private void showAppointmentDetails(AppointmentBean app) {
        selectedAppointment = app;

        lblTutor.setText("Tutor: " + app.getTutorEmail());
        lblData.setText("Data: " + app.getData());
        lblOra.setText("Ora: " + app.getOra());
        lblStato.setText("Stato: " + app.getStato());

        requestPane.setVisible(true);
    }

    @FXML
    private void onChiudiClick(ActionEvent event) {
        requestPane.setVisible(false);
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
    }
}