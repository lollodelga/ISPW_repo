package ldg.progettoispw.engineering.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ldg.progettoispw.engineering.exception.SentimentException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SentimentClient {

    /**
     * Chiama il server Python per ottenere il sentiment
     * @param text Testo da analizzare
     * @return JSON di risposta dal server Python
     * @throws SentimentException in caso di errore HTTP o I/O
     */
    public String getSentiment(String text) throws SentimentException {
        try {
            URL url = new URL("http://127.0.0.1:8000/bert-sentiment");

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

            return response.toString();

        } catch (Exception e) {
            throw new SentimentException("Errore nella chiamata al servizio di sentiment.", e);
        }
    }


    /**
     * Converte il JSON del server in un valore intero 1–5
     */
    public int parseSentiment(String jsonResponse) throws SentimentException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            String label = root.get("label").asText();

            return Integer.parseInt(label.substring(0, 1));
        } catch (Exception e) {
            throw new SentimentException("Errore nel parsing del sentiment", e);
        }
    }
}
