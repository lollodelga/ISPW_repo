package ldg.progettoispw.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ldg.progettoispw.exception.ViewException;

import java.io.IOException;

public class FirstPageGCon {
    private void changeView(String fxmlPath, MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new ViewException("Errore nel caricamento della view: " + fxmlPath, e);
        }
    }

    @FXML
    void startLogin(MouseEvent event) {
        changeView("/ldg/progettoispw/LoginPage.fxml", event);
    }

    @FXML
    void startRegister(MouseEvent event) {
        changeView("/ldg/progettoispw/RegisterPage.fxml", event);
    }
}