package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
// import javafx.scene.shape.Rectangle; // NON SERVE PIÙ
// import javafx.scene.text.Text;       // NON SERVE PIÙ
import ldg.progettoispw.controller.RegistrationCtrlApplicativo;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.util.GController;

public class RegistrationCtrlGrafico extends BaseGCon implements GController {

    @FXML private TextField nome;
    @FXML private TextField cognome;
    @FXML private TextField nascita;
    @FXML private TextField email;
    @FXML private TextField password;
    @FXML private TextField materie;

    @FXML private ToggleGroup roleGroup;
    @FXML private RadioButton studenteButton;
    @FXML private RadioButton tutorButton;

    // 🔹 MODIFICA 1: Cambiato da Text a Label
    @FXML private Label textMateria;

    @FXML private Label warningLabel;

    // 🔹 MODIFICA 2: Rimosso warningRectangle (non esiste più nell'FXML nuovo)
    // @FXML private Rectangle warningRectangle;

    @FXML
    public void initialize() {
        warningLabel.setVisible(false);

        // Setup iniziale invisibile
        textMateria.setVisible(false);
        materie.setVisible(false);

        // Se BaseGCon richiede per forza il rettangolo, passagli null o rimuovi la chiamata
        // setWarningElements(warningLabel, null);
        // Oppure configura solo la label se il tuo BaseGCon lo permette
    }

    @FXML
    void clickStudente() {
        textMateria.setText("Materie di studio (separate da virgola)");
        textMateria.setVisible(true);
        materie.setVisible(true);
    }

    @FXML
    void clickTutor() {
        textMateria.setText("Materie trattate (separate da virgola)");
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

        RegistrationCtrlApplicativo ctrlApp = new RegistrationCtrlApplicativo();
        int result = ctrlApp.registerUser(bean);

        changeView(result, event);
    }

    @Override
    public void changeView(int result, ActionEvent event) {
        switch (result) {
            case 0 -> switchScene("/ldg/progettoispw/Login.fxml", event); // Assicurati che il nome file sia giusto (Login.fxml o LoginPage.fxml)
            case 1 -> showGuiWarning("ERRORE: email già in uso.");
            case 2 -> showGuiWarning("ERRORE: Riempi tutti i campi.");
            case 3 -> showGuiWarning("ERRORE: email non valida.");
            case 4 -> showGuiWarning("ERRORE: La password non rispetta i requisiti.");
            case 5 -> showGuiWarning("ERRORE: La data non è valida.");
            default -> showGuiWarning("ERRORE DI SISTEMA: riprovare");
        }
    }

    // Metodo helper locale se BaseGCon dipendeva dal rettangolo
    private void showGuiWarning(String msg) {
        warningLabel.setText(msg);
        warningLabel.setVisible(true);
    }
}