package ldg.progettoispw.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ldg.progettoispw.exception.ViewException;

import java.io.IOException;

public abstract class BaseGCon {
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    private Label warningLabelRef;
    private Rectangle warningRectangleRef;

    /**
     * Inizializza i riferimenti agli elementi grafici di avviso.
     * Deve essere chiamato dal controller figlio (es. LoginGCon) dentro initialize().
     */
    protected void setWarningElements(Label label, Rectangle rectangle) {
        this.warningLabelRef = label;
        this.warningRectangleRef = rectangle;
    }

    /**
     * Cambia scena verso il file FXML specificato.
     *
     * @param fxmlPath percorso del file FXML da caricare
     * @param event    evento che ha causato il cambio (serve per recuperare lo stage)
     */
    protected void switchScene(String fxmlPath, ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource(fxmlPath));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new ViewException("Errore nel caricamento della view: " + fxmlPath, e);
        }
    }

    /**
     * Mostra un messaggio di errore tramite gli elementi grafici inizializzati.
     *
     * @param message messaggio da visualizzare
     */
    protected void showWarning(String message) {
        if (warningLabelRef != null && warningRectangleRef != null) {
            warningLabelRef.setText(message);
            warningLabelRef.setVisible(true);
            warningRectangleRef.setVisible(true);
        }
    }
}
