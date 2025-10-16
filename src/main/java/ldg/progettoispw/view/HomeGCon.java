package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ldg.progettoispw.controller.HomePageController;
import ldg.progettoispw.util.GControllerHome;

public abstract class HomeGCon extends BaseGCon implements GControllerHome {
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


    @FXML
    void logout(ActionEvent event) {
        // Delego la logica applicativa al controller
        controller.logout();

        switchScene("/ldg/progettoispw/FirstPage.fxml", event);
    }
}
