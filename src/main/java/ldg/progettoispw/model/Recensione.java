package ldg.progettoispw.model;

public class Recensione {

    private int id;
    private String emailStudente;
    private String testo;
    private int sentimentScore;  // INT, non double

    public Recensione() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getEmailStudente() { return emailStudente; }
    public void setEmailStudente(String emailStudente) { this.emailStudente = emailStudente; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public int getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(int sentimentScore) { this.sentimentScore = sentimentScore; }
}
