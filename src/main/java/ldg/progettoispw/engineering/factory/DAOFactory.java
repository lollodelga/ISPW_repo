package ldg.progettoispw.engineering.factory;

import ldg.progettoispw.engineering.dao.*;

public class DAOFactory {

    private DAOFactory() {}

    public static RecensioneDAO getRecensioneDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();

        return switch (type) {
            case CSV  -> new RecensioneDAOCSV();
            case DEMO -> new RecensioneDAOMemory();
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

    public static AppointmentDAO getAppointmentDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();

        // Appointment non ha CSV, quindi CSV e JDBC usano entrambi la versione JDBC.
        // Solo DEMO usa la versione Memory.
        if (type == PersistenceConfig.PersistenceType.DEMO) {
            return new AppointmentDAOMemory();
        } else {
            return new AppointmentDAOJDBC();
        }
    }

    public static UserDAO getUserDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();

        if (type == PersistenceConfig.PersistenceType.DEMO) {
            return new UserDAOMemory();
        } else {
            // Sia CSV che JDBC usano il database MySQL per gli utenti
            return new UserDAOJDBC();
        }
    }
    public static RegistrationDAO getRegistrationDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();
        if (type == PersistenceConfig.PersistenceType.DEMO) {
            return new RegistrationDAOMemory();
        } else {
            return new RegistrationDAOJDBC();
        }
    }

    public static LoginDAO getLoginDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();
        if (type == PersistenceConfig.PersistenceType.DEMO) {
            return new LoginDAOMemory();
        } else {
            return new LoginDAOJDBC();
        }
    }
    // Dentro DAOFactory.java

    public static TutorSearchDAO getTutorSearchDAO() {
        PersistenceConfig.PersistenceType type = PersistenceConfig.getInstance().getType();

        if (type == PersistenceConfig.PersistenceType.DEMO) {
            return new TutorSearchDAOMemory();
        } else {
            // Sia CSV che JDBC usano il database per la ricerca complessa
            return new TutorSearchDAOJDBC();
        }
    }
}