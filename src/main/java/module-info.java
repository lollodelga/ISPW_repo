module ldg.progettoispw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires okhttp3;

    // Apre il pacchetto principale (dove c'è il Main)
    opens ldg.progettoispw to javafx.fxml;

    // ⚠️ QUESTA È LA RIGA CHE MANCAVA:
    // Apre il pacchetto 'view' dove hai i Controller grafici
    opens ldg.progettoispw.view to javafx.fxml;

    exports ldg.progettoispw;
}