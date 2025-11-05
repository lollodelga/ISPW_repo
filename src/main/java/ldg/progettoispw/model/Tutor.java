package ldg.progettoispw.model;

import java.util.ArrayList;
import java.util.List;

public class Tutor {
    private String email;
    private String nome;
    private String cognome;
    private List<String> materie;

    public Tutor(String email, String nome, String cognome) {
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.materie = new ArrayList<>();
    }

    public void addMateria(String materia) {
        if (!materie.contains(materia)) {
            materie.add(materia);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public List<String> getMaterie() {
        return materie;
    }

    @Override
    public String toString() {
        return nome + " " + cognome + " (" + String.join(", ", materie) + ")";
    }
}
