package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import ldg.progettoispw.view.HomeCtrlGrafico;

public class HomeTutorGCon extends HomeCtrlGrafico {

    @FXML
    private void openPendingRequests(ActionEvent event) {
        switchScene("/ldg/progettoispw/AppPendTutor.fxml", event);
    }

    @FXML
    private void openReviews(ActionEvent event) {
        switchScene("/ldg/progettoispw/ManageReview.fxml", event);
    }

    @FXML
    private void openCompletedRequests(ActionEvent event) {
        switchScene("/ldg/progettoispw/AppRispostiTutor.fxml", event);
    }
}