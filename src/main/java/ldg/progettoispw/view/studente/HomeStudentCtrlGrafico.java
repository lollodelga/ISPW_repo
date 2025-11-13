package ldg.progettoispw.view.studente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ldg.progettoispw.view.HomeCtrlGrafico;

public class HomeStudentCtrlGrafico extends HomeCtrlGrafico {

    @FXML
    private Button btnOpzione1;
    @FXML
    private Button btnOpzione2;
    @FXML
    private Button btnOpzione3;
    @FXML
    private Button btnOpzione4;

    @FXML
    public void cercaTutor(ActionEvent event) {
        switchScene("/ldg/progettoispw/SearchTutor.fxml", event);
    }

    @FXML
    public void apriRichiesteSospese(ActionEvent event) {
        switchScene("/ldg/progettoispw/AppInAttesaStudente.fxml", event);
    }

    @FXML
    public void apriRichiesteRisposte(ActionEvent event) {
        switchScene("/ldg/progettoispw/AppRispostiStudente.fxml", event);
    }
}
