package ldg.progettoispw.engineering.api;

import ldg.progettoispw.engineering.exception.SentimentException;

public interface SentimentAnalyzer {
    int analyze(String text) throws SentimentException;
}
