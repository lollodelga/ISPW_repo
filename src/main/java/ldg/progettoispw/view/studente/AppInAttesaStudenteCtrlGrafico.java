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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppInAttesaStudenteCtrlGrafico extends HomeCtrlGrafico {

    // ELEMENTI FXML PRINCIPALI
    @FXML private VBox requestsContainer;
    @FXML private AnchorPane requestPane;
    @FXML private ScrollPane scrollRequests;

    // LABEL NEL POPUP
    @FXML private Label lblTutor;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;

    // PULSANTI
    @FXML private Button btnChiudi;

    private static final Logger LOGGER = Logger.getLogger(AppInAttesaStudenteCtrlGrafico.class.getName());


    // CONTROLLER LOGICO
    private AppInAttesaStudenteCtrlApplicativo ctrlApplicativo = new AppInAttesaStudenteCtrlApplicativo();

    // VARIABILE DI SUPPORTO PER IL POPUP
    private AppointmentBean selectedAppointment;

    @Override
    @FXML
    public void initialize() {
        loadPendingRequests();
    }

    /**
     * Carica tutte le richieste in attesa per lo studente loggato
     */
    private void loadPendingRequests() {
        List<AppointmentBean> pendingAppointments = null;
        try {
            pendingAppointments = ctrlApplicativo.getAppuntamentiInAttesa();
        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il recupero degli appuntamenti in attesa per l'utente. ", e);

        }

        requestsContainer.getChildren().clear();

        if (pendingAppointments.isEmpty()) {
            Label emptyLabel = new Label("Nessuna richiesta in attesa.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16;");
            requestsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (AppointmentBean app : pendingAppointments) {
            Label item = new Label(
                    app.getData() + " - " + app.getOra() + " con " + app.getTutorEmail()
            );
            item.setStyle("-fx-background-color: #3498db; -fx-padding: 10; -fx-background-radius: 10; -fx-cursor: hand;");
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
