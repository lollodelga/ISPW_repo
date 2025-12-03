package ldg.progettoispw.engineering.api;

import ldg.progettoispw.engineering.exception.SentimentException;

public class SentimentAdapter implements SentimentAnalyzer {

    private final SentimentClient client;
    //il codice di tale classe andrebbe messo qui, ma va ve bene anche se la dichiaro e basta

    public SentimentAdapter(SentimentClient client) {
        this.client = client;
    }

    @Override
    public int analyze(String text) throws SentimentException {
        String response = client.getSentiment(text);
        return client.parseSentiment(response);
    }
}
