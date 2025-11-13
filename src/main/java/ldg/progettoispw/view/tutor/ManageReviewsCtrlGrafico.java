package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import ldg.progettoispw.controller.ManageReviewCtrlApplicativo;
import ldg.progettoispw.view.HomeCtrlGrafico;

public class ManageReviewsCtrlGrafico extends HomeCtrlGrafico {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private VBox reviewsContainer;

    @FXML
    private ScrollPane scrollReviews;

    private ManageReviewCtrlApplicativo controllerApplicativo;

    @FXML
    public void initialize() {
        // Inizializza controller applicativo (DAO + SentimentAnalyzer)

        // 1. Processa recensioni nuove e calcola sentiment

        // 2. Popola il grafico

        // 3. Popola lista recensioni sotto il grafico
    }

    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);    }
}

