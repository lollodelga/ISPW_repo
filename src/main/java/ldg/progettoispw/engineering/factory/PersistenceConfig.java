package ldg.progettoispw.engineering.factory;

public class PersistenceConfig {

    // Aggiunto DEMO
    public enum PersistenceType { JDBC, CSV, DEMO }

    private static PersistenceConfig instance = null;
    private PersistenceType type;

    private PersistenceConfig() {
        this.type = PersistenceType.JDBC; // Default
    }

    public static PersistenceConfig getInstance() {
        if (instance == null) instance = new PersistenceConfig();
        return instance;
    }

    public void setType(PersistenceType type) { this.type = type; }
    public PersistenceType getType() { return type; }
}