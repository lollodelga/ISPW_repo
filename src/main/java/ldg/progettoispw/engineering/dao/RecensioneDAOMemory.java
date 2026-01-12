package ldg.progettoispw.engineering.dao;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Recensione;

import java.util.ArrayList;
import java.util.List;

public class RecensioneDAOMemory implements RecensioneDAO {

    // 1. Lista interna STATICA e AUTONOMA
    private static final List<RecensioneBean> REVIEWS_LIST = new ArrayList<>();

    // 2. Blocco statico per caricare dati di prova (Demo)
    static {
        RecensioneBean r1 = new RecensioneBean();
        r1.setId(1);
        r1.setTutorEmail("tutor@demo.it");     // Deve coincidere col tutor di UserDAOMemory
        r1.setStudentEmail("studente@demo.it");
        r1.setRecensione("Tutor bravissimo, spiegazione molto chiara!");
        r1.setSentimentValue(1); // 1 = Positivo

        REVIEWS_LIST.add(r1);
        // Rimosso System.out per pulizia CLI
    }

    @Override
    public void insertRecensione(RecensioneBean bean) throws DBException {
        // Uso la lista interna per salvare
        bean.setId(REVIEWS_LIST.size() + 1);
        REVIEWS_LIST.add(bean);

        // Rimosso System.out per pulizia CLI
    }

    @Override
    public List<Recensione> getRecensioniByTutor(String tutorEmail) throws DBException {
        List<Recensione> list = new ArrayList<>();

        // Leggo dalla lista interna
        for (RecensioneBean b : REVIEWS_LIST) {

            if (b.getTutorEmail() != null && b.getTutorEmail().equals(tutorEmail)) {

                // MAPPING Bean -> Entity
                Recensione r = new Recensione();
                r.setId(b.getId());
                r.setEmailStudente(b.getStudentEmail());
                r.setTesto(b.getRecensione());
                r.setSentimentScore(b.getSentimentValue());

                list.add(r);
            }
        }
        return list;
    }

    @Override
    public boolean isEmpty() {
        return REVIEWS_LIST.isEmpty();
    }

    @Override
    public List<RecensioneBean> getAllRecensioni() throws DBException {
        return new ArrayList<>(REVIEWS_LIST);
    }

    @Override
    public void deleteAll() throws DBException {
        REVIEWS_LIST.clear();
        // Rimosso System.out per pulizia CLI
    }
}