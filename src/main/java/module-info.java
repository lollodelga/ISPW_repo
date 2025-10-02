module ldg.progettoispw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.management;

    opens ldg.progettoispw to javafx.fxml;
    exports ldg.progettoispw;
    exports ldg.progettoispw.view;
    opens ldg.progettoispw.view to javafx.fxml;
}