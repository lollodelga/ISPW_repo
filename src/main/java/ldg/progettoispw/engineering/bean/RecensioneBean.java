package ldg.progettoispw.engineering.bean;

public class RecensioneBean {
    private int id;
    private String tutorEmail;
    private String studentEmail;
    private String recensione;
    private int sentimentValue;

    public RecensioneBean() {
        // Costruttore vuoto necessario per JavaFX / FXML / deserializzazione
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTutorEmail() {
        return tutorEmail;
    }
    public void setTutorEmail(String tutorEmail) {
        this.tutorEmail = tutorEmail;
    }

    public String getStudentEmail() {
        return studentEmail;
    }
    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getRecensione() {
        return recensione;
    }
    public void setRecensione(String recensione) {
        this.recensione = recensione;
    }

    public int getSentimentValue() {
        return sentimentValue;
    }
    public void setSentimentValue(int sentimentValue) {
        this.sentimentValue = sentimentValue;
    }
}
