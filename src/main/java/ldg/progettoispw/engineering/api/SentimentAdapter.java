package ldg.progettoispw.engineering.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ldg.progettoispw.engineering.exception.SentimentException;

// Questo è l'ADAPTER. Implementa il Target.
public class SentimentAdapter implements SentimentAnalyzer {

    private final SentimentClient client; // Adaptee
    private final ObjectMapper mapper;

    public SentimentAdapter() {
        // L'Adapter incapsula l'istanza dell'Adaptee
        this.client = new SentimentClient();
        this.mapper = new ObjectMapper();
    }

    @Override
    public int analyze(String text) throws SentimentException {
        try {
            // 1. Chiamata al metodo incompatibile dell'Adaptee
            String jsonResponse = client.getRawSentimentJSON(text);

            // 2. ADATTAMENTO (Logica di conversione)
            // L'Adapter traduce il "linguaggio" dell'Adaptee (JSON)
            // nel "linguaggio" del Target (int)
            JsonNode root = mapper.readTree(jsonResponse);

            // Verifica errori dell'API Python
            if (root.has("error")) {
                throw new SentimentException("API Error: " + root.get("error").asText());
            }

            String label = root.get("label").asText(); // Es: "5 stars"

            // Logica di estrazione
            return Integer.parseInt(label.substring(0, 1));

        } catch (Exception e) {
            throw new SentimentException("Errore durante l'adattamento del sentiment", e);
        }
    }
}