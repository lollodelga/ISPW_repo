package ldg.progettoispw.engineering.bean;

import java.util.ArrayList;
import java.util.List;

public class TutorBean {
    private String email;
    private String nome;
    private String cognome;
    private List<String> materie = new ArrayList<>();

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public List<String> getMaterie() {
        return materie;
    }

    public void setMaterie(List<String> materie) {
        this.materie = materie;
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + String.join(", ", materie) + ")";
    }
}
