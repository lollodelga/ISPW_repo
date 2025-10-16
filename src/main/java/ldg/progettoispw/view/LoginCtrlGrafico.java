package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import ldg.progettoispw.controller.LoginCtrlApplicativo;
import ldg.progettoispw.exception.*;

public class LoginCtrlGrafico extends BaseGCon {

    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Label warningLabel;
    @FXML
    private Rectangle warningRectangle;

    @FXML
    public void initialize() {
        setWarningElements(warningLabel, warningRectangle);
        warningLabel.setVisible(false);
        warningRectangle.setVisible(false);
    }

    @FXML
    void login(ActionEvent event) {
        String userEmail = this.email.getText().trim();
        String userPassword = this.password.getText().trim();

        LoginCtrlApplicativo loginCtrl = new LoginCtrlApplicativo();

        try {
            int role = loginCtrl.verificaCredenziali(userEmail, userPassword);

            if (role == 1) {
                switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
            } else {
                switchScene("/ldg/progettoispw/HomePageStudent.fxml", event);
            }

        } catch (InvalidEmailException | UserDoesNotExistException | IncorrectPasswordException | DBException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        showWarning("ERRORE: " + message);
    }

    @FXML
    void backaction(ActionEvent event) {
        switchScene("/ldg/progettoispw/FirstPage.fxml", event);
    }
}