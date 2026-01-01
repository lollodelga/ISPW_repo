package ldg.progettoispw.engineering.api;

import ldg.progettoispw.engineering.exception.SentimentException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// Questo è l'ADAPTEE
public class SentimentClient {

    public String getRawSentimentJSON(String text) throws SentimentException {
        try {
            URL url = URI.create("http://127.0.0.1:8000/bert-sentiment").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput = "{\"text\": [\"" + text.replace("\"", "\\\"") + "\"]}";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)
            )) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            // Ritorna la stringa JSON grezza. NON FA PARSING.
            return response.toString();

        } catch (Exception e) {
            throw new SentimentException("Errore nella chiamata al servizio API", e);
        }
    }
}