package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

public abstract class HomeCtrlGrafico extends BaseGCon implements GControllerHome {
    @FXML
    protected Label labelRuolo;
    @FXML
    protected Label labelNome;
    @FXML
    protected Label labelCognome;
    @FXML
    protected Label labelData;

    protected final HomePageController controller = new HomePageController();

    @FXML
    public void initialize() {
        // Chiede al controller di aggiornare la view all'avvio
        controller.refreshUserData(this);
    }

    public void updateUserData(String name, String surname, String birthDate, String role) {
        labelNome.setText("Nome: " + name);
        labelCognome.setText("Cognome: " + surname);
        labelData.setText("Nascita: " + birthDate);
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void logout(ActionEvent event) {
        // Delego la logica applicativa al controller
        controller.logout();

        switchScene("/ldg/progettoispw/FirstPage.fxml", event);
    }
}
