package ldg.progettoispw.view.studente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ldg.progettoispw.view.HomeCtrlGrafico;

public class HomeStudentCtrlGrafico extends HomeCtrlGrafico {

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
