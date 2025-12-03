package ldg.progettoispw.view.tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import ldg.progettoispw.controller.ManageReviewCtrlApplicativo;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.view.HomeCtrlGrafico;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageReviewsCtrlGrafico extends HomeCtrlGrafico {

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private VBox reviewsContainer;

    @FXML
    private ScrollPane scrollReviews;

    private ManageReviewCtrlApplicativo controllerApplicativo;

    private static final Logger LOGGER = Logger.getLogger(ManageReviewsCtrlGrafico.class.getName());

    @FXML
    public void initialize() {
        controllerApplicativo = new ManageReviewCtrlApplicativo();

        try {
            // Recupero liste dal controller applicativo
            ManageReviewCtrlApplicativo.RecensioniResult result = controllerApplicativo.getRecensioniEValoriSentiment();
            List<RecensioneBean> recensioni = result.getRecensioni();
            List<Integer> sentimentValues = result.getSentimentValues();

            // Popolo il grafico
            popolaGrafico(sentimentValues);

            // Popolo la lista recensioni
            popolaListaRecensioni(recensioni);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Errore nel caricamento delle recensioni o nel grafico", e);
        }
    }

    private void popolaGrafico(List<Integer> sentimentValues) {
        barChart.getData().clear();

        // Raggruppiamo per valore sentiment (1-5)
        int[] counts = new int[5]; // indice 0 -> 1 stella, indice 4 -> 5 stelle
        for (int val : sentimentValues) {
            if (val >= 1 && val <= 5) {
                counts[val - 1]++;
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Numero Recensioni");

        for (int i = 0; i < counts.length; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), counts[i]));
        }

        barChart.getData().add(series);

        // Opzionale: impostare assi se non definiti in FXML
        CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
        xAxis.setLabel("Stelle");

        NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
        yAxis.setLabel("Numero Recensioni");
    }

    private void popolaListaRecensioni(List<RecensioneBean> recensioni) {
        reviewsContainer.getChildren().clear();

        for (RecensioneBean bean : recensioni) {
            TextArea ta = new TextArea("Studente: " + bean.getStudentEmail() +
                    "\nRecensione: " + bean.getRecensione() +
                    "\nStelle: " + bean.getSentimentValue());
            ta.setWrapText(true);
            ta.setEditable(false);
            ta.setMaxWidth(480); // adatta alla larghezza del VBox
            ta.setStyle("-fx-control-inner-background: #3498DBAA; -fx-text-fill: white; -fx-font-size: 14px;");
            reviewsContainer.getChildren().add(ta);
        }
    }


    @FXML
    private void backAction(ActionEvent event) {
        switchScene("/ldg/progettoispw/HomePageTutor.fxml", event);
    }
}
