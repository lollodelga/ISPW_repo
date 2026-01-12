package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ldg.progettoispw.controller.ManageAppointmentCtrlApplicativo;
import ldg.progettoispw.engineering.bean.AppointmentBean;
import ldg.progettoispw.engineering.exception.DBException;

import java.util.List;

public class ManageAppointmentCtrlGrafico extends BaseAppointmentCtrlGrafico {

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
        selectedAppointment = bean;
        appointmentPane.setVisible(true);

        // Accesso ai campi ereditati
        lblStudente.setText("Studente: " + bean.getStudenteEmail());
        lblData.setText("Data: " + bean.getData());
        lblOra.setText("Ora: " + bean.getOra());
        lblStato.setText("Stato: " + bean.getStato());
    }

    @FXML
    private void onConfermaClick(ActionEvent event) {
        try {
            ctrlApplicativo.confermaAppuntamento(selectedAppointment);
            switchScene("/ldg/progettoispw/AppPendTutor.fxml", event);
        } catch (DBException e) {
            showError("Errore Database", "Impossibile confermare l'appuntamento:\n" + e.getMessage());
        } catch (IllegalArgumentException e) {
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

    @FXML
    private void onChiudiClick(ActionEvent event) {
        appointmentPane.setVisible(false);
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
    }
}