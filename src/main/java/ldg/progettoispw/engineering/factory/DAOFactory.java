package ldg.progettoispw.engineering.factory;

import ldg.progettoispw.engineering.dao.RecensioneDAOCSV;
import ldg.progettoispw.engineering.dao.RecensioneDAOJDBC;
import ldg.progettoispw.util.RecensioneDAO;

public class DAOFactory {

    private DAOFactory() {}

    public static RecensioneDAO getRecensioneDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();

        if (type == PersistenceConfig.PersistenceType.CSV) {
            return new RecensioneDAOCSV();
        } else if (type == PersistenceConfig.PersistenceType.DEMO) {
            return null; // O implementazione Memory
        } else {
            return new RecensioneDAOJDBC();
        }
    }

    // Metodi specifici per il Sincronizzatore
    public static RecensioneDAO getRecensioneDAOJDBC() {
        return new RecensioneDAOJDBC();
    }

    public static RecensioneDAO getRecensioneDAOCSV() {
        return new RecensioneDAOCSV();
    }
}