package ldg.progettoispw.view.studente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import ldg.progettoispw.controller.AppRispostiStudenteCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppRispostiStudenteCtrlGrafico extends HomeCtrlGrafico implements Initializable {

    @FXML private VBox resultsContainer;
    @FXML private AnchorPane appointmentPane;
    @FXML private Label lblTutor;
    @FXML private Label lblData;
    @FXML private Label lblOra;
    @FXML private Label lblStato;
    @FXML private TextArea txtRecensione;
    @FXML private Button btnInviaRecensione;
    @FXML private Button btnChiudi;

    private AppRispostiStudenteCtrlApplicativo ctrlApp;
    private AppointmentBean selectedAppointment;
    private static final Logger LOGGER = Logger.getLogger(AppRispostiStudenteCtrlGrafico.class.getName());
    private static final String WHITE_TEXT_STYLE = "-fx-text-fill: white;";


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ctrlApp = new AppRispostiStudenteCtrlApplicativo();

        appointmentPane.setVisible(false); // inizialmente nascosto

        try {
            List<AppointmentBean> appuntamenti = ctrlApp.getAppuntamentiStudente();

            for (AppointmentBean bean : appuntamenti) {
                VBox box = createAppointmentBox(bean);
                resultsContainer.getChildren().add(box);
            }

            // Ascolta il resize della finestra per centrare il popup
            resultsContainer.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.widthProperty().addListener((obs, oldVal, newVal) -> centerPopup());
                    newScene.heightProperty().addListener((obs, oldVal, newVal) -> centerPopup());
                }
            });

        } catch (DBException e) {
            LOGGER.log(Level.SEVERE, "Errore nel database: " + e.getMessage(), e);
        }
    }

    private VBox createAppointmentBox(AppointmentBean bean) {
        VBox box = new VBox();
        box.setSpacing(5);
        box.setStyle("-fx-background-color: #3498DB55; -fx-padding: 10; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ffffffAA;");
        box.setOnMouseClicked((MouseEvent event) -> showAppointmentDetails(bean));

        Label lblTut = new Label("Tutor: " + bean.getTutorEmail());
        Label lblDat = new Label("Data: " + bean.getData());
        Label lblOr = new Label("Ora: " + bean.getOra());
        Label lblStat = new Label("Stato: " + bean.getStato());

        lblTut.setStyle(WHITE_TEXT_STYLE);
        lblDat.setStyle(WHITE_TEXT_STYLE);
        lblOr.setStyle(WHITE_TEXT_STYLE);
        lblStat.setStyle(WHITE_TEXT_STYLE);

        box.getChildren().addAll(lblTut, lblDat, lblOr, lblStat);
        return box;
    }

    private void showAppointmentDetails(AppointmentBean bean) {
        selectedAppointment = bean;

        lblTutor.setText("Tutor: " + bean.getTutorEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato());

        // Mostra recensione solo se completato
        boolean completato = "completato".equalsIgnoreCase(bean.getStato());
        txtRecensione.setVisible(completato);
        btnInviaRecensione.setVisible(completato);

        appointmentPane.setVisible(true);
        centerPopup(); // centra il popup ogni volta che viene aperto
    }

    private void centerPopup() {
        if (appointmentPane.getScene() != null) {
            double sceneWidth = appointmentPane.getScene().getWidth();
            double sceneHeight = appointmentPane.getScene().getHeight();

            appointmentPane.setLayoutX((sceneWidth - appointmentPane.getPrefWidth()) / 2);
            appointmentPane.setLayoutY((sceneHeight - appointmentPane.getPrefHeight()) / 2);
        }
    }

    @FXML
    private void onInviaRecensioneClick() {
        // qui implementerai l'invio della recensione
    }

    @FXML
    private void onChiudiClick() {
        appointmentPane.setVisible(false);
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
    }
}
