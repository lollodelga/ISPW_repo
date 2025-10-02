package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import ldg.progettoispw.controller.RegisterController;
import ldg.progettoispw.util.GController;


public class RegisterGCon extends BaseGCon implements GController {
    @FXML
    private TextField cognome;
    @FXML
    private TextField email;
    @FXML
    private TextField nascita;
    @FXML
    private TextField nome;
    @FXML
    private TextField materie;
    @FXML
    private TextField password;
    @FXML
    private ToggleGroup roleGroup;
    @FXML
    private RadioButton studenteButton;
    @FXML
    private RadioButton tutorButton;
    @FXML
    private Text textMateria;
    @FXML
    private Label warningLabel;
    @FXML
    private Rectangle warningRectangle;

    @FXML
    public void initialize() {
        warningLabel.setVisible(false);
        warningRectangle.setVisible(false);
        textMateria.setVisible(false);
        materie.setVisible(false);
        setWarningElements(warningLabel, warningRectangle);

    }

    @FXML
    void clickStudente(ActionEvent event) {
        textMateria.setText("Materie di studio (materia1, materia2, ...)");
        textMateria.setVisible(true);
        materie.setVisible(true);
    }

    @FXML
    void clickTutor(ActionEvent event) {
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
        RegisterController controller = new RegisterController(this, event);
        String nomeTF = nome.getText();
        String cognomeTF = cognome.getText();
        String birthTF = nascita.getText();
        String emailTF = email.getText();
        String passwordTF = password.getText();
        String materieTF = materie.getText();
        String ruoloTG = "";
        // Controllo del bottone selezione per dare un valore al ruolo scelto
        // ruolo = 1 allora ho scelto tutor, mentre ruolo = 2 ho scelto studente
        if (roleGroup.getSelectedToggle() == tutorButton) {
            ruoloTG = "1";
        } else if (roleGroup.getSelectedToggle() == studenteButton) {
            ruoloTG = "2";
        }
        controller.register(nomeTF, cognomeTF, birthTF, emailTF, passwordTF, materieTF, ruoloTG);
    }


    @Override
    public void changeView(int result, ActionEvent event) {
        //in base al risultato dell'applicativo dopo aver premuto il tasto di registrazione
        //avrò un risultato ben preciso, che verrà comunicato dall'applicativo e di conseguenza avrò questi casi.
        switch (result) {
            case 0: switchScene("/ldg/progettoispw/LoginPage.fxml", event);
                break;
            case 1: showWarning("ERRORE: email già in uso.");
                break;
            case 2: showWarning("ERRORE: Riempi tutti i campi.");
                break;
            case 3: showWarning("ERRORE: email non valida.");
                break;
            case 4: showWarning("ERRORE: La password non rispetta i requisiti.");
                break;
            case 5: showWarning("ERRORE: La data non è valida.");
                break;
            default: showWarning("ERRORE DI SISTEMA: riprovare");
        }
    }
}