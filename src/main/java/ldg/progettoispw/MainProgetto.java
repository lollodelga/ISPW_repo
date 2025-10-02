package ldg.progettoispw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ldg.progettoispw.model.applicativo.LoginSessionManager;
import ldg.progettoispw.model.dao.ConnectionFactory;

public class MainProgetto extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FirstPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/iconApp.jpeg")));
        stage.setTitle("TutorOnline");
        stage.setResizable(false);
        //Alla chiusura dell'app elimino i dati di login e chiudo la connection
        stage.setOnCloseRequest(event -> {
            LoginSessionManager.clearSession();
            ConnectionFactory.getInstance().closeConnection();
        });
        stage.show();
    }
    public void run(){ launch();}
}
