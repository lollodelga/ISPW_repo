package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import ldg.progettoispw.controller.LoginController;
import ldg.progettoispw.util.GController;


public class LoginGCon extends BaseGCon implements GController {
    @FXML
    private TextField email;
    @FXML
    private TextField password;
    @FXML
    private Label warningLabel;
    @FXML
    private Rectangle warningRectangle;

    public void initialize() {
        warningLabel.setVisible(false);
        warningRectangle.setVisible(false);
        setWarningElements(warningLabel, warningRectangle);
    }

    @FXML
    void backaction(ActionEvent event) {
        switchScene("/ldg/progettoispw/FirstPage.fxml", event);
    }

    @FXML
    void login(ActionEvent event) {
        String inEmail = email.getText();
        String inPassword = password.getText();
        LoginController controller = new LoginController(inEmail, inPassword, this, event);
        controller.start();
    }

    @Override
    public void changeView(int result, ActionEvent event) {
        switch(result) {
            case 0: switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
                break;
            case 1: switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
                break;
            case 2: showWarning("ERRORE: Password errata.");
                break;
            case 3: showWarning("ERRORE: Riempi tutti i campi.");
                break;
            case 4: showWarning("ERRORE: Email non valida.");
                break;
            case 5: showWarning("ERRORE: L'utente non esiste.");
                break;
            default: showWarning("ERRORE DI SISTEMA: riprovare.");
        }
    }
}