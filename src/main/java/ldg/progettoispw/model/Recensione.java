package ldg.progettoispw.model;

public class Recensione {

    private int id;
    private int idStudente;
    private int idTutor;
    private String testo;
    private double sentimentScore;
    private boolean sentimentChecked;
    private String emailStudente; // utile per visualizzare nella lista

    public Recensione() {}

    public Recensione(int id, int idStudente, int idTutor, String testo, double sentimentScore,
                      boolean sentimentChecked, String emailStudente) {
        this.id = id;
        this.idStudente = idStudente;
        this.idTutor = idTutor;
        this.testo = testo;
        this.sentimentScore = sentimentScore;
        this.sentimentChecked = sentimentChecked;
        this.emailStudente = emailStudente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdStudente() {
        return idStudente;
    }

    public void setIdStudente(int idStudente) {
        this.idStudente = idStudente;
    }

    public int getIdTutor() {
        return idTutor;
    }

    public void setIdTutor(int idTutor) {
        this.idTutor = idTutor;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public boolean isSentimentChecked() {
        return sentimentChecked;
    }

    public void setSentimentChecked(boolean sentimentChecked) {
        this.sentimentChecked = sentimentChecked;
    }

    public String getEmailStudente() {
        return emailStudente;
    }

    public void setEmailStudente(String emailStudente) {
        this.emailStudente = emailStudente;
    }
}
