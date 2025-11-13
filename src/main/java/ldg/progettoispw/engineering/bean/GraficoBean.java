package ldg.progettoispw.engineering.bean;

public class GraficoBean {
    private String tutorNome;
    private double sentimentMedio;

    public GraficoBean() {}

    public GraficoBean(String tutorNome, double sentimentMedio) {
        this.tutorNome = tutorNome;
        this.sentimentMedio = sentimentMedio;
    }

    public String getTutorNome() {
        return tutorNome;
    }

    public void setTutorNome(String tutorNome) {
        this.tutorNome = tutorNome;
    }

    public double getSentimentMedio() {
        return sentimentMedio;
    }

    public void setSentimentMedio(double sentimentMedio) {
        this.sentimentMedio = sentimentMedio;
    }
}