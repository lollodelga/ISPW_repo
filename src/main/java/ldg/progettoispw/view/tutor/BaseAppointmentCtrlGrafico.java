package ldg.progettoispw.view.tutor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ldg.progettoispw.view.HomeCtrlGrafico;

public abstract class BaseAppointmentCtrlGrafico extends HomeCtrlGrafico {

    @FXML
    protected Label lblStudente;

    @FXML
    protected Label lblData;

    @FXML
    protected Label lblOra;

    @FXML
    protected Label lblStato;

    @FXML
    protected VBox resultsContainer;

    @FXML
    protected AnchorPane appointmentPane;
}