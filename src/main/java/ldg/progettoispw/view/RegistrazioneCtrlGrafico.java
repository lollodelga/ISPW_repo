package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ldg.progettoispw.controller.RegistrazioneCtrlApplicativo;
import ldg.progettoispw.model.bean.UserBean;
import ldg.progettoispw.util.GController;

public class RegistrazioneCtrlGrafico extends BaseGCon implements GController {

    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private TextField nascita;
    @FXML private TextField email;
    @FXML private TextField password;
    @FXML private TextField materie;
    @FXML private ToggleGroup roleGroup;
    @FXML private RadioButton studenteButton;
    @FXML private RadioButton tutorButton;
    @FXML private Text textMateria;
    @FXML private Label warningLabel;
    @FXML private Rectangle warningRectangle;

    @FXML
    public void initialize() {
        warningLabel.setVisible(false);
        warningRectangle.setVisible(false);
        textMateria.setVisible(false);
        materie.setVisible(false);
        setWarningElements(warningLabel, warningRectangle);
    }

    @FXML
    void clickStudente() {
        textMateria.setText("Materie di studio (materia1, materia2, ...)");
        textMateria.setVisible(true);
        materie.setVisible(true);
    }

    @FXML
    void clickTutor() {
        textMateria.setText("Materie trattate (materia1, materia2, ...)");
        textMateria.setVisible(true);
        materie.setVisible(true);
    }

    @FXML
    private void backaction(ActionEvent event){
        switchScene("/ldg/progettoispw/FirstPage.fxml", event);
    }

    @FXML
    private void register(ActionEvent event){
        UserBean bean = new UserBean();
        bean.setName(nome.getText());
        bean.setSurname(cognome.getText());
        bean.setBirthDate(nascita.getText());
        bean.setEmail(email.getText());
        bean.setPassword(password.getText());
        bean.setSubjects(materie.getText());

        if (roleGroup.getSelectedToggle() == tutorButton)
            bean.setRole("1");
        else if (roleGroup.getSelectedToggle() == studenteButton)
            bean.setRole("2");

        RegistrazioneCtrlApplicativo ctrlApp = new RegistrazioneCtrlApplicativo();
        int result = ctrlApp.registerUser(bean);

        changeView(result, event);
    }

    @Override
    public void changeView(int result, ActionEvent event) {
        switch (result) {
            case 0 -> switchScene("/ldg/progettoispw/LoginPage.fxml", event);
            case 1 -> showWarning("ERRORE: email già in uso.");
            case 2 -> showWarning("ERRORE: Riempi tutti i campi.");
            case 3 -> showWarning("ERRORE: email non valida.");
            case 4 -> showWarning("ERRORE: La password non rispetta i requisiti.");
            case 5 -> showWarning("ERRORE: La data non è valida.");
            default -> showWarning("ERRORE DI SISTEMA: riprovare");
        }
    }
}