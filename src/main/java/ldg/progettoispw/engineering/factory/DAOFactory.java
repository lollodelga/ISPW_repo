package ldg.progettoispw.engineering.factory;

import ldg.progettoispw.engineering.dao.RecensioneDAOCSV;
import ldg.progettoispw.engineering.dao.RecensioneDAOJDBC;
import ldg.progettoispw.util.RecensioneDAO;

public class DAOFactory {

    private DAOFactory() {}

    public static RecensioneDAO getRecensioneDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();

        return switch (type) {
            case CSV  -> new RecensioneDAOCSV();
            case DEMO -> null; // Qui restituirò new demo()
            default   -> new RecensioneDAOJDBC();
        };
    }

    // Metodi specifici per il Sincronizzatore
    public static RecensioneDAO getRecensioneDAOJDBC() {
        return new RecensioneDAOJDBC();
    }

    public static RecensioneDAO getRecensioneDAOCSV() {
        return new RecensioneDAOCSV();
    }
}