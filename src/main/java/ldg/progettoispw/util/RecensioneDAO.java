package ldg.progettoispw.util;

import ldg.progettoispw.engineering.bean.RecensioneBean;
import ldg.progettoispw.engineering.exception.DBException;
import ldg.progettoispw.model.Recensione;
import java.util.List;

public interface RecensioneDAO {
    void insertRecensione(RecensioneBean bean) throws DBException;
    List<Recensione> getRecensioniByTutor(String tutorEmail) throws DBException;

    boolean isEmpty();
    List<RecensioneBean> getAllRecensioni() throws DBException;
    void deleteAll() throws DBException;
}