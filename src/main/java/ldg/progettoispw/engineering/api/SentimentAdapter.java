package ldg.progettoispw.engineering.api;

public class SentimentAdapter implements SentimentAnalyzer {

    private final SentimentClient client;

    public SentimentAdapter(SentimentClient client) {
        this.client = client;
    }

    @Override
    public int analyze(String text) throws Exception {
        String response = client.getSentiment(text);
        return client.parseSentiment(response);
    }
}
