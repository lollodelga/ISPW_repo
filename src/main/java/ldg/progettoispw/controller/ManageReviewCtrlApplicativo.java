package ldg.progettoispw.controller;

import ldg.progettoispw.engineering.applicativo.LoginSessionManager;
import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.bean.UserBean;
import ldg.progettoispw.engineering.dao.RecensioneDAO;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Recensione;

import java.util.ArrayList;
import java.util.List;

public class ManageReviewCtrlApplicativo {

    private final RecensioneDAO recensioneDAO = new RecensioneDAO();

    /**
     * Classe di supporto per restituire due liste contemporaneamente
     */
    public static class RecensioniResult {
        private final List<RecensioneBean> recensioni;
        private final List<Integer> sentimentValues;

        public RecensioniResult(List<RecensioneBean> recensioni, List<Integer> sentimentValues) {
            this.recensioni = recensioni;
            this.sentimentValues = sentimentValues;
        }

        public List<RecensioneBean> getRecensioni() { return recensioni; }
        public List<Integer> getSentimentValues() { return sentimentValues; }
    }

    /**
     * Recupera tutte le recensioni del tutor loggato e crea le liste di bean
     */
    public RecensioniResult getRecensioniEValoriSentiment() throws DBException {
        UserBean tutor = LoginSessionManager.loadUserSession();
        if (tutor == null || tutor.getEmail() == null || tutor.getEmail().isEmpty()) {
            throw new IllegalStateException("Nessun tutor loggato.");
        }

        String tutorEmail = tutor.getEmail();

        List<Recensione> recensioniModel = recensioneDAO.getRecensioniByTutor(tutorEmail);

        List<RecensioneBean> recensioniBean = new ArrayList<>();
        List<Integer> sentimentValues = new ArrayList<>();

        for (Recensione r : recensioniModel) {
            RecensioneBean bean = new RecensioneBean();
            bean.setStudentEmail(r.getEmailStudente());
            bean.setRecensione(r.getTesto());
            bean.setSentimentValue(r.getSentimentScore()); // int
            recensioniBean.add(bean);

            sentimentValues.add(r.getSentimentScore());
        }

        return new RecensioniResult(recensioniBean, sentimentValues);
    }
}
